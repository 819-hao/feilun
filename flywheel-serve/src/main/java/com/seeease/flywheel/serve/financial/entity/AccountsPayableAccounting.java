package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName accounts_payable_accounting
 */
@TableName(value = "accounts_payable_accounting", autoResultMap = true)
@Data
public class AccountsPayableAccounting extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String serialNo;

    /**
     * 关联单号
     */
    private String originSerialNo;

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
     * 审核说明
     */
    private String auditDescription;
    /**
     * 审核人
     */
    private String auditor;
    /**
     * 审核时间
     */
    private Date auditTime;
    /**
     * 订单分类
     */
    private OriginTypeEnum originType;
    /**
     * 订单种类
     */
    private ReceiptPaymentTypeEnum type;

    /**
     * 订单类型
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
     * 待核销金额
     */
    private BigDecimal waitAuditPrice;
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
     * 表身号
     */
    private String stockSn;

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

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 订单来源
     */
    private Integer shopId;

    /**
     * 需方id
     */
    private Integer demanderStoreId;

    /**
     * 实际采购人id
     */
    private Integer purchaseId;

    /**
     * 是否生成过申请打款单
     */
    private Integer whetherUse;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}