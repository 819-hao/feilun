package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/14 15:14
 */
@Data
public class PurchaseForSaleRequest implements Serializable {

    private String originSaleSerialNo;

    private Integer originStockId;
}