package com.seeease.flywheel.serve.fix.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/17 14:41
 */
@Getter
@AllArgsConstructor
public enum FixTypeEnum implements IStateEnum<Integer> {
    UNDEFINED(0, "待编辑"),
    RECEIVE(1, "总部维修"),
    REFUSE(2, "拒绝维修"),
    ;
    private Integer value;
    private String desc;


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

        ;
        private FixTypeEnum fromState;
        private FixTypeEnum toState;
        private String desc;

    }

    public static FixTypeEnum fromCode(int value) {
        return Arrays.stream(FixTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
