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
public enum StocktakingSourceEnum implements IEnum<Integer> {
    RFID(1, "RFID"),
    ;

    private Integer value;
    private String desc;

    public static StocktakingSourceEnum fromCode(int value) {
        return Arrays.stream(StocktakingSourceEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
