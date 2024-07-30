package com.seeease.flywheel.pricing.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/22 19:45
 */
@Data
public class PricingLog implements Serializable {

    private Integer pricingNode;

    private String createdBy;

    private String createdTime;

    private Integer autoPrice;
}
