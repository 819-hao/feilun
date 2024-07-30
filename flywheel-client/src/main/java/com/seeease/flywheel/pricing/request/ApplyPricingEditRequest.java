package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 调价申请编辑
 *
 * @author Tiro
 * @date 2024/2/23
 */
@Data
public class ApplyPricingEditRequest implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 备注
     */
    private String remarks;
}
