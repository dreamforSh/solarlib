package com.xinian.solarlib.feature;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * 死亡保留物品栏指令 - /keepinventory
 * 用法：
 *   /keepinventory on - 为当前玩家启用
 *   /keepinventory off - 为当前玩家禁用
 *   /keepinventory status - 查看当前状态
 *   /keepinventory global on - 全局启用（需要管理员权限）
 *   /keepinventory global off - 全局禁用（需要管理员权限）
 */
public class KeepInventoryCommand extends AbstractCommand {
    private static final String PERMISSION_USE = "solarlib.keepinventory.use";
    private static final String PERMISSION_GLOBAL = "solarlib.keepinventory.global";
    
    private final KeepInventoryManager manager;
    
    public KeepInventoryCommand() {
        super("keepinventory", "管理死亡保留物品栏功能");
        this.manager = KeepInventoryManager.getInstance();
        this.addAliases("keepinv", "ki");
        this.requirePermission(PERMISSION_USE);
        
        // 添加子命令
        this.addSubCommand(new OnCommand());
        this.addSubCommand(new OffCommand());
        this.addSubCommand(new StatusCommand());
        this.addSubCommand(new GlobalCommand());
    }
    
    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        // 默认行为：显示使用帮助
        return CompletableFuture.runAsync(() -> {
            if (!context.isPlayer()) {
                context.sendMessage(Message.raw("§c此指令只能由玩家执行"));
                return;
            }
            
            Player player = context.senderAs(Player.class);
            sendUsage(player);
        });
    }
    
    @SuppressWarnings("removal")
    private void handleEnable(@Nonnull Player player) {
        if (player.getUuid() != null) {
            manager.enableForPlayer(player.getUuid());
            player.sendMessage(Message.raw("§a死亡保留物品栏已启用！死亡时将保留你的物品栏。"));
        }
    }
    
    @SuppressWarnings("removal")
    private void handleDisable(@Nonnull Player player) {
        if (player.getUuid() != null) {
            manager.disableForPlayer(player.getUuid());
            player.sendMessage(Message.raw("§c死亡保留物品栏已禁用！死亡时将按正常规则掉落物品。"));
        }
    }
    
    @SuppressWarnings("removal")
    private void handleStatus(@Nonnull Player player) {
        if (player.getUuid() != null) {
            boolean enabled = manager.isEnabledForPlayer(player.getUuid());
            boolean globalEnabled = manager.isGlobalEnabled();
            
            player.sendMessage(Message.raw("§6=== 死亡保留物品栏状态 ==="));
            player.sendMessage(Message.raw("§7个人状态: " + (enabled ? "§a已启用" : "§c已禁用")));
            player.sendMessage(Message.raw("§7全局状态: " + (globalEnabled ? "§a已启用" : "§c已禁用")));
            player.sendMessage(Message.raw("§7实际生效: " + (manager.isEnabledForPlayer(player.getUuid()) ? "§a保留物品" : "§c正常掉落")));
            player.sendMessage(Message.raw("§7启用玩家数: §e" + manager.getEnabledPlayersCount()));
        }
    }
    

    
    private void sendUsage(@Nonnull Player player) {
        player.sendMessage(Message.raw("§6=== 死亡保留物品栏指令帮助 ==="));
        player.sendMessage(Message.raw("§e/keepinventory on §7- 启用死亡保留物品栏"));
        player.sendMessage(Message.raw("§e/keepinventory off §7- 禁用死亡保留物品栏"));
        player.sendMessage(Message.raw("§e/keepinventory status §7- 查看当前状态"));
        player.sendMessage(Message.raw("§e/keepinventory global <on|off> §7- 全局开关（需要权限）"));
    }
    
    // ===== 子命令 =====
    
    /**
     * /keepinventory on - 启用子命令
     */
    private class OnCommand extends AbstractCommand {
        public OnCommand() {
            super("on", "启用死亡保留物品栏");
        }
        
        @Nullable
        @Override
        protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                if (!context.isPlayer()) {
                    context.sendMessage(Message.raw("§c此指令只能由玩家执行"));
                    return;
                }
                Player player = context.senderAs(Player.class);
                handleEnable(player);
            });
        }
    }
    
    /**
     * /keepinventory off - 禁用子命令
     */
    private class OffCommand extends AbstractCommand {
        public OffCommand() {
            super("off", "禁用死亡保留物品栏");
        }
        
        @Nullable
        @Override
        protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                if (!context.isPlayer()) {
                    context.sendMessage(Message.raw("§c此指令只能由玩家执行"));
                    return;
                }
                Player player = context.senderAs(Player.class);
                handleDisable(player);
            });
        }
    }
    
    /**
     * /keepinventory status - 状态查询子命令
     */
    private class StatusCommand extends AbstractCommand {
        public StatusCommand() {
            super("status", "查看当前保留物品栏状态");
        }
        
        @Nullable
        @Override
        protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                if (!context.isPlayer()) {
                    context.sendMessage(Message.raw("§c此指令只能由玩家执行"));
                    return;
                }
                Player player = context.senderAs(Player.class);
                handleStatus(player);
            });
        }
    }
    
    /**
     * /keepinventory global <on|off> - 全局设置子命令
     */
    private class GlobalCommand extends AbstractCommand {
        public GlobalCommand() {
            super("global", "管理全局死亡保留物品栏设置");
            this.requirePermission(PERMISSION_GLOBAL);
            
            // 添加 on/off 子命令
            this.addSubCommand(new GlobalOnCommand());
            this.addSubCommand(new GlobalOffCommand());
        }
        
        @Nullable
        @Override
        protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                if (!context.isPlayer()) {
                    context.sendMessage(Message.raw("§c此指令只能由玩家执行"));
                    return;
                }
                Player player = context.senderAs(Player.class);
                player.sendMessage(Message.raw("§c用法: /keepinventory global <on|off>"));
            });
        }
        
        /**
         * /keepinventory global on
         */
        private class GlobalOnCommand extends AbstractCommand {
            public GlobalOnCommand() {
                super("on", "全局启用死亡保留物品栏");
            }
            
            @Nullable
            @Override
            protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
                return CompletableFuture.runAsync(() -> {
                    if (!context.isPlayer()) {
                        context.sendMessage(Message.raw("§c此指令只能由玩家执行"));
                        return;
                    }
                    Player player = context.senderAs(Player.class);
                    manager.setGlobalEnabled(true);
                    player.sendMessage(Message.raw("§a全局死亡保留物品栏已启用！所有玩家死亡时都将保留物品。"));
                });
            }
        }
        
        /**
         * /keepinventory global off
         */
        private class GlobalOffCommand extends AbstractCommand {
            public GlobalOffCommand() {
                super("off", "全局禁用死亡保留物品栏");
            }
            
            @Nullable
            @Override
            protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
                return CompletableFuture.runAsync(() -> {
                    if (!context.isPlayer()) {
                        context.sendMessage(Message.raw("§c此指令只能由玩家执行"));
                        return;
                    }
                    Player player = context.senderAs(Player.class);
                    manager.setGlobalEnabled(false);
                    player.sendMessage(Message.raw("§c全局死亡保留物品栏已禁用！玩家将使用各自的个人设置。"));
                });
            }
        }
    }
}
