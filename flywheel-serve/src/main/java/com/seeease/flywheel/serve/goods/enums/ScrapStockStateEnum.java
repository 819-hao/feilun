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
public enum ScrapStockStateEnum implements IEnum<Integer> {
    NOT_SCRAPPED(0, "未报废"),
    SCRAPPED(1, "已报废"),
    ;
    private Integer value;
    private String desc;

    public static ScrapStockStateEnum fromValue(int value) {
        return Arrays.stream(ScrapStockStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
