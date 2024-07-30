package com.seeease.flywheel.serve.sale.enums;

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
public enum SaleOrderTypeEnum implements IEnum<Integer> {
    TO_B_JS(1, "同行"),
    TO_C_XS(2, "个人"),
    ;
    private Integer value;
    private String desc;

    public static SaleOrderTypeEnum fromCode(int value) {
        return Arrays.stream(SaleOrderTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
