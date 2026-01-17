package com.xinian.solarlib.api.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Command sender interface
 * Extracted from Hytale's CommandSender
 */
public interface ICommandSender {
    
    /**
     * Send a message to this sender
     *
     * @param message The message to send
     */
    void sendMessage(@Nonnull String message);
    
    /**
     * Get the display name of this sender
     *
     * @return The display name
     */
    @Nonnull
    String getDisplayName();
    
    /**
     * Get the UUID of this sender
     *
     * @return The UUID
     */
    @Nonnull
    UUID getUuid();
    
    /**
     * Check if this sender has a specific permission
     *
     * @param permission The permission to check
     * @return true if has permission, false otherwise
     */
    boolean hasPermission(@Nonnull String permission);
    
    /**
     * Check if this sender has a specific permission with default value
     *
     * @param permission The permission to check
     * @param defaultValue The default value if permission is not set
     * @return true if has permission, false otherwise
     */
    boolean hasPermission(@Nonnull String permission, boolean defaultValue);
    
    /**
     * Check if this sender is a player
     *
     * @return true if player, false otherwise
     */
    boolean isPlayer();
    
    /**
     * Check if this sender is the console
     *
     * @return true if console, false otherwise
     */
    default boolean isConsole() {
        return !isPlayer();
    }
}
