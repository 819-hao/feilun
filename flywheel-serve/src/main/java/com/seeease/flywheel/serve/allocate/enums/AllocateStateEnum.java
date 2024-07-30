package com.seeease.flywheel.serve.allocate.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/3/7
 */
@Getter
@AllArgsConstructor
public enum AllocateStateEnum implements IEnum<Integer> {
    CREATE(1, "调拨创建"),
    OUT_STOCK(2, "已出库"),
    CANCEL_WHOLE(3, "全部取消"),
    COMPLETE(4, "已完成"),
    ;

    private Integer value;
    private String desc;

    public static AllocateStateEnum fromCode(int value) {
        return Arrays.stream(AllocateStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}