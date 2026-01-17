package com.xinian.solarlib.api.command;

import com.xinian.solarlib.api.registry.IRegistration;

import javax.annotation.Nonnull;

/**
 * Command registration interface
 * Extracted from Hytale's CommandRegistration
 */
public interface ICommandRegistration extends IRegistration {
    
    /**
     * Get the registered command
     *
     * @return The command
     */
    @Nonnull
    ICommand getCommand();
}
