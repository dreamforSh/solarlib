
package com.xinian.solarlib.feature;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.simple.BooleanCodec;

import javax.annotation.Nonnull;

/**
 * KeepInventory 功能配置
 * 
 * 支持通过配置文件自定义默认行为
 */
public class KeepInventoryConfig {
    
    /**
     * 全局默认是否启用死亡保留物品栏
     * 默认值：false（需要玩家手动启用）
     */
    private boolean enabledByDefault;
    
    /**
     * 是否允许玩家使用 /keepinventory 指令修改个人设置
     * 默认值：true
     */
    private boolean allowPlayerToggle;
    
    /**
     * 是否在服务器重启后保留玩家的设置
     * 默认值：true
     */
    private boolean persistPlayerSettings;
    
    /**
     * 全局强制模式：所有玩家都保留物品栏，忽略个人设置
     * 默认值：false
     */
    private boolean forceEnabled;
    
    /**
     * 调试模式：输出详细日志
     * 默认值：false
     */
    private boolean debugMode;
    
    /**
     * 配置文件 Codec
     */
    @Nonnull
    public static final BuilderCodec<KeepInventoryConfig> CODEC = BuilderCodec.builder(
        KeepInventoryConfig.class,
        KeepInventoryConfig::new
    )
    .append(
        new KeyedCodec<>("enabledByDefault", new BooleanCodec(), false), 
        KeepInventoryConfig::setEnabledByDefault, 
        KeepInventoryConfig::isEnabledByDefault
    ).add()
    .append(
        new KeyedCodec<>("allowPlayerToggle", new BooleanCodec(), true), 
        KeepInventoryConfig::setAllowPlayerToggle, 
        KeepInventoryConfig::isAllowPlayerToggle
    ).add()
    .append(
        new KeyedCodec<>("persistPlayerSettings", new BooleanCodec(), true),
        KeepInventoryConfig::setPersistPlayerSettings, 
        KeepInventoryConfig::isPersistPlayerSettings
    ).add()
    .append(
        new KeyedCodec<>("forceEnabled", new BooleanCodec(), false), 
        KeepInventoryConfig::setForceEnabled, 
        KeepInventoryConfig::isForceEnabled
    ).add()
    .append(
        new KeyedCodec<>("debugMode", new BooleanCodec(), false), 
        KeepInventoryConfig::setDebugMode, 
        KeepInventoryConfig::isDebugMode
    ).add()
    .build();
    
    /**
     * 默认构造函数
     */
    public KeepInventoryConfig() {
        this.enabledByDefault = false;
        this.allowPlayerToggle = true;
        this.persistPlayerSettings = true;
        this.forceEnabled = false;
        this.debugMode = false;
    }
    
    // Getters
    
    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }
    
    public boolean isAllowPlayerToggle() {
        return allowPlayerToggle;
    }
    
    public boolean isPersistPlayerSettings() {
        return persistPlayerSettings;
    }
    
    public boolean isForceEnabled() {
        return forceEnabled;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    // Setters
    
    public void setEnabledByDefault(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }
    
    public void setAllowPlayerToggle(boolean allowPlayerToggle) {
        this.allowPlayerToggle = allowPlayerToggle;
    }
    
    public void setPersistPlayerSettings(boolean persistPlayerSettings) {
        this.persistPlayerSettings = persistPlayerSettings;
    }
    
    public void setForceEnabled(boolean forceEnabled) {
        this.forceEnabled = forceEnabled;
    }
    
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    @Override
    public String toString() {
        return "KeepInventoryConfig{" +
                "enabledByDefault=" + enabledByDefault +
                ", allowPlayerToggle=" + allowPlayerToggle +
                ", persistPlayerSettings=" + persistPlayerSettings +
                ", forceEnabled=" + forceEnabled +
                ", debugMode=" + debugMode +
                '}';
    }
}
