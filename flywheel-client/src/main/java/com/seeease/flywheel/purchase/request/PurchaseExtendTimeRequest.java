package com.seeease.flywheel.purchase.request;

import lombok.Data;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/15 11:15
 */
@Data
public class PurchaseExtendTimeRequest extends PurchaseCancelRequest {

    private String dealEndTime;
}
