package com.seeease.flywheel.serve.recycle.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


@Getter
@AllArgsConstructor
public enum RecycleOrderPurchaseTypeEnum implements IEnum<Integer> {

    RECYCLE(1, "仅回收"),
    BUY_BACK(2, "置换"),
    ;
    private Integer value;
    private String desc;

    public static RecycleOrderPurchaseTypeEnum fromCode(int value) {
        return Arrays.stream(RecycleOrderPurchaseTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
