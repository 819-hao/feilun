package com.seeease.flywheel.serve.sale.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/9/4
 */
@Getter
@AllArgsConstructor
public enum SaleOrderInspectionTypeEnum implements IEnum<Integer> {
    NO_INSPECTION(0, "默认：不质检"),
    OFFLINE(1, "线下质检"),
    ONLINE(2, "线上质检"),
    OFF(3, "飞轮发客户"),
    ;
    private Integer value;
    private String desc;

    public static SaleOrderInspectionTypeEnum fromCode(int value) {
        return Arrays.stream(SaleOrderInspectionTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}