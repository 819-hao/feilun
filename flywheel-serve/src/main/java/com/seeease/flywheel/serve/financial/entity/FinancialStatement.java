package com.seeease.flywheel.serve.financial.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * 财务流水记录
 *
 * @TableName financial_statement
 */
@TableName(value = "financial_statement", autoResultMap = true)
@Data
public class FinancialStatement extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 财务流水号
     */
    private String serialNo;

    /**
     * 收款时间
     */
    private Date collectionTime;

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

    /**
     * 审核人
     */
    private String auditName;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核说明
     */
    private String auditDescription;

    /**
     *
     */
    private FinancialStatusEnum status;

    private static final long serialVersionUID = 1L;
}