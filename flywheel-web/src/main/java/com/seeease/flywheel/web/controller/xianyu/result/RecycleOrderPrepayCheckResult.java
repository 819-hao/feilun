package com.seeease.flywheel.web.controller.xianyu.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Tiro
 * @date 2023/10/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecycleOrderPrepayCheckResult extends QiMenBaseResult {
    /**
     * true：满足信用预付 false：不满足信用预付
     */
    private Boolean creditPay;
    /**
     * 信用预付金额,单位分
     */
    private Long creditPayAmount;
    /**
     * true：可以下单 false：不能下单
     */
    private Boolean createOrder;
}
