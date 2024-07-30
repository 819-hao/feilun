package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentDetailResult implements Serializable {

    private Integer id;

    /**
     * 打款类型
     */
    private Integer typePayment;

    private Integer payment;

    private String typePaymentName;

    /**
     * 客户id
     */
    private Integer customerContactsId;

    /**
     * 银行卡id
     */
    private Integer bankId;

    /**
     * 打款金额
     */
    private BigDecimal pricePayment;

    /**
     * 打款主体
     */
    private Integer subjectPayment;

    private String subjectPaymentName;

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
     *
     */
    private Integer state;

    /**
     * 父id为主键id
     */
    private Integer parentId;

    /**
     *
     */
    private String serialNo;

    /**
     *
     */
    private String customerName;

    /**
     *
     */
    private String customerPhone;

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

    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;

    private String result;

    /**
     * 正面身份证
     */
    private String frontIdentityCard;
    /**
     * 反面身份证
     */
    private String reverseIdentityCard;

    private String merchandiseNews;

    private String bankCustomerName;

    /**
     * 回购承诺函
     */
    private String buyBackTransfer;

    private Integer demanderStoreId;

    private String demanderStoreName;

    private List<String> batchPictureUrl;

    private Integer salesMethod;


    /**
     * 是否手动创建 0:否 1:是
     */
    private Integer manualCreation;

    /**
     * 是否新表 0:不是 1:是
     */
    private Integer brandNew;

    /**
     * 蜥蜴助手可用额度
     */
    private String availableCredit = "0";

    /**
     * 作废单数量
     */
    private Long obsoleteRecordCount;

    private Integer purchaseTaskId;

    /**
     * 取消按钮
     */
    private Boolean preButton;

    /**
     * 打款人
     */
    private String operator;

    /**
     * 打款时间
     */
    private String operateTime;

    private String shopName;

    private Integer shopId;

    private String originSerialNo;

    /**
     * 是否使用
     */
    private Integer whetherUse;
}
