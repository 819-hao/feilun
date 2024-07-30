package com.seeease.flywheel.pricing.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2024/2/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyPricingAuditorResult implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 是否自动拒绝
     */
    private boolean autoRefuse;
}
