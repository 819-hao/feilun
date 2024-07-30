package com.seeease.flywheel.serve.financial.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 确认收款单状态枚举值
 */
@Getter
@AllArgsConstructor
public enum AccountReceiptConfirmStatusEnum {

    /**
     * 待确认
     */
    WAIT(0),

    /**
     * 部分确认
     */
    PART(1),

    /**
     * 已确认
     */
    FINISH(2),

    /**
     * 已驳回
     */
    REJECTED(3),

    /**
     * 已取消
     */
    CANCEL(4),
    ;


    private final int value;
}
