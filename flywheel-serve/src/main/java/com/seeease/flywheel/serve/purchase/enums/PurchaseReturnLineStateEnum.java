package com.seeease.flywheel.serve.purchase.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Getter
@AllArgsConstructor
public enum PurchaseReturnLineStateEnum implements IStateEnum<Integer> {
    TO_BE_CONFIRMED(1, "待确认", "待开始"),
    WAITING_WAREHOUSE_DELIVERY(2, "已出库", "进行中"),
    WAITING_LOGISTICS_DELIVERY(3, "已发货", "已完成"),
    CANCEL_WHOLE(4, "已取消", "已取消"),
    ;
    private Integer value;
    private String desc;

    private String lineDesc;

    public static PurchaseReturnLineStateEnum fromValue(int value) {
        return Arrays.stream(PurchaseReturnLineStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
