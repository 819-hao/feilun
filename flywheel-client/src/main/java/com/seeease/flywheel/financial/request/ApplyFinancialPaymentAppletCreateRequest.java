package com.seeease.flywheel.financial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentAppletCreateRequest implements Serializable {

    //*********************1.0
    /**
     * 打款类型
     * ApplyFinancialPaymentTypeEnum
     */
    private Integer typePayment;

    /**
     * 业务方式
     * FinancialSalesMethodEnum
     */
    private Integer salesMethod;

    /**
     * 需求门店
     */
    private Integer demanderStoreId;

    /**
     * 打款主体
     */
    private Integer subjectPayment;

    /**
     * 打款金额
     */
    private BigDecimal pricePayment;


    //*********************1.0


    //*********************2.0

    /**
     * 银行卡id
     */
    private Integer bankId;

    /**
     * 银行收款名   &&
     */
    private String bankCustomerName;


    /**
     * 客户id
     */
    private Integer customerContactsId;

    /**
     * 当类型是企业客户时候 从customerName 取
     * 当类型是个人客户时候 从customerCantactsName取
     */
    private String customerName;

    /**
     * 银行名称   &&
     */
    private String bankName;

    /**
     * 银行开户行     &&
     */
    private String bankAccount;

    /**
     * 银行卡号     &&
     */
    private String bankCard;

    //*********************2.0


    //********************3.0
    /**
     * 转让协议
     */
    private String agreementTransfer;

    /**
     * 回收定价记录
     */
    private String recoveryPricingRecord;

    /**
     * 正面身份证
     */
    private String frontIdentityCard;
    /**
     * 反面身份证
     */
    private String reverseIdentityCard;

    /**
     * 回购承诺函
     */
    private String repurchaseCommitment;

    /**
     * 图片上传 && 寄售协议
     */
    private String batchPictureUrl;

    //********************3.0

    /**
     * 备注
     */
    private String remarks;

    /**
     * 父id为主键id
     */
    private Integer parentId;

    /**
     * 商品信息
     */
    private String merchandiseNews;

    private Integer whetherUse = 0;

    private Integer shopId;


    /**
     * 是否新表 0:不是 1:是
     */
    private Integer brandNew;


    /**
     * 采购任务ID
     */
    private Integer purchaseTaskId;

    private Integer payment;

    private Integer manualCreation;

}
