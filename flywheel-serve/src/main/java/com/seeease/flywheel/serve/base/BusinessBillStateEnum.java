package com.seeease.flywheel.serve.base;

import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author wbh
 * @date 2023/3/6
 */
@Getter
@AllArgsConstructor
public enum BusinessBillStateEnum implements IStateEnum<Integer> {
    UNCONFIRMED(1, "待确认"),
    UNDER_WAY(2, "进行中"),
    COMPLETE(4, "已完成"),
    CANCEL_WHOLE(3, "全部取消"),
    ;
    private Integer value;
    private String desc;

    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

        UNCONFIRMED_TO_UNDER_WAY(BusinessBillStateEnum.UNCONFIRMED, BusinessBillStateEnum.UNDER_WAY, "进行中"),
        UNDER_WAY_TO_COMPLETE(BusinessBillStateEnum.UNDER_WAY, BusinessBillStateEnum.COMPLETE, "已完成"),
        UNCONFIRMED_TO_COMPLETE(BusinessBillStateEnum.UNCONFIRMED, BusinessBillStateEnum.COMPLETE, "已完成"),
        UNDER_WAY_TO_CANCEL_WHOLE(BusinessBillStateEnum.UNDER_WAY, BusinessBillStateEnum.CANCEL_WHOLE, "进行中取消"),
        UNCONFIRMED_TO_CANCEL_WHOLE(BusinessBillStateEnum.UNCONFIRMED, BusinessBillStateEnum.CANCEL_WHOLE, "全部取消"),
        COMPLETE_TO_COMPLETE(BusinessBillStateEnum.COMPLETE, BusinessBillStateEnum.COMPLETE, "已完成已完成"),
        ;
        private BusinessBillStateEnum fromState;
        private BusinessBillStateEnum toState;
        private String desc;

    }


    public static BusinessBillStateEnum fromCode(int value) {
        return Arrays.stream(BusinessBillStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
