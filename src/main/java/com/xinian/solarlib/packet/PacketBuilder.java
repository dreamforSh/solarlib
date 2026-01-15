package com.xinian.solarlib.packet;

import javax.annotation.Nonnull;

/**
 * 数据包构建器
 * 提供链式调用快速构建数据包
 */
public class PacketBuilder {
    private final Packet packet;

    private PacketBuilder(@Nonnull String packetId) {
        this.packet = new Packet(packetId);
    }

    /**
     * 创建数据包构建器
     */
    @Nonnull
    public static PacketBuilder create(@Nonnull String packetId) {
        return new PacketBuilder(packetId);
    }

    /**
     * 添加字符串数据
     */
    @Nonnull
    public PacketBuilder putString(@Nonnull String key, String value) {
        packet.put(key, value);
        return this;
    }

    /**
     * 添加整数数据
     */
    @Nonnull
    public PacketBuilder putInt(@Nonnull String key, int value) {
        packet.put(key, value);
        return this;
    }

    /**
     * 添加长整数据
     */
    @Nonnull
    public PacketBuilder putLong(@Nonnull String key, long value) {
        packet.put(key, value);
        return this;
    }

    /**
     * 添加布尔数据
     */
    @Nonnull
    public PacketBuilder putBoolean(@Nonnull String key, boolean value) {
        packet.put(key, value);
        return this;
    }

    /**
     * 添加双精度数据
     */
    @Nonnull
    public PacketBuilder putDouble(@Nonnull String key, double value) {
        packet.put(key, value);
        return this;
    }

    /**
     * 添加浮点数据
     */
    @Nonnull
    public PacketBuilder putFloat(@Nonnull String key, float value) {
        packet.put(key, value);
        return this;
    }

    /**
     * 添加对象数据
     */
    @Nonnull
    public PacketBuilder put(@Nonnull String key, Object value) {
        packet.put(key, value);
        return this;
    }

    /**
     * 设置时间戳
     */
    @Nonnull
    public PacketBuilder timestamp(long timestamp) {
        packet.setTimestamp(timestamp);
        return this;
    }

    /**
     * 构建数据包
     */
    @Nonnull
    public Packet build() {
        return packet;
    }
}
