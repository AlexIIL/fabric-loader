package net.fabricmc.api;

@FunctionalInterface
public interface ModApiProvider {

	/** Attempts to provide the implementation for the given class, returning null if this doesn't provide an
	 * implementation of the given api. */
	<T> T provideImplementation(Class<T> apiClass);
}
