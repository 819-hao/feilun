package com.seeease.flywheel.serve.stocktaking.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/6/17
 */
@Getter
@AllArgsConstructor
public enum StocktakingStateEnum implements IEnum<Integer> {
    COMPLETE(1, "已完成"),
    ;
    private Integer value;
    private String desc;

    public static StocktakingStateEnum fromCode(int value) {
        return Arrays.stream(StocktakingStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}