package com.seeease.flywheel.serve.purchase.enums;

import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 任务状态基
 *
 * @Auther Gilbert
 * @Date 2023/1/17 17:21
 */
@Getter
@AllArgsConstructor
public enum TaskStateEnum implements IStateEnum<Integer> {

    WAIT_FOR_UPSTREAM_DELIVERY(0, "需求草稿箱"),
    WAIT_FOR_RECEIVING(1, "需求待审核"),
    RECEIVED(2, "需求已审核"),
    WAIT_FOR_IN_STORAGE(3, "需求已打款"),
    IN_STORAGE(4, "需求已完成"),

    CANCEL(5, "需求已取消"),
    APPLY_FIN(6, "待财务打款"),
    ;
    private Integer value;
    private String desc;


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

        /**
         * 作业取消收货
         */
        WORK_CANCEL_RECEIVING(TaskStateEnum.WAIT_FOR_RECEIVING, TaskStateEnum.CANCEL, "调拨作业取消收货"),

        RECEIVED_CANCEL_RECEIVING(TaskStateEnum.RECEIVED, TaskStateEnum.CANCEL, "调拨作业取消收货"),

        /**
         * 物流收货
         */
        LOGISTICS_RECEIVING(TaskStateEnum.WAIT_FOR_RECEIVING, TaskStateEnum.RECEIVED, "物流收货"),


        QT_PASSED_IN_STORAGE(TaskStateEnum.APPLY_FIN, TaskStateEnum.WAIT_FOR_IN_STORAGE, "已质检待入库"),

        RECEIVED_APPLY_FIN(TaskStateEnum.RECEIVED, TaskStateEnum.APPLY_FIN, "待财务打款"),

        /**
         * 入库
         */
        IN_STORAGE(TaskStateEnum.WAIT_FOR_IN_STORAGE, TaskStateEnum.IN_STORAGE, "已入库"),

        ;
        private TaskStateEnum fromState;
        private TaskStateEnum toState;
        private String desc;

    }

    public static TaskStateEnum fromCode(int value) {
        return Arrays.stream(TaskStateEnum.values())
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
}
