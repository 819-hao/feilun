package com.seeease.flywheel.serve.allocate.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/8/30
 */
@Getter
@AllArgsConstructor
public enum AllocateTaskStateEnum implements IEnum<Integer> {
    AT_ING(1, "调拨中"),
    COMPLETE(2, "已完成"),
    CANCEL(3, "已取消"),
    ;

    private Integer value;
    private String desc;

    public static AllocateTaskStateEnum fromCode(int value) {
        return Arrays.stream(AllocateTaskStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}