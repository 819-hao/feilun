package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 调价申请创建
 *
 * @author Tiro
 * @date 2024/2/23
 */
@Data
public class ApplyPricingCreateRequest implements Serializable {

    /**
     * 商品id
     */
    private Integer stockId;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 建议TOC价格
     */
    private BigDecimal suggestedTocPrice;

}
