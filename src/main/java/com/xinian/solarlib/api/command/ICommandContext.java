package com.xinian.solarlib.api.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Command context for execution
 * Extracted from Hytale's CommandContext
 */
public interface ICommandContext {
    
    /**
     * Get the command sender
     *
     * @return The command sender
     */
    @Nonnull
    ICommandSender getSender();
    
    /**
     * Get the input string
     *
     * @return The input string
     */
    @Nonnull
    String getInputString();
    
    /**
     * Get the called command
     *
     * @return The called command
     */
    @Nonnull
    ICommand getCalledCommand();
    
    /**
     * Get an argument value by name
     *
     * @param argumentName The argument name
     * @param <T> The type of the argument value
     * @return The argument value, or null if not provided
     */
    @Nullable
    <T> T getArgument(@Nonnull String argumentName);
    
    /**
     * Get an argument value with default
     *
     * @param argumentName The argument name
     * @param defaultValue The default value
     * @param <T> The type of the argument value
     * @return The argument value, or default if not provided
     */
    @Nonnull
    default <T> T getArgument(@Nonnull String argumentName, @Nonnull T defaultValue) {
        T value = getArgument(argumentName);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Check if an argument was provided
     *
     * @param argumentName The argument name
     * @return true if provided, false otherwise
     */
    boolean hasArgument(@Nonnull String argumentName);
    
    /**
     * Send a message to the sender
     *
     * @param message The message to send
     */
    default void sendMessage(@Nonnull String message) {
        getSender().sendMessage(message);
    }
    
    /**
     * Check if the sender is a player
     *
     * @return true if player, false otherwise
     */
    default boolean isPlayer() {
        return getSender().isPlayer();
    }
    
    /**
     * Get all argument values
     *
     * @return Map of argument names to values
     */
    @Nonnull
    Map<String, Object> getAllArguments();
}
