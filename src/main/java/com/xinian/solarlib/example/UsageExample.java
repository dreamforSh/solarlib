package com.xinian.solarlib.example;

import com.xinian.solarlib.SolarLib;
import com.xinian.solarlib.command.Command;
import com.xinian.solarlib.command.CommandBuilder;
import com.xinian.solarlib.command.CommandRegistry;
import com.xinian.solarlib.command.CommandSender;
import com.xinian.solarlib.event.EventRegistry;
import com.xinian.solarlib.event.HytaleEvents;
import com.xinian.solarlib.network.NetworkManager;
import com.xinian.solarlib.packet.Packet;
import com.xinian.solarlib.packet.PacketBuilder;
import com.xinian.solarlib.registry.RegisterHelper;
import io.netty.channel.Channel;

import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.event.events.player.*;

/**
 * 使用示例
 * 演示如何使用 solarlib 的各项功能
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
     * 命令系统示例
     */
    public static void commandExample() {
        CommandRegistry commandRegistry = SolarLib.getInstance().getCommandManager();

        // 使用 CommandBuilder 快速注册命令
        CommandBuilder.create("hello")
                .description("打招呼命令")
                .usage("/hello [player]")
                .alias("hi", "greet")
                .minArgs(0)
                .maxArgs(1)
                .executes((sender, args) -> {
                    if (args.length == 0) {
                        sender.sendMessage("你好，" + sender.getName() + "!");
                    } else {
                        sender.sendMessage("你好，" + args[0] + "!");
                    }
                    return true;
                })
                .buildAndRegister();

        // 注册带权限的命令
        CommandBuilder.create("admin")
                .description("管理员命令")
                .permission("solarlib.admin")
                .executes((sender, args) -> {
                    sender.sendMessage("你有管理员权限!");
                    return true;
                })
                .buildAndRegister();

        // 注册带 Tab 补全的命令
        CommandBuilder.create("gamemode")
                .description("切换游戏模式")
                .usage("/gamemode <mode>")
                .alias("gm")
                .args(1, 1)
                .executes((sender, args) -> {
                    sender.sendMessage("切换游戏模式为: " + args[0]);
                    return true;
                })
                .tabCompletes((sender, args) -> {
                    if (args.length == 1) {
                        return java.util.Arrays.asList("survival", "creative", "adventure", "spectator");
                    }
                    return new java.util.ArrayList<>();
                })
                .buildAndRegister();

        // 手动创建并注册命令
        Command customCommand = new Command("info", "查看信息", "/info") {
            @Override
            public boolean execute(CommandSender sender, String[] args) {
                sender.sendMessage("SolarLib v0.1.1");
                sender.sendMessage("已注册命令数: " + commandRegistry.getCommandCount());
                return true;
            }
        };
        commandRegistry.register(customCommand);

        // 执行命令 (模拟)
        // commandRegistry.execute(someSender, "hello", new String[]{"World"});
    }

    /**
     * Hytale 官方事件系统示例
     */
    public static void hytaleEventExample() {
        HytaleEvents hytaleEvents = SolarLib.getInstance().getHytaleEvents();

        // 监听玩家连接事件
        EventRegistration reg1 = hytaleEvents.onPlayerConnect(event -> {
            System.out.println("玩家连接: " + event.toString());
        });

        // 监听玩家准备就绪事件（带优先级）
        EventRegistration reg2 = hytaleEvents.onPlayerReady(event -> {
            System.out.println("玩家准备就绪!");
        });

        // 监听玩家聊天事件（高优先级）
        EventRegistration reg3 = hytaleEvents.onPlayerChat(event -> {
            System.out.println("玩家聊天事件触发");
            // 可以访问事件属性
            // event.getPlayer() // 获取玩家
        }, EventPriority.NORMAL);

        // 监听玩家断开连接
        EventRegistration reg4 = hytaleEvents.onPlayerDisconnect(event -> {
            System.out.println("玩家断开连接");
        });

        // 监听服务器关闭事件
        EventRegistration reg5 = hytaleEvents.onServerShutdown(event -> {
            System.out.println("服务器正在关闭...");
            // 清理资源
        });

        // 使用通用方法监听任意事件
        EventRegistration reg6 = hytaleEvents.on(PlayerInteractEvent.class, event -> {
            System.out.println("玩家交互事件");
        });

        // 注销事件监听（在需要时）
        // hytaleEvents.unregister(reg1);
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
