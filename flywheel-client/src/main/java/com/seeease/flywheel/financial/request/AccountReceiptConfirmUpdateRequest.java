package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 企业微信小程序---新增确认收货单
 */
@Data
public class AccountReceiptConfirmUpdateRequest implements Serializable {

    private Integer id;

    private String batchPictureUrl;
}
