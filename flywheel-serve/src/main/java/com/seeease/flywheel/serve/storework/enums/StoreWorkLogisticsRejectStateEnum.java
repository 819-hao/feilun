package com.seeease.flywheel.serve.storework.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/3/15
 */
@Getter
@AllArgsConstructor
public enum StoreWorkLogisticsRejectStateEnum implements IEnum<Integer> {
    NORMAL(0, "正常收货"),
    REJECT(1, "拒绝收货"),
    ;
    private Integer value;
    private String desc;

    public static StoreWorkLogisticsRejectStateEnum fromCode(int value) {
        return Arrays.stream(StoreWorkLogisticsRejectStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
