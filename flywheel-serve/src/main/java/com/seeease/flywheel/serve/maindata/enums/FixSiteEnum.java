package com.seeease.flywheel.serve.maindata.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FixSiteEnum implements IStateEnum<Integer> {
    UNDEFINED(0, "禁用"),
    CREATE(1, "启用"),
    ;
    private Integer value;
    private String desc;
    public static FixSiteEnum fromValue(int value) {
        return Arrays.stream(FixSiteEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
