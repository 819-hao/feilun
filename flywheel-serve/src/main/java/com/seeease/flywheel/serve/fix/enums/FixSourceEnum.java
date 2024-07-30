package com.seeease.flywheel.serve.fix.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
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
public enum FixSourceEnum implements IStateEnum<Integer> {

    /**
     * //同行经销
     * THJX(0),
     * //同行寄售
     * THJS(1),
     * //个人回收
     * GRHS(2),
     * //个人置换
     * GRZH(3),
     * //抖音回收
     * DYHS(4),
     * //抖音寄售
     * DYJS(5),
     * //抖音未知
     * DYUK(6),
     * YYCL(7),
     * //门店维修
     * STORE(8);
     */


    THJX(0, "同行采购"),
    THJS(1, "同行寄售"),
    GRHS(2, "个人回收"),
    GRZH(3, "个人置换"),
    YYCL(4, "异常处理"),
    DYHS(5, "个人寄售"),
    DYJS(6, "三方回收"),
    DYUK(7, "门店维修"),

    STORE(8, "其他"),
    ;
    private Integer value;
    private String desc;


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {

//        CREATE_RECEIVE_DELIVERY(QualityTestingStateEnum.CREATE, QualityTestingStateEnum.RECEIVE, "质检确认收货"),
//        RECEIVE_NORMAL_DELIVERY(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.NORMAL, "质检正常通过"),
//        RECEIVE_ANOMALY_DELIVERY(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.ANOMALY, "质检异常通过"),
//        RECEIVE_RETURN_DELIVERY(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.RETURN, "质检确认去退货"),
//        RECEIVE_FIX_DELIVERY(QualityTestingStateEnum.RECEIVE, QualityTestingStateEnum.FIX, "质检确认去维修"),
//        FIX_RECEIVE_DELIVERY(QualityTestingStateEnum.FIX, QualityTestingStateEnum.RECEIVE, "维修后质检确认"),
        ;
        private FixSourceEnum fromState;
        private FixSourceEnum toState;
        private String desc;

    }

    public static FixSourceEnum fromCode(int value) {
        return Arrays.stream(FixSourceEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
