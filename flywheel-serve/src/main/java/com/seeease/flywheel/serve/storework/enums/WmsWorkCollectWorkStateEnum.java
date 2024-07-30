package com.seeease.flywheel.serve.storework.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/9/1
 */
@Getter
@AllArgsConstructor
public enum WmsWorkCollectWorkStateEnum implements IStateEnum<Integer> {
    WAIT_PRINT(1, "待打单"),
    WAIT_DELIVERY(2, "待发货"),
    COMPLETE(3, "已发货"),
    CANCEL(4, "已取消"),
    ;
    private Integer value;
    private String desc;

    public static WmsWorkCollectWorkStateEnum fromCode(int value) {
        return Arrays.stream(WmsWorkCollectWorkStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {
        /**
         * 取消
         */
        WAIT_PRINT_CANCEL(WmsWorkCollectWorkStateEnum.WAIT_PRINT, WmsWorkCollectWorkStateEnum.CANCEL, "待打单取消"),
        WAIT_DELIVERY_CANCEL(WmsWorkCollectWorkStateEnum.WAIT_DELIVERY, WmsWorkCollectWorkStateEnum.CANCEL, "待发货取消"),
        /**
         * 打单
         */
        PRINT(WmsWorkCollectWorkStateEnum.WAIT_PRINT, WmsWorkCollectWorkStateEnum.WAIT_DELIVERY, "调拨作业取消收货"),
        /**
         * 发货
         */
        DELIVERY(WmsWorkCollectWorkStateEnum.WAIT_DELIVERY, WmsWorkCollectWorkStateEnum.COMPLETE, "调拨作业取消收货"),

        ;
        private WmsWorkCollectWorkStateEnum fromState;
        private WmsWorkCollectWorkStateEnum toState;
        private String desc;
    }
}
