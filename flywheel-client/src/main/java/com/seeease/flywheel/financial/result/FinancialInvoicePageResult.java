package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInvoicePageResult implements Serializable {

    private String createdBy;

    private String customerName;

    private Integer orderType;

    private BigDecimal invoiceAmount;

    private Integer invoiceOrigin;

    private Integer id;
}
