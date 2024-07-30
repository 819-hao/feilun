package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wbh
 * @date 2023/5/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentObsoleteRecordPageResult implements Serializable {
    /**
     * 打款单id
     */
    private Integer id;
    /**
     * 创建人id
     */
    private Integer createdId;

    /**
     * 状态
     */
    private String state;

    /**
     * 门店
     */
    private Integer shopId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * afp_id
     */
    private Integer afpId;

    /**
     * 打款类型
     */
    private Integer typePayment;

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

    /**
     * 正面身份证
     */
    private String frontIdentityCard;

    /**
     * 反面身份证
     */
    private String reverseIdentityCard;

    /**
     * 商品信息
     */
    private String merchandiseNews;

    /**
     * 银行客户名称
     */
    private String bankCustomerName;

    /**
     * 是否使用
     */
    private Integer whetherUse;

    /**
     * 打款人
     */
    private String operator;

    /**
     * 打款时间
     */
    private Date operateTime;

    /**
     * 申请时间
     */
    private Date applicantTime;

    /**
     * 回购承诺函
     */
    private String repurchaseCommitment;

    /**
     * 业务方式
     */
    private Integer salesMethod;

    /**
     * 批量图片上传url
     */
    private List<String> batchPictureUrl;

    private Integer demanderStoreId;
    private String demanderStoreName;
}
