package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderSettlementListResult implements Serializable {
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
     * 附件列表
     */
    private String attachment;

    private BigDecimal preClinchPrice;
    private BigDecimal clinchPrice;

    private Integer rightOfManagement;
    private Integer deliveryLocationId;

    private String rightOfManagementName;
    private String locationName;
    private Integer balanceDirection;
    private Integer saleMode;

    private BigDecimal tobPrice;
    private BigDecimal marginPrice;
}
