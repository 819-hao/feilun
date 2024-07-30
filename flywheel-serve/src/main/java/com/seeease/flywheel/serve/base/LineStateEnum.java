package com.seeease.flywheel.serve.base;

import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/1/9
 */
@Getter
@AllArgsConstructor
public enum LineStateEnum implements IStateEnum<Integer> {

    UNDEFINED(0, "未定义"),
    START(1, "待开始"),
    RUNNING(2, "进行中"),
    COMPLETE(3, "已完成"),
    CANCEL(3, "已取消"),

    ;
    private Integer value;
    private String desc;

    public static LineStateEnum fromValue(int value) {
        return Arrays.stream(LineStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
