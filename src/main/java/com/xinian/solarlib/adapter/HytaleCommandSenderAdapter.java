package com.xinian.solarlib.adapter;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.xinian.solarlib.api.command.ICommandSender;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Adapter for Hytale CommandSender to SolarLib ICommandSender
 * Wraps Hytale's CommandSender and provides ICommandSender interface
 */
public class HytaleCommandSenderAdapter implements ICommandSender {
    private final CommandSender hytaleCommandSender;
    
    public HytaleCommandSenderAdapter(@Nonnull CommandSender hytaleCommandSender) {
        this.hytaleCommandSender = hytaleCommandSender;
    }
    
    @Override
    public void sendMessage(@Nonnull String message) {
        // Hytale uses Message objects, we need to create a simple text message
        hytaleCommandSender.sendMessage(
            com.hypixel.hytale.server.core.Message.raw(message)
        );
    }
    
    @Nonnull
    @Override
    public String getDisplayName() {
        return hytaleCommandSender.getDisplayName();
    }
    
    @Nonnull
    @Override
    public UUID getUuid() {
        return hytaleCommandSender.getUuid();
    }
    
    @Override
    public boolean hasPermission(@Nonnull String permission) {
        return hytaleCommandSender.hasPermission(permission);
    }
    
    @Override
    public boolean hasPermission(@Nonnull String permission, boolean defaultValue) {
        return hytaleCommandSender.hasPermission(permission, defaultValue);
    }
    
    @Override
    public boolean isPlayer() {
        // Check if the sender is an instance of player-related classes
        // This might need adjustment based on Hytale's actual class hierarchy
        return hytaleCommandSender instanceof com.hypixel.hytale.component.Ref;
    }
    
    /**
     * Get the wrapped Hytale CommandSender
     *
     * @return The original Hytale CommandSender
     */
    @Nonnull
    public CommandSender getHytaleCommandSender() {
        return hytaleCommandSender;
    }
    
    @Override
    public String toString() {
        return "HytaleCommandSenderAdapter{sender=" + getDisplayName() + ", uuid=" + getUuid() + "}";
    }
}
