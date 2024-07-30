package com.seeease.flywheel.serve.goods.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/7 10:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsStockCPrice implements Serializable {

    private Integer stockId;

    private BigDecimal tocPrice;

    private String stockSn;

    private BigDecimal tagPrice;

    private Integer goodsId;

    /**
     * 品牌
     */
    private String brandName;
    /**
     * 系列
     */
    private String seriesName;
    /**
     * 型号
     */
    private String model;
}
