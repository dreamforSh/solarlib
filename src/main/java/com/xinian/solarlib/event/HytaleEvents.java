package com.xinian.solarlib.event;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.server.core.event.events.player.*;
import com.hypixel.hytale.server.core.event.events.*;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Hytale 常用事件快捷注册工具
 * 提供便捷的事件监听方法
 */
@SuppressWarnings("ALL")
public class HytaleEvents {
    private final HytaleEventAdapter adapter;

    public HytaleEvents(@Nonnull HytaleEventAdapter adapter) {
        this.adapter = adapter;
    }

    // ==================== 服务器生命周期事件 ====================

    /**
     * 服务器启动事件
     */
    public EventRegistration onServerBoot(@Nonnull Consumer<BootEvent> handler) {
        return adapter.register(BootEvent.class, handler);
    }

    /**
     * 服务器准备宇宙事件
     */
    public EventRegistration onPrepareUniverse(@Nonnull Consumer<PrepareUniverseEvent> handler) {
        return adapter.register(PrepareUniverseEvent.class, handler);
    }

    /**
     * 服务器关闭事件
     */
    public EventRegistration onServerShutdown(@Nonnull Consumer<ShutdownEvent> handler) {
        return adapter.register(ShutdownEvent.class, handler);
    }

    // ==================== 玩家事件 ====================

    /**
     * 玩家连接事件
     */
    public EventRegistration onPlayerConnect(@Nonnull Consumer<PlayerConnectEvent> handler) {
        return adapter.register(PlayerConnectEvent.class, handler);
    }

    /**
     * 玩家连接事件（带优先级）
     */
    public EventRegistration onPlayerConnect(@Nonnull Consumer<PlayerConnectEvent> handler, @Nonnull EventPriority priority) {
        return adapter.register(PlayerConnectEvent.class, handler, priority);
    }

    /**
     * 玩家断开连接事件
     */
    public EventRegistration onPlayerDisconnect(@Nonnull Consumer<PlayerDisconnectEvent> handler) {
        return adapter.register(PlayerDisconnectEvent.class, handler);
    }

    /**
     * 玩家准备就绪事件
     */
    public EventRegistration onPlayerReady(@Nonnull Consumer<PlayerReadyEvent> handler) {
        return adapter.register(PlayerReadyEvent.class, handler);
    }

    /**
     * 玩家加入世界事件
     */
    public EventRegistration onPlayerAddToWorld(@Nonnull Consumer<AddPlayerToWorldEvent> handler) {
        return adapter.register(AddPlayerToWorldEvent.class, handler);
    }

    /**
     * 玩家离开世界事件
     */
    public EventRegistration onPlayerDrainFromWorld(@Nonnull Consumer<DrainPlayerFromWorldEvent> handler) {
        return adapter.register(DrainPlayerFromWorldEvent.class, handler);
    }

    /**
     * 玩家聊天事件
     */
    public EventRegistration onPlayerChat(@Nonnull Consumer<PlayerChatEvent> handler) {
        return adapter.register(PlayerChatEvent.class, handler);
    }

    /**
     * 玩家聊天事件（带优先级）
     */
    public EventRegistration onPlayerChat(@Nonnull Consumer<PlayerChatEvent> handler, @Nonnull EventPriority priority) {
        return adapter.register(PlayerChatEvent.class, handler, priority);
    }

    /**
     * 玩家交互事件
     */
    public EventRegistration onPlayerInteract(@Nonnull Consumer<PlayerInteractEvent> handler) {
        return adapter.register(PlayerInteractEvent.class, handler);
    }

    /**
     * 玩家合成事件
     */
    public EventRegistration onPlayerCraft(@Nonnull Consumer<PlayerCraftEvent> handler) {
        return adapter.register(PlayerCraftEvent.class, handler);
    }

    /**
     * 玩家鼠标按钮事件
     */
    public EventRegistration onPlayerMouseButton(@Nonnull Consumer<PlayerMouseButtonEvent> handler) {
        return adapter.register(PlayerMouseButtonEvent.class, handler);
    }

    /**
     * 玩家鼠标移动事件
     */
    public EventRegistration onPlayerMouseMotion(@Nonnull Consumer<PlayerMouseMotionEvent> handler) {
        return adapter.register(PlayerMouseMotionEvent.class, handler);
    }

    // ==================== 通用方法 ====================

    /**
     * 注册任意 Hytale 事件
     */
    public <T extends com.hypixel.hytale.event.IBaseEvent> EventRegistration on(
            @Nonnull Class<T> eventClass,
            @Nonnull Consumer<T> handler) {
        return adapter.register(eventClass, handler);
    }

    /**
     * 注册任意 Hytale 事件（带优先级）
     */
    public <T extends com.hypixel.hytale.event.IBaseEvent> EventRegistration on(
            @Nonnull Class<T> eventClass,
            @Nonnull Consumer<T> handler,
            @Nonnull EventPriority priority) {
        return adapter.register(eventClass, handler, priority);
    }

    /**
     * 注销事件监听器
     */
    public void unregister(@Nonnull EventRegistration registration) {
        adapter.unregister(registration);
    }

    /**
     * 获取底层适配器
     */
    @Nonnull
    public HytaleEventAdapter getAdapter() {
        return adapter;
    }
}
