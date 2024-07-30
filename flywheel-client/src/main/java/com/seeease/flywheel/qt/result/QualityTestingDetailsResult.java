package com.seeease.flywheel.qt.result;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Mr. Du
 * @Description 质检详情
 * @Date create in 2023/1/18 14:43
 */
@Data
public class QualityTestingDetailsResult implements Serializable {

    private Integer id;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 作业单id
     */
    private Integer workId;

    /**
     * 质检单号
     */
    private String serialNo;

    /**
     * 仓库预作业单号
     */
    private String storeWorkSerialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 维修单id
     */
    private Integer fixId;

    private Integer qtState;

    /**
     * 质检结论1入库
     * 2需维修
     * 3需退货
     */
    private Integer qtConclusion;

    /**
     * 质检来源
     */
    private Integer qtSource;

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
     * 是否拒绝维修
     */
    private Integer refuseFix;

    /**
     * 是否维修
     */
    private Integer fixFlag;

    /**
     * 维修费用
     */
    private Double fixMoney;

    /**
     * 维修建议
     */
    private String fixAdvise;

    /**
     * 振频字段
     */
    private String vibrationFrequencyQuality;

    /**
     * 瞬时日差字段
     */
    private String instantaneousWorse;

    /**
     * 摆幅字段
     */
    private String swing;

    /**
     * 异常id
     */
    private Integer exceptionReasonId;

    /**
     * 异常说明
     */
    private String exceptionReason;

    /**
     * 成色
     */
    private String finess;

    /**
     * 腕周
     */
    private String week;

    /**
     * 表带材质
     */
    private String strapMaterial;

    /**
     * 表节
     */
    private String watchSection;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系人id
     */
    private Integer customerContactId;

    /**
     * 埋点时间
     */
    private Date taskArriveTime;
}
