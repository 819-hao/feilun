package com.seeease.flywheel.serve.sale.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 */
@Data
public class BillSaleOrderLineSettlementVO implements Serializable {

    /**
     * 详情id
     */
    private Integer saleLineId;
    private Integer saleLineState;
    private Integer stockId;
    private Integer saleId;
    private String serialNo;
    /**
     * 型号id
     */
    private Integer goodsId;
    private Integer saleMode;

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
    private BigDecimal gmvPerformance;


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
    private Integer isUnderselling;

    /**
     * 附件列表
     */
    private String attachment;

    private BigDecimal preClinchPrice;
    private BigDecimal clinchPrice;

    private Integer rightOfManagement;
    private Integer deliveryLocationId;
    private Integer balanceDirection;

    private BigDecimal tobPrice;
    private BigDecimal marginPrice;


}
