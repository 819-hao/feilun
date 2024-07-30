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
public enum ShopGroupEnum implements IStateEnum<Integer> {

    INIT(1, "零售门店"),
    QT_ING(2, "总部直播"),
    DELIVERED(3, "商家业务"),
    ;
    private Integer value;
    private String desc;

    public static ShopGroupEnum fromCode(String desc) {
        return Arrays.stream(ShopGroupEnum.values())
                .filter(t -> desc.equals(t.getDesc()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    public static ShopGroupEnum fromCode(int value) {
        return Arrays.stream(ShopGroupEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
