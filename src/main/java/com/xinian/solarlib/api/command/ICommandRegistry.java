package com.xinian.solarlib.api.command;

import com.xinian.solarlib.api.registry.IRegistry;
import com.xinian.solarlib.api.registry.IRegistration;

import javax.annotation.Nonnull;

/**
 * Command registry interface
 * Extracted from Hytale's CommandRegistry
 */
public interface ICommandRegistry extends IRegistry<ICommandRegistration> {
    
    /**
     * Register a command
     *
     * @param command The command to register
     * @return The command registration
     */
    @Nonnull
    ICommandRegistration registerCommand(@Nonnull ICommand command);
}
