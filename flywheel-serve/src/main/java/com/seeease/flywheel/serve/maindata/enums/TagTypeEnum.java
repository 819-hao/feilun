package com.seeease.flywheel.serve.maindata.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TagTypeEnum implements IStateEnum<Integer> {
    UNDEFINED(0, "维修站点等级"),
    CREATE(1, "维修站点"),
    ;
    private Integer value;
    private String desc;

    public static TagTypeEnum fromValue(int value) {
        return Arrays.stream(TagTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
