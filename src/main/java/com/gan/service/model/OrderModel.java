package com.gan.service.model;

import java.math.BigDecimal;

/**
 * 交易model
 */
public class OrderModel {
    //企业级一般用String类型，而不是int
    private String id;
    private Integer userId;
    private Integer itemId;
    private Integer promoId;
    //购买数量
    private Integer amount;
    //购买商品单价
    private BigDecimal itemPrice;
    //购买金额,若promoId非空，则表示秒杀商品价格
    private BigDecimal orderPrice;

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }
}
