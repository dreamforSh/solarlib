package com.xinian.solarlib.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 数据包编解码器
 * 提供数据包与 ByteBuf 之间的转换
 */
public class PacketCodec {

    /**
     * 将数据包编码为 ByteBuf
     */
    @Nonnull
    public static ByteBuf encode(@Nonnull Packet packet) {
        ByteBuf buffer = Unpooled.buffer();
        
        // 写入数据包ID
        writeString(buffer, packet.getPacketId());
        
        // 写入时间戳
        buffer.writeLong(packet.getTimestamp());
        
        // 写入数据条目数量
        Map<String, Object> data = packet.getData();
        buffer.writeInt(data.size());
        
        // 写入每个数据条目
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            writeString(buffer, entry.getKey());
            writeObject(buffer, entry.getValue());
        }
        
        return buffer;
    }

    /**
     * 从 ByteBuf 解码数据包
     */
    @Nonnull
    public static Packet decode(@Nonnull ByteBuf buffer) {
        // 读取数据包ID
        String packetId = readString(buffer);
        Packet packet = new Packet(packetId);
        
        // 读取时间戳
        long timestamp = buffer.readLong();
        packet.setTimestamp(timestamp);
        
        // 读取数据条目数量
        int size = buffer.readInt();
        
        // 读取每个数据条目
        for (int i = 0; i < size; i++) {
            String key = readString(buffer);
            Object value = readObject(buffer);
            packet.put(key, value);
        }
        
        return packet;
    }

    /**
     * 写入字符串到 ByteBuf
     */
    private static void writeString(@Nonnull ByteBuf buffer, @Nonnull String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    /**
     * 从 ByteBuf 读取字符串
     */
    @Nonnull
    private static String readString(@Nonnull ByteBuf buffer) {
        int length = buffer.readInt();
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 写入对象到 ByteBuf
     */
    private static void writeObject(@Nonnull ByteBuf buffer, Object value) {
        if (value == null) {
            buffer.writeByte(0); // NULL
        } else if (value instanceof String) {
            buffer.writeByte(1); // STRING
            writeString(buffer, (String) value);
        } else if (value instanceof Integer) {
            buffer.writeByte(2); // INTEGER
            buffer.writeInt((Integer) value);
        } else if (value instanceof Long) {
            buffer.writeByte(3); // LONG
            buffer.writeLong((Long) value);
        } else if (value instanceof Boolean) {
            buffer.writeByte(4); // BOOLEAN
            buffer.writeBoolean((Boolean) value);
        } else if (value instanceof Double) {
            buffer.writeByte(5); // DOUBLE
            buffer.writeDouble((Double) value);
        } else if (value instanceof Float) {
            buffer.writeByte(6); // FLOAT
            buffer.writeFloat((Float) value);
        } else {
            buffer.writeByte(1); // 默认转字符串
            writeString(buffer, value.toString());
        }
    }

    /**
     * 从 ByteBuf 读取对象
     */
    private static Object readObject(@Nonnull ByteBuf buffer) {
        byte type = buffer.readByte();
        return switch (type) {
            case 0 -> null;
            case 1 -> readString(buffer);
            case 2 -> buffer.readInt();
            case 3 -> buffer.readLong();
            case 4 -> buffer.readBoolean();
            case 5 -> buffer.readDouble();
            case 6 -> buffer.readFloat();
            default -> null;
        };
    }
}
