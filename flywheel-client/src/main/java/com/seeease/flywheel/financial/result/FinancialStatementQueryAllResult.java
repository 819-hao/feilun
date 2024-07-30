package com.seeease.flywheel.financial.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialStatementQueryAllResult implements Serializable {


    /**
     * $column.columnComment
     */
    private Integer id;

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
     * 流水归属id
     */
    private Integer shopId;
    /**
     * 流水归属
     */
    private String shopName;

    /**
     * 收款主体id
     */
    private Integer subjectId;
    /**
     * 收款主体
     */
    private String subjectName;

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

    /**
     * 审核人
     */
    private String auditName;

    /**
     * 审核时间
     */
    private String auditTime;

    /**
     * 审核说明
     */
    private String auditDescription;

    /**
     * 状态
     */
    private Integer status;
}
