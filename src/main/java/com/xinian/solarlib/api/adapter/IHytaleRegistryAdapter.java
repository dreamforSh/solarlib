package com.xinian.solarlib.api.adapter;

import com.xinian.solarlib.api.registry.IRegistration;
import com.xinian.solarlib.api.registry.IRegistry;

import javax.annotation.Nonnull;

/**
 * Adapter interface for bridging Hytale registries to SolarLib API
 */
public interface IHytaleRegistryAdapter<T extends IRegistration> {
    
    /**
     * Adapt a SolarLib registry to Hytale's registry system
     *
     * @param registry The SolarLib registry to adapt
     * @return The Hytale registry object
     */
    @Nonnull
    Object adaptToHytale(@Nonnull IRegistry<T> registry);
    
    /**
     * Adapt a Hytale registry to SolarLib's registry system
     *
     * @param hytaleRegistry The Hytale registry to adapt
     * @return The SolarLib registry
     */
    @Nonnull
    IRegistry<T> adaptFromHytale(@Nonnull Object hytaleRegistry);
    
    /**
     * Adapt a SolarLib registration to Hytale's registration system
     *
     * @param registration The SolarLib registration to adapt
     * @return The Hytale registration object
     */
    @Nonnull
    Object adaptRegistrationToHytale(@Nonnull T registration);
    
    /**
     * Adapt a Hytale registration to SolarLib's registration system
     *
     * @param hytaleRegistration The Hytale registration to adapt
     * @return The SolarLib registration
     */
    @Nonnull
    T adaptRegistrationFromHytale(@Nonnull Object hytaleRegistration);
}
