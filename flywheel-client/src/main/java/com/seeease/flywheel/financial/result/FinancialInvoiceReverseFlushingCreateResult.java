package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/5/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInvoiceReverseFlushingCreateResult implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 单号
     */
    private String serialNo;

}
