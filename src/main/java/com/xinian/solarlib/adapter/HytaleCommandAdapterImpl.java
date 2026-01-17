package com.xinian.solarlib.adapter;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.xinian.solarlib.api.command.ICommand;
import com.xinian.solarlib.api.command.ICommandContext;
import com.xinian.solarlib.api.command.ICommandSender;
import com.xinian.solarlib.api.adapter.IHytaleCommandAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of IHytaleCommandAdapter
 * Provides bidirectional adaptation between Hytale commands and SolarLib commands
 */
public class HytaleCommandAdapterImpl implements IHytaleCommandAdapter {
    
    @Nonnull
    @Override
    public Object adaptToHytale(@Nonnull ICommand command) {
        return new SolarLibToHytaleCommand(command);
    }
    
    @Nonnull
    @Override
    public ICommand adaptFromHytale(@Nonnull Object hytaleCommand) {
        if (!(hytaleCommand instanceof AbstractCommand)) {
            throw new IllegalArgumentException("Object is not a Hytale AbstractCommand");
        }
        return new HytaleToSolarLibCommand((AbstractCommand) hytaleCommand);
    }
    
    @Nonnull
    @Override
    public ICommandSender adaptCommandSender(@Nonnull Object hytaleCommandSender) {
        if (!(hytaleCommandSender instanceof com.hypixel.hytale.server.core.command.system.CommandSender)) {
            throw new IllegalArgumentException("Object is not a Hytale CommandSender");
        }
        return new HytaleCommandSenderAdapter(
            (com.hypixel.hytale.server.core.command.system.CommandSender) hytaleCommandSender
        );
    }
    
    /**
     * Adapter that wraps a SolarLib ICommand as a Hytale AbstractCommand
     */
    private static class SolarLibToHytaleCommand extends AbstractCommand {
        private final ICommand solarLibCommand;
        
        public SolarLibToHytaleCommand(@Nonnull ICommand solarLibCommand) {
            super(solarLibCommand.getName(), solarLibCommand.getDescription());
            this.solarLibCommand = solarLibCommand;
            
            // Copy permission
            if (solarLibCommand.getPermission() != null) {
                requirePermission(solarLibCommand.getPermission());
            }
            
            // Copy aliases
            Set<String> aliases = solarLibCommand.getAliases();
            if (!aliases.isEmpty()) {
                addAliases(aliases.toArray(new String[0]));
            }
        }
        
        @Nullable
        @Override
        protected CompletableFuture<Void> execute(@Nonnull CommandContext commandContext) {
            // Create adapter for the context
            HytaleCommandContextAdapter contextAdapter = 
                new HytaleCommandContextAdapter(commandContext, solarLibCommand);
            
            // Execute the SolarLib command
            return solarLibCommand.execute(contextAdapter);
        }
        
        /**
         * Get the wrapped SolarLib command
         */
        @Nonnull
        public ICommand getSolarLibCommand() {
            return solarLibCommand;
        }
    }
    
    /**
     * Adapter that wraps a Hytale AbstractCommand as a SolarLib ICommand
     */
    private static class HytaleToSolarLibCommand implements ICommand {
        private final AbstractCommand hytaleCommand;
        
        public HytaleToSolarLibCommand(@Nonnull AbstractCommand hytaleCommand) {
            this.hytaleCommand = hytaleCommand;
        }
        
        @Nullable
        @Override
        public CompletableFuture<Void> execute(@Nonnull ICommandContext context) {
            // This requires converting ICommandContext back to Hytale CommandContext
            // For now, throw an exception as this direction is more complex
            throw new UnsupportedOperationException(
                "Direct execution of Hytale commands through SolarLib context is not yet supported. " +
                "Use adaptToHytale() to convert SolarLib commands to Hytale commands instead."
            );
        }
        
        @Nullable
        @Override
        public String getName() {
            return hytaleCommand.getName();
        }
        
        @Nullable
        @Override
        public String getDescription() {
            return hytaleCommand.getDescription();
        }
        
        @Nonnull
        @Override
        public Set<String> getAliases() {
            return hytaleCommand.getAliases();
        }
        
        @Override
        public void addAliases(@Nonnull String... aliases) {
            hytaleCommand.addAliases(aliases);
        }
        
        @Nullable
        @Override
        public String getPermission() {
            return hytaleCommand.getPermission();
        }
        
        @Override
        public void requirePermission(@Nonnull String permission) {
            hytaleCommand.requirePermission(permission);
        }
        
        @Override
        public boolean hasPermission(@Nonnull ICommandSender sender) {
            if (sender instanceof HytaleCommandSenderAdapter) {
                return hytaleCommand.hasPermission(
                    ((HytaleCommandSenderAdapter) sender).getHytaleCommandSender()
                );
            }
            return false;
        }
        
        @Nullable
        @Override
        public com.xinian.solarlib.api.command.ICommandOwner getOwner() {
            var owner = hytaleCommand.getOwner();
            if (owner == null) {
                return null;
            }
            return () -> owner.getName();
        }
        
        @Override
        public void setOwner(@Nonnull com.xinian.solarlib.api.command.ICommandOwner owner) {
            hytaleCommand.setOwner(() -> owner.getName());
        }
        
        @Nonnull
        @Override
        public Map<String, ICommand> getSubCommands() {
            Map<String, ICommand> result = new HashMap<>();
            hytaleCommand.getSubCommands().forEach((name, cmd) -> {
                result.put(name, new HytaleToSolarLibCommand(cmd));
            });
            return result;
        }
        
        @Override
        public void addSubCommand(@Nonnull ICommand command) {
            if (command instanceof HytaleToSolarLibCommand) {
                hytaleCommand.addSubCommand(((HytaleToSolarLibCommand) command).hytaleCommand);
            } else {
                // Convert SolarLib command to Hytale command first
                hytaleCommand.addSubCommand(new SolarLibToHytaleCommand(command));
            }
        }
        
        @Nullable
        @Override
        public String getFullyQualifiedName() {
            return hytaleCommand.getFullyQualifiedName();
        }
        
        @Nonnull
        @Override
        public String getUsageString(@Nonnull ICommandSender sender) {
            if (sender instanceof HytaleCommandSenderAdapter) {
                return hytaleCommand.getUsageString(
                    ((HytaleCommandSenderAdapter) sender).getHytaleCommandSender()
                ).toString();
            }
            return "/" + (getName() != null ? getName() : "unknown");
        }
        
        @Override
        public boolean hasBeenRegistered() {
            return hytaleCommand.hasBeenRegistered();
        }
        
        @Override
        public void completeRegistration() {
            // Hytale's completeRegistration is called automatically
        }
        
        @Override
        public boolean isVariant() {
            return hytaleCommand.isVariant();
        }
        
        /**
         * Get the wrapped Hytale command
         */
        @Nonnull
        public AbstractCommand getHytaleCommand() {
            return hytaleCommand;
        }
    }
}
