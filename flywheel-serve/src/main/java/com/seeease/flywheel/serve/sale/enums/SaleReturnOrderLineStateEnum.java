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
public enum SaleReturnOrderLineStateEnum implements IStateEnum<Integer> {

    UNCONFIRMED(1, "待确认", SeeeaseConstant.UN_STARTED),
    UPLOAD_EXPRESS_NUMBER(2, "客户已发货", SeeeaseConstant.UNDER_WAY),
    QUALITY_TESTING(3, "质检中", SeeeaseConstant.UNDER_WAY),
    IN_FIX_INSPECTION(4, "维修中", SeeeaseConstant.UNDER_WAY),
    IN_RETURNED(5, "退货中", SeeeaseConstant.UNDER_WAY),
    IN_STORAGE(6, "入库", SeeeaseConstant.COMPLETE),
    CANCEL_WHOLE(7, "已取消", SeeeaseConstant.CANCEL_WHOLE),
    RETURNED(8, "已退货", SeeeaseConstant.CANCEL_WHOLE),
    LOGISTICS_RECEIVING(9, "物流收货", SeeeaseConstant.UNDER_WAY),
    ;
    private Integer value;
    private String desc;
    private String lineDesc;

    public static SaleReturnOrderLineStateEnum fromValue(int value) {
        return Arrays.stream(SaleReturnOrderLineStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

        UNCONFIRMED_TO_CANCEL_WHOLE(SaleReturnOrderLineStateEnum.UNCONFIRMED, SaleReturnOrderLineStateEnum.CANCEL_WHOLE, "全部取消"),
        UNCONFIRMED_TO_UPLOAD_EXPRESS_NUMBER(SaleReturnOrderLineStateEnum.UNCONFIRMED, SaleReturnOrderLineStateEnum.UPLOAD_EXPRESS_NUMBER, "客户已发货"),
        UPLOAD_EXPRESS_NUMBER_TO_IN_STORAGE(SaleReturnOrderLineStateEnum.UPLOAD_EXPRESS_NUMBER, SaleReturnOrderLineStateEnum.IN_STORAGE, "入库"),
        UPLOAD_EXPRESS_NUMBER_TO_LOGISTICS_RECEIVING(SaleReturnOrderLineStateEnum.UPLOAD_EXPRESS_NUMBER, SaleReturnOrderLineStateEnum.LOGISTICS_RECEIVING, "物流待收货"),
        LOGISTICS_RECEIVING_TO_QUALITY_TESTING(SaleReturnOrderLineStateEnum.LOGISTICS_RECEIVING, SaleReturnOrderLineStateEnum.QUALITY_TESTING, "收货去质检"),
        QUALITY_TESTING_TO_IN_STORAGE(SaleReturnOrderLineStateEnum.QUALITY_TESTING, SaleReturnOrderLineStateEnum.IN_STORAGE, "质检去入库"),
        QUALITY_TESTING_TO_IN_FIX_INSPECTION(SaleReturnOrderLineStateEnum.QUALITY_TESTING, SaleReturnOrderLineStateEnum.IN_FIX_INSPECTION, "质检去维修"),
        LOGISTICS_RECEIVING_TO_IN_FIX_INSPECTION(SaleReturnOrderLineStateEnum.LOGISTICS_RECEIVING, SaleReturnOrderLineStateEnum.IN_FIX_INSPECTION, "物流去维修"),
        IN_FIX_INSPECTION_TO_QUALITY_TESTING(SaleReturnOrderLineStateEnum.IN_FIX_INSPECTION, SaleReturnOrderLineStateEnum.QUALITY_TESTING, "维修去质检"),
        LOGISTICS_RECEIVING_TO_RETURNED(SaleReturnOrderLineStateEnum.LOGISTICS_RECEIVING, SaleReturnOrderLineStateEnum.RETURNED, "收货去退货"),
        QUALITY_TESTING_TO_RETURNED(SaleReturnOrderLineStateEnum.QUALITY_TESTING, SaleReturnOrderLineStateEnum.RETURNED, "质检去退货"),
        IN_RETURNED_TO_RETURNED(SaleReturnOrderLineStateEnum.IN_RETURNED, SaleReturnOrderLineStateEnum.RETURNED, "已退货"),


        UPLOAD_EXPRESS_NUMBER_TO_RETURNED(SaleReturnOrderLineStateEnum.UPLOAD_EXPRESS_NUMBER, SaleReturnOrderLineStateEnum.CANCEL_WHOLE, "入库"),


        ;
        private SaleReturnOrderLineStateEnum fromState;
        private SaleReturnOrderLineStateEnum toState;
        private String desc;

    }
}
