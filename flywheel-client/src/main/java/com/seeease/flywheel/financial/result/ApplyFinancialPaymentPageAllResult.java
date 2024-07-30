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
public class ApplyFinancialPaymentPageAllResult implements Serializable {


    private String serialNo;
//    private String customerName;
//    private Integer customerId;
//    private String accountName;
//    private String bank;
//    private String bankAccount;
//    private String contactName;
//
//    private Integer contactId;
//    private String contactAddress;
//    private String contactPhone;
    private Integer purchaseSubjectId;

    private BigDecimal pricePayment;
}
