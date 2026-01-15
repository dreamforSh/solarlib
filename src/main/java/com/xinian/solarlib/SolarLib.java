package com.xinian.solarlib;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.xinian.solarlib.event.EventRegistry;
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

        LOGGER.info("SolarLib initialized successfully!");
        LOGGER.info("- Network Manager: Ready");
        LOGGER.info("- Register Helper: Ready");
        LOGGER.info("- Event Registry: Ready");
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
}
