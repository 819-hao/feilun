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
public class ApplyFinancialPaymentPageResult implements Serializable {


    private Integer id;

    private String serialNo;

    private String originSerialNo;

    private String shopName;

    private Integer shopId;

    /**
     * 打款类型
     */
    private Integer payment;

    private Integer typePayment;

    private Integer salesMethod;

    private String demanderStoreName;

    private Integer demanderStoreId;

    /**
     * 关联表
     */
    private Integer number;
    /**
     * 银行收款名   &&
     */
    private String bankCustomerName;

    private BigDecimal pricePayment;

    /**
     * 是否使用
     */
    private Integer whetherUse;

    /**
     * 状态
     */
    private Integer state;


    private String createdBy;

    private String createdTime;

    private String applicant;

    private String applicantTime;

    private Integer subjectPayment;

    private String subjectPaymentName;

    private String customerNameOrCustomerContactName;

    private Integer customerContactsId;

    private Integer customerType;

}
