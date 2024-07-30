package com.seeease.flywheel.serve.stocktaking.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 1-盘盈，2-盘亏
 *
 * @author Tiro
 * @date 2023/6/17
 */
@Getter
@AllArgsConstructor
public enum StocktakingLineStateEnum implements IEnum<Integer> {
    PROFIT(1, "盘盈"),
    LOSS(2, "盘亏"),
    MATCH(3,"无误")
    ;
    private Integer value;
    private String desc;


    public static StocktakingLineStateEnum fromCode(int value) {
        return Arrays.stream(StocktakingLineStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
