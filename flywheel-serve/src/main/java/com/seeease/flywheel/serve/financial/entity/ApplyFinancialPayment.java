package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentStateEnum;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialSalesMethodEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @TableName apply_financial_payment
 */
@TableName(value = "apply_financial_payment", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplyFinancialPayment extends BaseDomain implements TransitionStateEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 打款类型
     */
    //@ValidValue(message = "打款类型不能为空")
    private ApplyFinancialPaymentTypeEnum typePayment;

    /**
     * 客户id
     */
    //@ValidValue(message = "客户ID不能为空")
    private Integer customerContactsId;

    /**
     * 银行卡id
     */
    //@ValidValue(message = "银行卡ID不能为空")
    private Integer bankId;

    /**
     * 打款金额
     */
    //@ValidValue(message = "打款金额不能为空")
    private BigDecimal pricePayment;

    /**
     * 打款主体
     */
    private Integer subjectPayment;

    /**
     * 转让协议
     */
    //@ValidValue(message = "转让协议不能为空")
    private String agreementTransfer;

    /**
     * 回收定价记录
     */
    //@ValidValue(message = "回收记录 不能为空")
    private String recoveryPricingRecord;

    /**
     * 备注
     */
    private String remarks;

    /**
     *
     */
    @TransitionState
    private ApplyFinancialPaymentStateEnum state;

    /**
     * 父id为主键id
     */
    private Integer parentId;
    /**
     * 是否使用过
     */
    private WhetherEnum whetherUse;

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
     * 正面身份证
     */
    private String frontIdentityCard;
    /**
     * 反面身份证
     */
    private String reverseIdentityCard;

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
    private Date operateTime;
    private Date applicantTime;
    private String operator;
    private Integer shopId;
    private Integer demanderStoreId;
    private FinancialSalesMethodEnum salesMethod;
    /**
     * 回购承诺函
     */
    private String repurchaseCommitment;

    /**
     * 是否新表 0:不是 1:是
     */
    private Integer brandNew;
    /**
     * 是否手动创建 0:否 1:是
     */
    private Integer manualCreation;
    /**
     * 是否重复
     */
    private Integer whetherRepeat;
    /**
     * 批量图片上传url
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> batchPictureUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;

    /**
     * 退款类型
     */
    private Integer refundType;

    private Integer payment;

    private String originSerialNo;
}