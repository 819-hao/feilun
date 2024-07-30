package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 企业微信小程序---新增确认收货单
 */
@Data
public class AccountReceiptConfirmCreateRequest implements Serializable {

    /**
     * 财务流水号
     */
    private String serialNo;

    /**
     * 流水归属id
     */
    private Integer shopId;

    /**
     * 收款主体id
     */
    private Integer statementCompanyId;

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
    /**
     * 打款人
     */
    private String payer;

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
     */
    private Integer salesMethod;

    /**
     * 总数
     * ---默认1
     */
    private Integer totalNumber;

    /**
     * 图片
     */
    private String batchPictureUrl;

    /**
     * 寄售保证金
     */
    private BigDecimal consignmentMargin;
    /**
     * 正常余额
     */
    private BigDecimal accountBalance;
}
