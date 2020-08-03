package com.gan.dao;


import com.gan.dataobject.ItemStockDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * 商品库存Mapper
 */
public interface ItemStockDOMapper {
    ItemStockDO selectByItemId(Integer id);
    /**
     * 减少库存
     * @param itemId 商品id
     * @param amount  商品数量
     * @return 改变的行数
     */
    int decreaseStock(@Param("itemId") Integer itemId,
                      @Param("amount") Integer amount);

    int deleteByPrimaryKey(Integer id);

    int insert(ItemStockDO record);

    int insertSelective(ItemStockDO record);

    ItemStockDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemStockDO record);

    int updateByPrimaryKey(ItemStockDO record);
}