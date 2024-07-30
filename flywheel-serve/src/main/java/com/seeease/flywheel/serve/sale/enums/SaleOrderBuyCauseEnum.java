package com.seeease.flywheel.serve.sale.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author wbh
 * @date 2023/3/6
 */
@Getter
@AllArgsConstructor
public enum SaleOrderBuyCauseEnum implements IEnum<Integer> {
    ONESELF(1, "自戴"),
    OTHERS(2, "送人"),
    ;
    private Integer value;
    private String desc;

    public static SaleOrderBuyCauseEnum fromCode(int value) {
        return Arrays.stream(SaleOrderBuyCauseEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
