package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
public class ApplyFinancialPaymentUpdateRequest implements Serializable {

    private Integer id;

    private Integer state;

    /**
     * 打款类型
     */
    private Integer typePayment;

    private Integer salesMethod;

    /**
     * 打款金额
     */
    private BigDecimal pricePayment;

    /**
     * 打款主体
     */
    private Integer subjectPayment;

    /**
     * 转让协议
     */
    private String agreementTransfer;

    /**
     * 回收定价记录
     */
    private String recoveryPricingRecord;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 父id为主键id
     */
    private Integer parentId;

    //----------------------------客户信息-------------------------

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
     * 正面身份证
     */
    private String frontIdentityCard;
    /**
     * 反面身份证
     */
    private String reverseIdentityCard;

    //----------------------------账户信息--------------------------

    /**
     * 银行卡id
     */
    private Integer bankId;

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

    private String merchandiseNews;

    private String bankCustomerName;

    /**
     * 回购承诺函
     */
    private String buyBackTransfer;

    /**
     * 回购承诺函
     */
    private String repurchaseCommitment;

    private Integer demanderStoreId;

    /**
     * 是否新表 0:不是 1:是
     */
    private Integer brandNew;

    /**
     * 图片上传
     */
    private String batchPictureUrl;
}
