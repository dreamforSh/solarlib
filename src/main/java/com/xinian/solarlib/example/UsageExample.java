package com.xinian.solarlib.example;

import com.xinian.solarlib.SolarLib;
import com.xinian.solarlib.event.EventRegistry;
import com.xinian.solarlib.network.NetworkManager;
import com.xinian.solarlib.packet.Packet;
import com.xinian.solarlib.packet.PacketBuilder;
import com.xinian.solarlib.registry.RegisterHelper;
import io.netty.channel.Channel;

/**
 * 使用示例
 * 演示如何使用 XiNian Lib 的各项功能
 */
public class UsageExample {

    /**
     * 数据包使用示例
     */
    public static void packetExample() {
        // 使用 PacketBuilder 快速创建数据包
        Packet packet = PacketBuilder.create("player.login")
                .putString("username", "player123")
                .putInt("level", 10)
                .putBoolean("isVip", true)
                .putLong("lastLogin", System.currentTimeMillis())
                .build();

        // 访问数据包内容
        String username = packet.getString("username");
        Integer level = packet.getInt("level");
        Boolean isVip = packet.getBoolean("isVip");

        System.out.println("Username: " + username);
        System.out.println("Level: " + level);
        System.out.println("Is VIP: " + isVip);
    }

    /**
     * 网络通信示例
     */
    public static void networkExample(Channel channel) {
        NetworkManager networkManager = SolarLib.getInstance().getNetworkManager();

        // 注册数据包处理器
        networkManager.registerHandler("player.login", packet -> {
            String username = packet.getString("username");
            System.out.println("Player logged in: " + username);
            
            // 处理登录逻辑...
        });

        // 发送数据包到指定通道
        Packet response = PacketBuilder.create("server.welcome")
                .putString("message", "Welcome to the server!")
                .build();
        networkManager.sendPacket(channel, response);

        // 广播数据包到所有通道
        Packet broadcast = PacketBuilder.create("server.announcement")
                .putString("message", "Server maintenance in 10 minutes")
                .build();
        networkManager.broadcast(broadcast);
    }

    /**
     * 注册工具示例
     */
    public static void registerExample() {
        RegisterHelper registerHelper = SolarLib.getInstance().getRegisterHelper();

        // 创建一个命令注册表
        RegisterHelper.Registry<Runnable> commandRegistry = 
                registerHelper.getOrCreateRegistry("commands", Runnable.class);

        // 注册命令
        commandRegistry.register("help", () -> {
            System.out.println("Available commands: help, info, quit");
        });

        commandRegistry.register("info", () -> {
            System.out.println("Server info: XiNian Lib v1.0");
        });

        // 使用延迟加载注册
        commandRegistry.registerSupplier("heavy_command", () -> {
            // 这里只在首次使用时才会创建对象
            return () -> System.out.println("Heavy command executed");
        });

        // 获取并执行命令
        Runnable helpCommand = commandRegistry.get("help");
        if (helpCommand != null) {
            helpCommand.run();
        }

        // 获取所有注册的命令
        commandRegistry.getAll().forEach((name, command) -> {
            System.out.println("Registered command: " + name);
        });
    }

    /**
     * 事件系统示例
     */
    public static void eventExample() {
        EventRegistry eventRegistry = SolarLib.getInstance().getEventManager();

        // 使用函数式接口注册事件监听器
        eventRegistry.register(PlayerJoinEvent.class, event -> {
            System.out.println("Player joined: " + event.getPlayerName());
        });

        // 使用优先级注册
        eventRegistry.register(PlayerJoinEvent.class, 
                event -> System.out.println("High priority handler"), 
                EventRegistry.EventPriority.HIGH);

        // 触发事件
        PlayerJoinEvent event = new PlayerJoinEvent("player123");
        eventRegistry.fire(event);

        // 使用注解方式注册
        PlayerListener listener = new PlayerListener();
        eventRegistry.registerObject(listener);
    }

    /**
     * 示例事件类
     */
    public static class PlayerJoinEvent {
        private final String playerName;

        public PlayerJoinEvent(String playerName) {
            this.playerName = playerName;
        }

        public String getPlayerName() {
            return playerName;
        }
    }

    /**
     * 示例监听器类（使用注解）
     */
    public static class PlayerListener {
        @EventRegistry.EventHandlerAnnotation(priority = EventRegistry.EventPriority.NORMAL)
        public void onPlayerJoin(PlayerJoinEvent event) {
            System.out.println("Listener received: " + event.getPlayerName());
        }
    }

    /**
     * 综合使用示例
     */
    public static void comprehensiveExample(Channel channel) {
        // 1. 创建数据包
        Packet packet = PacketBuilder.create("game.action")
                .putString("action", "attack")
                .putString("target", "enemy123")
                .putInt("damage", 50)
                .build();

        // 2. 注册处理器
        NetworkManager networkManager = SolarLib.getInstance().getNetworkManager();
        networkManager.registerHandler("game.action", receivedPacket -> {
            String action = receivedPacket.getString("action");
            
            // 3. 触发游戏事件
            GameActionEvent event = new GameActionEvent(action);
            SolarLib.getInstance().getEventManager().fire(event);
        });

        // 4. 发送数据包
        networkManager.sendPacket(channel, packet);
    }

    public static class GameActionEvent {
        private final String action;

        public GameActionEvent(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }
}
