package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/1/9
 */
@Data
public class PurchaseAcceptRepairRequest implements Serializable {
    /**
     * 采购单id
     */
    private Integer purchaseId;

    private Integer storeId;

    /**
     * 是否维修
     */
    private Integer acceptState;
}
