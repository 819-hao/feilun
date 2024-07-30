package com.seeease.flywheel.serve.helper.enmus;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BreakPriceAuditTypeEnum implements IEnum<Integer> {
    B(1),C(2)
    ;
    private Integer value;
}
