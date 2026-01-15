package com.xinian.solarlib.command;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 命令构建器
 * 提供链式调用快速创建命令
 */
public class CommandBuilder {
    private final String name;
    private String description = "No description provided";
    private String usage;
    private String permission;
    private int minArgs = 0;
    private int maxArgs = Integer.MAX_VALUE;
    private BiFunction<CommandSender, String[], Boolean> executor;
    private BiFunction<CommandSender, String[], List<String>> tabCompleter;
    private final java.util.List<String> aliases = new java.util.ArrayList<>();

    private CommandBuilder(@Nonnull String name) {
        this.name = name;
        this.usage = "/" + name;
    }

    /**
     * 创建命令构建器
     */
    @Nonnull
    public static CommandBuilder create(@Nonnull String name) {
        return new CommandBuilder(name);
    }

    /**
     * 设置命令描述
     */
    @Nonnull
    public CommandBuilder description(@Nonnull String description) {
        this.description = description;
        return this;
    }

    /**
     * 设置命令用法
     */
    @Nonnull
    public CommandBuilder usage(@Nonnull String usage) {
        this.usage = usage;
        return this;
    }

    /**
     * 设置所需权限
     */
    @Nonnull
    public CommandBuilder permission(@Nonnull String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * 设置参数范围
     */
    @Nonnull
    public CommandBuilder args(int min, int max) {
        this.minArgs = min;
        this.maxArgs = max;
        return this;
    }

    /**
     * 设置最小参数数量
     */
    @Nonnull
    public CommandBuilder minArgs(int minArgs) {
        this.minArgs = minArgs;
        return this;
    }

    /**
     * 设置最大参数数量
     */
    @Nonnull
    public CommandBuilder maxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
        return this;
    }

    /**
     * 添加命令别名
     */
    @Nonnull
    public CommandBuilder alias(@Nonnull String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    /**
     * 设置命令执行器
     */
    @Nonnull
    public CommandBuilder executes(@Nonnull BiFunction<CommandSender, String[], Boolean> executor) {
        this.executor = executor;
        return this;
    }

    /**
     * 设置Tab补全
     */
    @Nonnull
    public CommandBuilder tabCompletes(@Nonnull BiFunction<CommandSender, String[], List<String>> tabCompleter) {
        this.tabCompleter = tabCompleter;
        return this;
    }

    /**
     * 构建命令并自动注册
     */
    @Nonnull
    public Command buildAndRegister() {
        Command command = build();
        CommandRegistry.getInstance().register(command);
        return command;
    }

    /**
     * 构建命令
     */
    @Nonnull
    public Command build() {
        if (executor == null) {
            throw new IllegalStateException("Command executor must be set");
        }

        return new Command(name, description, usage) {
            @Override
            public boolean execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
                return executor.apply(sender, args);
            }

            @Override
            @Nonnull
            public List<String> tabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
                if (tabCompleter != null) {
                    return tabCompleter.apply(sender, args);
                }
                return super.tabComplete(sender, args);
            }

            {
                // 初始化块，设置命令属性
                if (permission != null) {
                    setPermission(permission);
                }
                setMinArgs(minArgs);
                setMaxArgs(maxArgs);
                for (String alias : aliases) {
                    addAlias(alias);
                }
            }
        };
    }
}
