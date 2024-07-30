package com.seeease.flywheel.purchase.result;

import com.seeease.flywheel.financial.result.ApplyFinancialPaymentDetailResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTaskDetailsResult implements Serializable {

    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;
    private Integer seriesType;
    /**
     * 需求成色
     */
    private String finess;

    /**
     * 附件详情
     */
    private String attachment;

    /**
     * 型号
     */
    private String model;

    private BigDecimal pricePub;

    private String createdBy;

    private String createdTime;

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

    /**
     * 0 无 1 空白 1 有
     */
    private Integer isCard;

    private String warrantyDate;

    private Map<String, List<Integer>> attachmentMap;

    private Integer purchaseId;

    /**
     * 申请打款单id
     */
    private Integer applyFinancialPaymentId;

    /**
     * 需求型号id
     */
    private Integer goodsId;

    private String purchaseJoinBy;

    private Integer purchaseJoinId;

    private String storeName;

    private Integer storeId;


    /**
     * 实际数量
     */
    private Integer realityTaskNumber;

    private Integer taskState;


    //todo 申请打款单

    private PurchaseDetailsResult purchaseDetailsResult;


    private ApplyFinancialPaymentDetailResult applyFinancialPaymentDetailResult;

}
