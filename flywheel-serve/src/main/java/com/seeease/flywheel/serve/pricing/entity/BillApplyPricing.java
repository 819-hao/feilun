package com.seeease.flywheel.serve.pricing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.pricing.enums.ApplyPricingStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 调价申请
 *
 * @TableName bill_apply_pricing
 */
@TableName(value = "bill_apply_pricing")
@Data
public class BillApplyPricing extends BaseDomain implements TransitionStateEntity, Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 申请批次号
     */
    private String serialNo;

    /**
     * 商品id
     */
    private Integer stockId;

    /**
     * 审核状态
     */
    @TransitionState
    private ApplyPricingStateEnum applyStatus;

    /**
     * 申请门店id
     */
    private Integer applyShopId;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 建议TOC价格
     */
    private BigDecimal suggestedTocPrice;

    /**
     * 审核人
     */
    private String auditor;

    /**
     * 审核时间
     */
    private Date approvedTime;

    /**
     * 核定TOC价格
     */
    private BigDecimal approvedTocPrice;

    /**
     * 核定吊牌价
     */
    private BigDecimal approvedTagPrice;

    /**
     * 拒绝原因
     */
    private String rejectionReason;

    /**
     * 备注
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}