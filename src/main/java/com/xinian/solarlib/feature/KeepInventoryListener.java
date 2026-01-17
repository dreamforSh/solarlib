package com.xinian.solarlib.feature;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.xinian.solarlib.event.HytaleEvents;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 死亡保留物品栏监听器 - 监听玩家死亡事件，实现物品栏保留功能
 */
public class KeepInventoryListener {
    private static final Logger LOGGER = Logger.getLogger(KeepInventoryListener.class.getName());
    
    private final KeepInventoryManager manager;
    private final HytaleEvents events;
    
    // 临时存储玩家死亡时的物品栏，用于重生后恢复
    private final Map<UUID, InventorySnapshot> deathInventories;
    
    public KeepInventoryListener(@Nonnull HytaleEvents events) {
        this.manager = KeepInventoryManager.getInstance();
        this.events = events;
        this.deathInventories = new ConcurrentHashMap<>();
        
        registerListeners();
    }
    
    private void registerListeners() {
        // TODO: 注册ECS系统的死亡事件监听器
        // Hytale使用ECS系统，需要通过System注册事件监听
        // 这里暂时保留占位符实现
        // 正确的实现需要创建一个 System 来监听DeathComponent的添加/移除
        
        LOGGER.info("KeepInventoryListener initialized (event registration pending)");
    }
    
    /**
     * 玩家死亡时调用
     */
    @SuppressWarnings("removal")
    private void onPlayerDeath(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull DeathComponent deathComponent,
            @Nonnull Store<EntityStore> store,
            @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        
        // 获取玩家组件
        Player player = componentAccessor.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return; // 不是玩家实体
        }
        
        UUID playerUuid = player.getUuid();
        if (playerUuid == null) {
            return;
        }
        
        // 检查是否启用了保留物品栏
        if (!manager.isEnabledForPlayer(playerUuid)) {
            LOGGER.info("Player " + playerUuid + " died without keep inventory enabled");
            return;
        }
        
