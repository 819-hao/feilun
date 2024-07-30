package com.seeease.flywheel.serve.account.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 17:08
 */
@Getter
@AllArgsConstructor
public enum CompanyGroupEnum implements IStateEnum<Integer> {

    INIT(1, "人力成本"),
    QT_ING(2, "场地费用"),
    DELIVERED(3, "固定费用"),
    IN_STOCK(4, "日常费用"),

    ;
    private Integer value;
    private String desc;

    public static CompanyGroupEnum fromCode(String desc) {
        return Arrays.stream(CompanyGroupEnum.values())
                .filter(t -> desc.equals(t.getDesc()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    public static CompanyGroupEnum fromCode(int value) {
        return Arrays.stream(CompanyGroupEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
