package com.seeease.flywheel.serve.sf.enums;

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
public enum ExpressOrderSourceEnum implements IEnum<Integer> {

    TH_CG(1, "销售"),
    ;
    private Integer value;
    private String desc;

    public static ExpressOrderSourceEnum fromCode(int value) {
        return Arrays.stream(ExpressOrderSourceEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TYPE_NOT_SUPPORT));
    }
}
