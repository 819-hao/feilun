package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class SaleReturnOrderExpressNumberUploadRequest implements Serializable {
    /**
     * id
     */
    private Integer saleReturnId;
    /**
     * 快递单号
     */
    private String expressNumber;

    private Integer storeId;
    /**
     * 抖音退货时使用的shopId
     */
    private Integer tiktokSaleReturnShopId;
}
