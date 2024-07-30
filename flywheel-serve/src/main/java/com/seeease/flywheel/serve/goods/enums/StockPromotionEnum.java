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
public enum StockPromotionEnum implements IEnum<Integer> {
    STOP_PRODUCTION(0, "商品下架"),
    ITEM_UP_SHELF(1, "商品上架"),
    ;
    private Integer value;
    private String desc;

    public static StockPromotionEnum fromValue(int value) {
        return Arrays.stream(StockPromotionEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
