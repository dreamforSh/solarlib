package com.xinian.solarlib.api.adapter;

/**
 * Factory interface for creating adapters
 */
public interface IAdapterFactory {
    
    /**
     * Get the command adapter
     *
     * @return The command adapter instance
     */
    IHytaleCommandAdapter getCommandAdapter();
    
    /**
     * Get the registry adapter
     *
     * @param <T> The type of registration
     * @return The registry adapter instance
     */
    <T extends com.xinian.solarlib.api.registry.IRegistration> IHytaleRegistryAdapter<T> getRegistryAdapter();
    
    /**
     * Check if adapters are available
     * (i.e., if Hytale classes are present)
     *
     * @return true if adapters can be used, false otherwise
     */
    boolean isAvailable();
}
