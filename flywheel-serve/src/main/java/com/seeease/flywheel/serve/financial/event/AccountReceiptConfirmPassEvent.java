package com.seeease.flywheel.serve.financial.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 确认收款
 */
@Getter
public class AccountReceiptConfirmPassEvent implements BillHandlerEvent {

    private String arcSerialNo;

    private String originSerialNo;

    private Integer typePayment;
    private FinancialStatusEnum statusEnum;
    /**
     * 待核销金额
     */
    private BigDecimal waitAuditPrice;

    private List<Integer> stockIdList;

    public AccountReceiptConfirmPassEvent(FinancialStatusEnum statusEnum, String arcSerialNo, String originSerialNo, Integer typeEnum, BigDecimal waitAuditPrice, List<Integer> stockIdList) {
        this.statusEnum = statusEnum;
        this.arcSerialNo = arcSerialNo;
        this.originSerialNo = originSerialNo;
        this.typePayment = typeEnum;
        this.waitAuditPrice = waitAuditPrice;
        this.stockIdList = stockIdList;
    }

}
