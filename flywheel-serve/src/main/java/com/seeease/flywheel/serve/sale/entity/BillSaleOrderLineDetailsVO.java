package com.seeease.flywheel.serve.sale.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Data
public class BillSaleOrderLineDetailsVO implements Serializable {

    /**
     * 详情id
     */
    private Integer id;
    private Integer stockId;
    private Integer belongId;
    private Integer saleId;

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 商品编码
     */
    private String wno;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 成色
     */
    private String finess;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    /**
     * 单行状态
     */
    private Integer saleLineState;

    /**
     * 表节
     */
    private String watchSection;

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

    /**
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 附件列表
     */
    private String attachment;

    private BigDecimal preClinchPrice;
    private BigDecimal clinchPrice;
    private BigDecimal marginPrice;
    private BigDecimal strapReplacementPrice;
    private BigDecimal gmvPerformance;
    private BigDecimal proportion;
    /**
     * 活动价格
     */
    private BigDecimal promotionConsignmentPrice;
    private BigDecimal totalPrice;
    private BigDecimal tagPrice;
    private BigDecimal tocPrice;
    private BigDecimal tobPrice;

    private String remarks;

    private String strapMaterial;

    private String buyBackPolicy;
    private String expressNumber;

    private Integer rightOfManagement;
    private Integer balanceDirection;
    private Integer isRepurchasePolicy;
    private Integer isCounterPurchase;

    private String consignmentSaleFinishTime;
    private String consignmentSettlementOperator;

    /**
     * 第三方子订单
     */
    private String subOrderCode;

    /**
     * 质保年限
     */
    private Integer warrantyPeriod;

    /**
     * 型号主图
     */
    private String image;

    /**
     * 是否开票
     */
    private Integer whetherInvoice;


    /**
     * 行情价
     */
    private BigDecimal marketsPrice;
    /**
     * 最新结算价
     */
    private BigDecimal newSettlePrice;
}
