package com.xinian.solarlib.api.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Base implementation of ICommand
 * Provides common functionality for commands
 */
public abstract class BaseCommand implements ICommand {
    @Nullable
    private final String name;
    @Nullable
    private final String description;
    @Nonnull
    private final Set<String> aliases;
    @Nonnull
    private final Map<String, ICommand> subCommands;
    @Nullable
    private ICommandOwner owner;
    @Nullable
    private String permission;
    private boolean hasBeenRegistered;
    
    protected BaseCommand(@Nullable String name, @Nullable String description) {
        this.name = name;
        this.description = description;
        this.aliases = new HashSet<>();
        this.subCommands = new HashMap<>();
        this.hasBeenRegistered = false;
    }
    
    protected BaseCommand(@Nullable String name) {
        this(name, null);
    }
    
    @Override
    @Nullable
    public String getName() {
        return name;
    }
    
    @Override
    @Nullable
    public String getDescription() {
        return description;
    }
    
    @Override
    @Nonnull
    public Set<String> getAliases() {
        return new HashSet<>(aliases);
    }
    
    @Override
    public void addAliases(@Nonnull String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
    }
    
    @Override
    @Nullable
    public String getPermission() {
        return permission;
    }
    
    @Override
    public void requirePermission(@Nonnull String permission) {
        this.permission = permission;
    }
    
    @Override
    public boolean hasPermission(@Nonnull ICommandSender sender) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        return sender.hasPermission(permission);
    }
    
    @Override
    @Nullable
    public ICommandOwner getOwner() {
        return owner;
    }
    
    @Override
    public void setOwner(@Nonnull ICommandOwner owner) {
        this.owner = owner;
    }
    
    @Override
    @Nonnull
    public Map<String, ICommand> getSubCommands() {
        return new HashMap<>(subCommands);
    }
    
    @Override
    public void addSubCommand(@Nonnull ICommand command) {
        if (command.getName() != null) {
            subCommands.put(command.getName().toLowerCase(), command);
        }
    }
    
    @Override
    @Nullable
    public String getFullyQualifiedName() {
        if (name == null) {
            return null;
        }
        return name;
    }
    
    @Override
    @Nonnull
    public String getUsageString(@Nonnull ICommandSender sender) {
        StringBuilder usage = new StringBuilder("/");
        if (name != null) {
            usage.append(name);
        }
        if (description != null) {
            usage.append(" - ").append(description);
        }
        return usage.toString();
    }
    
    @Override
    public boolean hasBeenRegistered() {
        return hasBeenRegistered;
    }
    
    @Override
    public void completeRegistration() {
        this.hasBeenRegistered = true;
    }
    
    @Override
    public boolean isVariant() {
        return name == null;
    }
    
    @Override
    public String toString() {
        return "BaseCommand{name='" + name + "', permission='" + permission + "'}";
    }
}
