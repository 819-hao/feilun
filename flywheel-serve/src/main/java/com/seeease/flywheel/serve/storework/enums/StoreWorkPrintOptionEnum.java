package com.seeease.flywheel.serve.storework.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:29
 */
@Getter
@AllArgsConstructor
public enum StoreWorkPrintOptionEnum implements IEnum<Integer> {

    TH_Ck(0, "禁止打印"),
    TH_CG(1, "无需国检 滨江到其他"),
    TH_JS(2, "平台需国检 滨江到国检"),
    GR_JS(3, "线下需国检 滨江到其他 滨江到国检"),
    ;
    private Integer value;
    private String desc;

    public static StoreWorkPrintOptionEnum fromCode(int value) {
        return Arrays.stream(StoreWorkPrintOptionEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TYPE_NOT_SUPPORT));
    }
}
