package com.seeease.flywheel.serve.storework.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 商品情况：0-正常，1-缺货，2-商品实物不符
 *
 * @author Tiro
 * @date 2023/2/3
 */
@Getter
@AllArgsConstructor
public enum StoreWorkCommoditySituationEnum implements IEnum<Integer> {

    NORMAL(0, "正常"),
    MISSING(1, "缺货"),
    NONCONFORMING(2, "实物不符"),
    ;
    private Integer value;
    private String desc;

    public static StoreWorkCommoditySituationEnum fromCode(int value) {
        return Arrays.stream(StoreWorkCommoditySituationEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
