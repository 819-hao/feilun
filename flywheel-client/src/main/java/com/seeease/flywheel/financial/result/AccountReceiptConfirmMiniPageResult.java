package com.seeease.flywheel.financial.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountReceiptConfirmMiniPageResult implements Serializable {

    private Integer id;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 关联单号
     */
    private String originSerialNo;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 打款账户
     */
    private String payer;
    private Integer statementCompanyId;
    /**
     * 收款账户
     */
    private String financialStatementCompany;
    /**
     * 订单类型
     * 1-同行采购、7-个人销售、8-同行销售、3-个人回收
     * ---FinancialClassificationEnum
     */
    private Integer classification;
    /**
     * 类型
     */
    private Integer type;

    /**
     * 业务方式
     */
    private Integer salesMethod;

    /**
     * 收款类型
     */
    private Integer collectionType;

    /**
     * 应收账款
     */
    private BigDecimal receivableAmount;

    /**
     * 已收金额---等于应收金额减去待绑定金额
     * 应收金额=个人销售单的总金额
     */
    private BigDecimal receivedAmount;

    private Integer createId;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;

}
