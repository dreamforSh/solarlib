package com.xinian.solarlib.feature;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 死亡保留物品栏系统 - 通过ECS系统监听玩家死亡事件
 * 
 * 这个系统继承自 DeathSystems.OnDeathSystem，会在玩家死亡时自动触发
 */
public class KeepInventoryDeathSystem extends DeathSystems.OnDeathSystem {
    private static final Logger LOGGER = Logger.getLogger(KeepInventoryDeathSystem.class.getName());
    
    private final KeepInventoryManager manager;
    private KeepInventoryConfig config;
    
    // 临时存储玩家死亡时的物品栏，用于重生后恢复
    private final Map<UUID, InventorySnapshot> deathInventories;
    
    // 查询：只处理玩家实体
    private static final Query<EntityStore> QUERY = Player.getComponentType();
    
    public KeepInventoryDeathSystem() {
        this.manager = KeepInventoryManager.getInstance();
        this.deathInventories = new ConcurrentHashMap<>();
        LOGGER.info("KeepInventoryDeathSystem initialized");
    }
    
    /**
     * 设置配置
     */
    public void setConfig(@Nonnull KeepInventoryConfig config) {
        this.config = config;
        LOGGER.info("KeepInventoryDeathSystem config set: " + config);
    }
    
    /**
     * 输出调试日志
     */
    private void debug(String message) {
        // 强制输出日志以便调试
        LOGGER.info("[KeepInventory] " + message);
    }
    
    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }
    
    /**
     * 当 DeathComponent 添加到玩家时触发（玩家死亡）
     */
    @Override
    @SuppressWarnings("removal")
    public void onComponentAdded(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull DeathComponent deathComponent,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        
        debug("onComponentAdded triggered");
        
        // 获取玩家组件
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            debug("Player component is null!");
            return; // 不是玩家实体
        }
        
        UUID playerUuid = player.getUuid();
        if (playerUuid == null) {
            debug("Player UUID is null!");
            return;
        }
        debug("Player died: " + playerUuid + " (" + player.getDisplayName() + ")");
        
        // 检查是否启用了保留物品栏
        boolean enabled = manager.isEnabledForPlayer(playerUuid);
        debug("Keep inventory enabled for " + playerUuid + ": " + enabled);
        
        if (!enabled) {
            debug("Player " + playerUuid + " died without keep inventory enabled");
            return;
        }
        
        try {
            // 获取玩家物品栏
            Inventory inventory = player.getInventory();
            if (inventory == null) {
                LOGGER.warning("Player " + playerUuid + " has null inventory on death");
                return;
            }
            
            InventorySnapshot snapshot = null;
            
            // 1. 尝试从当前物品栏保存快照
            if (!isInventoryEmpty(inventory)) {
                debug("Saving snapshot from current inventory");
                snapshot = createSnapshot(inventory);
                
                // 关键：清空玩家物品栏，防止后续系统（或并发系统）处理掉落
                inventory.clear();
                debug("Player inventory cleared to prevent drops");
            } 
            // 2. 如果物品栏为空，检查是否已经被其他系统处理并放入了 itemsLostOnDeath
            else {
                ItemStack[] lostItems = deathComponent.getItemsLostOnDeath();
                if (lostItems != null && lostItems.length > 0) {
                    debug("Inventory empty, but found " + lostItems.length + " items in itemsLostOnDeath. Recovering...");
                    snapshot = createSnapshotFromList(Arrays.asList(lostItems));
                } else {
                    debug("Inventory is empty and no items in itemsLostOnDeath.");
                }
            }
            
            if (snapshot != null) {
                deathInventories.put(playerUuid, snapshot);
                debug("Snapshot saved with " + snapshot.getTotalItems() + " items.");
            }
            
            // 3. 清除 DeathComponent 中的掉落信息，防止生成掉落物实体
            deathComponent.setItemsLostOnDeath(new ArrayList<>());
            deathComponent.setItemsAmountLossPercentage(0.0);
            deathComponent.setItemsDurabilityLossPercentage(0.0);
            deathComponent.setItemsLossMode(DeathConfig.ItemsLossMode.NONE);
            
            debug("DeathComponent drop settings cleared.");
            
        } catch (Exception e) {
            LOGGER.severe("Error saving inventory on death for player " + playerUuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 当 DeathComponent 移除时触发（玩家重生）
     */
    @Override
    @SuppressWarnings("removal")
    public void onComponentRemoved(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull DeathComponent deathComponent,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        
        debug("onComponentRemoved triggered");

        // 获取玩家组件
        Player player = store.getComponent(ref, Player.getComponentType());
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
            
            debug("Restored inventory for player " + playerUuid + 
                       " with " + snapshot.getTotalItems() + " items");
            
        } catch (Exception e) {
            LOGGER.severe("Error restoring inventory on respawn for player " + playerUuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean isInventoryEmpty(Inventory inventory) {
        if (inventory == null) return true;
        if (!isContainerEmpty(inventory.getStorage())) return false;
        if (!isContainerEmpty(inventory.getArmor())) return false;
        if (!isContainerEmpty(inventory.getHotbar())) return false;
        if (!isContainerEmpty(inventory.getUtility())) return false;
        if (inventory.getBackpack() != null && !isContainerEmpty(inventory.getBackpack())) return false;
        return true;
    }

    private boolean isContainerEmpty(ItemContainer container) {
        for (int i = 0; i < container.getCapacity(); i++) {
            ItemStack item = container.getItemStack((short)i);
            if (item != null && !item.isEmpty()) return false;
        }
        return true;
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
     * 从列表创建快照（用于从 itemsLostOnDeath 恢复）
     * 注意：这会丢失物品的原始位置信息，只能尽量填入 Storage
     */
    @Nonnull
    private InventorySnapshot createSnapshotFromList(@Nonnull List<ItemStack> items) {
        List<ItemStack> storage = new ArrayList<>();
        // 简单地将所有物品放入 storage 列表
        for (ItemStack item : items) {
            if (item != null && !item.isEmpty()) {
                storage.add(new ItemStack(
                    item.getItemId(),
                    item.getQuantity(),
                    item.getDurability(),
                    item.getMaxDurability(),
                    item.getMetadata()
                ));
            }
        }
        
        // 其他容器为空
        return new InventorySnapshot(
            storage,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            (byte) 0
        );
    }
    
    /**
     * 恢复物品栏快照
     */
    private void restoreSnapshot(@Nonnull Inventory inventory, @Nonnull InventorySnapshot snapshot) {
        // 先清空物品栏
        inventory.clear();
        
        // 如果是从 itemsLostOnDeath 恢复的（只有 storage 有数据），我们需要智能分配
        boolean isRecoveredFromDrop = snapshot.getArmor().isEmpty() && 
                                      snapshot.getHotbar().isEmpty() && 
                                      snapshot.getUtility().isEmpty() &&
                                      !snapshot.getStorage().isEmpty();
                                      
        if (isRecoveredFromDrop) {
            debug("Restoring from unstructured item list...");
            // 尝试将物品填入物品栏（使用组合容器以便智能分配到各个容器）
            List<ItemStack> allItems = snapshot.getStorage();
            ItemContainer combinedContainer = inventory.getCombinedStorageFirst();
            for (ItemStack item : allItems) {
                if (item != null && !item.isEmpty()) {
                    // 使用 addItemStack 方法添加物品
                    combinedContainer.addItemStack(item);
                }
            }
        } else {
            // 正常恢复
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
    }
    
    /**
     * 复制容器中的所有物品
     */
    @Nonnull
    private List<ItemStack> copyContainer(@Nonnull ItemContainer container) {
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
    private void restoreContainer(@Nonnull ItemContainer container, @Nonnull List<ItemStack> items) {
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
