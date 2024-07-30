package com.seeease.flywheel.financial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentCancelRequest implements Serializable {

    /**
     *
     */
    private String serialNo;

    /**
     * 使用场景
     */
    private UseScenario useScenario;

    public enum UseScenario {
        /**
         * 同行采购 取消
         */
        PURCHASE_PEER_CANCEL,
        /**
         * 个人回收 取消
         */
        RECYCLE_PERSON_CANCEL,
        /**
         * 采购绑定申请打款单 已使用
         */
        PURCHASE_BINDING,
        /**
         * 申请打款单解绑 为未使用
         */
        PURCHASE_UNBIND,
        /**
         * 余额退款
         */
        BALANCE_REFUND;
    }
}
