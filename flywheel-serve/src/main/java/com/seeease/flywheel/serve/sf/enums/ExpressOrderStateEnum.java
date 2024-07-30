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
public enum ExpressOrderStateEnum implements IEnum<Integer> {

    INIT(1, "初始化"),
    SUCCESS(2, "下单成功"),
    FAIL(3, "下单失败"),
    CANCEL(4,"已回收")
    ;
    private Integer value;
    private String desc;

    public static ExpressOrderStateEnum fromCode(int value) {
        return Arrays.stream(ExpressOrderStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TYPE_NOT_SUPPORT));
    }
}
