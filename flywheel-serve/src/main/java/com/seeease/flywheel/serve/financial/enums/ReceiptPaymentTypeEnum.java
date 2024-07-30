package com.seeease.flywheel.serve.financial.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author wbh
 * @date 2023/3/6
 */
@Getter
@AllArgsConstructor
public enum ReceiptPaymentTypeEnum implements IEnum<Integer> {
    PRE_PAID_AMOUNT(1, "预付金额"),
    PRE_RECEIVE_AMOUNT(2, "预收金额"),
    AMOUNT_RECEIVABLE(3, "应收金额"),
    AMOUNT_PAYABLE(4, "应付金额"),


    ;
    private Integer value;
    private String desc;

    public static ReceiptPaymentTypeEnum fromCode(int value) {
        return Arrays.stream(ReceiptPaymentTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
