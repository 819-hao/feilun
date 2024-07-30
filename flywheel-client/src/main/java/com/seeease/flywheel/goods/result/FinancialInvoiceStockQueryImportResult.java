package com.seeease.flywheel.goods.result;

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
public class FinancialInvoiceStockQueryImportResult implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;
    /**
     * 采购商品
     */
    private Integer goodsId;
    /**
     * 商品编号：XYW+8位阿拉伯数字
     */
    private String wno;
    /**
     * 成色
     */
    private String finess;
    private String belongName;
    private Integer belongId;
    /**
     * 附件
     */
    private String attachment;
    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 品牌id
     */
    private Integer brandId;
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

    private String warrantyDate;
    /**
     * 关联单号
     */
    private String originSerialNo;
    private String serialNo;

    private Integer stockStatus;

    /**
     * 成交价
     */
    private BigDecimal clinchPrice;

    private Integer lineId;
}
