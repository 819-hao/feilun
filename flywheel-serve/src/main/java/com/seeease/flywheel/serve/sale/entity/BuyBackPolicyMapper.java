package com.seeease.flywheel.serve.sale.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/9 11:06
 */
@Data
public class BuyBackPolicyMapper implements Serializable {
    /**
     * {"buyBackTime":"12","discount":9,"priceThreshold":20000,"replacementDiscounts":0.5,"type":1}
     */

    private Integer buyBackTime;

    /**
     *
     */
    private BigDecimal discount;

    private Integer priceThreshold;

    /**
     *
     */
    private BigDecimal replacementDiscounts;

    private Integer type;
}
