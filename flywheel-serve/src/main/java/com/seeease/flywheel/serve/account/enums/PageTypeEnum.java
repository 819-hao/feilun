package com.seeease.flywheel.serve.account.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 17:08
 */
@Getter
@AllArgsConstructor
public enum PageTypeEnum implements IStateEnum<Integer> {

    INIT(1, "财务费用模版"),
    QT_ING(2, "人力成本模版"),
    DELIVERED(3, "人员数量模版"),
    ;
    private Integer value;
    private String desc;

    public static PageTypeEnum fromCode(String desc) {
        return Arrays.stream(PageTypeEnum.values())
                .filter(t -> desc.equals(t.getDesc()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    public static PageTypeEnum fromCode(int value) {
        return Arrays.stream(PageTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
