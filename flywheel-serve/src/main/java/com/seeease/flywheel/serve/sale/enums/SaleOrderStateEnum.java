package com.seeease.flywheel.serve.sale.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 销售订单状态
 *
 * @author Tiro
 * @date 2023/3/29
 */
@Getter
@AllArgsConstructor
public enum SaleOrderStateEnum implements IEnum<Integer> {
    UN_CONFIRMED(0, "待确认"),
    UN_STARTED(1, "待开始"),
    UNDER_WAY(2, "进行中"),
    COMPLETE(4, "已完成"),
    CANCEL_WHOLE(3, "全部取消"),
    ;
    private Integer value;
    private String desc;

    public static SaleOrderStateEnum fromCode(int value) {
        return Arrays.stream(SaleOrderStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}