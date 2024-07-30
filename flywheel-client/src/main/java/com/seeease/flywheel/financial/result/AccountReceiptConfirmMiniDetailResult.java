package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountReceiptConfirmMiniDetailResult implements Serializable {
    private Integer id;

    /**
     * 单号---确认收款单号
     */
    private String serialNo;

    /**
     * 关联单号---关联销售单单号
     */
    private String originSerialNo;
    /**
     * 上传图片
     */
    private String batchPictureUrl;
    /**
     * 驳回原因
     */
    private String rejectionCause;

    /**
     * 状态
     * AccountReceiptConfirmStatusEnum
     * ---小程序完成新建打款单，则自动已核销
     */
    private Integer status;
    private Integer statementCompanyId;
    private String statementCompanyName;
    private Integer shopId;
    private String shopName;
    /**
     * 付款人
     */
    private String payer;

    /**
     * 收款金额
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

    /**
     * 收款类型:客户充值，消费收款
     * ---CollectionTypeEnum
     */
    private Integer collectionType;

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
     * 订单类型
     * 1-同行采购、7-个人销售、8-同行销售、3-个人回收
     * ---FinancialClassificationEnum
     */
    private Integer classification;

    private String createdBy;

    private String createdTime;

    /**
     * 寄售保证金
     */
    private BigDecimal consignmentMargin;
    /**
     * 正常余额
     */
    private BigDecimal accountBalance;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系人电话
     */
    private String phone;


    // 后端计算期末值
    private BigDecimal consignmentOpeningBalance;

    private BigDecimal consignmentClosingBalance;


    private BigDecimal accountOpeningBalance;

    private BigDecimal accountClosingBalance;

}
