package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 */
@Data
public class FinancialStatementCreateRequest implements Serializable {

    /**
     * 财务流水号
     */
    private String serialNo;

    /**
     * 收款时间
     */
    private String collectionTime;

    /**
     * 门店id
     */
    private Integer shopId;

    /**
     * 收款主体
     */
    private Integer subjectId;

    /**
     * 付款人
     */
    private String payer;
    /**
     * 摘要
     */
    private String remarks;

    /**
     * 实收金额
     */
    private BigDecimal fundsReceived;

    /**
     * 手续费
     */
    private BigDecimal procedureFee;

    /**
     * 收款金额
     */
    private BigDecimal receivableAmount;

    /**
     * 待核销金额
     */
    private BigDecimal waitAuditPrice;

}
