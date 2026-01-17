package com.xinian.solarlib.adapter;

import com.hypixel.hytale.registry.Registration;
import com.hypixel.hytale.registry.Registry;
import com.xinian.solarlib.api.adapter.IHytaleRegistryAdapter;
import com.xinian.solarlib.api.registry.IRegistration;
import com.xinian.solarlib.api.registry.IRegistry;
import com.xinian.solarlib.api.registry.IRegistrationConsumer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Implementation of IHytaleRegistryAdapter
 * Provides bidirectional adaptation between Hytale registries and SolarLib registries
 */
public class HytaleRegistryAdapterImpl<T extends IRegistration> implements IHytaleRegistryAdapter<T> {
    
    @Nonnull
    @Override
    public Object adaptToHytale(@Nonnull IRegistry<T> registry) {
        // This is complex as it requires creating a Hytale Registry
        // For now, we'll throw an exception indicating this direction is not supported
        throw new UnsupportedOperationException(
            "Converting SolarLib registry to Hytale registry is not yet supported. " +
            "Use adaptFromHytale() to wrap Hytale registries instead."
        );
    }
    
    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public IRegistry<T> adaptFromHytale(@Nonnull Object hytaleRegistry) {
        if (!(hytaleRegistry instanceof Registry)) {
            throw new IllegalArgumentException("Object is not a Hytale Registry");
        }
        return new HytaleToSolarLibRegistry<>((Registry<?>) hytaleRegistry);
    }
    
    @Nonnull
    @Override
    public Object adaptRegistrationToHytale(@Nonnull T registration) {
        // Convert SolarLib registration to Hytale registration
        return new Registration(
            registration.getIsEnabled(),
            registration.getUnregister()
        );
    }
    
    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T adaptRegistrationFromHytale(@Nonnull Object hytaleRegistration) {
        if (!(hytaleRegistration instanceof Registration)) {
            throw new IllegalArgumentException("Object is not a Hytale Registration");
        }
        return (T) new HytaleToSolarLibRegistration((Registration) hytaleRegistration);
    }
    
    /**
     * Adapter that wraps a Hytale Registry as a SolarLib IRegistry
     */
    private static class HytaleToSolarLibRegistry<T extends IRegistration> implements IRegistry<T> {
        private final Registry<?> hytaleRegistry;
        
        public HytaleToSolarLibRegistry(@Nonnull Registry<?> hytaleRegistry) {
            this.hytaleRegistry = hytaleRegistry;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public T register(@Nonnull T registration) {
            // Convert SolarLib registration to Hytale registration
            Registration hytaleReg = new Registration(
                registration.getIsEnabled(),
                registration.getUnregister()
            );
            
            // Register with Hytale registry - use raw type to avoid generic issues
            ((Registry) hytaleRegistry).register(hytaleReg);
            
            return registration;
        }
        
        @Override
        public boolean isEnabled() {
            return hytaleRegistry.isEnabled();
        }
        
        @Override
        public void enable() {
            hytaleRegistry.enable();
        }
        
        @Override
        public void shutdown() {
            hytaleRegistry.shutdown();
        }
        
        @Nonnull
        @Override
        public List<IRegistrationConsumer> getRegistrations() {
            List<IRegistrationConsumer> result = new ArrayList<>();
            // Hytale uses BooleanConsumer, which is compatible with our IRegistrationConsumer
            hytaleRegistry.getRegistrations().forEach(bc -> result.add(bc::accept));
            return result;
        }
        
        @Override
        public void checkPrecondition() {
            // Hytale's checkPrecondition is protected, so we can't call it directly
            // We'll skip this check for now
            // The registry should handle precondition checks internally
        }
        
        /**
         * Get the wrapped Hytale registry
         */
        @Nonnull
        public Registry<?> getHytaleRegistry() {
            return hytaleRegistry;
        }
    }
    
    /**
     * Adapter that wraps a Hytale Registration as a SolarLib IRegistration
     */
    private static class HytaleToSolarLibRegistration implements IRegistration {
        private final Registration hytaleRegistration;
        
        public HytaleToSolarLibRegistration(@Nonnull Registration hytaleRegistration) {
            this.hytaleRegistration = hytaleRegistration;
        }
        
        @Override
        public void unregister() {
            hytaleRegistration.unregister();
        }
        
        @Override
        public boolean isRegistered() {
            return hytaleRegistration.isRegistered();
        }
        
        @Nonnull
        @Override
        public BooleanSupplier getIsEnabled() {
            // Hytale Registration doesn't expose isEnabled directly
            // Return a default supplier
            return () -> isRegistered();
        }
        
        @Nonnull
        @Override
        public Runnable getUnregister() {
            // Return a runnable that calls unregister
            return this::unregister;
        }
        
        /**
         * Get the wrapped Hytale registration
         */
        @Nonnull
        public Registration getHytaleRegistration() {
            return hytaleRegistration;
        }
    }
}
