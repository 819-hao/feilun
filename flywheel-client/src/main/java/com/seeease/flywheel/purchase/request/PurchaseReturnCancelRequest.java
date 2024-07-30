package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Data
public class PurchaseReturnCancelRequest implements Serializable {
    /**
     * 采购退货单id
     */
    private Integer purchaseReturnId;
}
