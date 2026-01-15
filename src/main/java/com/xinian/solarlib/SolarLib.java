package com.xinian.solarlib;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.xinian.solarlib.command.CommandRegistry;
import com.xinian.solarlib.event.EventRegistry;
import com.xinian.solarlib.event.HytaleEventAdapter;
import com.xinian.solarlib.event.HytaleEvents;
import com.xinian.solarlib.network.NetworkManager;
import com.xinian.solarlib.registry.RegisterHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import javax.annotation.Nonnull;
import java.util.logging.Logger;


/**
 * SolarLib - 快速开发库插件
 * 提供数据包创建、网络通信、快捷注册等核心功能
 */
public class SolarLib extends JavaPlugin {
    private static final Logger LOGGER = Logger.getLogger(SolarLib.class.getName());
    private static SolarLib instance;

    private NetworkManager networkManager;
    private RegisterHelper registerHelper;
    private EventRegistry eventRegistry;
    private CommandRegistry commandRegistry;
    private HytaleEventAdapter hytaleEventAdapter;
    private HytaleEvents hytaleEvents;

    public SolarLib(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        super.setup();
        
        // 初始化各个管理器
        this.networkManager = NetworkManager.getInstance();
        this.registerHelper = RegisterHelper.getInstance();
        this.eventRegistry = EventRegistry.getInstance();
        this.commandRegistry = CommandRegistry.getInstance();
        
        // 初始化 Hytale 事件系统适配器
        this.hytaleEventAdapter = new HytaleEventAdapter(getEventRegistry());
        this.hytaleEvents = new HytaleEvents(hytaleEventAdapter);

        LOGGER.info("SolarLib initialized successfully!");
        LOGGER.info("- Network Manager: Ready");
        LOGGER.info("- Register Helper: Ready");
        LOGGER.info("- Event Registry: Ready");
        LOGGER.info("- Command Registry: Ready");
        LOGGER.info("- Hytale Event Adapter: Ready");
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
     * 获取注册助手
     */
    @Nonnull
    public RegisterHelper getRegisterHelper() {
        return registerHelper;
    }

    /**
     * 获取事件注册管理器
     */
    @Nonnull
    public EventRegistry getEventManager() {
        return eventRegistry;
    }

    /**
     * 获取命令管理器
     */
    @Nonnull
    public CommandRegistry getCommandManager() {
        return commandRegistry;
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
}
