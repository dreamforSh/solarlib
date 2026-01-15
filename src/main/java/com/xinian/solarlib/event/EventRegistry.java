package com.xinian.solarlib.event;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 事件注册工具
 * 提供简单的事件监听和分发机制
 */
public class EventRegistry {
    private static final Logger LOGGER = Logger.getLogger(EventRegistry.class.getName());
    private static EventRegistry instance;

    private final Map<Class<?>, List<EventListener>> listeners;
    private final Map<Object, List<Method>> listenerMethods;

    private EventRegistry() {
        this.listeners = new ConcurrentHashMap<>();
        this.listenerMethods = new ConcurrentHashMap<>();
    }

    /**
     * 获取单例实例
     */
    @Nonnull
    public static EventRegistry getInstance() {
        if (instance == null) {
            synchronized (EventRegistry.class) {
                if (instance == null) {
                    instance = new EventRegistry();
                }
            }
        }
        return instance;
    }

    /**
     * 注册事件监听器（通过函数式接口）
     */
    public <T> void register(@Nonnull Class<T> eventType, @Nonnull EventHandler<T> handler) {
        register(eventType, handler, EventPriority.NORMAL);
    }

    /**
     * 注册事件监听器（指定优先级）
     */
    public <T> void register(@Nonnull Class<T> eventType, @Nonnull EventHandler<T> handler, @Nonnull EventPriority priority) {
        EventListener listener = new EventListener(handler, priority);
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        // 按优先级排序
        listeners.get(eventType).sort(Comparator.comparingInt(l -> l.priority.getValue()));
        LOGGER.info("Registered event handler for: " + eventType.getSimpleName() + " with priority " + priority);
    }

    /**
     * 注册对象中所有带 @EventHandler 注解的方法
     */
    public void registerObject(@Nonnull Object obj) {
        Class<?> clazz = obj.getClass();
        List<Method> methods = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandlerAnnotation.class)) {
                if (method.getParameterCount() == 1) {
                    method.setAccessible(true);
                    Class<?> eventType = method.getParameterTypes()[0];
                    EventHandlerAnnotation annotation = method.getAnnotation(EventHandlerAnnotation.class);
                    EventPriority priority = annotation.priority();

                    register(eventType, event -> {
                        try {
                            method.invoke(obj, event);
                        } catch (Exception e) {
                            LOGGER.severe("Error invoking event handler: " + e.getMessage());
                        }
                    }, priority);

                    methods.add(method);
                } else {
                    LOGGER.warning("Event handler method must have exactly one parameter: " + method.getName());
                }
            }
        }

        if (!methods.isEmpty()) {
            listenerMethods.put(obj, methods);
            LOGGER.info("Registered " + methods.size() + " event handlers from " + clazz.getSimpleName());
        }
    }

    /**
     * 取消注册对象的所有事件监听器
     */
    public void unregisterObject(@Nonnull Object obj) {
        listenerMethods.remove(obj);
        LOGGER.info("Unregistered event handlers from " + obj.getClass().getSimpleName());
    }

    /**
     * 触发事件
     */
    public <T> void fire(@Nonnull T event) {
        Class<?> eventType = event.getClass();
        List<EventListener> eventListeners = listeners.get(eventType);

        if (eventListeners != null) {
            for (EventListener listener : new ArrayList<>(eventListeners)) {
                try {
                    @SuppressWarnings("unchecked")
                    EventHandler<T> handler = (EventHandler<T>) listener.handler;
                    handler.handle(event);
                } catch (Exception e) {
                    LOGGER.severe("Error handling event " + eventType.getSimpleName() + ": " + e.getMessage());
                }
            }
        }

        // 也触发父类事件
        Class<?> superClass = eventType.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            List<EventListener> superListeners = listeners.get(superClass);
            if (superListeners != null) {
                for (EventListener listener : new ArrayList<>(superListeners)) {
                    try {
                        @SuppressWarnings("unchecked")
                        EventHandler<T> handler = (EventHandler<T>) listener.handler;
                        handler.handle(event);
                    } catch (Exception e) {
                        LOGGER.severe("Error handling event " + superClass.getSimpleName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 清空所有事件监听器
     */
    public void clear() {
        listeners.clear();
        listenerMethods.clear();
        LOGGER.info("Cleared all event handlers");
    }

    /**
     * 获取指定事件类型的监听器数量
     */
    public int getListenerCount(@Nonnull Class<?> eventType) {
        List<EventListener> eventListeners = listeners.get(eventType);
        return eventListeners != null ? eventListeners.size() : 0;
    }

    /**
     * 事件处理器接口
     */
    @FunctionalInterface
    public interface EventHandler<T> {
        void handle(T event);
    }

    /**
     * 事件监听器包装类
     */
    private static class EventListener {
        private final EventHandler<?> handler;
        private final EventPriority priority;

        public EventListener(EventHandler<?> handler, EventPriority priority) {
            this.handler = handler;
            this.priority = priority;
        }
    }

    /**
     * 事件优先级
     */
    public enum EventPriority {
        LOWEST(0),
        LOW(1),
        NORMAL(2),
        HIGH(3),
        HIGHEST(4);

        private final int value;

        EventPriority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 事件处理器注解
     */
    public @interface EventHandlerAnnotation {
        EventPriority priority() default EventPriority.NORMAL;
    }
}
