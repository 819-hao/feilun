package com.seeease.flywheel.serve.pricing.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2024/2/22
 */
@Getter
@AllArgsConstructor
public enum ApplyPricingStateEnum implements IStateEnum<Integer> {

    CREATE(1, "待审核"),
    PASS(2, "审核通过"),
    REJECTION(3, "审核拒绝"),
    ;

    private Integer value;
    private String desc;

    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

        PASS(ApplyPricingStateEnum.CREATE, ApplyPricingStateEnum.PASS, "通过"),
        REJECTION(ApplyPricingStateEnum.CREATE, ApplyPricingStateEnum.REJECTION, "拒绝"),
        ;
        private ApplyPricingStateEnum fromState;
        private ApplyPricingStateEnum toState;
        private String desc;

    }

    public static ApplyPricingStateEnum fromCode(int value) {
        return Arrays.stream(ApplyPricingStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}