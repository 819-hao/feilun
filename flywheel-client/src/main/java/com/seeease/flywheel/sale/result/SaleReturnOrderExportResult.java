package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturnOrderExportResult implements Serializable {

    /**
     * 单号
     */
    private String serialNo;
    /**
     * 销售单号
     */
    private String saleSerialNo;
    private Integer saleId;
    private Integer saleLineId;
    /**
     * 订单来源
     */
    private String shopName;

    private Integer shopId;

    /**
     * 第一销售人
     */
    private String firstSalesmanName;

    /**
     * 方式
     */
    private Integer saleMode;

    /**
     * 表id
     */
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
     * 公价
     */
    private BigDecimal pricePub;
    /**
     *
     */
    private BigDecimal gmvPerformance;

    /**
     * tob价格
     */
    private BigDecimal tobPrice;

    /**
     * toc价
     */
    private BigDecimal tocPrice;

    /**
     * 吊牌价
     */
    private BigDecimal tagPrice;

    /**
     * 成本价
     */
    private BigDecimal consignmentPrice;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;

    /**
     * 状态
     */
    private Integer saleReturnLineState;

    /**
     * 联系人
     */
    private String customerName;
    private Integer customerContactId;

    /**
     * 状态
     */
    private Integer saleReturnState;

    /**
     * 附件列表
     */
    private String attachment;
    /**
     * 经营权
     */
    private Integer rightOfManagement;
    private String rightOfManagementName;

    private Integer locationId;
    private String locationName;
}
