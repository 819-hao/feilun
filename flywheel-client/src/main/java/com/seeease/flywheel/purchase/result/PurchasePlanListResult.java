package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePlanListResult implements Serializable {

    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 采购数量
     */
    private Integer purchaseNumber;

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
     * 允许修改时间
     */
    private String enableChangeTime;
    /**
     * 选品时间
     */
    private String selectionTime;
    /**
     * 需求提交时间
     */
    private String demandStartTime;
    /**
     * 预估到货时间
     */
    private String estimatedDeliveryTime;
    /**
     * 业务类型：业务类型：0-默认其他,1-新表集采
     */
    private Integer businessType;
}
