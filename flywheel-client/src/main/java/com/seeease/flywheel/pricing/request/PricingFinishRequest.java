package com.seeease.flywheel.pricing.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 定价申请
 * @Date create in 2023/3/21 10:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingFinishRequest implements Serializable {

    private Integer id;

    private String serialNo;

    private BigDecimal tobPrice;

    private BigDecimal tocPrice;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;
    /**
     * 商品级别
     */
    private String goodsLevel;

    /**
     * 销售等级
     */
    private Integer salesPriority;



    private BigDecimal wuyuBuyBackPrice;

    private Integer demandId;
}
