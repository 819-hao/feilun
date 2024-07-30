package com.seeease.flywheel.serve.purchase.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/1/9
 */
@Getter
@AllArgsConstructor
public enum PurchaseLineStateEnum implements IStateEnum<Integer> {

    TO_BE_CONFIRMED(1, "待确认", "待开始"),

    CUSTOMER_HAS_SHIPPED(2, "客户已发货", "进行中"),
    IN_QUALITY_INSPECTION(3, "质检中", "进行中"),
    IN_FIX_INSPECTION(4, "维修中", "进行中"),
    //质检判断退货 & 物流拒收
    IN_RETURN(5, "退回中", "进行中"),
    TO_CUSTOMER_CONFIRMATION(6, "待客户确认", "进行中"),
    ON_CONSIGNMENT(7, "寄售中", "进行中"),
    TO_BE_SETTLED(8, "待结算", "进行中"),

    RETURNED(9, "已退回", "已取消"),
    ORDER_CANCEL_WHOLE(10, "已取消", "已取消"),

    WAREHOUSED(11, "已入库", "已完成"),

    IN_SETTLED(12, "已结算", "已完成"),

    ;
    private Integer value;
    private String desc;

    private String lineDesc;

    public static PurchaseLineStateEnum fromValue(int value) {
        return Arrays.stream(PurchaseLineStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
