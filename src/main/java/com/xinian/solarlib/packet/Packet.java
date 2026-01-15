package com.xinian.solarlib.packet;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据包基础类
 * 提供快速创建和管理网络数据包的功能
 */
public class Packet {
    private final String packetId;
    private final Map<String, Object> data;
    private long timestamp;

    public Packet(@Nonnull String packetId) {
        this.packetId = packetId;
        this.data = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 获取数据包ID
     */
    @Nonnull
    public String getPacketId() {
        return packetId;
    }

    /**
     * 获取时间戳
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 添加数据
     */
    public Packet put(@Nonnull String key, Object value) {
        data.put(key, value);
        return this;
    }

    /**
     * 获取数据
     */
    public Object get(@Nonnull String key) {
        return data.get(key);
    }

    /**
     * 获取字符串数据
     */
    public String getString(@Nonnull String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取整数数据
     */
    public Integer getInt(@Nonnull String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    /**
     * 获取长整数据
     */
    public Long getLong(@Nonnull String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    /**
     * 获取布尔数据
     */
    public Boolean getBoolean(@Nonnull String key) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }

    /**
     * 获取所有数据
     */
    @Nonnull
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    /**
     * 检查是否包含指定键
     */
    public boolean containsKey(@Nonnull String key) {
        return data.containsKey(key);
    }

    /**
     * 清空数据
     */
    public void clear() {
        data.clear();
    }

    /**
     * 获取数据大小
     */
    public int size() {
        return data.size();
    }

    @Override
    public String toString() {
        return "Packet{" +
                "packetId='" + packetId + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}
