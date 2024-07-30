package com.seeease.flywheel.serve.helper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.helper.enmus.BreakPriceAuditStatusEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.*;

import java.math.BigDecimal;

/**
 * @ Description   :  小程序破价审核
 * @ Author        :  西门 游
 * @ CreateDate    :  9/11/23
 * @ Version       :  1.0
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "app_break_price_audit_history", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BreakPriceAuditHistory extends BaseDomain  {

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 审核主表id
     */
    private Integer auditId;
    /**
     * 驳回原因
     */
    private String failReason;
    /**
     * 状态
     */
    private BreakPriceAuditStatusEnum changeStatus;
}
