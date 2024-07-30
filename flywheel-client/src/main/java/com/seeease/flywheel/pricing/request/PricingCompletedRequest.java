package com.seeease.flywheel.pricing.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingCompletedRequest implements Serializable {

    private Integer id;

    private String serialNo;

    private Integer checkState;

    /**
     * 商品级别
     */
    private String goodsLevel;

    /**
     * 销售等级
     */
    private Integer salesPriority;

    private BigDecimal tobPrice;

    private BigDecimal tocPrice;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    private BigDecimal wuyuBuyBackPrice;

    private Integer demandId;
}
