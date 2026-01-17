package com.xinian.solarlib.adapter;

import com.xinian.solarlib.api.inventory.IInventory;
import com.xinian.solarlib.api.inventory.IItemContainer;
import com.xinian.solarlib.api.inventory.IItemStack;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Hytale物品栏适配器 - 将Hytale的Inventory适配到IInventory接口
 */
public class HytaleInventoryAdapter implements IInventory {
    private final Inventory hytaleInventory;
    private final HytaleItemContainerAdapter storageAdapter;
    private final HytaleItemContainerAdapter armorAdapter;
    private final HytaleItemContainerAdapter hotbarAdapter;
    private final HytaleItemContainerAdapter utilityAdapter;
    private final HytaleItemContainerAdapter backpackAdapter;
    
    public HytaleInventoryAdapter(@Nonnull Inventory hytaleInventory) {
        this.hytaleInventory = hytaleInventory;
        this.storageAdapter = new HytaleItemContainerAdapter(hytaleInventory.getStorage());
        this.armorAdapter = new HytaleItemContainerAdapter(hytaleInventory.getArmor());
        this.hotbarAdapter = new HytaleItemContainerAdapter(hytaleInventory.getHotbar());
        this.utilityAdapter = new HytaleItemContainerAdapter(hytaleInventory.getUtility());
        
        ItemContainer backpack = hytaleInventory.getBackpack();
        this.backpackAdapter = backpack != null ? new HytaleItemContainerAdapter(backpack) : null;
    }
    
    /**
     * 获取原始Hytale物品栏对象
     */
    @Nonnull
    public Inventory getHytaleInventory() {
        return hytaleInventory;
    }
    
    @Nonnull
    @Override
    public IItemContainer getStorage() {
        return storageAdapter;
    }
    
    @Nonnull
    @Override
    public IItemContainer getArmor() {
        return armorAdapter;
    }
    
    @Nonnull
    @Override
    public IItemContainer getHotbar() {
        return hotbarAdapter;
    }
    
    @Nonnull
    @Override
    public IItemContainer getUtility() {
        return utilityAdapter;
    }
    
    @Nullable
    @Override
    public IItemContainer getBackpack() {
        return backpackAdapter;
    }
    
    @Override
    public void clearAll() {
        hytaleInventory.clear();
    }
    
    @Override
    public byte getActiveHotbarSlot() {
        return hytaleInventory.getActiveHotbarSlot();
    }
    
    @Override
    public void setActiveHotbarSlot(byte slot) {
        hytaleInventory.setActiveHotbarSlot(slot);
    }
    
    @Nullable
    @Override
    public IItemStack getItemInHand() {
        ItemStack item = hytaleInventory.getItemInHand();
        if (item == null || item.isEmpty()) {
            return null;
        }
        return new HytaleItemStackAdapter(item);
    }
    
    @Override
    public void markChanged() {
        hytaleInventory.markChanged();
    }
}
