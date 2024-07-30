package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 采购任务创建
 * @Date create in 2023/10/25 15:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTaskCreateRequest implements Serializable {

    /**
     * 需求型号id
     */
    private Integer goodsId;

    /**
     * 需求成色
     */
    private String finess;

//    /**
//     * 采购附件详情
//     */
//    private Map<String, List<Integer>> attachmentMap;

//    /**
//     * 保卡日期
//     */
//    private String warrantyDate;

    /**
     * 0 单表 1 全套
     */
    private Integer isCard;

    /**
     * 采购人id
     */
    private Integer purchaseJoinId;

    /**
     * 需求日期开始
     */
    private String deliveryTimeStart;

    /**
     * 需求日期结束
     */
    private String deliveryTimeEnd;

    /**
     * 任务数量
     */
    private Integer taskNumber;

    /**
     * 销售成交价
     */
    private BigDecimal clinchPrice;

    /**
     * 利率开始
     */
    private BigDecimal clinchRateStart;

    /**
     * 利率结束
     */
    private BigDecimal clinchRateEnd;

    /**
     * 申请备注
     */
    private String remarks;
}
