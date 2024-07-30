package com.seeease.flywheel.serve.pricing.enums;

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
 * @Date create in 2023/3/21 11:03
 */
@Getter
@AllArgsConstructor
public enum PricingStateEnum implements IStateEnum<Integer> {

    CREATE(1, "定价已创建"),
    CHECK(2, "定价已填写"),
    COMPLETE(3, "定价已审核"),
    // 定价
    CANCEL_WHOLE(4, "全部取消"),
    ;

    private Integer value;
    private String desc;

    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

        CREATE_CHECK(PricingStateEnum.CREATE, PricingStateEnum.CHECK, "定价已通过"),
        CHECK_CREATE(PricingStateEnum.CHECK, PricingStateEnum.CREATE, "定价审核不通过"),
        CHECK_COMPLETE(PricingStateEnum.CHECK, PricingStateEnum.COMPLETE, "定价审核通过"),
        COMPLETE_CREATE(PricingStateEnum.COMPLETE, PricingStateEnum.CREATE, "重新定价发起"),

        //定价取消 -- 发生
        CREATE_CANCEL_WHOLE(PricingStateEnum.CREATE, PricingStateEnum.CANCEL_WHOLE, "定价已创建-取消"),
        CHECK_CANCEL_WHOLE(PricingStateEnum.CHECK, PricingStateEnum.CANCEL_WHOLE, "定价已定价-取消"),
        COMPLETE_CANCEL_WHOLE(PricingStateEnum.COMPLETE, PricingStateEnum.CANCEL_WHOLE, "定价已审核-取消"),
        //取消退货 直接开启重启定价
        CANCEL_WHOLE_CREATE(PricingStateEnum.CANCEL_WHOLE, PricingStateEnum.CREATE, "定价取消-待定价"),
        ;
        private PricingStateEnum fromState;
        private PricingStateEnum toState;
        private String desc;

    }

    public static PricingStateEnum fromCode(int value) {
        return Arrays.stream(PricingStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
