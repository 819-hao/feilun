package com.seeease.flywheel.purchase.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/14 15:35
 */
@Data
public class PurchaseForSaleResult implements Serializable {

    private Integer stockId;

    private Integer goodsId;
    /**
     * 主图
     */
    private String image;
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
    private String finess;

    /**
     * 公价
     */
    private BigDecimal pricePub;


    /**
     * 表身号
     */
    private String stockSn;


    /**
     * 附件信息，
     */
    private String attachment;

    /**
     * 表节数
     */
    private String watchSection;

    /**
     * 表带材质
     */
    private String strapMaterial;

    /**
     * 回收
     */
    private BigDecimal referenceBuyBackRecyclePrice;

    /**
     * 置换
     */
    private BigDecimal referenceBuyBackInPrice;

    /**
     * 表带更换费
     */
    private BigDecimal strapReplacementPrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;

    /**
     * 置换折扣区间
     */
    private List<String> displaceDiscountRange;
    /**
     * 回收折扣区间
     */
    private List<String> recycleDiscountRange;
}
