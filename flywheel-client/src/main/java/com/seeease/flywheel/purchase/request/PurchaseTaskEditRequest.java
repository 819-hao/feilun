package com.seeease.flywheel.purchase.request;

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
public class PurchaseTaskEditRequest implements Serializable {


    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 需求型号id
     */
    private Integer goodsId;

    /**
     * 需求成色
     */
    private String finess;
//
//    /**
//     * 采购附件详情
//     */
//    private Map<String, List<Integer>> attachmentMap;
//
//    /**
//     * 保卡日期
//     */
//    private String warrantyDate;

    /**
     * 0 无 1 有 2空白保卡
     */
    private Integer isCard;

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
