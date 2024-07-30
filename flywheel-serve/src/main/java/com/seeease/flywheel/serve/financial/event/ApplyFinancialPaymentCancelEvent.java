package com.seeease.flywheel.serve.financial.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * 申请打款单取消
 * @author wbh
 * @date 2023/10/10
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentCancelEvent implements BillHandlerEvent {
    private Integer refundType;

    private Integer customerId;

    /**
     * 退款金额
     */
    private BigDecimal clinchPrice;

    private Integer contactId;

    private ApplyFinancialPaymentTypeEnum typePayment;

    private Integer userId;

    private String originSerialNo;
}
