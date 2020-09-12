package com.gan.service.impl;

import com.gan.dao.OrderDOMapper;
import com.gan.dao.SequenceDOMapper;
import com.gan.dao.StockLogDOMapper;
import com.gan.dataobject.OrderDO;
import com.gan.dataobject.SequenceDO;
import com.gan.dataobject.StockLogDO;
import com.gan.error.BizException;
import com.gan.error.EmBizError;
import com.gan.service.ItemService;
import com.gan.service.OrderService;
import com.gan.service.UserService;
import com.gan.service.model.ItemModel;
import com.gan.service.model.OrderModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDOMapper orderDOMapper;
    @Autowired
    private SequenceDOMapper sequenceDOMapper;
    @Autowired
    private StockLogDOMapper stockLogDOMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) throws BizException {
    /*  generateSecondKillToken
        因为PromoService.generateSecondKillToken()方法在生成promoToken的时候已验证，下单业务不需要验证，解耦。
        //1. 校验下单状态。下单商品是否存在，用户是否合法，购买数量是否正确
        // 商品无缓存:
        //ItemModel itemModel=itemService.getItemById(itemId);
        //商品缓存优化：
        ItemModel itemModel = itemService.getItemByIdInCache(itemId);
        if (itemModel == null)
            throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        // 用户无缓存:
        //UserModel userModel=userService.getUserById(userId);
        //缓存优化：
        UserModel userModel = userService.getUserByIdInCache(userId);
        if (userModel == null)
            throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        //校验活动信息
        if (promoId != null) {
            //1.校验对应活动是否适用于该商品
            if (promoId.intValue() != itemModel.getPromoModel().getId()) {
                throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "活动信息不存在");
                //2.校验活动是否在进行中
            } else if (itemModel.getPromoModel().getStatus() != 2) {
                throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "活动还未开始");
            }
        }*/

        ItemModel itemModel = itemService.getItemByIdInCache(itemId);
        if (amount <= 0 || amount > 99)
            throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "数量信息不存在");

        //2. 落单减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result)
            throw new BizException(EmBizError.STOCK_NOT_ENOUGH);
        //3. 创建订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        // 秒杀价格 or 正常价格
        if (promoId != null) {
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        //生成交易流水号，即订单号
        orderModel.setId(generateOrderNo());
        //model->dataobject
        OrderDO orderDO = convertFromOrderModel(orderModel);
        //订单入库
        orderDOMapper.insertSelective(orderDO);
        //销量增加
        itemService.increaseSales(itemId, amount);

     /*   优化—事务型消息第一步：异步更新库存，将发送消息从ItemService.decreaseStock() 里面独立出来，在入库、销量增加后再发送消息。
          但事务提交过程发生异常，无法保证一致性，解决方法见registerSynchronization
        boolean mqResult = itemService.asyncDecreaseStock(itemId, amount);
        if (!mqResult) {
            //回滚redis 库存
            itemService.increaseStock(itemId, amount);
            throw new BizException(EmBizError.MQ_SEND_FAIL);
        }*/

      /*  优化—事务型消息第二步：能解决事务提交过程异常的问题，但是如果消息发送失败，则无能为力。
          解决方法见MqProduce事务消息 transactionAsyncReduceStock（） 和 executeLocalTransaction（），将下单和发送消息合在一起
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                boolean mqResult = itemService.asyncDecreaseStock(itemId, amount);
                if (!mqResult) {
                    //回滚redis 库存
                    //itemService.increaseStock(itemId, amount);
                    //throw new BizException(EmBizError.MQ_SEND_FAIL);
                }
            }
        });*/

        //4.设置库存流水状态为成功
        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
        if (stockLogDO == null)
            throw new BizException(EmBizError.UNKNOWN_ERROR);
        stockLogDO.setStatus(2);
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);

        //5.用户秒杀成功商品标记
        redisTemplate.opsForValue().set("seckill_success_itemid"+itemId+"userid"+userId,true);
        redisTemplate.expire("seckill_success_itemid"+itemId+"userid"+userId,6, TimeUnit.HOURS);

        //6. 返回前端
        return orderModel;
    }

    @Override
    public OrderModel getOrderByUserIdAndItemId(Integer userId, Integer itemId) {
        OrderDO orderDO=orderDOMapper.selectByUserIdAndItemId(userId,itemId);
        OrderModel orderModel=convertFromOrdeDO(orderDO);
        return orderModel;
    }

    /**
     * 将 orderModel 对象转换成 OrderDO
     *
     * @param orderModel orderModel
     * @return OrderDO
     */
    private OrderDO convertFromOrderModel(OrderModel orderModel) {
        if (orderModel == null)
            return null;
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }

    /**
     * 将  OrderDO 对象转换成 orderModel
     *
     * @param orderDO orderDO
     * @return orderModel
     */
    private OrderModel convertFromOrdeDO(OrderDO orderDO) {
        if (orderDO == null)
            return null;
        OrderModel orderModel = new OrderModel();
        BeanUtils.copyProperties(orderDO, orderModel);
        return orderModel;
    }

    /**
     * 生成订单号
     *
     * @return 订单号
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo() {
        //订单号为16位
        StringBuilder stringBuilder = new StringBuilder();
        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);
        //中间6位为自增序列
        int sequence = 0;
        //获取当前Sequence
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        //获得当前的值
        sequence = sequenceDO.getCurrentValue();
        //设置下一次的值
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        //保存到数据库
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        //转成换String，用于拼接
        String seqStr = String.valueOf(sequence);
        //不足的位数，用0填充
        for (int i = 0; i < 6 - seqStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(seqStr);
        //最后2位为分库分表为，暂时写死
        stringBuilder.append("00");
        return stringBuilder.toString();
    }
}
