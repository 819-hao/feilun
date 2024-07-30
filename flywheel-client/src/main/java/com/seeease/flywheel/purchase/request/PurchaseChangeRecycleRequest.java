package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/15 11:15
 */
@Data
public class PurchaseChangeRecycleRequest extends PurchaseCancelRequest {

    private BigDecimal recyclePrice;

    private Integer stockId;
}
