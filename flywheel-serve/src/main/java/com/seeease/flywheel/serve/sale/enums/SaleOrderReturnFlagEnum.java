package com.seeease.flywheel.serve.sale.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SaleOrderReturnFlagEnum implements IEnum<Integer> {

    DEFAULT(0,"默认"),
    CDTH(1, "错单退货"),
    TK(2, "退款"),
    ;
    private Integer value;
    private String desc;

    public static SaleOrderReturnFlagEnum fromCode(int value) {
        return Arrays.stream(SaleOrderReturnFlagEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
