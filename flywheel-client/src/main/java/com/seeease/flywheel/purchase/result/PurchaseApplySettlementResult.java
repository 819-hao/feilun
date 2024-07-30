package com.seeease.flywheel.purchase.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
public class PurchaseApplySettlementResult implements Serializable {

    /**
     * 打款类型
     */
    private Integer typePayment;

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
     * 银行卡id
     */
    private Integer bankId;

    /**
     * 打款金额
     */
    private BigDecimal pricePayment;
    /**
     * 结算金额
     */
    private BigDecimal settlementPrice;
    /**
     * 打款主体
     */
    private Integer subjectPayment;

    /**
     * 转让协议
     */
    private String agreementTransfer;
    /**
     * 回购协议
     */
    private String buyBackTransfer;

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

    private Integer stockId;
    private Integer purchaseSource;
    private Integer purchaseId;

    private String stockSn;

    /**
     * 本次销售价（个人回购，个人置换）
     */
    private BigDecimal salePrice;

    /**
     * 总采购成本
     */
    private BigDecimal totalPurchasePrice;

    /**
     * 采购单号
     */
    private String serialNo;

    private Integer purchaseMode;

    private Integer demanderStoreId;

    private List<String> batchPictureUrl;

    private Integer lineId;

    /**
     * 创建时间
     */
    private Date time;
}
