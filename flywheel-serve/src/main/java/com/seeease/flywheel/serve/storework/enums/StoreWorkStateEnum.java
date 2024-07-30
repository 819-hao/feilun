package com.seeease.flywheel.serve.storework.enums;

import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.Tuple2;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 出库列表
 *
 * @Auther Gilbert
 * @Date 2023/1/17 17:21
 */
@Getter
@AllArgsConstructor
public enum StoreWorkStateEnum implements IStateEnum<Integer> {

    WAIT_FOR_UPSTREAM_DELIVERY(-1, "待上游发货"),
    WAIT_FOR_RECEIVING(1, "待收货"),
    RECEIVED(2, "已收货"),
    WAIT_FOR_IN_STORAGE(3, "待入库"),
    IN_STORAGE(4, "已入库"),

    WAIT_FOR_OUT_STORAGE(5, "待出库"),
    OUT_STORAGE(6, "已出库"),
    WAIT_FOR_DELIVERY(7, "待发货"),
    DELIVERY(8, "已发货"),
    REFUSE_RECEIVING(9, "已退回"),
    CANCEL(10, "已取消"),
    ;
    private Integer value;
    private String desc;


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

        /**
         * 调拨作业取消收货
         */
        ALLOCATE_WORK_CANCEL_RECEIVING(StoreWorkStateEnum.WAIT_FOR_UPSTREAM_DELIVERY, StoreWorkStateEnum.CANCEL, "调拨作业取消收货"),

        /**
         * 作业取消收货
         */
        WORK_CANCEL_RECEIVING(StoreWorkStateEnum.WAIT_FOR_RECEIVING, StoreWorkStateEnum.CANCEL, "调拨作业取消收货"),
        /**
         * 作业取消发货
         */
        SHOP_WORK_CANCEL_DELIVERY(StoreWorkStateEnum.WAIT_FOR_DELIVERY, StoreWorkStateEnum.CANCEL, "调拨作业取消发货"),
        /**
         * 作业取消出库
         */
        WORK_CANCEL_OUT(StoreWorkStateEnum.WAIT_FOR_OUT_STORAGE, StoreWorkStateEnum.CANCEL, "调拨作业取消发货"),

        /**
         * 上游发货，通知下游收货
         */
        UPSTREAM_DELIVERY(StoreWorkStateEnum.WAIT_FOR_UPSTREAM_DELIVERY, StoreWorkStateEnum.WAIT_FOR_RECEIVING, "上游发货"),

        /**
         * 上游发货取消
         */
        UPSTREAM_DELIVERY_CANCEL(StoreWorkStateEnum.WAIT_FOR_UPSTREAM_DELIVERY, StoreWorkStateEnum.CANCEL, "上游发货取消"),

        /**
         * 下游拒绝收货
         */
        DOWNSTREAM_REFUSE_RECEIVING(StoreWorkStateEnum.DELIVERY, StoreWorkStateEnum.WAIT_FOR_RECEIVING, "下游拒绝收货"),

        /**
         * 物流收货
         */
        LOGISTICS_RECEIVING(StoreWorkStateEnum.WAIT_FOR_RECEIVING, StoreWorkStateEnum.RECEIVED, "物流收货"),

        /**
         * 物流拒收
         */
        LOGISTICS_REFUSE_RECEIVING(StoreWorkStateEnum.WAIT_FOR_RECEIVING, StoreWorkStateEnum.DELIVERY, "物流拒绝收货"),

        QT_PASSED_IN_STORAGE(StoreWorkStateEnum.RECEIVED, StoreWorkStateEnum.WAIT_FOR_IN_STORAGE, "已质检待入库"),

        QT_PASSED_OUT_STORAGE(StoreWorkStateEnum.OUT_STORAGE, StoreWorkStateEnum.WAIT_FOR_DELIVERY, "已质检待发货"),

        QT_PASSED_OUT_STORAGE_EXCEPTION(StoreWorkStateEnum.OUT_STORAGE, StoreWorkStateEnum.WAIT_FOR_IN_STORAGE, "异常待入库"),

        QT_REJECT_WAIT_FOR_DELIVERY(StoreWorkStateEnum.RECEIVED, StoreWorkStateEnum.WAIT_FOR_DELIVERY, "质检退货待发货"),

