package com.seeease.flywheel.financial.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业微信小程序---新增确认收货单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountReceiptConfirmAddRequest implements Serializable {

    /**
     * 关联的流水单id
     */
    private Integer financialStatementId;

    private Integer statementCompanyId;

    /**
     * 财务流水号
     */
    private String serialNo;

    /**
     * 收款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date collectionTime;

    /**
     * 流水归属id
     */
    private Integer shopId;

    /**
     * 收款主体id
     */
    private Integer subjectId;

    /**
     * 收款主体
     */
    private String subjectName;

    /**
     * 付款人
     */
    private String payer;


    /**
     * 实收金额
     */
    private BigDecimal fundsReceived;

    /**
     * 手续费
     */
    private BigDecimal procedureFee;

    /**
     * 收款金额
     */
    private BigDecimal receivableAmount;

    /**
     * 待核销金额
     */
    private BigDecimal waitAuditPrice;

    /**
     * 状态
     * ---AccountReceiptConfirmStatusEnum
     */
    private Integer status;

//---------客户信息

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
    private Integer contactId;

    /**
     * 联系人名称
     */
    private String contactName;

    /**
     * 联系人地址
     */
    private String contactAddress;

    /**
     * 联系方式
     */
    private String contactPhone;

    /**
     * 寄售保证金
     */
    private BigDecimal consignmentMargin;

    /**
     * 正常余额，账户余额
     */
    private BigDecimal accountBalance;

    /**
     * 收款性质
     * CollectionNatureEnum
     * 0寄售保证金 1客户余额
     */
    private Integer collectionNature;

    /**
     * 接口来源是否来自小程序
     */
    private Boolean miniAppSource;

    /**
     * 业务方式
     * FinancialSalesMethodEnum
     * 同行采购-备货---7、订金----6、批量----8
     * 个人销售-正常---1、订金----2、赠送---3
     * 同行销售-正常---1、寄售----4
     * 个人回收-仅回收----9
     * <p>
     * 10 取消采购计划
     */
    private Integer salesMethod;

    /**
     * 无用
     */
    private Integer type;


    /**
     * 总数
     * ---默认1
     */
    private Integer totalNumber;

    /**
     * 关联单号---关联销售单单号
     * ---采购退货、个人销售、同行销售
     */
    private String originSerialNo;

    /**
     * 订单分类
     * 2采购退货、3销售
     * OriginTypeEnum
     */
    private Integer originType;

    /**
     * 订单类型
     * 1-同行采购、7-个人销售、8-同行销售、3-个人回收
     * ---FinancialClassificationEnum
     */
    private Integer classification;

    /**
     * 收款类型:客户充值，消费收款
     * ---CollectionTypeEnum
     */
    private Integer collectionType;

    private String batchPictureUrl;


    private String createdBy;

    private Integer createdId;
}
