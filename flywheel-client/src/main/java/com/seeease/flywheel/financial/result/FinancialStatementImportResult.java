package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialStatementImportResult implements Serializable {
    /**
     * 财务流水号
     */
    private String serialNo;
}