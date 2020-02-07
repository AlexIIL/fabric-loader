package net.fabricmc.loader.api.metadata;

import net.fabricmc.loader.api.metadata.ModApi.ApiImpl;

public interface ModApiImpl extends ApiImpl {
	public enum ApiImplPriority {
		/** Identifies that this mod is a default implementation of the given API, with minimal features implemented.
		 * (In other words fabric-loader will try to load something else before this one, and only fall back to the
		 * default ones if no better ones were found). */
		DEFAULT,

		/** (The default value, if omitted). Higher priority than {@link #DEFAULT}. */
		NORMAL,
	}

	String getApiModid();

	String getApiClassName();

	boolean shouldLoadOnlyIfRequired();

	ApiImplPriority getPriority();
}
