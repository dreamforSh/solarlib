package com.xinian.solarlib.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 命令基础类
 * 提供命令的基本结构和执行接口
 */
public abstract class Command {
    private final String name;
    private final String description;
    private final String usage;
    private final List<String> aliases;
    private String permission;
    private int minArgs;
    private int maxArgs;

    public Command(@Nonnull String name) {
        this(name, "No description provided", "/" + name);
    }

    public Command(@Nonnull String name, @Nonnull String description, @Nonnull String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = new ArrayList<>();
        this.minArgs = 0;
        this.maxArgs = Integer.MAX_VALUE;
    }

    /**
     * 执行命令
     * 
     * @param sender 命令发送者
     * @param args 命令参数
     * @return 是否成功执行
     */
    public abstract boolean execute(@Nonnull CommandSender sender, @Nonnull String[] args);

    /**
     * 获取命令名称
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * 获取命令描述
     */
    @Nonnull
    public String getDescription() {
        return description;
    }

    /**
     * 获取命令用法
     */
    @Nonnull
    public String getUsage() {
        return usage;
    }

    /**
     * 获取命令别名列表
     */
    @Nonnull
    public List<String> getAliases() {
        return new ArrayList<>(aliases);
    }

    /**
     * 添加命令别名
     */
    public void addAlias(@Nonnull String alias) {
        if (!aliases.contains(alias)) {
            aliases.add(alias);
        }
    }

    /**
     * 获取所需权限
     */
    @Nullable
    public String getPermission() {
        return permission;
    }

    /**
     * 设置所需权限
     */
    public void setPermission(@Nullable String permission) {
        this.permission = permission;
    }

    /**
     * 获取最小参数数量
     */
    public int getMinArgs() {
        return minArgs;
    }

    /**
     * 设置最小参数数量
     */
    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    /**
     * 获取最大参数数量
     */
    public int getMaxArgs() {
        return maxArgs;
    }

    /**
     * 设置最大参数数量
     */
    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    /**
     * 检查参数数量是否有效
     */
    public boolean isValidArgCount(int argCount) {
        return argCount >= minArgs && argCount <= maxArgs;
    }

    /**
     * 检查发送者是否有权限
     */
    public boolean hasPermission(@Nonnull CommandSender sender) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        return sender.hasPermission(permission);
    }

    /**
     * 获取命令补全建议
     * 
     * @param sender 命令发送者
     * @param args 当前参数
     * @return 补全建议列表
     */
    @Nonnull
    public List<String> tabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
        return new ArrayList<>();
    }
}
