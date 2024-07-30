package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePlanExportResult implements Serializable {

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 需方id
     */
    private Integer demanderStoreId;
    /**
     * 创建门店
     */
    private Integer storeId;
    private String storeName;
    private String demanderStoreName;

    private String createdBy;

    private String createdTime;

    /**
     * 计划开始时间
     */
    private String planStartTime;
    /**
     * 计划结束时间
     */
    private String planEndTime;

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
     * 主图
     */
    private String image;
    /**
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 型号id
     */
    private Integer goodsId;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 采购数量
     */
    private Integer planNumber;
    /**
     * 当前行情价
     */
    private BigDecimal currentPrice;

    /**
     * 20年行情价
     */
    private BigDecimal twoZeroFullPrice;

    /**
     * 22年行情价
     */
    private BigDecimal twoTwoFullPrice;

    /**
     * 建议采购价
     */
    private BigDecimal suggestedPurchasePrice;
}
