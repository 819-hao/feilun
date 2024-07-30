package com.seeease.flywheel.serve.purchase.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 付款方式
 *
 * @author Tiro
 * @date 2023/2/28
 */
@Getter
@AllArgsConstructor
public enum PurchasePaymentMethodEnum implements IEnum<Integer> {

    FK_DJ(1, "订金"),
    FK_QK(2, "全款"),
    FK_CE(3, "差额"),

    ;
    private Integer value;
    private String desc;

    public static PurchasePaymentMethodEnum fromCode(int value) {
        return Arrays.stream(PurchasePaymentMethodEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}