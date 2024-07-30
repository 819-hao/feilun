package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/4/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderWarrantyPeriodUpdateResult implements Serializable {
    /**
     *
     */
    private Integer warrantyPeriod;
}
