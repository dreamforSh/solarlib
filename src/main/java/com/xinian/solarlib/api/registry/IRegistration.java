package com.xinian.solarlib.api.registry;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

/**
 * Registration interface for managing registered objects
 * Extracted from Hytale's Registration system
 */
public interface IRegistration {
    
    /**
     * Unregister this registration
     */
    void unregister();
    
    /**
     * Check if this registration is currently registered
     *
     * @return true if registered, false otherwise
     */
    boolean isRegistered();
    
    /**
     * Get the enabled state supplier
     *
     * @return BooleanSupplier that returns if this registration is enabled
     */
    @Nonnull
    BooleanSupplier getIsEnabled();
    
    /**
     * Get the unregister runnable
     *
     * @return Runnable to execute when unregistering
     */
    @Nonnull
    Runnable getUnregister();
}
