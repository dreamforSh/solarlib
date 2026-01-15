package com.xinian.solarlib.command;

import javax.annotation.Nonnull;

/**
 * 命令发送者接口
 * 表示可以执行命令的实体
 */
public interface CommandSender {
    
    /**
     * 向发送者发送消息
     */
    void sendMessage(@Nonnull String message);

    /**
     * 检查发送者是否有指定权限
     */
    boolean hasPermission(@Nonnull String permission);

    /**
     * 获取发送者名称
     */
    @Nonnull
    String getName();

    /**
     * 检查是否为控制台
     */
    boolean isConsole();

    /**
     * 检查是否为玩家
     */
    boolean isPlayer();
}
