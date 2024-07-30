package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 20:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockExceptionListResult implements Serializable {

    private Integer stockId;

    private String stockSn;


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


    private String attachment;


    /**
     * 成色
     */
    private String finess;

    private String image;

    /**
     * 异常原因
     */
    private String bqtExceptionReason;

    private String rkTime;

    private Integer stockStatus;


    /**
     * 商品归属
     */
    private String belongName;
    /**
     * 所处仓库(库存所属)
     */
    private String locationName;

    /**
     * 经营权
     */
    private String managementOf;

    private Integer stockSrc;

    private BigDecimal pricePub;

    private BigDecimal purchasePrice;

    private BigDecimal totalPrice;

    /**
     * 异常原因
     */
    private String unusualDesc;

    /**
     * 供应商
     */
    private String customerName;

    private String remarks;
}
