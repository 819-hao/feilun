package com.seeease.flywheel.serve.sale.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 */
@Data
public class BillSaleReturnOrderLineDetailsVO implements Serializable {

    /**
     * 详情id
     */
    private Integer id;
    private Integer saleLineId;
    private Integer saleReturnId;

    /**
     * 型号id
     */
    private Integer goodsId;
    private Integer stockId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 成色
     */
    private String finess;

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
     * 附件列表
     */
    private String attachment;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    /**
     * 单行状态
     */
    private Integer saleReturnLineState;



    private String saleSerialNo;
    /**
     * 公价
     */
    private BigDecimal pricePub;
    private BigDecimal returnPrice;
    private BigDecimal clinchPrice;
    private BigDecimal marginPrice;
    private BigDecimal totalPrice;
    private BigDecimal tagPrice;
    private BigDecimal tocPrice;
    private BigDecimal tobPrice;
    private BigDecimal proportion;

    private BigDecimal gmvPerformance;

    private Integer rightOfManagement;
    private Integer locationId;
    private Integer balanceDirection;
    private Integer whetherInvoice;
    private Integer whetherOperate;

    /**
     * 最新结算价
     */
    private BigDecimal newSettlePrice;
}
