package com.seeease.flywheel.customer.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 企业微信小程序--查询客户或联系人
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAndContractsPageQueryResult implements Serializable {

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


}
