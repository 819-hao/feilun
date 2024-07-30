package com.seeease.flywheel.sale.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 回购政策
 *
 * @author Tiro
 * @date 2023/4/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BuyBackPolicyInfo implements Serializable {

    /**
     * 回购时间
     */
    private Integer buyBackTime;

    /**
     * 回购折扣
     */
    private BigDecimal discount;

    /**
     * 置换折扣
     */
    private BigDecimal replacementDiscounts;

    private Integer priceThreshold;

    private Integer type;
}
