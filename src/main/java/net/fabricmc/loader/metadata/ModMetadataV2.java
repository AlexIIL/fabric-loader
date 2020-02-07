package net.fabricmc.loader.metadata;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.fabricmc.api.ModApiProvider;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.metadata.ModApi;
import net.fabricmc.loader.api.metadata.ModApi.ApiImpl;
import net.fabricmc.loader.api.metadata.ModApiImpl;
import net.fabricmc.loader.metadata.ModMetadataV2.ApiContainer.ApiEntry;
import net.fabricmc.loader.metadata.ModMetadataV2.ApiContainer.DefaultApiImpl;

public class ModMetadataV2 extends ModMetadataV1 {

	// Optional (api)
	private ApiContainer apis;
	private ApiImplContainer implementations;

	public static abstract class ApiContainer {

		private final ApiEntry[] apis;

		public ApiContainer(ApiEntry[] apis) {
			this.apis = apis;
		}

		public static class Deserializer implements JsonDeserializer<ApiContainer> {
			@Override
			public ApiContainer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			}
		}

		public static class ApiEntry implements ModApi {
			private final String apiClassName;

			/** If non-null then this refers to the single modid that may implement this APi. */
			private final String singleImplementorModId;

			private final DefaultApiImpl defaultImpl;

			public ApiEntry(String apiClassName, String singleImplementorModId, String defaultImplClassName,
				boolean defaultImplIsProvider, String defaultImplLanguageAdaptorName) {

				this.apiClassName = apiClassName;
				this.singleImplementorModId = singleImplementorModId;
				if (defaultImplClassName == null) {
					defaultImpl = null;
				} else {
					defaultImpl = new DefaultApiImpl(
						defaultImplClassName, defaultImplIsProvider, defaultImplLanguageAdaptorName
					);
				}
			}

			@Override
			public String getApiClassName() {
				return apiClassName;
			}

			@Override
			public String getMandatedImplModId() {
				return singleImplementorModId;
			}

			@Override
			public DefaultApiImpl getDefaultImplementation() {
				return defaultImpl;
			}
		}

		public static class DefaultApiImpl implements ApiImpl {

			/** The class name that implements the {@link ModApi#getApiClassName()}. (Or a {@link ModApiProvider} if
			 * {@link #implIsProvider} is true).
			 * <p>
			 * This is passed to the {@link LanguageAdapter} to be loaded. */
			private final String implClassName;

			/** If true then {@link #implClassName} must be an instance of {@link ModApiProvider}, rather than being a
			 * direct instance of the given class. */
			private final boolean implIsProvider;

			private final String languageAdaptorName;

			public DefaultApiImpl(String implClassName, boolean implIsProvider, String languageAdaptorName) {
				this.implClassName = implClassName;
				this.implIsProvider = implIsProvider;
				this.languageAdaptorName = languageAdaptorName;
			}

			@Override
			public String getImplClassName() {
				return implClassName;
			}

			@Override
			public boolean isImplProvider() {
				return implIsProvider;
			}

			@Override
			public String getLanguageAdaptorName() {
				return languageAdaptorName;
			}
		}
	}

	public static class ApiImplContainer {

		private final ApiImplEntry[] implementations;

		public ApiImplContainer(ApiImplEntry[] implementations) {
			this.implementations = implementations;
		}

		public static class Deserializer implements JsonDeserializer<ApiImplContainer> {
			@Override
			public ApiImplContainer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			}
		}

		public static class ApiImplEntry extends DefaultApiImpl implements ModApiImpl {

			/** The modid of the mod that contains the API. */
			private final String apiModId;

			/** The API class. The mod identified by {@link #apiModId} must have an api entry with
			 * {@link ApiEntry#apiClassName} equal to this. */
			private final String apiClassName;

			/** If true then the containing mod will only be loaded if another mod depends on the API that it
			 * implements, and all of the API's provided */
			private final boolean loadOnlyIfRequired;

			private final ModApiImpl.ApiImplPriority priority;

			public ApiImplEntry(String apiModId, String apiClassName, String implClassName, boolean implIsProvider,
				String languageAdaptorName, boolean loadOnlyIfRequired, ApiImplPriority priority) {
				super(implClassName, implIsProvider, languageAdaptorName);
				this.apiModId = apiModId;
				this.apiClassName = apiClassName;
				this.loadOnlyIfRequired = loadOnlyIfRequired;
				this.priority = priority;
			}

			@Override
			public String getApiModid() {
				return apiModId;
			}

			@Override
			public String getApiClassName() {
				return apiClassName;
			}

			@Override
			public boolean shouldLoadOnlyIfRequired() {
				return loadOnlyIfRequired;
			}

			@Override
			public ApiImplPriority getPriority() {
				return priority;
			}
		}
	}
}
