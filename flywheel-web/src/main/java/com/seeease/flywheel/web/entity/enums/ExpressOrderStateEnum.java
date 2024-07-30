package com.seeease.flywheel.web.entity.enums;

import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:29
 */
@Getter
@AllArgsConstructor
public enum ExpressOrderStateEnum implements IStateEnum<Integer> {

    INIT(1, "初始化"),
    SUCCESS(2, "下单成功"),
    FAIL(3, "下单失败"),
    CANCEL(4, "已回收"),
    ING(5, "进行中"),
    ;
    private Integer value;
    private String desc;


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {
        INIT_ING(INIT, ING, "下单进行中"),
        FAIL_ING(FAIL, ING, "下单重新进入进行中"),
        ING_FAIL(ING, FAIL, "下单失败"),
        ING_SUCCESS(ING, SUCCESS, "下单成功"),
        SUCCESS_CANCEL(SUCCESS, CANCEL, "回收订单成功"),
        ;
        private ExpressOrderStateEnum fromState;
        private ExpressOrderStateEnum toState;
        private String desc;
    }

}
