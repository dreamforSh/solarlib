package com.xinian.solarlib.event;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.IBaseEvent;
import com.hypixel.hytale.event.IEventRegistry;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
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
    
    /**
     * 死亡组件事件处理器接口
     */
    @FunctionalInterface
    public interface DeathComponentHandler {
        void handle(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull DeathComponent component,
            @Nonnull Store<EntityStore> store,
            @Nonnull ComponentAccessor<EntityStore> accessor
        );
    }
    
    /**
     * 注册死亡组件添加监听器（玩家死亡时触发）
     * 注意：这是一个简化的接口，实际的ECS系统事件需要通过系统注册
     */
    public void registerDeathComponentAddedListener(@Nonnull DeathComponentHandler handler) {
        // 这里应该通过ECS系统注册，但由于我们使用的是反编译的API
        // 实际实现需要访问World的ECS系统管理器
        SolarLib.LOGGER.warning("DeathComponent listeners should be registered through ECS systems");
        SolarLib.LOGGER.warning("This is a placeholder - implement through proper ECS system registration");
    }
    
    /**
     * 注册死亡组件移除监听器（玩家重生时触发）
     */
    public void registerDeathComponentRemovedListener(@Nonnull DeathComponentHandler handler) {
        SolarLib.LOGGER.warning("DeathComponent listeners should be registered through ECS systems");
        SolarLib.LOGGER.warning("This is a placeholder - implement through proper ECS system registration");
    }
}
