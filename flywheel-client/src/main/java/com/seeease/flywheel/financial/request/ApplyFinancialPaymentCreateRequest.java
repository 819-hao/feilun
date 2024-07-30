package com.seeease.flywheel.financial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentCreateRequest implements Serializable {

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

    /**
     * 商品信息
     */
    private String merchandiseNews;
    /**
     * 银行收款名
     */
    private String bankCustomerName;

    private Integer whetherUse = 0;

    /**
     * 回购承诺函
     */
    private String buyBackTransfer;

    private Integer shopId;
    private Integer demanderStoreId;
    private Integer salesMethod;

    /**
     * 批量图片上传url
     */
    private List<String> batchPictureUrl;

    /**
     * 是否新表 0:不是 1:是
     */
    private Integer brandNew;
    /**
     * 是否手动创建 0:否 1:是
     */
    private Integer manualCreation = 0;

    /**
     * 退款类型
     */
    private Integer refundType;

    /**
     * 采购任务ID
     */
    private Integer purchaseTaskId;

    /**
     * 打款类型
     */
    private Integer payment;

    /**
     * 关联单号
     */
    private String originSerialNo;

}
