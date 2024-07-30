package com.seeease.flywheel.serve.fix.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description 订单类型
 * @Date create in 2023/1/17 14:41
 */
@Getter
@AllArgsConstructor
public enum OrderTypeEnum implements IStateEnum<Integer> {
    UNDEFINED(0, "系统自建"),
    CREATE(1, "客户自建"),
    ;
    private Integer value;
    private String desc;


    public static OrderTypeEnum fromValue(int value) {
        return Arrays.stream(OrderTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
