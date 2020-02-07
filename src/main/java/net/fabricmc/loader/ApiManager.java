package net.fabricmc.loader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.api.ModApiProvider;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.LanguageAdapterException;
import net.fabricmc.loader.api.metadata.ModApi;
import net.fabricmc.loader.api.metadata.ModApi.ApiImpl;
import net.fabricmc.loader.api.metadata.ModApiImpl;

public class ApiManager {

	/** Map of api class name to api declaration. */
	private final Map<String, ModApiEntry> declaredApis = new HashMap<>();

	/** Map of mod api provider class name to actual provider. */
	private final Map<String, ModApiProvider> apiProviders = new ConcurrentHashMap<>();

	/** Map of api class name to implementation object. */
	private final Map<String, Object> loadedApis = new ConcurrentHashMap<>();

	public static class ModApiEntry {
		public final String implModId;
		public final ApiImpl impl;
		public final LanguageAdapter languageAdaptor;

		public ModApiEntry(String implModId, ApiImpl impl, LanguageAdapter languageAdaptor) {
			this.implModId = implModId;
			this.impl = impl;
			this.languageAdaptor = languageAdaptor;
		}
	}

	public <T> T getApiInstance(Class<T> apiClass) {
		Object impl = loadedApis.get(apiClass.getName());

		if (impl == null) {
			return loadApi(apiClass);
		}

		return apiClass.cast(impl);
	}

	public ApiImpl getApiMapping(ModApi api) {
		String key = api.getApiClassName();
		ModApiEntry entry = declaredApis.get(key);

		if (entry != null) {
			return entry.impl;
		}

		throw new IllegalArgumentException("The API " + api + " hasn't been declared!");
	}

	public String getApiImplementor(ModApi api) {
		String key = api.getApiClassName();
		ModApiEntry entry = declaredApis.get(key);

		if (entry != null) {
			return entry.implModId;
		}

		throw new IllegalArgumentException("The API " + api + " hasn't been declared!");
	}

	private synchronized <T> T loadApi(Class<T> apiClass) {
		Object impl = loadedApis.get(apiClass.getName());

		if (impl != null) {
			return apiClass.cast(apiClass);
		}

		ModApiEntry apiEntry = declaredApis.get(apiClass.getName());

		if (apiEntry == null) {
			throw new IllegalArgumentException(apiClass + " hasn't been declared as an API!");
		}

		ModContainer mod = FabricLoader.INSTANCE.modMap.get(apiEntry.implModId);
		T newImpl;

		if (apiEntry.impl.isImplProvider()) {
			ModApiProvider provider = getProvider(apiEntry, mod);
			newImpl = provider.provideImplementation(apiClass);

			if (newImpl == null) {
				throw new IllegalStateException(
					"The api provider " + apiEntry.impl.getImplClassName() + " didn't provide an API for " + apiClass
				);
			}
		} else {
			try {
				newImpl = apiEntry.languageAdaptor.create(mod, apiEntry.impl.getImplClassName(), apiClass);
			} catch (LanguageAdapterException e) {
				throw new IllegalStateException(
					"An error occured while loading the API implementation for " + apiClass, e
				);
			}
		}

		loadedApis.put(apiClass.getName(), newImpl);
		return newImpl;
	}

	private <T> ModApiProvider getProvider(ModApiEntry apiEntry, ModContainer mod) {
		String implClass = apiEntry.impl.getImplClassName();
		ModApiProvider provider = apiProviders.get(implClass);

		if (provider != null) {
			return provider;
		}

		try {
			provider = apiEntry.languageAdaptor.create(mod, implClass, ModApiProvider.class);
		} catch (LanguageAdapterException e) {
			throw new IllegalStateException("An error occured while loading the API provider for " + implClass, e);
		}

		apiProviders.put(implClass, provider);
		return provider;
	}

	protected synchronized void defineApis(Map<ModApi, String> mappings) {
		for (Map.Entry<ModApi, String> entry : mappings.entrySet()) {

			ModApi api = entry.getKey();
			String implModId = entry.getValue();

			ApiImpl theImpl = null;
			if (implModId == null) {

				theImpl = api.getDefaultImplementation();
				if (theImpl == null) {
					throw new IllegalArgumentException("");
				}
			} else {
				ModContainer mc = FabricLoader.INSTANCE.modMap.get(implModId);
				assert mc != null : "The caller should pass in a validated map!";

				for (ModApiImpl impl : mc.getMetadata().getApiImpls()) {
					if (impl.getApiClassName().equals(api.getApiClassName())) {
						if (theImpl == null) {
							theImpl = impl;
						} else {
							assert false : "The caller shouldn't have let the mod " + implModId
								+ " load with a duplicated mod impl map!";
						}
					}
				}

				if (theImpl == null) {
					throw new IllegalArgumentException(
						"The caller should pass in a validated map! (The mod " + implModId
							+ " didn't provide an implementation of " + api.getApiClassName()
					);
				}
			}

			String langName = theImpl.getLanguageAdaptorName();
			LanguageAdapter languageAdaptor = FabricLoader.INSTANCE.adapterMap.get(langName);
			declaredApis.put(api.getApiClassName(), new ModApiEntry(implModId, theImpl, languageAdaptor));
		}
	}
}
