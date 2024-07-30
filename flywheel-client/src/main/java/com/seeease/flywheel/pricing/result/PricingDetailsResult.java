package com.seeease.flywheel.pricing.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class PricingDetailsResult implements Serializable {

    private Integer id;

    private String serialNo;

    /**
     * 定价状态
     */
    private Integer pricingState;

    private Integer stockId;

    /**
     * 来源单据
     */
    private String originSerialNo;

    /**
     * 仓库单据
     */
    private String storeWorkSerialNo;

    /**
     * 加价
     */
    private BigDecimal addPrice;

    /**
     * 总价格
     */
    private BigDecimal allPrice;

    /**
     * tob 毛利率
     */
    private BigDecimal tobMargin;

    /**
     * toc毛利率
     */
    private BigDecimal tocMargin;

    /**
     * tob价格
     */
    private BigDecimal tobPrice;

    /**
     * toc价格
     */
    private BigDecimal tocPrice;

    /**
     * 活动价
     */
    private BigDecimal toaPrice;

    /**
     * 吊牌价
     */
    private BigDecimal tagPrice;

    /**
     * 库存来源
     */
    private Integer pricingSource;

    /**
     * 采购价格
     */
    private BigDecimal purchasePrice;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    private String createdBy;

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


    private String stockSn;

    private String attachment;


    /**
     * 成色
     */
    private String finess;


    /**
     * 公价
     */
    private BigDecimal pricePub;

    private String image;

    private List<PricingLog> logList;

    private Integer fixDay;

    private BigDecimal fixPrice;

    /**
     * 商品级别
     */
    private String goodsLevel;

    /**
     * 销售等级
     */
    private Integer salesPriority;

    private Integer autoPrice;

    /**
     * 是否销售
     */
    private Boolean isSale;

    private Boolean again;


    private BigDecimal wuyuPrice;


    private BigDecimal newSettlePrice;

    private BigDecimal wuyuBuyBackPrice;
    private String demandName;

    private Integer demandId;

}
