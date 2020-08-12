package com.gan.dao;


import com.gan.dataobject.OrderDO;
import org.apache.ibatis.annotations.Param;

/**
 * 订单Mapper
 */
public interface OrderDOMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrderDO record);

    int insertSelective(OrderDO record);

    OrderDO selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderDO record);

    int updateByPrimaryKey(OrderDO record);
    /**
     * 根据用户id和商品id查询
     * @param userId 用户id
     * @param itemId  商品id
     * @return 商品DO
     */
    OrderDO selectByUserIdAndItemId(@Param("userId") Integer userId, @Param("itemId") Integer itemId);

}