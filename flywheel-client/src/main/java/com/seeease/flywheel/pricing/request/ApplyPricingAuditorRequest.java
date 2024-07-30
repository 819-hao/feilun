package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 调价申请审核
 *
 * @author Tiro
 * @date 2024/2/23
 */
@Data
public class ApplyPricingAuditorRequest implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 审核状态
     */
    private Integer applyStatus;

    /**
     * 审核人
     */
    private String auditor;

    /**
     * 核定TOC价格
     */
    private BigDecimal approvedTocPrice;

    /**
     * 拒绝原因
     */
    private String rejectionReason;
}
