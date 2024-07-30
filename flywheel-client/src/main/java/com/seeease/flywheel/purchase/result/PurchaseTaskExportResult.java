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
public class PurchaseTaskExportResult implements Serializable {

    /**
     * 采购需求单信息
     */

    private String createdTime;

    private String createdBy;

    /**
     * 业务单元
     */
    private String storeName;

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
     * 需求附件
     */
    private String taskAttachment;


    /**
     * 需求年份
     */
    private String deliveryTime;

    /**
     * 销售成交价
     */
    private BigDecimal clinchPrice;

    /**
     * 利率开始
     */
    private String clinchRate;

    private String purchaseJoinBy;

    /**
     * 采购单信息
     */
    private String stockSn;

    private String attachment;

    private BigDecimal purchasePrice;

    private String lineStateDesc;

    private String remarks;
}
