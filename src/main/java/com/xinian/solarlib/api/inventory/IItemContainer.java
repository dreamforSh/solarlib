package com.xinian.solarlib.api.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 物品容器接口 - 表示可以存储物品的容器
 */
public interface IItemContainer {
    
    /**
     * 获取容器容量
     * 
     * @return 容器的最大槽位数
     */
    short getCapacity();
    
    /**
     * 获取指定槽位的物品
     * 
     * @param slot 槽位索引
     * @return 槽位中的物品堆栈，如果为空则返回 null
     */
    @Nullable
    IItemStack getItem(short slot);
    
    /**
     * 设置指定槽位的物品
     * 
     * @param slot 槽位索引
     * @param itemStack 要设置的物品堆栈
     */
    void setItem(short slot, @Nullable IItemStack itemStack);
    
    /**
     * 清空指定槽位
     * 
     * @param slot 槽位索引
     */
    void clearSlot(short slot);
    
    /**
     * 清空整个容器
     */
    void clear();
    
    /**
     * 获取容器中所有非空物品
     * 
     * @return 非空物品列表
     */
    @Nonnull
    List<IItemStack> getAllItems();
    
    /**
     * 添加物品到容器
     * 
     * @param itemStack 要添加的物品
     * @return 添加后剩余的物品（如果容器已满），如果全部添加成功则返回 null
     */
    @Nullable
    IItemStack addItem(@Nonnull IItemStack itemStack);
    
    /**
     * 检查容器是否为空
     * 
     * @return 如果所有槽位都为空，返回 true
     */
    boolean isEmpty();
    
    /**
     * 检查容器是否已满
     * 
     * @return 如果没有空槽位，返回 true
     */
    boolean isFull();
}
