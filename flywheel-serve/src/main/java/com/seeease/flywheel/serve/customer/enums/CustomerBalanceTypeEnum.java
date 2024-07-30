package com.seeease.flywheel.serve.customer.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 客户余额类型
 */
@Getter
@AllArgsConstructor
public enum CustomerBalanceTypeEnum {

    JS_AMOUNT(0, "寄售保证金"),
    ACCOUNT_BALANCE(1, "客户余额"),
    ;
    private Integer value;
    private String desc;

    public static CustomerBalanceTypeEnum fromCode(int value) {
        return Arrays.stream(CustomerBalanceTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
