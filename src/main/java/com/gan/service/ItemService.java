package com.gan.service;


import com.gan.error.BizException;
import com.gan.service.model.ItemModel;

import java.util.List;

/**
 * 商品Service
 */
public interface ItemService {

    /**
     * 创建商品
     *
     * @param itemModel 商品Model
     * @return 商品Model(生成主键id)
     * @throws BizException
     */
    ItemModel createItem(ItemModel itemModel) throws BizException;

    /**
     * 商品列表浏览
     *
     * @return 商品列表
     */
    List<ItemModel> listItem();

    /**
     * 商品详情浏览
     *
     * @param id 商品id
     * @return 商品Model
     */
    ItemModel getItemById(Integer id);

    /**
     * 库存扣减
     *
     * @param itemId 商品id
     * @param amount 商品数量
     * @return 是否扣减成功
     */
    boolean decreaseStock(Integer itemId, Integer amount);


    /**
     * 库存增加
     *
     * @param itemId 商品id
     * @param amount 商品数量
     * @return 是否增加成功
     */
    boolean increaseStock(Integer itemId, Integer amount);

    /**
     * 增加销量
     *
     * @param itemId 商品id
     * @param amount 商品数量
     */
    void increaseSales(Integer itemId, Integer amount);



    /**
     * 优化：Item及Promo model缓存模型;
     * 验证item及promo是否有效
     * @param id 商品id
     * @return  商品Model
     */
    ItemModel getItemByIdInCache(Integer id);

    /**
     * 异步更新库存
     * @param itemId 商品id
     * @param amount 商品数量
     * @return  是否更新成功
     */
    boolean asyncDecreaseStock(Integer itemId, Integer amount);

    /**
     * 初始化库存流水
     * @param itemId 商品id
     * @param amount 商品数量
     * @return 库存流水id
     */
    String initStockLog(Integer itemId, Integer amount);
}


