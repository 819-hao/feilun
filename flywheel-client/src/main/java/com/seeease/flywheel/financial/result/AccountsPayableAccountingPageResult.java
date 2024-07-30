package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountsPayableAccountingPageResult implements Serializable {

    /**
     * 应收应付主键id
     */
    private Integer id;

    /**
     *
     */
    private String serialNo;

    /**
     * 关联单号
     */
    private String originSerialNo;

    /**
     * 申请打款id
     */
    private Integer afpId;

    /**
     * 申请打款编号
     */
    private String afpSerialNo;
    /**
     * 确认收款单编号
     */
    private String arcSerialNo;
    /**
     * 审核说明
     */
    private String auditDescription;
    /**
     * 审核人
     */
    private String auditor;
    /**
     * 审核时间
     */
    private String auditTime;
    /**
     * 订单种类
     */
    private Integer type;
    /**
     * 订单分类
     */
    private Integer originType;
    /**
     * 订单类型
     */
    private Integer classification;

    /**
     * 业务方式
     */
    private Integer salesMethod;

    /**
     *
     */
    private Integer status;

    /**
     *
     */
    private BigDecimal totalPrice;

    /**
     *
     */
    private Integer customerId;
    private String customerName;
    /**
     * 客户联系人
     */
    private Integer customerContactId;
    private String customerContactName;
    /**
     * 客户类别
     */
    private Integer customerType;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     *
     */
    private String brandName;

    /**
     *
     */
    private String seriesName;

    /**
     *
     */
    private String model;

    /**
     *
     */
    private Integer stockId;

    /**
     * 老的归属
     */
    private Integer belongId;

    private String belongName;

    /**
     * 创建人
     */
    private String applicant;

    private String createdTime;

    /**
     * 订单来源
     */
    private String shopName;

    private Integer shopId;

    /**
     * 需方id
     */
    private Integer demanderStoreId;
    /**
     * 需求门店
     */
    private String demanderStoreName;
    /**
     * 实际采购人id
     */
    private Integer purchaseId;

    /**
     * 实际采购人
     */
    private String purchaseBy;

    /**
     * 待核销金额
     */
    private BigDecimal waitAuditPrice;

    private String customerNameOrCustomerContactName;
}
