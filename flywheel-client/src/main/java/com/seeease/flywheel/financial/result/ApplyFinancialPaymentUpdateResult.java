package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/1/29 15:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentUpdateResult implements Serializable {

    private Integer id;

    private String serialNo;
}
