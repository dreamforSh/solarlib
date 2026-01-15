package com.xinian.solarlib.event;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.IBaseEvent;
import com.hypixel.hytale.event.IEventRegistry;
import com.xinian.solarlib.SolarLib;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Hytale 事件系统适配器
 * 将 Hytale 官方事件系统桥接到 SolarLib
 */
@SuppressWarnings("ALL")
public class HytaleEventAdapter {
    private final IEventRegistry hytaleEventRegistry;

    public HytaleEventAdapter(@Nonnull IEventRegistry hytaleEventRegistry) {
        this.hytaleEventRegistry = hytaleEventRegistry;
    }

    /**
     * 注册 Hytale 事件监听器
     * @param eventClass 事件类
     * @param handler 事件处理器
     * @param <T> 事件类型
     * @return 事件注册对象
     */
    public <T extends IBaseEvent> EventRegistration register(
            @Nonnull Class<T> eventClass,
            @Nonnull Consumer<T> handler) {
        return register(eventClass, handler, EventPriority.NORMAL);
    }

    /**
     * 注册 Hytale 事件监听器（带优先级）
     * @param eventClass 事件类
     * @param handler 事件处理器
     * @param priority 事件优先级
     * @param <T> 事件类型
     * @return 事件注册对象
     */
    public <T extends IBaseEvent> EventRegistration register(
            @Nonnull Class<T> eventClass,
            @Nonnull Consumer<T> handler,
            @Nonnull EventPriority priority) {
        return hytaleEventRegistry.register(eventClass, priority, handler::accept);
    }

    /**
     * 触发 Hytale 事件（仅用于自定义事件）
     * 注意：通常不需要手动触发 Hytale 官方事件
     * @param event 事件实例
     * @param <T> 事件类型
     */
    public <T extends IBaseEvent> void fire(@Nonnull T event) {

        throw new UnsupportedOperationException("Hytale events are dispatched by the system. Use SolarLib EventRegistry for custom events.");
    }

    /**
     * 注销事件监听器
     * @param registration 事件注册对象
     */
    public void unregister(@Nonnull EventRegistration registration) {
        registration.unregister();
    }

    /**
     * 获取底层 Hytale 事件注册器
     */
    @Nonnull
    public IEventRegistry getHytaleEventRegistry() {
        return hytaleEventRegistry;
    }
}
