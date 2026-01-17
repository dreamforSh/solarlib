package com.xinian.solarlib.api.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Command interface
 * Extracted from Hytale's AbstractCommand
 */
public interface ICommand {
    
    /**
     * Execute the command
     *
     * @param context The command context
     * @return CompletableFuture that completes when command execution is done
     */
    @Nullable
    CompletableFuture<Void> execute(@Nonnull ICommandContext context);
    
    /**
     * Get the command name
     *
     * @return The command name, or null if this is a variant command
     */
    @Nullable
    String getName();
    
    /**
     * Get the command description
     *
     * @return The command description
     */
    @Nullable
    String getDescription();
    
    /**
     * Get command aliases
     *
     * @return Set of aliases
     */
    @Nonnull
    Set<String> getAliases();
    
    /**
     * Add aliases to this command
     *
     * @param aliases The aliases to add
     */
    void addAliases(@Nonnull String... aliases);
    
    /**
     * Get the permission required to execute this command
     *
     * @return The permission, or null if no permission required
     */
    @Nullable
    String getPermission();
    
    /**
     * Set the required permission
     *
     * @param permission The permission to require
     */
    void requirePermission(@Nonnull String permission);
    
    /**
     * Check if sender has permission to execute this command
     *
     * @param sender The command sender
     * @return true if has permission, false otherwise
     */
    boolean hasPermission(@Nonnull ICommandSender sender);
    
    /**
     * Get the command owner
     *
     * @return The command owner
     */
    @Nullable
    ICommandOwner getOwner();
    
    /**
     * Set the command owner
     *
     * @param owner The owner to set
     */
    void setOwner(@Nonnull ICommandOwner owner);
    
    /**
     * Get sub-commands
     *
     * @return Map of sub-command names to commands
     */
    @Nonnull
    Map<String, ICommand> getSubCommands();
    
    /**
     * Add a sub-command
     *
     * @param command The sub-command to add
     */
    void addSubCommand(@Nonnull ICommand command);
    
    /**
     * Get the fully qualified name (including parent commands)
     *
     * @return The fully qualified name
     */
    @Nullable
    String getFullyQualifiedName();
    
    /**
     * Get usage string for this command
     *
     * @param sender The command sender (for permission checks)
     * @return The usage string
     */
    @Nonnull
    String getUsageString(@Nonnull ICommandSender sender);
    
    /**
     * Check if this command has been registered
     *
     * @return true if registered, false otherwise
     */
    boolean hasBeenRegistered();
    
    /**
     * Complete the registration process
     * Called after all configuration is done
     */
    void completeRegistration();
    
    /**
     * Check if this is a variant command
     *
     * @return true if variant, false otherwise
     */
    boolean isVariant();
    
    /**
     * Get tab completion suggestions
     *
     * @param sender The command sender
     * @param args The current arguments
     * @return List of suggestions
     */
    @Nonnull
    default List<String> tabComplete(@Nonnull ICommandSender sender, @Nonnull String[] args) {
        return List.of();
    }
}
