package com.seeease.flywheel.serve.purchase.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.purchase.enums.PurchaseTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/1/9
 */
@Data
public class PurchaseUploadEvent implements Serializable, BillHandlerEvent {
    /**
     * 采购单id
     */
    private Integer purchaseId;

    private PurchaseTypeEnum purchaseType;

    public PurchaseUploadEvent(Integer purchaseId, PurchaseTypeEnum purchaseType) {
        this.purchaseId = purchaseId;
        this.purchaseType = purchaseType;
    }
}
