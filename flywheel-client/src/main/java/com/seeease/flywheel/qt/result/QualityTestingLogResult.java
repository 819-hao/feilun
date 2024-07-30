package com.seeease.flywheel.qt.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 质检列表
 * @Date create in 2023/1/18 14:43
 */
@Data
public class QualityTestingLogResult implements Serializable {

    private String serialNo;

    private Integer qtState;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    private String storeWorkSerialNo;

    private Integer id;

    private Integer stockId;

    private Integer goodsId;

    private String brandName;

    private String seriesName;

    private String model;

    private String stockSn;

    private Integer fixOnQt;

    private String finess;

    private String week;

    /**
     * 表带材质
     */
    private String strapMaterial;

    /**
     * 表节
     */
    private String watchSection;

    private String attachment;

    private String createdBy;

    private String createdTime;

    private String taskArriveTime;

    private Integer qtSource;

    private Integer qtConclusion;

    private String remarks;

    //5.25
    /**
     * 是否是新表带
     */
    private Integer isNewStrap;

    /**
     * 异常说明
     */
    private String exceptionReason;

    /**
     * 退货原因
     */
    private String returnReasonId;

    /**
     * 退货原因说明
     */
    private String returnReason;

    /**
     * 退货图片
     */
    private String returnImgs;

    /**
     * 预计维修项目和天数+加钱
     */
    private String content;

    private Integer fixDay;

    private BigDecimal fixMoney;
}
