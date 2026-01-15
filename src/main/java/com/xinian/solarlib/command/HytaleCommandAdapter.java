package com.xinian.solarlib.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import javax.annotation.Nonnull;

/**
 * Hytale 命令适配器
 * 将 SolarLib 命令系统桥接到 Hytale 官方命令系统
 */
public class HytaleCommandAdapter extends CommandBase {
    private final Command solarCommand;

    public HytaleCommandAdapter(@Nonnull Command command) {
        super(command.getName(), command.getDescription());
        this.solarCommand = command;
        setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        var sender = context.sender();
        

        CommandSender wrappedSender = new HytaleCommandSender(sender);
        
        // 解析参数
        String[] args = parseArgs(context);
        
        // 检查权限
        if (!solarCommand.hasPermission(wrappedSender)) {
            sender.sendMessage(Message.raw("§cYou don't have permission to use this command."));
            return;
        }
        
        // 检查参数数量
        if (!solarCommand.isValidArgCount(args.length)) {
            sender.sendMessage(Message.raw("§cUsage: " + solarCommand.getUsage()));
            return;
        }
        
        // 执行命令
        try {
            boolean success = solarCommand.execute(wrappedSender, args);
            if (!success) {
                sender.sendMessage(Message.raw("§cCommand execution failed."));
            }
        } catch (Exception e) {
            sender.sendMessage(Message.raw("§cError: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * 从 CommandContext 解析参数
     */
    private String[] parseArgs(CommandContext context) {
        String input = context.getInputString();
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }
        
        // 移除命令名
        String[] parts = input.trim().split("\\s+");
        if (parts.length <= 1) {
            return new String[0];
        }
        
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        return args;
    }

    /**
     * Hytale CommandSender 实现
     */
    private static class HytaleCommandSender implements CommandSender {
        private final com.hypixel.hytale.server.core.command.system.CommandSender hytaleSender;

        public HytaleCommandSender(com.hypixel.hytale.server.core.command.system.CommandSender sender) {
            this.hytaleSender = sender;
        }

        @Override
        public void sendMessage(@Nonnull String message) {
            hytaleSender.sendMessage(Message.raw(message));
        }

        @Override
        public boolean hasPermission(@Nonnull String permission) {
            // Hytale 的权限检查
            if (hytaleSender instanceof Player player) {
                return player.hasPermission(permission);
            }
            // 控制台默认有所有权限
            return true;
        }

        @Override
        @Nonnull
        public String getName() {

            if (hytaleSender instanceof Player) {
                return hytaleSender.toString();
            }
            return "Console";
        }

        @Override
        public boolean isConsole() {
            return !(hytaleSender instanceof Player);
        }

        @Override
        public boolean isPlayer() {
            return hytaleSender instanceof Player;
        }
        
        /**
         * 获取原始 Hytale sender
         */
        public com.hypixel.hytale.server.core.command.system.CommandSender getHytaleSender() {
            return hytaleSender;
        }
        
        /**
         * 如果是玩家，获取 Player 对象
         */
        public Player getPlayer() {
            if (hytaleSender instanceof Player player) {
                return player;
            }
            return null;
        }
    }
}
