package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceOrderTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.financial.enums.InvoiceOriginEnum;
import com.seeease.flywheel.serve.financial.enums.InvoiceTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName financial_invoice
 */
@TableName(value = "financial_invoice", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FinancialInvoice extends BaseDomain {
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
     * 原关联开票单号
     */
    private String originalInvoiceSerialNo;

    /**
     * 开票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 开票号码
     */
    private String invoiceNumber;

    /**
     * 开票时间
     */
    private Date invoiceTime;

    /**
     * 开票人
     */
    private String invoiceUser;
    private String batchPictureUrl;
    private String result;
    /**
     *
     */
    private Integer totalNumber;

    /**
     * 开票主体
     */
    private Integer invoiceSubject;

    /**
     * 客户联系人id
     */
    private Integer customerContactsId;
    private Integer customerId;

    private String customerEmail;
    private String customerName;
    /**
     * 付款方式
     */
    private Integer paymentMethod;
    /**
     * 订单类型
     */
    private FinancialInvoiceOrderTypeEnum orderType;

    /**
     * 开票抬头
     */
    private String invoiceTitle;

    /**
     * 单位税号
     */
    private String unitTaxNumber;

    /**
     * 发票类型
     */
    private InvoiceTypeEnum invoiceType;

    /**
     * 开票类型
     */
    private InvoiceOriginEnum invoiceOrigin;

    /**
     *
     */
    private FinancialInvoiceStateEnum state;
    private String remarks;
    /**
     * 申请人门店
     */
    private Integer shopId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}