package com.seeease.flywheel.financial.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountReceiptConfirmPageResult implements Serializable {

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
     * 门店
     */
    private Integer shopId;

    /**
     * 总数
     */
    private Integer totalNumber;

    /**
     * 收款类型
     */
    private Integer collectionType;

    /**
     * 收款性质
     */
    private Integer collectionNature;

    private Integer originType;

    /**
     * 业务方式
     */
    private Integer salesMethod;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 分类
     */
    private Integer classification;

    /**
     * 应收账款
     */
    private BigDecimal receivableAmount;

    /**
     * 待绑定金额
     */
    private BigDecimal waitBindingAmount;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 联系人id
     */
    private Integer customerContractId;

    /**
     * 联系人名称
     */
    private String customerContractName;

    private Integer createdId;

    private String createdBy;

    private String createdTime;

    /**
     * 订单来源
     */
    private String shopName;
    /**
     * 图片
     */
    private String batchPictureUrl;
    private Integer statementCompanyId;
    /**
     * 收款账户
     */
    private String financialStatementCompany;
    /**
     * 打款账户
     */
    private String payer;

    /**
     * 审核说明
     */
    private String auditDescription;


    // 关联单号销售类型
    private Integer originSerialNoSaleType;

    /**
     * 关联单号销售三方单号
     */
    private String originSerialNoSaleBizOrderCode;

    /**
     * 关联单号销售三方单号 渠道
     */
    private Integer originSerialNoSaleChannel;



    //添加一些小程序详情的业务
    /**
     * 地址
     */
    private String address;

    /**
     * 联系人电话
     */
    private String phone;


    // 后端计算期末值
    /**
     * 寄售期初
     */
    private BigDecimal consignmentOpeningBalance;

    /**
     * 寄售期末
     */
    private BigDecimal consignmentClosingBalance;

    /**
     * 余额期初
     */
    private BigDecimal accountOpeningBalance;

    /**
     * 余额期末
     */
    private BigDecimal accountClosingBalance;


    /**
     * 寄售保证金
     */
    private BigDecimal consignmentMargin;
    /**
     * 正常余额
     */
    private BigDecimal accountBalance;


    private String customerNameOrCustomerContactName;
}
