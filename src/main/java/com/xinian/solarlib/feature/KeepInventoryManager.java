package com.xinian.solarlib.feature;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 死亡保留物品栏管理器 - 管理哪些玩家启用了死亡保留物品栏功能
 */
public class KeepInventoryManager {
    private static final Logger LOGGER = Logger.getLogger(KeepInventoryManager.class.getName());
    private static KeepInventoryManager instance;
    
    // 存储启用了保留物品栏功能的玩家UUID
    private final Set<UUID> keepInventoryPlayers;
    // 全局开关
    private boolean globalEnabled;
    // 配置
    private KeepInventoryConfig config;
    
    private KeepInventoryManager() {
        this.keepInventoryPlayers = ConcurrentHashMap.newKeySet();
        this.globalEnabled = false;
    }
    
    @Nonnull
    public static synchronized KeepInventoryManager getInstance() {
        if (instance == null) {
            instance = new KeepInventoryManager();
        }
        return instance;
    }
    
    /**
     * 设置配置
     */
    public void setConfig(@Nonnull KeepInventoryConfig config) {
        this.config = config;
        // 应用配置
        if (config.isForceEnabled()) {
            this.globalEnabled = true;
            LOGGER.info("Force enabled mode activated - all players will keep inventory");
        }
        LOGGER.info("Config applied: " + config);
    }
    
    /**
     * 获取配置
     */
    public KeepInventoryConfig getConfig() {
        return config;
    }
    
    /**
     * 为指定玩家启用死亡保留物品栏
     */
    public void enableForPlayer(@Nonnull UUID playerUuid) {
        if (config != null && !config.isAllowPlayerToggle() && !config.isForceEnabled()) {
            LOGGER.warning("Player toggle is disabled in config");
            return;
        }
        keepInventoryPlayers.add(playerUuid);
        LOGGER.info("Enabled keep inventory for player: " + playerUuid);
    }
    
    /**
     * 为指定玩家禁用死亡保留物品栏
     */
    public void disableForPlayer(@Nonnull UUID playerUuid) {
        if (config != null && config.isForceEnabled()) {
            LOGGER.warning("Cannot disable when force enabled mode is active");
            return;
        }
        if (config != null && !config.isAllowPlayerToggle()) {
            LOGGER.warning("Player toggle is disabled in config");
            return;
        }
        keepInventoryPlayers.remove(playerUuid);
        LOGGER.info("Disabled keep inventory for player: " + playerUuid);
    }
    
    /**
     * 检查指定玩家是否启用了死亡保留物品栏
     */
    public boolean isEnabledForPlayer(@Nonnull UUID playerUuid) {
        // 全局强制模式
        if (config != null && config.isForceEnabled()) {
            return true;
        }
        // 全局开关
        if (globalEnabled) {
            return true;
        }
        // 默认启用 + 未明确禁用
        if (config != null && config.isEnabledByDefault() && !keepInventoryPlayers.contains(playerUuid)) {
            // 如果配置为默认启用，且玩家未设置过，则默认启用
            return true;
        }
        // 玩家个人设置
        return keepInventoryPlayers.contains(playerUuid);
    }
    
    /**
     * 设置全局开关
     */
    public void setGlobalEnabled(boolean enabled) {
        this.globalEnabled = enabled;
        LOGGER.info("Global keep inventory " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * 获取全局开关状态
     */
    public boolean isGlobalEnabled() {
        return globalEnabled;
    }
    
    /**
     * 获取所有启用了保留物品栏的玩家数量
     */
    public int getEnabledPlayersCount() {
        return keepInventoryPlayers.size();
    }
    
    /**
     * 清空所有设置
     */
    public void clear() {
        keepInventoryPlayers.clear();
        globalEnabled = false;
        LOGGER.info("Keep inventory settings cleared");
    }
}
