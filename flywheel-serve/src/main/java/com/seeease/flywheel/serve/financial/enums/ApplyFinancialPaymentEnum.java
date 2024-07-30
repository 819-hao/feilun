package com.seeease.flywheel.serve.financial.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 打款类型
 */
@Getter
@AllArgsConstructor
public enum ApplyFinancialPaymentEnum implements IStateEnum<Integer> {
    PAID(1, "采购打款"),
    REJECTED(2, "寄售结算"),
    CANCEL(3, "集采结算"),
    RETURN(4, "退款"),
    ;
    private Integer value;
    private String desc;

    public static ApplyFinancialPaymentEnum fromCode(int value) {
        return Arrays.stream(ApplyFinancialPaymentEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
