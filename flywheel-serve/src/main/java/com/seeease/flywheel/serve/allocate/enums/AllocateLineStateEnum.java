package com.seeease.flywheel.serve.allocate.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 调拨行状态
 *
 * @author Tiro
 * @date 2023/3/7
 */
@Getter
@AllArgsConstructor
public enum AllocateLineStateEnum implements IStateEnum<Integer> {
    INIT(1, "待确认"),
    QT_ING(2, "质检中"),
    DELIVERED(3, "已发货"),
    IN_STOCK(4, "已入库"),
    RETURNING(5, "退回中"),
    RETURNED(6, "已退回"),
    CANCEL(7, "已取消"),
    ;
    private Integer value;
    private String desc;


    public static AllocateLineStateEnum fromCode(int value) {
        return Arrays.stream(AllocateLineStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {
        /**
         * 取消调拨单
         */
        CANCEL_ALLOCATE(AllocateLineStateEnum.INIT, AllocateLineStateEnum.CANCEL, "取消调拨单"),
        /**
         * 总部发货
         */
        DELIVERY(AllocateLineStateEnum.QT_ING, AllocateLineStateEnum.DELIVERED, "总部已发货"),
        /**
         * 门店发货
         */
        SHOP_DELIVERY(AllocateLineStateEnum.INIT, AllocateLineStateEnum.DELIVERED, "门店已发货"),
        /**
         * 总部出库
         */
        OUT_STOCK(AllocateLineStateEnum.INIT, AllocateLineStateEnum.QT_ING, "总部已出库"),
        /**
         * 总部入库
         */
        IN_STOCK(AllocateLineStateEnum.QT_ING, AllocateLineStateEnum.IN_STOCK, "总部已入库"),
        /**
         * 异常入库
         */
        EXCEPTION_IN_STOCK(AllocateLineStateEnum.QT_ING, AllocateLineStateEnum.CANCEL, "总部异常入库"),
        /**
         * 门店正常收货
         */
        SHOP_RECEIVING(AllocateLineStateEnum.DELIVERED, AllocateLineStateEnum.IN_STOCK, "门店正常收货"),
        /**
         * 总部正常收货
         */
        RECEIVING(AllocateLineStateEnum.DELIVERED, AllocateLineStateEnum.QT_ING, "总部正常收货"),
        /**
         * 门店拒绝收货
         */
        SHOP_RECEIVING_REJECT(AllocateLineStateEnum.DELIVERED, AllocateLineStateEnum.RETURNING, "门店拒绝收货"),
        /**
         * 退回收货
         */
        RECEIVING_RETURNED(AllocateLineStateEnum.RETURNING, AllocateLineStateEnum.RETURNED, "门店退回收货"),

        ;
        private AllocateLineStateEnum fromState;
        private AllocateLineStateEnum toState;
        private String desc;

    }
}