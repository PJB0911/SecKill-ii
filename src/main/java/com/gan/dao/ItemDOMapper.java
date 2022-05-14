package com.gan.dao;


import com.gan.dataobject.ItemDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 商品Mapper
 */
public interface ItemDOMapper {
    List<ItemDO> listItem();

    /**
     * 增加销量
     * @param id 商品id
     * @param amount  商品数量
     * @return 改变的行数
     */
    int increaseSales(@Param("id") Integer id, @Param("amount") Integer amount);

    int deleteByPrimaryKey(Integer id);

    int insert(ItemDO record);

    int insertSelective(ItemDO record);

    ItemDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemDO record);

    int updateByPrimaryKey(ItemDO record);
}