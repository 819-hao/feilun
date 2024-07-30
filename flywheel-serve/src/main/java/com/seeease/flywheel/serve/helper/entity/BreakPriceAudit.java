package com.seeease.flywheel.serve.helper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.helper.enmus.BreakPriceAuditStatusEnum;
import com.seeease.flywheel.serve.helper.enmus.BusinessCustomerAuditStatusEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.*;

import java.math.BigDecimal;

/**
 * @ Description   :  小程序破价审核
 * @ Author        :  西门 游
 * @ CreateDate    :  9/11/23
 * @ Version       :  1.0
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "app_break_price_audit", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BreakPriceAudit extends BaseDomain  {

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 商品id
     */
    private Integer stockId;
    /**
     * 成交价
     */
    private BigDecimal clinchPrice;
    /**
     * 原因
     */
    private String reason;
    /**
     * 编号
     */
    private String serial;

    /**
     * 驳回原因
     */
    private String failReason;
    /**
     * 状态
     */
    private BreakPriceAuditStatusEnum status;

    /**
     * 门店id
     */
    private Integer shopId;
}
