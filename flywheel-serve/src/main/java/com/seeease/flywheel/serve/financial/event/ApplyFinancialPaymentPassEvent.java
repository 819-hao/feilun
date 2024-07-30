package com.seeease.flywheel.serve.financial.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import lombok.Getter;

/**
 * 申请打款通过
 */
@Getter
public class ApplyFinancialPaymentPassEvent implements BillHandlerEvent {

    private Integer afpId;

    private String afpSerialNo;

    private String originSerialNo;

    private ApplyFinancialPaymentTypeEnum typePayment;

    public ApplyFinancialPaymentPassEvent(Integer afpId, String afpSerialNo, String originSerialNo, ApplyFinancialPaymentTypeEnum typeEnum) {
        this.afpId = afpId;
        this.afpSerialNo = afpSerialNo;
        this.originSerialNo = originSerialNo;
        this.typePayment = typeEnum;
    }

}
