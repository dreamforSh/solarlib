package com.xinian.solarlib.api.command;

import com.xinian.solarlib.api.registry.BaseRegistration;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

/**
 * Base implementation of ICommandRegistration
 * Extracted from Hytale's CommandRegistration
 */
public class BaseCommandRegistration extends BaseRegistration implements ICommandRegistration {
    @Nonnull
    private final ICommand command;
    
    public BaseCommandRegistration(@Nonnull ICommand command,
                                   @Nonnull BooleanSupplier isEnabled,
                                   @Nonnull Runnable unregister) {
        super(isEnabled, unregister);
        this.command = command;
    }
    
    @Nonnull
    @Override
    public ICommand getCommand() {
        return command;
    }
    
    @Nonnull
    @Override
    public String toString() {
        return "CommandRegistration{command=" + command.getName() + ", registered=" + isRegistered() + "}";
    }
}
