package com.seeease.flywheel.serve.purchase.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 回收来源
 *
 * @author Tiro
 * @date 2023/1/9
 */
@Getter
@AllArgsConstructor
public enum RecycleModeEnum implements IEnum<Integer> {

    DEPOSIT(1, "总部回收"),
    PREPARE(2, "门店回收"),
    BATCH(3, "二手表小程序"),
    RECYCLE(4, "寄售转"),
    ;
    private Integer value;
    private String desc;

    public static RecycleModeEnum fromCode(int value) {
        return Arrays.stream(RecycleModeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TYPE_NOT_SUPPORT));
    }
}
