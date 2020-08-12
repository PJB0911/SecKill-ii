package com.gan.service.impl;

import com.gan.dao.PromoDOMapper;
import com.gan.dataobject.PromoDO;
import com.gan.service.ItemService;
import com.gan.service.OrderService;
import com.gan.service.PromoService;
import com.gan.service.UserService;
import com.gan.service.model.ItemModel;
import com.gan.service.model.OrderModel;
import com.gan.service.model.PromoModel;
import com.gan.service.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoDOMapper promoDOMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        //获取商品的秒杀活动信息
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);
        //dataobject->model
        PromoModel promoModel = convertFromDataObj(promoDO);
        if (promoModel == null)
            return null;
        //判断当前时间秒杀活动是还未开始还是即将开始
        DateTime now = new DateTime();
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }
        return promoModel;

    }

    @Override
    public void publishPromo(Integer promoId) {
        //通过活动id获取活动
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if (promoDO.getItemId() == null || promoDO.getItemId().intValue() == 0)
            return;
        ItemModel itemModel = itemService.getItemById(promoDO.getItemId());
        //库存同步到Redis
        redisTemplate.opsForValue().set("promo_item_stock_" + itemModel.getId(), itemModel.getStock());
        //大闸限制数量设置到redis内，设置为秒杀商品库存的5倍
        redisTemplate.opsForValue().set("promo_door_count_" + promoId, itemModel.getStock().intValue() * 5);
    }


    @Override
    public String generateSecondKillToken(Integer promoId, Integer itemId, Integer userId) {
        //判断库存是否售罄，若Key存在，则直接返回下单失败
        if (redisTemplate.hasKey("promo_item_stock_invalid_" + itemId))
            return null;

        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        PromoModel promoModel = convertFromDataObj(promoDO);
        if (promoModel == null)
            return null;
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }
        //判断活动是否正在进行
        if (promoModel.getStatus() != 2)
            return null;
        //判断item信息是否存在
        ItemModel itemModel = itemService.getItemByIdInCache(itemId);
        if (itemModel == null)
            return null;
        //判断用户是否存在
        UserModel userModel = userService.getUserByIdInCache(userId);
        if (userModel == null)
            return null;

        // 判断是否已经秒杀到商品，防止一人多次秒杀成功
        if(redisTemplate.hasKey("seckill_success_itemid"+itemId+"userid"+userId))
            return null;
        OrderModel orderModel= orderService.getOrderByUserIdAndItemId(userModel.getId(),itemId);
        if (orderModel != null){
            redisTemplate.opsForValue().set("seckill_success_itemid"+itemId+"userid"+userId,true);
            redisTemplate.expire("seckill_success_itemid"+itemId+"userid"+userId,6, TimeUnit.HOURS);
            return null;
        }

        //如果已有秒杀令牌，表示进行过秒杀操作（即是否点击过秒杀按钮）
        String token= (String) redisTemplate.opsForValue().get("promo_token_" + promoId + "_userid_" + userId + "_itemid_" + itemId);
        if(token!=null)
            return null;

        //获取大闸数量
        long result = redisTemplate.opsForValue().increment("promo_door_count_" + promoId, -1);
        if (result < 0)
            return null;

        //生成Token，并且存入redis内，5分钟时限
        token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("promo_token_" + promoId + "_userid_" + userId + "_itemid_" + itemId, token);
        redisTemplate.expire("promo_token_" + promoId + "_userid_" + userId + "_itemid_" + itemId, 5, TimeUnit.MINUTES);
        return token;
    }


    /**
     * 将 promoDO 对象转换成 PromoModel
     *
     * @param promoDO promoDO
     * @return PromoModel
     */
    private PromoModel convertFromDataObj(PromoDO promoDO) {
        if (promoDO == null)
            return null;
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        //秒杀DO是double而Model是BigDecimal，要转一下
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        //日期格式调整
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
