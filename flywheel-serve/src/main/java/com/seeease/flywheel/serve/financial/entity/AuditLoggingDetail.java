package com.seeease.flywheel.serve.financial.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * @TableName audit_logging_detail
 */
@TableName(value = "audit_logging_detail", autoResultMap = true)
@Data
public class AuditLoggingDetail extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 应收应付表的id
     */
    private Integer apaId;
    private String serialNo;
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}