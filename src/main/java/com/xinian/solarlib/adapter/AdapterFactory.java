package com.xinian.solarlib.adapter;

import com.xinian.solarlib.api.adapter.IAdapterFactory;
import com.xinian.solarlib.api.adapter.IHytaleCommandAdapter;
import com.xinian.solarlib.api.adapter.IHytaleRegistryAdapter;
import com.xinian.solarlib.api.registry.IRegistration;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

/**
 * Factory for creating Hytale adapters
 * Provides singleton access to adapter implementations
 */
public class AdapterFactory implements IAdapterFactory {
    private static final Logger LOGGER = Logger.getLogger(AdapterFactory.class.getName());
    private static AdapterFactory instance;
    
    private final IHytaleCommandAdapter commandAdapter;
    private final boolean available;
    
    private AdapterFactory() {
        boolean tempAvailable = false;
        IHytaleCommandAdapter tempCommandAdapter = null;
        
        try {
            // Check if Hytale classes are available
            Class.forName("com.hypixel.hytale.server.core.command.system.AbstractCommand");
            Class.forName("com.hypixel.hytale.registry.Registry");
            
            // If we get here, Hytale classes are available
            tempCommandAdapter = new HytaleCommandAdapterImpl();
            tempAvailable = true;
            LOGGER.info("Hytale adapters initialized successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Hytale classes not found. Adapters will not be available. " +
                          "This is expected if running in a non-Hytale environment.");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize Hytale adapters: " + e.getMessage());
            e.printStackTrace();
        }
        
        this.commandAdapter = tempCommandAdapter;
        this.available = tempAvailable;
    }
    
    /**
     * Get the singleton instance
     *
     * @return The adapter factory instance
     */
    @Nonnull
    public static AdapterFactory getInstance() {
        if (instance == null) {
            synchronized (AdapterFactory.class) {
                if (instance == null) {
                    instance = new AdapterFactory();
                }
            }
        }
        return instance;
    }
    
    @Override
    public IHytaleCommandAdapter getCommandAdapter() {
        if (!available) {
            throw new IllegalStateException(
                "Hytale adapters are not available. Make sure Hytale classes are in the classpath."
            );
        }
        return commandAdapter;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IRegistration> IHytaleRegistryAdapter<T> getRegistryAdapter() {
        if (!available) {
            throw new IllegalStateException(
                "Hytale adapters are not available. Make sure Hytale classes are in the classpath."
            );
        }
        return new HytaleRegistryAdapterImpl<>();
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * Check if Hytale command adapter is available
     *
     * @return true if command adapter is available
     */
    public boolean isCommandAdapterAvailable() {
        return available && commandAdapter != null;
    }
    
    /**
     * Get a safe command adapter that throws clear exceptions if not available
     *
     * @return The command adapter
     * @throws IllegalStateException if adapters are not available
     */
    @Nonnull
    public IHytaleCommandAdapter requireCommandAdapter() {
        if (!isCommandAdapterAvailable()) {
            throw new IllegalStateException(
                "Command adapter is not available. Ensure Hytale server classes are loaded."
            );
        }
        return commandAdapter;
    }
    
    @Override
    public String toString() {
        return "AdapterFactory{available=" + available + "}";
    }
}
