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
public class SaleReturnStockQueryImportResult implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 附件
     */
    private String attachment;
    /**
     * 表身号
     */
    private String stockSn;
    /**
     * store_management 所在地 ID 急售商品所在地
     */
    private Integer locationId;
    /**
     * 商品位置
     */
    private String locationName;
    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;
    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     * 经营权名
     */
    private String rightOfManagementName;

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

    private String serialNo;

    private BigDecimal tobPrice;

    private Integer isUnderselling;

    /**
     * 同行寄售价
     */
    private BigDecimal preClinchPrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;
    /**
     * 详情id
     */
    private Integer saleLineId;
    private Integer saleLineState;
    private Integer saleId;

    private Integer deliveryLocationId;
    /**
     * 差额类型
     */
    private Integer balanceDirection;
    /**
     * 差额
     */
    private BigDecimal marginPrice;

    private Integer saleMode;


}
