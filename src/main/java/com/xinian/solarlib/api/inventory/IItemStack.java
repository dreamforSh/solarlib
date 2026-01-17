package com.xinian.solarlib.api.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 物品堆栈接口 - 表示一个物品及其数量
 */
public interface IItemStack {
    
    /**
     * 获取物品ID
     * 
     * @return 物品的唯一标识符
     */
    @Nonnull
    String getItemId();
    
    /**
     * 获取物品数量
     * 
     * @return 物品数量
     */
    int getQuantity();
    
    /**
     * 设置物品数量
     * 
     * @param quantity 新的数量
     */
    void setQuantity(int quantity);
    
    /**
     * 获取物品元数据（可选）
     * 
     * @return 物品元数据，如果没有则返回 null
     */
    @Nullable
    String getMetadata();
    
    /**
     * 设置物品元数据
     * 
     * @param metadata 元数据字符串
     */
    void setMetadata(@Nullable String metadata);
    
    /**
     * 复制当前物品堆栈
     * 
     * @return 新的物品堆栈副本
     */
    @Nonnull
    IItemStack copy();
    
    /**
     * 检查是否为空物品堆栈
     * 
     * @return 如果数量为0或物品无效，返回 true
     */
    boolean isEmpty();
}
