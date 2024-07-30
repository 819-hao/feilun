package com.seeease.flywheel.serve.financial.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收款性质枚举值
 */
@Getter
@AllArgsConstructor
public enum CollectionNatureEnum {

    /**
     * 寄售保证金
     */
    CONSIGNMENT_MARGIN(0),

    /**
     * 客户余额
     */
    ACCOUNT_BALANCE(1),

    /**
     * 正常销售
     */
    SALE(2),

    /**
     * 采购退款
     */
    PURCHASE_RETURN(3),
    ;

    private final int value;
}
