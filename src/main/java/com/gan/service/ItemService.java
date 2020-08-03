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
     * 增加销量
     *
     * @param itemId 商品id
     * @param amount 商品数量
     */
    void increaseSales(Integer itemId, Integer amount);
}


