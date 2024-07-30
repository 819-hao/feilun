package com.seeease.flywheel.serve.customer.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/3/27
 */

@Getter
@AllArgsConstructor
public enum CustomerTypeEnum implements IEnum<Integer> {
    INDIVIDUAL(1, "个人"),
    ENTERPRISE(2, "企业"),
    ;
    private Integer value;
    private String desc;

    public static CustomerTypeEnum fromCode(int value) {
        return Arrays.stream(CustomerTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}

