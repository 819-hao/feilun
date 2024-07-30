package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class PricingDetailsRequest implements Serializable {

    private Integer id;

    private String serialNo;

}
