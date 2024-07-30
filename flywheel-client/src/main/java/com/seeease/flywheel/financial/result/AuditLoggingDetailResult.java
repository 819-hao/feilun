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
public class AuditLoggingDetailResult implements Serializable {
    /**
     * 应收应付表的id
     */
    private Integer apaId;
    private String serialNo;
    private Integer number;
    private String applicant;
    private String applicantTime;
    private String originSerialNo;
    /**
     *
     */
    private BigDecimal totalPrice;
    /**
     *
     */
    private Integer auditLoggingId;

    /**
     * 预付金额
     */
    private BigDecimal prePaidAmount;

    /**
     * 预收金额
     */
    private BigDecimal preReceiveAmount;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 应收金额
     */
    private BigDecimal amountReceivable;

    /**
     * 应付金额
     */
    private BigDecimal amountPayable;

    /**
     *
     */
    private String brandName;

    /**
     *
     */
    private String seriesName;

    /**
     *
     */
    private String model;

    /**
     *
     */
    private Integer stockId;

    /**
     * 老的归属
     */
    private Integer belongId;
    private String belongName;
}
