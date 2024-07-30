package com.seeease.flywheel.financial.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountReceiptConfirmDetailResult implements Serializable {

    /**
     * 流水表主键id
     */
    private Integer financialStatementId;

    /**
     * 财务流水号
     */
    private String serialNo;

    /**
     * 收款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date collectionTime;
    /**
     * 付款人
     */
    private String payer;

    /**
     * 实收金额
     */
    private BigDecimal fundsReceived;

    /**
     * 待绑定金额，剩余金额
     */
    private BigDecimal waitAuditPrice;

    /**
     * 收款金额
     */
    private BigDecimal receivableAmount;
}
