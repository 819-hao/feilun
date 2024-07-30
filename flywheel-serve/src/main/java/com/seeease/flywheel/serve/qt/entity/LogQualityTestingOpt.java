package com.seeease.flywheel.serve.qt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.qt.convert.FixProjectMapperTypeHandler;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 质检记录
 *
 * @TableName log_quality_testing_opt
 */
@TableName(value = "log_quality_testing_opt", autoResultMap = true)
@Data
public class LogQualityTestingOpt extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
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

    /**
     * 质检状态
     */
    private QualityTestingStateEnum qtState;

    /**
     * 质检结论1入库
     * 2需维修
     * 3需退货
     */
    private QualityTestingConclusionEnum qtConclusion;

    /**
     * 质检来源
     */
    private BusinessBillTypeEnum qtSource;

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

    private Integer customerContactId;

    private Integer customerId;


    /**
     * 埋点时间
     */
    private Date taskArriveTime;

    /**
     * 维修内容
     */
    @TableField(typeHandler = FixProjectMapperTypeHandler.class)
    private List<FixProjectMapper> content;

    private Integer fixDay;

    /**
     * 是否是新表带
     */
    private Integer isNewStrap;


    /**
     * 维修费用
     */
    private BigDecimal fixMoney;


    /**
     * 转交状态 -1 默认值 0 待转交 1 转交
     */
    private Integer deliver;

    /**
     * 转交方 -1 默认值 0 维修 1 物流 2 待确认
     */
    private Integer deliverTo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}