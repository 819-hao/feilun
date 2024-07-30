package com.seeease.flywheel.serve.financial.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
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
public enum ApplyFinancialPaymentStateEnum implements IStateEnum<Integer> {
    PENDING_REVIEW(0, "待审核"),//待确认
    PAID(1, "已打款"),//已确认
    REJECTED(2, "已驳回"),
    CANCEL(3, "已取消"),
    OBSOLETE(4, "已作废"),
    ;
    private Integer value;
    private String desc;


    @Getter
    @AllArgsConstructor
    public enum TransitionEnum implements ITransitionStateEnum {
        PENDING_REVIEW_TO_PAID(ApplyFinancialPaymentStateEnum.PENDING_REVIEW, ApplyFinancialPaymentStateEnum.PAID, "已打款"),
        PENDING_REVIEW_TO_REJECTED(ApplyFinancialPaymentStateEnum.PENDING_REVIEW, ApplyFinancialPaymentStateEnum.REJECTED, "已驳回"),
        REJECTED_TO_PENDING_REVIEW(ApplyFinancialPaymentStateEnum.REJECTED, ApplyFinancialPaymentStateEnum.PENDING_REVIEW, "重新待审核"),
        PENDING_REVIEW_TO_CANCEL(ApplyFinancialPaymentStateEnum.PENDING_REVIEW, ApplyFinancialPaymentStateEnum.CANCEL, "待审核取消"),
        REJECTED_TO_CANCEL(ApplyFinancialPaymentStateEnum.REJECTED, ApplyFinancialPaymentStateEnum.CANCEL, "驳回取消"),
        PAID_TO_OBSOLETE(ApplyFinancialPaymentStateEnum.PAID, ApplyFinancialPaymentStateEnum.OBSOLETE, "打款作废"),
        OBSOLETE_TO_PENDING_REVIEW(ApplyFinancialPaymentStateEnum.OBSOLETE, ApplyFinancialPaymentStateEnum.PENDING_REVIEW, "作废重新审核"),
        ;
        private ApplyFinancialPaymentStateEnum fromState;
        private ApplyFinancialPaymentStateEnum toState;
        private String desc;

    }

    public static ApplyFinancialPaymentStateEnum fromCode(int value) {
        return Arrays.stream(ApplyFinancialPaymentStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
