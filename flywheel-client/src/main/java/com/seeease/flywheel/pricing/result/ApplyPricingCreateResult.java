package com.seeease.flywheel.pricing.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2024/2/23
 */
@Data
public class ApplyPricingCreateResult implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 申请批次号
     */
    private String serialNo;
}
