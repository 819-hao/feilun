package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 申请结算
 *
 * @author Tiro
 * @date 2023/1/9
 */
@Data
public class PurchaseApplySettlementRequest implements Serializable {

    /**
     * 采购单id
     */
    private Integer purchaseId;

    private Integer storeId;

    /**
     * 开户行 开户地址
     */
    private String bank;

    /**
     * 银行名称 中国建行
     */
    private String accountName;

    /**
     * 银行账号 就是卡号
     */
    private String bankAccount;

    /**
     * 银行客户名称
     */
    private String bankCustomerName;

    /**
     * 正面身份证
     */
    private List<String> frontIdentityCard;
    /**
     * 反面身份证
     */
    private List<String> reverseIdentityCard;

    /**
     * 转让协议
     */
    private List<String> agreementTransfer;

    /**
     * 回收定价记录
     */
    private List<String> recoveryPricingRecord;
    /**
     * 回购协议
     */
    private List<String> buyBackTransfer;

    /**
     * 结算金额
     */
    private BigDecimal settlementPrice;

    /**
     * 结算其他图片
     */
    private List<String> otherPicture;

    private BigDecimal planFixPrice;

    private BigDecimal oldPlanFixPrice;

    private BigDecimal watchbandReplacePrice;

    private BigDecimal oldWatchbandReplacePrice;

    /**
     * 当类型是企业客户时候 从customerName 取
     * 当类型是个人客户时候 从customerCantactsName取
     */
    private String customerName;
}
