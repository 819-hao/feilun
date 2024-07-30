package com.seeease.flywheel.serve.qt.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.LineStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/17 14:41
 */
@Getter
@AllArgsConstructor
public enum QualityTestingStateEnum implements IStateEnum<Integer> {

    RECEIVE(0, "待判定", LineStateEnum.RUNNING),
    NORMAL(1, "质检正常，可以流转", LineStateEnum.RUNNING),
    ANOMALY(2, "质检不通过，异常入库", LineStateEnum.RUNNING),
    RETURN(3, "质检不通过，需要拒收", LineStateEnum.RUNNING),
    FIX(4, "质检不通过，需要维修", LineStateEnum.RUNNING),
    CONFIRM_FIX(5, "质检不通过，待客户确定是否需要维修", LineStateEnum.RUNNING),
    RETURN_FIX(6, "返修", LineStateEnum.RUNNING),
    RETURN_NEW(7, "换货", LineStateEnum.RUNNING),

    ;
    private Integer value;
    private String desc;

    private LineStateEnum state;

    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

        RECEIVE_NORMAL_DELIVERY(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.NORMAL, "质检-正常通过"),

        RECEIVE_ANOMALY_DELIVERY(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.ANOMALY, "质检-异常通过"),

        RECEIVE_RETURN_DELIVERY(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.RETURN, "质检-去退货"),
        RECEIVE_RETURN_FIX(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.RETURN_FIX, "质检-去退货"),
        RECEIVE_RETURN_NEW(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.RETURN_NEW, "质检-去退货"),

        //首次去维修
        RECEIVE_FIX_DELIVERY(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.FIX, "质检-去维修"),

        RECEIVE_CONFIRM_FIX(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.CONFIRM_FIX, "质检-待确认"),

        FIX_RECEIVE(QualityTestingStateEnum.FIX, QualityTestingStateEnum.RECEIVE, "维修-通知-质检"),

        CONFIRM_FIX_OK(QualityTestingStateEnum.CONFIRM_FIX, QualityTestingStateEnum.RETURN, "维修-退货"),

        CONFIRM_FIX_NOT(QualityTestingStateEnum.CONFIRM_FIX, QualityTestingStateEnum.FIX, "维修-维修"),

        ;
        private QualityTestingStateEnum fromState;
        private QualityTestingStateEnum toState;
        private String desc;

    }

    public static QualityTestingStateEnum fromCode(int value) {
        return Arrays.stream(QualityTestingStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
