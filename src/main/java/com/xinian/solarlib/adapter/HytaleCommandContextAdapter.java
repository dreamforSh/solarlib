package com.xinian.solarlib.adapter;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
import com.xinian.solarlib.api.command.ICommand;
import com.xinian.solarlib.api.command.ICommandContext;
import com.xinian.solarlib.api.command.ICommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for Hytale CommandContext to SolarLib ICommandContext
 * Wraps Hytale's CommandContext and provides ICommandContext interface
 */
public class HytaleCommandContextAdapter implements ICommandContext {
    private final CommandContext hytaleContext;
    private final ICommandSender sender;
    private final ICommand command;
    private final Map<String, Argument<?, ?>> argumentCache;
    
    public HytaleCommandContextAdapter(@Nonnull CommandContext hytaleContext,
                                       @Nonnull ICommand command) {
        this.hytaleContext = hytaleContext;
        this.sender = new HytaleCommandSenderAdapter(hytaleContext.sender());
        this.command = command;
        this.argumentCache = new HashMap<>();
    }
    
    @Nonnull
    @Override
    public ICommandSender getSender() {
        return sender;
    }
    
    @Nonnull
    @Override
    public String getInputString() {
        return hytaleContext.getInputString();
    }
    
    @Nonnull
    @Override
    public ICommand getCalledCommand() {
        return command;
    }
    
    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getArgument(@Nonnull String argumentName) {
        Argument<?, ?> argument = argumentCache.get(argumentName);
        if (argument == null) {
            // Try to find the argument in Hytale's context
            // This is a simplified implementation
            // In practice, you'd need to track arguments more carefully
            return null;
        }
        
        try {
            return (T) hytaleContext.get(argument);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public boolean hasArgument(@Nonnull String argumentName) {
        Argument<?, ?> argument = argumentCache.get(argumentName);
        if (argument == null) {
            return false;
        }
        return hytaleContext.provided(argument);
    }
    
    @Nonnull
    @Override
    public Map<String, Object> getAllArguments() {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Argument<?, ?>> entry : argumentCache.entrySet()) {
            if (hytaleContext.provided(entry.getValue())) {
                Object value = hytaleContext.get(entry.getValue());
                if (value != null) {
                    result.put(entry.getKey(), value);
                }
            }
        }
        return result;
    }
    
    /**
     * Register an argument for tracking
     * This allows the adapter to retrieve argument values by name
     *
     * @param name The argument name
     * @param argument The Hytale argument object
     */
    public void registerArgument(@Nonnull String name, @Nonnull Argument<?, ?> argument) {
        argumentCache.put(name, argument);
    }
    
    /**
     * Get the wrapped Hytale CommandContext
     *
     * @return The original Hytale CommandContext
     */
    @Nonnull
    public CommandContext getHytaleContext() {
        return hytaleContext;
    }
    
    @Override
    public String toString() {
        return "HytaleCommandContextAdapter{sender=" + sender.getDisplayName() + 
               ", command=" + command.getName() + "}";
    }
}
