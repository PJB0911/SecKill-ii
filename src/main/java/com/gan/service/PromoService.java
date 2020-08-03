package com.gan.service;


import com.gan.service.model.PromoModel;
/**
 * 秒杀Service
 */
public interface PromoService {

    /**
     * 获取即将进行或正在进行的商品活动
     * @param itemId 商品id
     * @return 秒杀model
     */
    PromoModel getPromoByItemId(Integer itemId);
}
