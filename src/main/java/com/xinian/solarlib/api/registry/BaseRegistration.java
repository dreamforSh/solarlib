package com.xinian.solarlib.api.registry;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

/**
 * Base implementation of IRegistration
 * Extracted from Hytale's Registration class
 */
public class BaseRegistration implements IRegistration {
    @Nonnull
    protected final BooleanSupplier isEnabled;
    @Nonnull
    protected final Runnable unregister;
    private boolean registered;
    
    public BaseRegistration(@Nonnull BooleanSupplier isEnabled, @Nonnull Runnable unregister) {
        this.isEnabled = isEnabled;
        this.unregister = unregister;
        this.registered = false;
    }
    
    @Override
    public void unregister() {
        if (registered) {
            unregister.run();
            registered = false;
        }
    }
    
    @Override
    public boolean isRegistered() {
        return registered;
    }
    
    @Nonnull
    @Override
    public BooleanSupplier getIsEnabled() {
        return isEnabled;
    }
    
    @Nonnull
    @Override
    public Runnable getUnregister() {
        return unregister;
    }
    
    /**
     * Mark this registration as registered
     * Internal method for registry use
     */
    protected void markRegistered() {
        this.registered = true;
    }
    
    @Nonnull
    @Override
    public String toString() {
        return "BaseRegistration{registered=" + registered + "}";
    }
}
