package com.seeease.flywheel.pricing.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class PricingCreateResult implements Serializable {

    private String serialNo;

    private Integer stockId;

    /**
     * 默认手动
     */
    private Integer auto = 1;
}
