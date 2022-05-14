package com.gan.dataobject;

/**
 * 库存流水DO
 */
public class StockLogDO {

    private String stockLogId;

    private Integer itemId;

    private Integer amount;

    /**
     * 流水状态
     */
    private Integer status;


    public String getStockLogId() {
        return stockLogId;
    }

    public void setStockLogId(String stockLogId) {
        this.stockLogId = stockLogId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}