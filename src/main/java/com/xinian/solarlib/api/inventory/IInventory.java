package com.xinian.solarlib.api.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 玩家物品栏接口 - 表示玩家的完整物品栏系统
 */
public interface IInventory {
    
    /**
     * 获取存储容器（主物品栏）
     * 
     * @return 存储容器
     */
    @Nonnull
    IItemContainer getStorage();
    
    /**
     * 获取护甲容器
     * 
     * @return 护甲容器
     */
    @Nonnull
    IItemContainer getArmor();
    
    /**
     * 获取快捷栏容器
     * 
     * @return 快捷栏容器
     */
    @Nonnull
    IItemContainer getHotbar();
    
    /**
     * 获取工具容器
     * 
     * @return 工具容器
     */
    @Nonnull
    IItemContainer getUtility();
    
    /**
     * 获取背包容器
     * 
     * @return 背包容器
     */
    @Nullable
    IItemContainer getBackpack();
    
    /**
     * 清空所有容器
     */
    void clearAll();
    
    /**
     * 获取当前激活的快捷栏槽位
     * 
     * @return 激活的槽位索引
     */
    byte getActiveHotbarSlot();
    
    /**
     * 设置激活的快捷栏槽位
     * 
     * @param slot 槽位索引
     */
    void setActiveHotbarSlot(byte slot);
    
    /**
     * 获取当前手持物品
     * 
     * @return 手持的物品堆栈，如果为空则返回 null
     */
    @Nullable
    IItemStack getItemInHand();
    
    /**
     * 标记物品栏已更改
     */
    void markChanged();
}
