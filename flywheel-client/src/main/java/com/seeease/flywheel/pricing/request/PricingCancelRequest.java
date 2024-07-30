package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class PricingCancelRequest implements Serializable {

    private Integer stockId;

    private String originSerialNo;

    private Integer createdId;

    private String createdBy;

    private Integer storeId;
}
