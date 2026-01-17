package com.xinian.solarlib.api.registry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Registry interface for managing registrations
 * Extracted from Hytale's Registry system
 *
 * @param <T> The type of registration
 */
public interface IRegistry<T extends IRegistration> {
    
    /**
     * Register a registration object
     *
     * @param registration The registration to register
     * @return The registered registration
     */
    T register(@Nonnull T registration);
    
    /**
     * Check if the registry is enabled
     *
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();
    
    /**
     * Enable the registry
     */
    void enable();
    
    /**
     * Shutdown the registry and cleanup
     */
    void shutdown();
    
    /**
     * Get all registrations
     *
     * @return List of registrations
     */
    @Nonnull
    List<IRegistrationConsumer> getRegistrations();
    
    /**
     * Check precondition before operations
     */
    void checkPrecondition();
}
