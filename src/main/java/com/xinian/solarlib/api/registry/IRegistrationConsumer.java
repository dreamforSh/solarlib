package com.xinian.solarlib.api.registry;

/**
 * Functional interface for consuming boolean values in registration context
 * Extracted from Hytale's BooleanConsumer
 */
@FunctionalInterface
public interface IRegistrationConsumer {
    
    /**
     * Accept a boolean value
     *
     * @param value The boolean value to accept
     */
    void accept(boolean value);
}
