package com.xinian.solarlib;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.xinian.solarlib.adapter.AdapterFactory;
import com.xinian.solarlib.event.EventRegistry;
import com.xinian.solarlib.event.HytaleEventAdapter;
import com.xinian.solarlib.event.HytaleEvents;
import com.xinian.solarlib.network.NetworkManager;
import com.xinian.solarlib.feature.KeepInventoryCommand;
import com.xinian.solarlib.feature.KeepInventoryConfig;
import com.xinian.solarlib.feature.KeepInventoryDeathSystem;
import com.xinian.solarlib.feature.KeepInventoryListener;
import com.xinian.solarlib.feature.KeepInventoryManager;
import javax.annotation.Nonnull;
import java.util.logging.Logger;


/**
 * SolarLib - Hytale 指令与注册快捷实现库
 * 
 * 核心功能：
 * - 命令系统：提供简化的命令注册和执行接口，封装 Hytale 的复杂命令 API
 * - 注册系统：统一的资源注册管理，支持各类游戏对象的注册和生命周期管理
 * - 事件系统：事件监听和分发的便捷封装
 * - 数据包系统：简化的网络数据包创建和管理
 * - 适配器层：桥接 Hytale 原生 API 与 SolarLib 简化 API
 * 
 * @author SolarLib Team
 * @version 0.11
 */
public class SolarLib extends JavaPlugin {
    public static final Logger LOGGER = Logger.getLogger(SolarLib.class.getName());
    private static SolarLib instance;

    private NetworkManager networkManager;
    private EventRegistry eventRegistry;
    private HytaleEventAdapter hytaleEventAdapter;
    private HytaleEvents hytaleEvents;
    private AdapterFactory adapterFactory;
    private KeepInventoryManager keepInventoryManager;
    private KeepInventoryListener keepInventoryListener;
    private KeepInventoryDeathSystem keepInventoryDeathSystem;
    private Config<KeepInventoryConfig> keepInventoryConfig;

    public SolarLib(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        super.setup();
        
        // 初始化各个管理器
        this.networkManager = NetworkManager.getInstance();
        this.eventRegistry = EventRegistry.getInstance();
        
        // 初始化 Hytale 事件系统适配器（使用 Hytale 的 EventRegistry）
        this.hytaleEventAdapter = new HytaleEventAdapter(getEventRegistry());
        this.hytaleEvents = new HytaleEvents(hytaleEventAdapter);
        
        // 初始化适配器工厂
        this.adapterFactory = AdapterFactory.getInstance();
        
        // 先初始化 KeepInventoryManager（在加载配置前）
        this.keepInventoryManager = KeepInventoryManager.getInstance();
        
        // 加载 KeepInventory 配置
        this.keepInventoryConfig = withConfig("keepinventory", KeepInventoryConfig.CODEC);
        
        LOGGER.info("Loading KeepInventory configuration...");
        this.keepInventoryConfig.load().thenAccept(config -> {
            LOGGER.info("KeepInventory config loaded: " + config);
            
            // 应用配置到管理器
            this.keepInventoryManager.setConfig(config);
            this.keepInventoryListener = new KeepInventoryListener(hytaleEvents);
            
            // 注册ECS死亡系统
            this.keepInventoryDeathSystem = new KeepInventoryDeathSystem();
            this.keepInventoryDeathSystem.setConfig(config);
            getEntityStoreRegistry().registerSystem(keepInventoryDeathSystem);
            
            LOGGER.info("- Keep Inventory Config: enabledByDefault=" + config.isEnabledByDefault() + 
                       ", forceEnabled=" + config.isForceEnabled() + 
                       ", debugMode=" + config.isDebugMode());
        }).exceptionally(ex -> {
            LOGGER.severe("Failed to load KeepInventory config: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
        
        // 注册指令 (移出异步加载，确保指令在 setup 阶段注册)
        registerCommands();
    }
    
    @Override
    protected void start() {
        super.start();
        
        LOGGER.info("SolarLib started successfully!");
        LOGGER.info("- Network Manager: Ready");
        LOGGER.info("- Event Registry (SolarLib): Ready");
        LOGGER.info("- Hytale Event Adapter: Ready");
        LOGGER.info("- Adapter Factory: " + (adapterFactory.isAvailable() ? "Ready" : "Not Available (Non-Hytale environment)"));
        LOGGER.info("- Keep Inventory Feature: Enabled");
        LOGGER.info("- Keep Inventory Death System: Registered");
    }
    
    /**
     * 注册所有指令
     */
    private void registerCommands() {
        try {
            // 注册死亡保留物品栏指令
            KeepInventoryCommand keepInventoryCommand = new KeepInventoryCommand();
            getCommandRegistry().registerCommand(keepInventoryCommand);
            LOGGER.info("Registered command: /keepinventory");
        } catch (Exception e) {
            LOGGER.severe("Failed to register commands: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取插件实例
     */
    @Nonnull
    public static SolarLib getInstance() {
        return instance;
    }

    /**
     * 获取网络管理器
     */
    @Nonnull
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    /**
     * 获取事件注册管理器
     */
    @Nonnull
    public EventRegistry getSolarEventRegistry() {
        return eventRegistry;
    }

    /**
     * 获取适配器工厂
     */
    @Nonnull
    public AdapterFactory getAdapterFactory() {
        return adapterFactory;
    }

    /**
     * 获取 Hytale 事件适配器
     */
    @Nonnull
    public HytaleEventAdapter getHytaleEventAdapter() {
        return hytaleEventAdapter;
    }

    /**
     * 获取 Hytale 事件快捷工具
     */
    @Nonnull
    public HytaleEvents getHytaleEvents() {
        return hytaleEvents;
    }
    
    /**
     * 获取死亡保留物品栏管理器
     */
    @Nonnull
    public KeepInventoryManager getKeepInventoryManager() {
        return keepInventoryManager;
    }
    
    /**
     * 获取死亡保留物品栏监听器
     */
    @Nonnull
    public KeepInventoryListener getKeepInventoryListener() {
        return keepInventoryListener;
    }
    
    /**
     * 获取死亡保留物品栏系统
     */
    @Nonnull
    public KeepInventoryDeathSystem getKeepInventoryDeathSystem() {
        return keepInventoryDeathSystem;
    }
}
