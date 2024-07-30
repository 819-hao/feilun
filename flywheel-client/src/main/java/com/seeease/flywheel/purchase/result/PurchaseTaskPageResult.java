package com.seeease.flywheel.purchase.result;

import com.seeease.flywheel.financial.result.ApplyFinancialPaymentDetailResult;
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
public class PurchaseTaskPageResult implements Serializable {

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 需求成色
     */
    private String finess;

    /**
     * 型号
     */
    private String model;


    /**
     * id
     */
    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

    private Integer taskState;

    private Integer taskNumber;

    private String createdBy;

    private String createdTime;

    /**
     * 需求型号id
     */
    private Integer goodsId;

    private String purchaseJoinBy;

    private Integer purchaseJoinId;

    private String storeName;

    private Integer storeId;

    private Integer applyFinancialPaymentId;

    private ApplyFinancialPaymentDetailResult applyFinancialPaymentDetailResult;

    /**
     * 公价
     */
    private BigDecimal pricePub;

}
