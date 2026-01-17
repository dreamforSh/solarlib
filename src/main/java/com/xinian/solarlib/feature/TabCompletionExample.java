package com.xinian.solarlib.feature;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.suggestion.SuggestionProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Tab 补全示例命令
 * 展示如何为命令参数添加自定义补全建议
 * 
 * 示例：/tabexample <action> <player>
 * 
 * 注意：这是一个示例类，演示 Tab 补全功能的使用方法
 */
public class TabCompletionExample extends AbstractCommand {
    
    // 定义参数
    private final RequiredArg<String> actionArg;
    private final RequiredArg<UUID> playerArg;
    
    public TabCompletionExample() {
        super("tabexample", "Tab补全示例命令");
        
        // 创建 action 参数，并添加自定义补全建议
        this.actionArg = withRequiredArg("action", "操作类型", ArgTypes.STRING)
            .suggest(createActionSuggestionProvider());
        
        // 创建 player 参数，使用内置的玩家 UUID 补全
        this.playerArg = withRequiredArg("player", "玩家 UUID", ArgTypes.PLAYER_UUID);
    }
    
    /**
     * 创建自定义的 action 参数补全提供者
     */
    @Nonnull
    private SuggestionProvider createActionSuggestionProvider() {
        return (sender, textAlreadyEntered, numParametersTyped, result) -> {
            // 提供可用的操作选项
            result.suggest("enable");
            result.suggest("disable");
            result.suggest("status");
            result.suggest("reset");
        };
    }
    
    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        return CompletableFuture.runAsync(() -> {
            // 获取参数值
            String action = context.get(actionArg);
            UUID playerUuid = context.get(playerArg);
            
            // 执行命令逻辑
            context.sendMessage(Message.raw("§a执行操作: §e" + action + " §a对玩家: §e" + playerUuid));
        });
    }
}
