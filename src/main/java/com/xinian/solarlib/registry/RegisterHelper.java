package com.xinian.solarlib.registry;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * 快捷注册工具
 * 提供统一的注册管理功能
 */
public class RegisterHelper {
    private static final Logger LOGGER = Logger.getLogger(RegisterHelper.class.getName());
    private static RegisterHelper instance;

    private final Map<String, Registry<?>> registries;

    private RegisterHelper() {
        this.registries = new ConcurrentHashMap<>();
    }

    /**
     * 获取单例实例
     */
    @Nonnull
    public static RegisterHelper getInstance() {
        if (instance == null) {
            synchronized (RegisterHelper.class) {
                if (instance == null) {
                    instance = new RegisterHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 创建或获取注册表
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> Registry<T> getOrCreateRegistry(@Nonnull String name, @Nonnull Class<T> type) {
        return (Registry<T>) registries.computeIfAbsent(name, k -> new Registry<>(name, type));
    }

    /**
     * 获取注册表
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> Registry<T> getRegistry(@Nonnull String name) {
        Registry<?> registry = registries.get(name);
        if (registry == null) {
            throw new IllegalArgumentException("Registry not found: " + name);
        }
        return (Registry<T>) registry;
    }

    /**
     * 检查注册表是否存在
     */
    public boolean hasRegistry(@Nonnull String name) {
        return registries.containsKey(name);
    }

    /**
     * 快速注册对象
     */
    public <T> void register(@Nonnull String registryName, @Nonnull String key, @Nonnull T value) {
        Registry<T> registry = getRegistry(registryName);
        registry.register(key, value);
    }

    /**
     * 快速注册供应商
     */
    public <T> void registerSupplier(@Nonnull String registryName, @Nonnull String key, @Nonnull Supplier<T> supplier) {
        Registry<T> registry = getRegistry(registryName);
        registry.registerSupplier(key, supplier);
    }

    /**
     * 注册表类
     */
    public static class Registry<T> {
        private final String name;
        private final Class<T> type;
        private final Map<String, T> entries;
        private final Map<String, Supplier<T>> suppliers;

        public Registry(@Nonnull String name, @Nonnull Class<T> type) {
            this.name = name;
            this.type = type;
            this.entries = new ConcurrentHashMap<>();
            this.suppliers = new ConcurrentHashMap<>();
        }

        /**
         * 注册对象
         */
        public void register(@Nonnull String key, @Nonnull T value) {
            if (entries.containsKey(key)) {
                LOGGER.warning("Overriding existing registration: " + key + " in registry " + name);
            }
            entries.put(key, value);
            LOGGER.info("Registered " + key + " in registry " + name);
        }

        /**
         * 注册供应商（延迟初始化）
         */
        public void registerSupplier(@Nonnull String key, @Nonnull Supplier<T> supplier) {
            if (suppliers.containsKey(key)) {
                LOGGER.warning("Overriding existing supplier: " + key + " in registry " + name);
            }
            suppliers.put(key, supplier);
            LOGGER.info("Registered supplier " + key + " in registry " + name);
        }

        /**
         * 获取注册对象
         */
        public T get(@Nonnull String key) {
            T value = entries.get(key);
            if (value == null) {
                // 尝试从供应商获取
                Supplier<T> supplier = suppliers.get(key);
                if (supplier != null) {
                    value = supplier.get();
                    entries.put(key, value); // 缓存结果
                }
            }
            return value;
        }

        /**
         * 检查是否包含指定键
         */
        public boolean contains(@Nonnull String key) {
            return entries.containsKey(key) || suppliers.containsKey(key);
        }

        /**
         * 取消注册
         */
        public void unregister(@Nonnull String key) {
            entries.remove(key);
            suppliers.remove(key);
            LOGGER.info("Unregistered " + key + " from registry " + name);
        }

        /**
         * 获取所有键
         */
        @Nonnull
        public Map<String, T> getAll() {
            // 触发所有延迟加载
            for (Map.Entry<String, Supplier<T>> entry : suppliers.entrySet()) {
                if (!entries.containsKey(entry.getKey())) {
                    T value = entry.getValue().get();
                    entries.put(entry.getKey(), value);
                }
            }
            return new ConcurrentHashMap<>(entries);
        }

        /**
         * 清空注册表
         */
        public void clear() {
            entries.clear();
            suppliers.clear();
            LOGGER.info("Cleared registry " + name);
        }

        /**
         * 获取注册表名称
         */
        @Nonnull
        public String getName() {
            return name;
        }

        /**
         * 获取注册表类型
         */
        @Nonnull
        public Class<T> getType() {
            return type;
        }

        /**
         * 获取注册数量
         */
        public int size() {
            return entries.size() + suppliers.size();
        }
    }
}
