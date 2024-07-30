package com.seeease.flywheel.customer.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户余额申请退款
 */
@Data
public class CustomerBalanceRefundRequest implements Serializable {

    private Integer customerId;

    private String customerName;

    private Integer contactId;

    private String contactName;

    private String contactPhone;

    /**
     * 退款性质
     */
    private Integer refundType;

    /**
     * 退款金额
     */
    private BigDecimal clinchPrice;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行开户行
     */
    private String bankAccount;

    /**
     * 银行卡号
     */
    private String bankCard;

    /**
     * 收款账户名
     */
    private String bankCustomerName;

    /**
     * 打款主体
     * 存在多个打款主体，使用的是打款主体的名称
     */
    private String subjectPayment;

    /**
     * 金额归属人
     */
    private Integer userId;
}
