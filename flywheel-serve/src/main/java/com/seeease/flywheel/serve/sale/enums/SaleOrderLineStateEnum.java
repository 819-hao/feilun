package com.seeease.flywheel.serve.sale.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 *
 */
@Getter
@AllArgsConstructor
public enum SaleOrderLineStateEnum implements IStateEnum<Integer> {

    WAIT_CONFIRM(0, "待确认", SeeeaseConstant.UN_CONFIRMED),
    WAIT_OUT_STORAGE(1, "待出库", SeeeaseConstant.UN_STARTED),
    QUALITY_TESTING(2, "质检中", SeeeaseConstant.UNDER_WAY),
    ON_CONSIGNMENT(3, "寄售中", SeeeaseConstant.UNDER_WAY),
    CONSIGNMENT_SETTLED(4, "寄售已结算", SeeeaseConstant.COMPLETE),
    DELIVERED(5, "已发货", SeeeaseConstant.COMPLETE),
    CANCEL_WHOLE(6, "取消", SeeeaseConstant.CANCEL_WHOLE),
    IN_RETURN(7, "退回中", SeeeaseConstant.COMPLETE),
    RETURN(8, "已退回", SeeeaseConstant.COMPLETE),
    ;
    private Integer value;
    private String desc;
    private String lineDesc;

    public static SaleOrderLineStateEnum fromValue(int value) {
        return Arrays.stream(SaleOrderLineStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {
        SALE_CONFIRM(SaleOrderLineStateEnum.WAIT_CONFIRM, SaleOrderLineStateEnum.WAIT_OUT_STORAGE, "已确认"),
        WAIT_CONFIRM_TO_CANCEL_WHOLE(SaleOrderLineStateEnum.WAIT_CONFIRM, SaleOrderLineStateEnum.CANCEL_WHOLE, "全部取消"),
        //卖门店商品时候的状态
        WAIT_OUT_STORAGE_TO_DELIVERED(SaleOrderLineStateEnum.WAIT_OUT_STORAGE, SaleOrderLineStateEnum.DELIVERED, "已发货"),
        //卖总部商品时候的状态
        OUT_STORAGE_TO_QUALITY_TESTING(SaleOrderLineStateEnum.WAIT_OUT_STORAGE, SaleOrderLineStateEnum.QUALITY_TESTING, "质检中"),
        QUALITY_TESTING_TO_DELIVERED(SaleOrderLineStateEnum.QUALITY_TESTING, SaleOrderLineStateEnum.DELIVERED, "质检通过发货"),
        QUALITY_TESTING_TO_CANCEL_WHOLE(SaleOrderLineStateEnum.QUALITY_TESTING, SaleOrderLineStateEnum.CANCEL_WHOLE, "异常入库"),
        QUALITY_TESTING_TO_RETURN(SaleOrderLineStateEnum.QUALITY_TESTING, SaleOrderLineStateEnum.RETURN, "质检退回"),
        QUALITY_TESTING_TO_ON_CONSIGNMENT(SaleOrderLineStateEnum.QUALITY_TESTING, SaleOrderLineStateEnum.ON_CONSIGNMENT, "总部寄售中"),
        WAIT_OUT_STORAGE_TO_ON_CONSIGNMENT(SaleOrderLineStateEnum.WAIT_OUT_STORAGE, SaleOrderLineStateEnum.ON_CONSIGNMENT, "门店寄售中"),
        ON_CONSIGNMENT_TO_CONSIGNMENT_SETTLED(SaleOrderLineStateEnum.ON_CONSIGNMENT, SaleOrderLineStateEnum.CONSIGNMENT_SETTLED, "寄售已结算"),
        WAIT_OUT_STORAGE_TO_CANCEL_WHOLE(SaleOrderLineStateEnum.WAIT_OUT_STORAGE, SaleOrderLineStateEnum.CANCEL_WHOLE, "全部取消"),
        IN_RETURN_TO_RETURN(SaleOrderLineStateEnum.IN_RETURN, SaleOrderLineStateEnum.RETURN, "已退货"),
        IN_RETURN_TO_DELIVERED(SaleOrderLineStateEnum.IN_RETURN, SaleOrderLineStateEnum.DELIVERED, "退货中已发货"),
        DELIVERED_TO_IN_RETURN(SaleOrderLineStateEnum.DELIVERED, SaleOrderLineStateEnum.IN_RETURN, "发货到退货中"),
        IN_RETURN_TO_ON_CONSIGNMENT(SaleOrderLineStateEnum.IN_RETURN, SaleOrderLineStateEnum.ON_CONSIGNMENT, "退货中到寄售中"),
        ON_CONSIGNMENT_TO_IN_RETURN(SaleOrderLineStateEnum.ON_CONSIGNMENT, SaleOrderLineStateEnum.IN_RETURN, "寄售中到退货中"),
        IN_RETURN_TO_CONSIGNMENT_SETTLED(SaleOrderLineStateEnum.IN_RETURN, SaleOrderLineStateEnum.CONSIGNMENT_SETTLED, "退货中到已结算"),
        CONSIGNMENT_SETTLED_TO_IN_RETURN(SaleOrderLineStateEnum.CONSIGNMENT_SETTLED, SaleOrderLineStateEnum.IN_RETURN, "已结算到退货中");
        private SaleOrderLineStateEnum fromState;
        private SaleOrderLineStateEnum toState;
        private String desc;

    }
}
