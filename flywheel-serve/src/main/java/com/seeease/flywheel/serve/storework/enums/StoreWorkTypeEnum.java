package com.seeease.flywheel.serve.storework.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 1-出库，2-入库
 *
 * @author Tiro
 * @date 2023/2/3
 */
@Getter
@AllArgsConstructor
public enum StoreWorkTypeEnum implements IEnum<Integer> {

    OUT_STORE(1, "出库"),
    INT_STORE(2, "入库"),
    ;
    private Integer value;
    private String desc;

    public static StoreWorkTypeEnum fromCode(int value) {
        return Arrays.stream(StoreWorkTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
