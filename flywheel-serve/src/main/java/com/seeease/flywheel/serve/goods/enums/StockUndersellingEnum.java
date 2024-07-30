package com.seeease.flywheel.serve.goods.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/5/6
 */
@Getter
@AllArgsConstructor
public enum StockUndersellingEnum implements IEnum<Integer> {
    NOT_ALLOW(0, "不允许破价销售"),
    ALLOW(1, "允许破价销售"),
//    ALLOW_TO_B(2, "ToB允许破价销售"),
//    ALLOW_TO_C(3, "ToC允许破价销售"),
    ;
    private Integer value;
    private String desc;

    public static StockUndersellingEnum fromValue(int value) {
        return Arrays.stream(StockUndersellingEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
