package com.seeease.flywheel.serve.goods.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockPo extends Stock {

    /**
     * 暂时用于财务那边 查询出来的值承载
     */
    private String brandName;
    private String seriesName;
    private BigDecimal pricePub;
    private String modelName;
    private BigDecimal serviceFee;
    /**
     * 是否经过景德镇2号默认0否
     */
    private Integer isJdzTwo;
}
