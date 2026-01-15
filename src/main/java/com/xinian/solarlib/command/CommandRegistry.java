package com.xinian.solarlib.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 命令注册管理器
 * 提供命令注册、执行和管理功能
 */
public class CommandRegistry {
    private static final Logger LOGGER = Logger.getLogger(CommandRegistry.class.getName());
    private static CommandRegistry instance;

    private final Map<String, Command> commands;
    private final Map<String, String> aliases;

    private CommandRegistry() {
        this.commands = new ConcurrentHashMap<>();
        this.aliases = new ConcurrentHashMap<>();
    }

    /**
     * 获取单例实例
     */
    @Nonnull
    public static CommandRegistry getInstance() {
        if (instance == null) {
            synchronized (CommandRegistry.class) {
                if (instance == null) {
                    instance = new CommandRegistry();
                }
            }
        }
        return instance;
    }

    /**
     * 注册命令（不自动注册到 Hytale）
     */
    public void register(@Nonnull Command command) {
        register(command, false);
    }

    /**
     * 注册命令
     * @param command 命令实例
     * @param registerToHytale 是否自动注册到 Hytale 服务端
     */
    public void register(@Nonnull Command command, boolean registerToHytale) {
        String name = command.getName().toLowerCase();
        
        if (commands.containsKey(name)) {
            LOGGER.warning("Command already registered: " + name + ", overwriting...");
        }
        
        commands.put(name, command);
        
        // 注册别名
        for (String alias : command.getAliases()) {
            String aliasLower = alias.toLowerCase();
            if (aliases.containsKey(aliasLower)) {
                LOGGER.warning("Alias already registered: " + aliasLower + ", overwriting...");
            }
            aliases.put(aliasLower, name);
        }
        
        // 如果需要，自动注册到 Hytale 服务端
        if (registerToHytale) {
            try {
                HytaleCommandAdapter adapter = new HytaleCommandAdapter(command);
                // Hytale 命令注册需要在插件中完成，这里只是创建适配器
                // 实际注册在 SolarLib 主类中处理
                LOGGER.info("Created Hytale adapter for command: " + name);
            } catch (Exception e) {
                LOGGER.warning("Failed to create Hytale adapter for command: " + name);
            }
        }
        
        LOGGER.info("Registered command: " + name + 
                   (command.getAliases().isEmpty() ? "" : " (aliases: " + String.join(", ", command.getAliases()) + ")"));
    }

    /**
     * 取消注册命令
     */
    public void unregister(@Nonnull String name) {
        String nameLower = name.toLowerCase();
        Command command = commands.remove(nameLower);
        
        if (command != null) {
            // 移除所有别名
            for (String alias : command.getAliases()) {
                aliases.remove(alias.toLowerCase());
            }
            LOGGER.info("Unregistered command: " + nameLower);
        }
    }

    /**
     * 获取命令
     */
    @Nullable
    public Command getCommand(@Nonnull String name) {
        String nameLower = name.toLowerCase();
        
        // 先尝试直接获取
        Command command = commands.get(nameLower);
        if (command != null) {
            return command;
        }
        
        // 尝试通过别名获取
        String actualName = aliases.get(nameLower);
        if (actualName != null) {
            return commands.get(actualName);
        }
        
        return null;
    }

    /**
     * 执行命令
     */
    public boolean execute(@Nonnull CommandSender sender, @Nonnull String commandLine) {
        if (commandLine.trim().isEmpty()) {
            return false;
        }

        // 解析命令行
        String[] parts = commandLine.trim().split("\\s+");
        String cmdName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        return execute(sender, cmdName, args);
    }

    /**
     * 执行命令
     */
    public boolean execute(@Nonnull CommandSender sender, @Nonnull String name, @Nonnull String[] args) {
        Command command = getCommand(name);
        
        if (command == null) {
            sender.sendMessage("§cUnknown command: " + name);
            return false;
        }

        // 检查权限
        if (!command.hasPermission(sender)) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return false;
        }

        // 检查参数数量
        if (!command.isValidArgCount(args.length)) {
            sender.sendMessage("§cUsage: " + command.getUsage());
            return false;
        }

        // 执行命令
        try {
            return command.execute(sender, args);
        } catch (Exception e) {
            sender.sendMessage("§cError executing command: " + e.getMessage());
            LOGGER.severe("Error executing command " + name + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取命令补全建议
     */
    @Nonnull
    public List<String> tabComplete(@Nonnull CommandSender sender, @Nonnull String commandLine) {
        String[] parts = commandLine.split("\\s+", -1);
        
        // 如果只有命令名，返回匹配的命令列表
        if (parts.length == 1) {
            String prefix = parts[0].toLowerCase();
            List<String> suggestions = new ArrayList<>();
            
            for (String cmdName : commands.keySet()) {
                if (cmdName.startsWith(prefix)) {
                    suggestions.add(cmdName);
                }
            }
            
            for (Map.Entry<String, String> entry : aliases.entrySet()) {
                if (entry.getKey().startsWith(prefix)) {
                    suggestions.add(entry.getKey());
                }
            }
            
            Collections.sort(suggestions);
            return suggestions;
        }
        
        // 获取命令并返回其补全建议
        String cmdName = parts[0];
        Command command = getCommand(cmdName);
        
        if (command != null && command.hasPermission(sender)) {
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);
            return command.tabComplete(sender, args);
        }
        
        return new ArrayList<>();
    }

    /**
     * 获取所有已注册的命令
     */
    @Nonnull
    public Collection<Command> getCommands() {
        return new ArrayList<>(commands.values());
    }

    /**
     * 获取命令数量
     */
    public int getCommandCount() {
        return commands.size();
    }

    /**
     * 清空所有命令
     */
    public void clear() {
        commands.clear();
        aliases.clear();
        LOGGER.info("Cleared all commands");
    }

    /**
     * 检查命令是否已注册
     */
    public boolean isRegistered(@Nonnull String name) {
        return getCommand(name) != null;
    }
}
