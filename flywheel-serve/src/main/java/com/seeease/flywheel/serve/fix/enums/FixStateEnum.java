package com.seeease.flywheel.serve.fix.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.LineStateEnum;
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
public enum FixStateEnum implements IStateEnum<Integer> {
    CREATE(0, "待接修", LineStateEnum.RUNNING),
    RECEIVE(1, "维修中", LineStateEnum.RUNNING),//送外
    NORMAL(2, "已完成", LineStateEnum.RUNNING),
    ALLOT(3, "待分配", LineStateEnum.RUNNING),
    CANCEL(4, "已取消", LineStateEnum.CANCEL);
    private Integer value;
    private String desc;

    private LineStateEnum state;

    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

        CREATE_RECEIVE_DELIVERY(FixStateEnum.CREATE, FixStateEnum.RECEIVE, "待接修-维修中"),

        NORMAL_RECEIVE_DELIVERY(FixStateEnum.NORMAL, FixStateEnum.RECEIVE, "质检确定维修"),


        NORMAL_CREATE(FixStateEnum.NORMAL, FixStateEnum.CREATE, "已完成-待接修"),
        CANCEL_CREATE(FixStateEnum.CANCEL, FixStateEnum.CREATE, "已取消-待接修"),

        CREATE_CANCEL(FixStateEnum.CREATE, FixStateEnum.CANCEL, "待接修-已取消"),

        CREATE_ALLOT(FixStateEnum.CREATE, FixStateEnum.ALLOT, "待接修-待分配"),

        ALLOT_RECEIVE(FixStateEnum.ALLOT, FixStateEnum.RECEIVE, "待分配-维修中"),

        RECEIVE_NORMAL_DELIVERY(FixStateEnum.RECEIVE, FixStateEnum.NORMAL, "维修正常通过"),
        ;
        private FixStateEnum fromState;
        private FixStateEnum toState;
        private String desc;

    }

    public static FixStateEnum fromCode(int value) {
        return Arrays.stream(FixStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
