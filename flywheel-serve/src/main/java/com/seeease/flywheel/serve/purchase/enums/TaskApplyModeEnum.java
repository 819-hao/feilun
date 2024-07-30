package com.seeease.flywheel.serve.purchase.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 采购类型
 *
 * @author Tiro
 * @date 2023/2/28
 */
@Getter
@AllArgsConstructor
public enum TaskApplyModeEnum implements IEnum<Integer> {

    TH_CG(0, "定金"),
    TH_JS(1, "备货"),
    GR_JS(2, "品牌"),

    ;
    private Integer value;
    private String desc;

    public static TaskApplyModeEnum fromCode(int value) {
        return Arrays.stream(TaskApplyModeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TYPE_NOT_SUPPORT));
    }

}