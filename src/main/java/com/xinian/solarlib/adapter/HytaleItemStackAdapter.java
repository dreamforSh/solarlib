package com.xinian.solarlib.adapter;

import com.xinian.solarlib.api.inventory.IItemStack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Hytale物品堆栈适配器 - 将Hytale的ItemStack适配到IItemStack接口
 * 注意：Hytale的ItemStack是不可变的，所有修改操作都返回新实例
 */
public class HytaleItemStackAdapter implements IItemStack {
    private final com.hypixel.hytale.server.core.inventory.ItemStack hytaleItemStack;
    
    public HytaleItemStackAdapter(@Nonnull com.hypixel.hytale.server.core.inventory.ItemStack hytaleItemStack) {
        this.hytaleItemStack = hytaleItemStack;
    }
    
    /**
     * 获取原始Hytale物品堆栈对象
     */
    @Nonnull
    public com.hypixel.hytale.server.core.inventory.ItemStack getHytaleItemStack() {
        return hytaleItemStack;
    }
    
    @Nonnull
    @Override
    public String getItemId() {
        return hytaleItemStack.getItemId();
    }
    
    @Override
    public int getQuantity() {
        return hytaleItemStack.getQuantity();
    }
    
    @Override
    public void setQuantity(int quantity) {
        // ItemStack是不可变的，无法直接修改
        // 此方法不应该在适配器中使用，需要通过 withQuantity 创建新实例
        throw new UnsupportedOperationException(
            "Cannot modify quantity directly. ItemStack is immutable. Use withQuantity() to create a new instance.");
    }
    
    @Nullable
    @Override
    public String getMetadata() {
        // Hytale uses BsonDocument for metadata, serialize it to JSON string
        if (hytaleItemStack.getMetadata() != null) {
            return hytaleItemStack.getMetadata().toString();
        }
        return null;
    }
    
    @Override
    public void setMetadata(@Nullable String metadata) {
        // ItemStack是不可变的，无法直接修改元数据
        // 此方法不应该在适配器中使用
        throw new UnsupportedOperationException(
            "Cannot modify metadata directly. ItemStack is immutable. Use withMetadata() to create a new instance.");
    }
    
    @Nonnull
    @Override
    public IItemStack copy() {
        // 创建一个新的ItemStack实例（使用构造函数）
        return new HytaleItemStackAdapter(
            new com.hypixel.hytale.server.core.inventory.ItemStack(
                hytaleItemStack.getItemId(),
                hytaleItemStack.getQuantity(),
                hytaleItemStack.getDurability(),
                hytaleItemStack.getMaxDurability(),
                hytaleItemStack.getMetadata()
            )
        );
    }
    
    @Override
    public boolean isEmpty() {
        return hytaleItemStack.isEmpty();
    }
}
