package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/23 15:30
 */
@Data
public class PricingCancelLineRequest implements Serializable {

    /**
     * 重新发起定价
     */
    private Integer id;

    private String serialNo;
}
