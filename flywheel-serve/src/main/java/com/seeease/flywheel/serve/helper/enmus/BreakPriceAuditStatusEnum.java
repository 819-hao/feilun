package com.seeease.flywheel.serve.helper.enmus;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum BreakPriceAuditStatusEnum implements IEnum<Integer> {
    WAIT(1),OK(2),FAIL(3)
    ;
    public static BreakPriceAuditStatusEnum of (Integer value){
        return Arrays.stream(BreakPriceAuditStatusEnum.values()).filter(v-> v.getValue().equals(value)).findFirst()
                .orElseThrow(() -> new  IllegalArgumentException("无效枚举值"));
    }

    private Integer value;
}
