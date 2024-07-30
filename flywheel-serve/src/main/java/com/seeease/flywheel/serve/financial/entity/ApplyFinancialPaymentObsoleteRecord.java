package com.seeease.flywheel.serve.financial.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentStateEnum;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialSalesMethodEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 申请打款作废记录
 * @TableName apply_financial_payment_obsolete_record
 */
@TableName(value = "apply_financial_payment_obsolete_record", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplyFinancialPaymentObsoleteRecord extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * afp_id
     */
    private Integer afpId;

    /**
     * 打款类型
     */
    private ApplyFinancialPaymentTypeEnum typePayment;

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
     * 
     */
    private ApplyFinancialPaymentStateEnum state;

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
     * 申请人门店
     */
    private Integer shopId;

    /**
     * 需方id
     */
    private Integer demanderStoreId;

    /**
     * 业务方式
     */
    private FinancialSalesMethodEnum salesMethod;

    /**
     * 批量图片上传url
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> batchPictureUrl;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}