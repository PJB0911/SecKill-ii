package com.gan.service.impl;

import com.gan.dao.ItemDOMapper;
import com.gan.dao.ItemStockDOMapper;
import com.gan.dao.StockLogDOMapper;
import com.gan.dataobject.ItemDO;
import com.gan.dataobject.ItemStockDO;
import com.gan.dataobject.StockLogDO;
import com.gan.error.BizException;
import com.gan.error.EmBizError;
import com.gan.mq.MqProducer;
import com.gan.service.ItemService;
import com.gan.service.PromoService;
import com.gan.service.model.ItemModel;
import com.gan.service.model.PromoModel;
import com.gan.validator.ValidationResult;
import com.gan.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ValidatorImpl validator;
    @Autowired
    private ItemDOMapper itemDOMapper;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;
    @Autowired
    private StockLogDOMapper stockLogDOMapper;
    @Autowired
    private PromoService promoService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MqProducer mqProducer;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BizException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()) {
            throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //转化ItemModel变成DataObject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);
        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        //得到生成的主键，并将主键一并写入到itemStock表
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> list = itemDOMapper.listItem();
        List<ItemModel> itemModelList = list.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            return this.convertModelFromDataObject(itemDO, itemStockDO);
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null)
            return null;
        //操作获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        //将dataObject转换成Model
        ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);
        //获取商品的秒杀活动信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if (promoModel != null && promoModel.getStatus() != 3) {
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
        //未引入缓存前，数据库中直接扣减
        //int affectedRow=itemStockDOMapper.decreaseStock(itemId,amount);

        // 库存扣减缓存优化,result表示扣减后的库存
        long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * -1);
        if (result > 0) {
               /*   事务性消息更改后的版本，在整个下单业务执行完毕后，再发送消息。
               //考虑售罄的情况
               //抽离了发送异步消息的逻辑,decreaseStock() 负责扣减Redis库存，不发送异步消息。
                boolean mqResult = mqProducer.asyncReduceStock(itemId, amount);
                if (!mqResult) {
                    //消息发送失败，需要回滚Redis
                    redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
                    return false;
                }*/
            return true;
        } else if (result == 0) {
            //打上售罄标识
            redisTemplate.opsForValue().set("promo_item_stock_invalid_" + itemId, "true");
            return true;
        } else {
            //Redis扣减失败，回滚
            increaseStock(itemId, amount);
            return false;
        }
    }

    @Override
    public boolean increaseStock(Integer itemId, Integer amount) {
        redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
        return true;
    }

    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        return mqProducer.asyncReduceStock(itemId, amount);
    }


    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDOMapper.increaseSales(itemId, amount);
    }


    @Override
    public ItemModel getItemByIdInCache(Integer id) {
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_validate_" + id);
        if (itemModel == null) {
            itemModel = this.getItemById(id);
            redisTemplate.opsForValue().set("item_validate_" + id, itemModel);
            redisTemplate.expire("item_validate_" + id, 10, TimeUnit.MINUTES);
        }
        return itemModel;
    }


    @Override
    @Transactional
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setItemId(itemId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-", ""));
        //1表示初始状态，2表示下单扣减库存成功，3表示下单回滚
        stockLogDO.setStatus(1);
        stockLogDOMapper.insertSelective(stockLogDO);
        return stockLogDO.getStockLogId();
    }


    /**
     * 将 itemModel 对象转换成 ItemDO
     *
     * @param itemModel itemModel
     * @return ItemDO
     */
    private ItemDO convertItemDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null)
            return null;
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    /**
     * 将 itemModel 对象转换成 ItemStockDO
     *
     * @param itemModel itemModel
     * @return ItemStockDO
     */
    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null)
            return null;
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    /**
     * 将 DataObject 对象转换成 ItemModel
     *
     * @param itemDO      itemDO
     * @param itemStockDO itemStockDO
     * @return ItemModel
     */
    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
