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
public enum SaleReturnOrderTypeEnum implements IEnum<Integer> {
    TO_B_JS_TH(1, "同行寄售退货"),
    TO_C_XS_TH(2, "个人销售退货"),
    ;
    private Integer value;
    private String desc;

    public static SaleReturnOrderTypeEnum fromCode(int value) {
        return Arrays.stream(SaleReturnOrderTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
