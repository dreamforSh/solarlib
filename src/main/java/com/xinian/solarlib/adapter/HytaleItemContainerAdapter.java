package com.xinian.solarlib.adapter;

import com.xinian.solarlib.api.inventory.IItemContainer;
import com.xinian.solarlib.api.inventory.IItemStack;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Hytale物品容器适配器 - 将Hytale的ItemContainer适配到IItemContainer接口
 */
public class HytaleItemContainerAdapter implements IItemContainer {
    private final ItemContainer hytaleContainer;
    
    public HytaleItemContainerAdapter(@Nonnull ItemContainer hytaleContainer) {
        this.hytaleContainer = hytaleContainer;
    }
    
    /**
     * 获取原始Hytale容器对象
     */
    @Nonnull
    public ItemContainer getHytaleContainer() {
        return hytaleContainer;
    }
    
    @Override
    public short getCapacity() {
        return hytaleContainer.getCapacity();
    }
    
    @Nullable
    @Override
    public IItemStack getItem(short slot) {
        ItemStack item = hytaleContainer.getItemStack(slot);
        if (item == null || item.isEmpty()) {
            return null;
        }
        return new HytaleItemStackAdapter(item);
    }
    
    @Override
    public void setItem(short slot, @Nullable IItemStack itemStack) {
        if (itemStack == null) {
            hytaleContainer.removeItemStackFromSlot(slot);
        } else if (itemStack instanceof HytaleItemStackAdapter) {
            hytaleContainer.setItemStackForSlot(slot, ((HytaleItemStackAdapter) itemStack).getHytaleItemStack());
        }
    }
    
    @Override
    public void clearSlot(short slot) {
        hytaleContainer.removeItemStackFromSlot(slot);
    }
    
    @Override
    public void clear() {
        hytaleContainer.clear();
    }
    
    @Nonnull
    @Override
    public List<IItemStack> getAllItems() {
        List<IItemStack> items = new ArrayList<>();
        for (short i = 0; i < getCapacity(); i++) {
            IItemStack item = getItem(i);
            if (item != null && !item.isEmpty()) {
                items.add(item);
            }
        }
        return items;
    }
    
    @Nullable
    @Override
    public IItemStack addItem(@Nonnull IItemStack itemStack) {
        if (!(itemStack instanceof HytaleItemStackAdapter)) {
            return itemStack; // Cannot add non-Hytale items
        }
        
        ItemStack hytaleItem = ((HytaleItemStackAdapter) itemStack).getHytaleItemStack();
        
        // 使用 addItemStackToSlot 尝试添加到每个槽位
        for (short i = 0; i < getCapacity(); i++) {
            ItemStack existing = hytaleContainer.getItemStack(i);
            if (existing == null || existing.isEmpty()) {
                // 空槽位，直接设置
                hytaleContainer.setItemStackForSlot(i, hytaleItem);
                return null;
            } else if (existing.isStackableWith(hytaleItem)) {
                // 可以堆叠
                int maxStack = existing.getItem().getMaxStack();
                int currentQuantity = existing.getQuantity();
                int toAdd = hytaleItem.getQuantity();
                
                if (currentQuantity + toAdd <= maxStack) {
                    // 可以完全堆叠
                    ItemStack newStack = existing.withQuantity(currentQuantity + toAdd);
                    hytaleContainer.setItemStackForSlot(i, newStack);
                    return null;
                } else {
                    // 只能部分堆叠
                    ItemStack newStack = existing.withQuantity(maxStack);
                    hytaleContainer.setItemStackForSlot(i, newStack);
                    int remaining = toAdd - (maxStack - currentQuantity);
                    hytaleItem = hytaleItem.withQuantity(remaining);
                }
            }
        }
        
        // 无法完全添加，返回剩余部分
        return new HytaleItemStackAdapter(hytaleItem);
    }
    
    @Override
    public boolean isEmpty() {
        return hytaleContainer.isEmpty();
    }
    
    @Override
    public boolean isFull() {
        for (short i = 0; i < getCapacity(); i++) {
            if (getItem(i) == null) {
                return false;
            }
        }
        return true;
    }
}
