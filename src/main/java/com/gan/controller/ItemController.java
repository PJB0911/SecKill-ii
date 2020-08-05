package com.gan.controller;


import com.gan.controller.viewobject.ItemVO;
import com.gan.error.BizException;
import com.gan.response.CommonReturnType;
import com.gan.service.CacheService;
import com.gan.service.ItemService;
import com.gan.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品Controller
 */
@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CacheService cacheService;
    /**
     * 创建商品
     * @param title 商品名称
     * @param description 商品描述
     * @param price 商品价格
     * @param stock 商品库存
     * @param imgUrl 商品图片链接
     * @return  返回给前端的创建成功的商品信息
     * @throws BizException
     */
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BizException {

        //封装service请求，用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertVOFromModel(itemModelForReturn);
        return CommonReturnType.create(itemVO);
    }

    /**
     * 商品详情页浏览
     * @param id 商品id
     * @return  返回给前端的商品信息
     */
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {
        ItemModel itemModel = null;
        //第一级：先查询本地缓存
        itemModel = (ItemModel) cacheService.getFromCommonCache("item_" + id);
        if (itemModel == null) {
            //第二级：查询redis缓存
            itemModel = (ItemModel) redisTemplate.opsForValue().get("item_" + id);
            //如果不存在，就执行下游操作，到数据库查询
            if (itemModel == null) {
                itemModel = itemService.getItemById(id);
                //设置itemModel到redis服务器
                redisTemplate.opsForValue().set("item_" + id, itemModel);
                //设置失效时间
                redisTemplate.expire("item_" + id, 10, TimeUnit.MINUTES);
            }
            //填充本地缓冲
            cacheService.setCommonCache("item_" + id, itemModel);
        }
        ItemVO itemVO = convertVOFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    /**
     * 商品列表页面浏览
     * @return  返回给前端的商品列表信息
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem() {
        List<ItemModel> itemModelList = itemService.listItem();
        //使用stream api将list内的itemModel转化为ITEMVO;
        List<ItemVO> list = itemModelList.stream().map(itemModel -> {
            return this.convertVOFromModel(itemModel);
        }).collect(Collectors.toList());
        return CommonReturnType.create(list);
    }

    /**
     *  将 itemModel 对象转换成 ItemVO
     * @param itemModel itemModel
     * @return ItemVO
     */
    private ItemVO convertVOFromModel(ItemModel itemModel) {
        if (itemModel == null)
            return null;
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        //有正在进行或即将进行的秒杀活动
        if (itemModel.getPromoModel() != null) {
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().
                    toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }
}