        WAIT_FOR_RECEIVING_DELIVERY(StoreWorkStateEnum.WAIT_FOR_RECEIVING, StoreWorkStateEnum.DELIVERY, "拒收退货"),

        /**
         * 物流发货
         */
        LOGISTICS_DELIVERY(StoreWorkStateEnum.WAIT_FOR_DELIVERY, StoreWorkStateEnum.DELIVERY, "物流发货"),

        /**
         * 入库
         */
        IN_STORAGE(StoreWorkStateEnum.WAIT_FOR_IN_STORAGE, StoreWorkStateEnum.IN_STORAGE, "已入库"),

        /**
         * 出库
         */
        OUT_STORAGE(StoreWorkStateEnum.WAIT_FOR_OUT_STORAGE, StoreWorkStateEnum.OUT_STORAGE, "已出库"),

        /**
         * 出库不质检
         */
        OUT_STORAGE_NOT_QT(StoreWorkStateEnum.WAIT_FOR_OUT_STORAGE, StoreWorkStateEnum.WAIT_FOR_DELIVERY, "已出库待发货"),

        /**
         * 门店收货
         */
        SHOP_RECEIVING(StoreWorkStateEnum.WAIT_FOR_RECEIVING, StoreWorkStateEnum.IN_STORAGE, "门店收货"),

        /**
         * 门店退回
         */
        SHOP_RECEIVING_RETURN(StoreWorkStateEnum.WAIT_FOR_RECEIVING, StoreWorkStateEnum.WAIT_FOR_DELIVERY, "拒收退回"),
        /**
         * 平台订单 拒收 已发货
         */
        //todo
//        SHOP_RECEIVING_DELIVERY(StoreWorkStateEnum.WAIT_FOR_RECEIVING, StoreWorkStateEnum.DELIVERY, "拒收退回"),
        SHOP_REFUSE_RECEIVING(StoreWorkStateEnum.WAIT_FOR_RECEIVING, StoreWorkStateEnum.REFUSE_RECEIVING, "拒收退回"),
        ;
        private StoreWorkStateEnum fromState;
        private StoreWorkStateEnum toState;
        private String desc;

    }

    public static StoreWorkStateEnum fromCode(int value) {
        return Arrays.stream(StoreWorkStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    /**
     * 调拨来源
     */
    private static final List<BusinessBillTypeEnum> ALLOCATE_SOURCE = Lists.newArrayList(
            BusinessBillTypeEnum.ZB_DB,
            BusinessBillTypeEnum.MD_DB,
            BusinessBillTypeEnum.MD_DB_ZB
    );

    /**
     * 创建- 获取初始状态
     *
     * @param belongingStoreId
     * @param workSource
     * @param workType
     * @return
     */
    public static Tuple2<StoreWorkStateEnum, TransitionEnum> getInitState(Integer belongingStoreId,
                                                                          BusinessBillTypeEnum workSource,
                                                                          StoreWorkTypeEnum workType) {
        switch (workType) {
            case INT_STORE:
                //调拨待上游发货
                if (ALLOCATE_SOURCE.contains(workSource)) {
                    return Tuple2.of(StoreWorkStateEnum.WAIT_FOR_UPSTREAM_DELIVERY, StoreWorkStateEnum.TransitionEnum.ALLOCATE_WORK_CANCEL_RECEIVING);
                } else {
                    //待收货
                    return Tuple2.of(StoreWorkStateEnum.WAIT_FOR_RECEIVING, StoreWorkStateEnum.TransitionEnum.WORK_CANCEL_RECEIVING);
                }
            case OUT_STORE:
                //总部出库
                if (FlywheelConstant._ZB_ID == belongingStoreId) {
                    return Tuple2.of(StoreWorkStateEnum.WAIT_FOR_OUT_STORAGE, StoreWorkStateEnum.TransitionEnum.WORK_CANCEL_OUT);
                } else {
                    //门店发货
                    return Tuple2.of(StoreWorkStateEnum.WAIT_FOR_DELIVERY, StoreWorkStateEnum.TransitionEnum.SHOP_WORK_CANCEL_DELIVERY);
                }
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
    }
}
