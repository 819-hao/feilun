package com.seeease.flywheel.serve.purchase.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 销售等级
 *
 * @author Tiro
 * @date 2023/1/9
 */
@Getter
@AllArgsConstructor
public enum SalesPriorityEnum implements IEnum<Integer> {

    TOB_C(0, "B/C可同销"),
    TOB(1, "仅B端销售"),
    TOC(2, "仅C端销售"),
    ;
    private Integer value;
    private String desc;

    public static SalesPriorityEnum fromCode(int value) {
        return Arrays.stream(SalesPriorityEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
