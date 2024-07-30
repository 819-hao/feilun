package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 采购取消
 *
 * @author Tiro
 * @date 2023/1/19
 */
@Data
public class PurchaseCancelRequest implements Serializable {
    /**
     * 采购单id
     */
    private Integer purchaseId;
}
