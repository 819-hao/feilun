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
public class ApplyFinancialPaymentPageQueryByConditionResult implements Serializable {

    private Integer id;
    private Integer typePayment;
    private String serialNo;
    private String purchaseSerialNo;
    private BigDecimal pricePayment;
    private Integer state;
    /**
     * 打款人
     */
    private String operator;
    /**
     * 打款时间
     */
    private String operateTime;
    private String applicant;
    private String applicantTime;
    private Integer whetherUse;

    private Integer salesMethod;
    private Integer shopId;
    private String shopName;
    private Integer demanderStoreId;
    private String demanderStoreName;

    /**
     *
     */
    private String customerName;

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

    private String bankCustomerName;

    /**
     * 是否重复
     */
    private Integer whetherRepeat;

    /**
     * 作废单数量
     */
    private Long obsoleteRecordCount;
    private Integer saleMode;
}
