package com.seeease.flywheel.customer.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询客户余额详情入参
 * ---兼容客户余额和寄售保证金
 */
@Data
public class CustomerBalanceDetailRequest implements Serializable {

    private Integer customerId;
}
