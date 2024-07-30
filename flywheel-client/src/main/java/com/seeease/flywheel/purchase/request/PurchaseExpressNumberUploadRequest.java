package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/1/9
 */
@Data
public class PurchaseExpressNumberUploadRequest implements Serializable {
    /**
     * 采购单id
     */
    private Integer purchaseId;
    /**
     * 快递单号
     */
    private String expressNumber;

    private Integer storeId;
}
