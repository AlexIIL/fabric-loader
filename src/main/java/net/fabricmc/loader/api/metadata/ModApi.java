package net.fabricmc.loader.api.metadata;

import net.fabricmc.api.ModApiProvider;

public interface ModApi {

	String getApiClassName();

	/** @return The modid of the mod that must implement the api, or null if it's an open API. */
	String getMandatedImplModId();

	/** If this value is present then requiring mods can load even if no implementations are present.
	 * 
	 * @return The default, or null if no default is provided. */
	ApiImpl getDefaultImplementation();

	public interface ApiImpl {
		String getImplClassName();

		/** @return True if the {@link #getImplClassName()} is a {@link ModApiProvider} rather than an impl of the
		 *         {@link #getApiClassName()}. */
		boolean isImplProvider();

		String getLanguageAdaptorName();
	}
}
