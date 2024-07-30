package com.seeease.flywheel.serve.financial.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * @TableName audit_logging
 */
@TableName(value = "audit_logging", autoResultMap = true)
@Data
public class AuditLogging extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 申请打款id
     */
    private Integer afpId;

    /**
     * 申请打款编号
     */
    private String afpSerialNo;
    /**
     * 确认收款单编号
     */
    private String arcSerialNo;
    /**
     * 关联单号
     */
    private String originSerialNo;

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
     * 订单分类
     */
    private OriginTypeEnum originType;
    /**
     * 类型
     */
    private ReceiptPaymentTypeEnum type;

    /**
     * 分类
     */
    private FinancialClassificationEnum classification;

    /**
     * 业务方式
     */
    private FinancialSalesMethodEnum salesMethod;

    /**
     *
     */
    private FinancialStatusEnum status;

    /**
     *
     */
    private Integer number;
    /**
     * 订单来源
     */
    private Integer shopId;

    /**
     *
     */
    private BigDecimal totalPrice;

    /**
     *
     */
    private Integer customerId;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    /**
     * 客户类别
     */
    private Integer customerType;

    /**
     * 申请人
     */
    private String applicant;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}