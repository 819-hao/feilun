package com.seeease.flywheel.customer.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CustomerBalancePageResult implements Serializable {

    private Integer customerId;

    /**
     * 所属公司
     */
    private String customerName;

    /**
     * 寄售保证金
     */
    private BigDecimal consignmentMargin;

    /**
     * 寄售货值
     * ---反向查找寄售货值
     */
    private BigDecimal consignmentGoods;

    /**
     * 保证金余额
     * ---寄售保证金 = 寄售货值+ 保证金余额
     */
    private BigDecimal insuranceFee;

    /**
     * 正常余额 ?
     */
    private BigDecimal accountBalance;

}