        try {
            // 获取玩家物品栏
            Inventory inventory = player.getInventory();
            if (inventory == null) {
                LOGGER.warning("Player " + playerUuid + " has null inventory on death");
                return;
            }
            
            // 创建物品栏快照
            InventorySnapshot snapshot = createSnapshot(inventory);
            deathInventories.put(playerUuid, snapshot);
            
            LOGGER.info("Saved inventory snapshot for player " + playerUuid + 
                       " with " + snapshot.getTotalItems() + " items");
            
            // 清空死亡物品丢失数据，防止物品掉落
            if (deathComponent.getItemsLostOnDeath() != null && deathComponent.getItemsLostOnDeath().length > 0) {
                deathComponent.setItemsLostOnDeath(new ArrayList<>());
                deathComponent.setItemsAmountLossPercentage(0.0);
                deathComponent.setItemsDurabilityLossPercentage(0.0);
                LOGGER.info("Cleared death item loss for player " + playerUuid);
            }
            
        } catch (Exception e) {
            LOGGER.severe("Error saving inventory on death for player " + playerUuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 玩家重生时调用
     */
    @SuppressWarnings("removal")
    private void onPlayerRespawn(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull DeathComponent deathComponent,
            @Nonnull Store<EntityStore> store,
            @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        
        // 获取玩家组件
        Player player = componentAccessor.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }
        
        UUID playerUuid = player.getUuid();
        if (playerUuid == null) {
            return;
        }
        
        // 检查是否有保存的物品栏快照
        InventorySnapshot snapshot = deathInventories.remove(playerUuid);
        if (snapshot == null) {
            return;
        }
        
        try {
            // 恢复物品栏
            restoreSnapshot(player.getInventory(), snapshot);
            player.getInventory().markChanged();
            player.sendInventory();
            
            LOGGER.info("Restored inventory for player " + playerUuid + 
                       " with " + snapshot.getTotalItems() + " items");
            
        } catch (Exception e) {
            LOGGER.severe("Error restoring inventory on respawn for player " + playerUuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建物品栏快照
     */
    @Nonnull
    private InventorySnapshot createSnapshot(@Nonnull Inventory inventory) {
        return new InventorySnapshot(
            copyContainer(inventory.getStorage()),
            copyContainer(inventory.getArmor()),
            copyContainer(inventory.getHotbar()),
            copyContainer(inventory.getUtility()),
            inventory.getBackpack() != null ? copyContainer(inventory.getBackpack()) : new ArrayList<>(),
            inventory.getActiveHotbarSlot()
        );
    }
    
    /**
     * 恢复物品栏快照
     */
    private void restoreSnapshot(@Nonnull Inventory inventory, @Nonnull InventorySnapshot snapshot) {
        // 先清空物品栏
        inventory.clear();
        
        // 恢复各个容器
        restoreContainer(inventory.getStorage(), snapshot.getStorage());
        restoreContainer(inventory.getArmor(), snapshot.getArmor());
        restoreContainer(inventory.getHotbar(), snapshot.getHotbar());
        restoreContainer(inventory.getUtility(), snapshot.getUtility());
        
        if (inventory.getBackpack() != null && !snapshot.getBackpack().isEmpty()) {
            restoreContainer(inventory.getBackpack(), snapshot.getBackpack());
        }
        
        // 恢复激活的快捷栏槽位
        inventory.setActiveHotbarSlot(snapshot.getActiveHotbarSlot());
    }
    
    /**
     * 复制容器中的所有物品
     */
    @Nonnull
    private List<ItemStack> copyContainer(@Nonnull com.hypixel.hytale.server.core.inventory.container.ItemContainer container) {
        List<ItemStack> items = new ArrayList<>();
        short capacity = container.getCapacity();
        
        for (short i = 0; i < capacity; i++) {
            ItemStack item = container.getItemStack(i);
            if (item != null && !item.isEmpty()) {
                // 创建新的ItemStack实例（复制）
                items.add(new ItemStack(
                    item.getItemId(),
                    item.getQuantity(),
                    item.getDurability(),
                    item.getMaxDurability(),
                    item.getMetadata()
                ));
            } else {
                items.add(null);
            }
        }
        
        return items;
    }
    
    /**
     * 恢复容器中的物品
     */
    private void restoreContainer(
            @Nonnull com.hypixel.hytale.server.core.inventory.container.ItemContainer container,
            @Nonnull List<ItemStack> items) {
        
        for (int i = 0; i < items.size() && i < container.getCapacity(); i++) {
            ItemStack item = items.get(i);
            if (item != null && !item.isEmpty()) {
                container.setItemStackForSlot((short) i, item);
            }
        }
    }
    
    /**
     * 清理指定玩家的快照数据
     */
    public void clearSnapshot(@Nonnull UUID playerUuid) {
        deathInventories.remove(playerUuid);
    }
    
    /**
     * 清理所有快照数据
     */
    public void clearAllSnapshots() {
        deathInventories.clear();
    }
    
    /**
     * 物品栏快照 - 存储玩家死亡时的物品栏状态
     */
    private static class InventorySnapshot {
        private final List<ItemStack> storage;
        private final List<ItemStack> armor;
        private final List<ItemStack> hotbar;
        private final List<ItemStack> utility;
        private final List<ItemStack> backpack;
        private final byte activeHotbarSlot;
        
        public InventorySnapshot(
                @Nonnull List<ItemStack> storage,
                @Nonnull List<ItemStack> armor,
                @Nonnull List<ItemStack> hotbar,
                @Nonnull List<ItemStack> utility,
                @Nonnull List<ItemStack> backpack,
                byte activeHotbarSlot) {
            
            this.storage = storage;
            this.armor = armor;
            this.hotbar = hotbar;
            this.utility = utility;
            this.backpack = backpack;
            this.activeHotbarSlot = activeHotbarSlot;
        }
        
        public List<ItemStack> getStorage() { return storage; }
        public List<ItemStack> getArmor() { return armor; }
        public List<ItemStack> getHotbar() { return hotbar; }
        public List<ItemStack> getUtility() { return utility; }
        public List<ItemStack> getBackpack() { return backpack; }
        public byte getActiveHotbarSlot() { return activeHotbarSlot; }
        
        public int getTotalItems() {
            return countNonNullItems(storage) + countNonNullItems(armor) + 
                   countNonNullItems(hotbar) + countNonNullItems(utility) + 
                   countNonNullItems(backpack);
        }
        
        private int countNonNullItems(List<ItemStack> items) {
            int count = 0;
            for (ItemStack item : items) {
                if (item != null && !item.isEmpty()) {
                    count++;
                }
            }
            return count;
        }
    }
}
