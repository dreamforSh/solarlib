package com.xinian.solarlib.network;

import com.xinian.solarlib.packet.Packet;
import com.xinian.solarlib.packet.PacketCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * 网络通信管理器
 * 提供数据包发送、接收和处理功能
 */
public class NetworkManager {
    private static final Logger LOGGER = Logger.getLogger(NetworkManager.class.getName());
    private static NetworkManager instance;
    
    private final Map<String, Consumer<Packet>> packetHandlers;
    private final Map<Channel, String> channelMap;

    private NetworkManager() {
        this.packetHandlers = new ConcurrentHashMap<>();
        this.channelMap = new ConcurrentHashMap<>();
    }

    /**
     * 获取单例实例
     */
    @Nonnull
    public static NetworkManager getInstance() {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new NetworkManager();
                }
            }
        }
        return instance;
    }

    /**
     * 注册数据包处理器
     */
    public void registerHandler(@Nonnull String packetId, @Nonnull Consumer<Packet> handler) {
        packetHandlers.put(packetId, handler);
        LOGGER.info("Registered packet handler for: " + packetId);
    }

    /**
     * 取消注册数据包处理器
     */
    public void unregisterHandler(@Nonnull String packetId) {
        packetHandlers.remove(packetId);
        LOGGER.info("Unregistered packet handler for: " + packetId);
    }

    /**
     * 处理接收到的数据包
     */
    public void handlePacket(@Nonnull Packet packet) {
        String packetId = packet.getPacketId();
        Consumer<Packet> handler = packetHandlers.get(packetId);
        
        if (handler != null) {
            try {
                handler.accept(packet);
            } catch (Exception e) {
                LOGGER.severe("Error handling packet " + packetId + ": " + e.getMessage());
            }
        } else {
            LOGGER.warning("No handler registered for packet: " + packetId);
        }
    }

    /**
     * 发送数据包到指定通道
     */
    public void sendPacket(@Nonnull Channel channel, @Nonnull Packet packet) {
        if (channel.isActive()) {
            try {
                ByteBuf buffer = PacketCodec.encode(packet);
                channel.writeAndFlush(buffer);
                LOGGER.fine("Sent packet " + packet.getPacketId() + " to channel");
            } catch (Exception e) {
                LOGGER.severe("Error sending packet: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Channel is not active, cannot send packet");
        }
    }

    /**
     * 注册通道
     */
    public void registerChannel(@Nonnull Channel channel, @Nonnull String identifier) {
        channelMap.put(channel, identifier);
        LOGGER.info("Registered channel: " + identifier);
    }

    /**
     * 取消注册通道
     */
    public void unregisterChannel(@Nonnull Channel channel) {
        String identifier = channelMap.remove(channel);
        if (identifier != null) {
            LOGGER.info("Unregistered channel: " + identifier);
        }
    }

    /**
     * 根据标识符获取通道
     */
    @Nullable
    public Channel getChannel(@Nonnull String identifier) {
        for (Map.Entry<Channel, String> entry : channelMap.entrySet()) {
            if (entry.getValue().equals(identifier)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 广播数据包到所有通道
     */
    public void broadcast(@Nonnull Packet packet) {
        for (Channel channel : channelMap.keySet()) {
            sendPacket(channel, packet);
        }
        LOGGER.info("Broadcasted packet " + packet.getPacketId() + " to all channels");
    }

    /**
     * 获取所有已注册的数据包处理器ID
     */
    @Nonnull
    public Map<String, Consumer<Packet>> getHandlers() {
        return new ConcurrentHashMap<>(packetHandlers);
    }

    /**
     * 清空所有处理器
     */
    public void clearHandlers() {
        packetHandlers.clear();
        LOGGER.info("Cleared all packet handlers");
    }

    /**
     * Netty 处理器适配器
     * 将接收到的 ByteBuf 转换为 Packet 并处理
     */
    public static class PacketHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof ByteBuf buffer) {
                try {
                    Packet packet = PacketCodec.decode(buffer);
                    NetworkManager.getInstance().handlePacket(packet);
                } catch (Exception e) {
                    LOGGER.severe("Error decoding packet: " + e.getMessage());
                } finally {
                    buffer.release();
                }
            } else {
                super.channelRead(ctx, msg);
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("Channel active: " + ctx.channel());
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            NetworkManager.getInstance().unregisterChannel(ctx.channel());
            LOGGER.info("Channel inactive: " + ctx.channel());
            super.channelInactive(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOGGER.severe("Exception in channel: " + cause.getMessage());
            ctx.close();
        }
    }
}
