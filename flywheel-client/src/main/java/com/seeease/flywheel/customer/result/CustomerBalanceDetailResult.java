package com.seeease.flywheel.customer.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户余额详情
 */
@Data
public class CustomerBalanceDetailResult implements Serializable {

    /**
     * 联系人姓名
     */
    private String customerContactName;

    /**
     * 金额
     */
    private BigDecimal amount;

    private Integer createId;

    private Integer userId;
}
