package com.seeease.flywheel.qt.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 质检判定
 * @Date create in 2023/1/17 15:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityTestingDecisionListResult implements Serializable {

    /**
     * 维修后质检 0 未维修 1 已维修
     */
    private Integer fixOnQt;

    /**
     * 维修单号
     */
    private String fixSerialNo;

    /**
     *
     */
    private Integer stockId;

    /**
     *
     */
    private String serialNo;

    /**
     * 关联采购需求单号
     */
    private String originApplyPurchaseSerialNo;

    private String applyPurchaseOwner;

    /**
     * 转交方
     */
    private Integer deliverTo;

    /**
     * 关联采购需求创建人id
     */
    private Integer originApplyPurchaseCreateId;

    //定价新增
    /**
     * 仓库单号
     */
    private String storeWorkSerialNo;

    private Integer workSource;

    private String originSerialNo;

    /**
     * 通知定价 质检完成以后会有定价异步
     * true 自动定价 false 手动定价
     */
    private Boolean autoPrice;

    //涉及到系统自建维修单

    /**
     * 是否接修
     */
    private Integer isRepair;

    /**
     * 是否分配
     */
    private Integer isAllot;
}
