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


    /**
     * 秒杀活动发布
     * @param promoId 秒杀活动id
     */
    void publishPromo(Integer promoId);

    /**
     *生成秒杀令牌
     * @param promoId 秒杀活动id
     * @param itemId 商品id
     * @param userId 用户id
     * @return 秒杀令牌
     */
    String generateSecondKillToken(Integer promoId, Integer itemId, Integer userId);
}
