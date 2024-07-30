package com.seeease.flywheel.serve.fix.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.fix.convert.FixProjectMapperTypeHandler;
import com.seeease.flywheel.serve.fix.enums.FixStateEnum;
import com.seeease.flywheel.serve.fix.enums.FixTypeEnum;
import com.seeease.flywheel.serve.fix.enums.FlowGradeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 维修单
 *
 * @TableName bill_fix
 */
@TableName(value = "bill_fix", autoResultMap = true)
@Data
public class BillFix2 extends BaseDomain implements TransitionStateEntity, Serializable {
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
     * 单号
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
     * 维修状态
     */
    @TransitionState
    private FixStateEnum fixState;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系人id
     */
    private Integer customerContactId;

    /**
     * 维修金额
     */
    private BigDecimal fixMoney;

    /**
     * 维修天数
     */
    private Integer fixDay;

    /**
     * 维修次数
     */
    private Integer fixTimes;

    /**
     * 随表附件
     */
    private String withAttachment;

    /**
     * 图片
     */
    private String imgList;

    /**
     * 维修内容
     */
    @TableField(typeHandler = FixProjectMapperTypeHandler.class)
    private List<FixProjectMapper> content;

    /**
     * 定金
     */
    private BigDecimal earnestMoney;

    /**
     * 送外维修原因
     */
    private String sendOutReason;

    /**
     * 送外维修公司名称
     */
    private String sendOutCompany;

    /**
     * 送外维修联系人
     */
    private String sendOutContact;

    /**
     * 维修类型-内部-送外
     */
    private FixTypeEnum fixType;

    /**
     * 0不合格1合格
     */
    private Integer testResult;

    /**
     * 维修来源 1采购2其他
     */
    private BusinessBillTypeEnum fixSource;

    /**
     * 是否返修 -1全部 1是 2是不是
     */
    private Integer repairFlag;

    /**
     * 配件金款
     */
    private BigDecimal partsMoney;

    /**
     * 维修建议
     */
    private String fixAdvise;

    /**
     * 是否有瑕疵
     */
    private Integer defectOrNot;

    /**
     * 瑕疵说明
     */
    private String defectDescription;

    /**
     * 特殊加急
     */
    private Integer specialExpediting;

    /**
     * 流转等级
     */
    private FlowGradeEnum flowGrade;

    /**
     * 维修师id
     */
    private Integer maintenanceMasterId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 埋点时间
     */
    private Date taskArriveTime;
    /**
     * 接修时间
     */
    private Date repairTime;

    /**
     * 超时变更后时间
     */
    private String finishTime;

    /**
     * 超时原因
     */
    private String timeoutMsg;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}