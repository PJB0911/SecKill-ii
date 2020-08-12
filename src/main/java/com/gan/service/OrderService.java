package com.gan.service;


import com.gan.error.BizException;
import com.gan.service.model.OrderModel;

/**
 * 订单Service
 */
public interface OrderService {

    /**
     * 创建订单:
     * 1.通过前端url上传过来秒杀活动Id，然后下单接口内校验对应id是否属于对应商品且活动已开始
     * 2.直接在下单接口内，判断对应的商品是否存在秒杀活动，若存在则以秒杀价格下单。
     * @param userId  用户id
     * @param itemId  商品id
     * @param promoId 秒杀活动id
     * @param amount  购买数量
     * @param stockLogId  库存流水id
     * @return 订单model
     * @throws BizException
     */
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BizException;


    /**
     * 获取用户秒杀订单
     * @param userId  用户id
     * @param itemId  商品id
     * @return 订单model
     */
    OrderModel getOrderByUserIdAndItemId(Integer userId, Integer itemId);
}
