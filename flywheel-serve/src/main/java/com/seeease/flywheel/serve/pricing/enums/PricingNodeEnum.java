package com.seeease.flywheel.serve.pricing.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
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
public enum PricingNodeEnum implements IStateEnum<Integer> {

    SUBMIT(1, "提交定价审核"),
    CHECK(2, "审核通过"),
    COMPLETE(3, "审核驳回"),
    INSERT(4, "新建定价"),
    AGAIN(5, "重新定价"),
    CANCEL(6, "取消定价"),
    ;

    private Integer value;
    private String desc;


    public static PricingNodeEnum fromCode(int value) {
        return Arrays.stream(PricingNodeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
