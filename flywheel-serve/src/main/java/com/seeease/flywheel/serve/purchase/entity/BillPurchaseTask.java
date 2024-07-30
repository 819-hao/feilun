package com.seeease.flywheel.serve.purchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.purchase.enums.TaskStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 采购需求任务
 *
 * @TableName bill_purchase_task
 */
@TableName(value = "bill_purchase_task", autoResultMap = true)
@Data
public class BillPurchaseTask extends BaseDomain implements Serializable, TransitionStateEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 状态流转
     */
    @TransitionState
    private TaskStateEnum taskState;

    /**
     * 需求型号id
     */
    private Integer goodsId;

    /**
     * 需求成色
     */
    private String finess;

//    /**
//     * 需求附件
//     */
//    private String attachmentList;

    /**
     * 附件列表
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<Integer> attachmentList;

    /**
     * 保卡日期
     */
    private String warrantyDate;

    /**
     * 0 无 1 有 2空白保卡
     */
    private Integer isCard;

    /**
     * 采购人id
     */
    private Integer purchaseId;

    /**
     * 发起门店id
     */
    private Integer storeId;

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
     * 采购交接人
     */
    private Integer purchaseJoinId;

    /**
     * 申请打款单id
     */
    private Integer applyFinancialPaymentId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}