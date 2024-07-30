package com.seeease.flywheel.serve.goods.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wbh
 * @date 2023/3/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyBackPolicyStrategyPo implements Serializable {
    private Integer stockId;
    private String finess;
    private String sourceSubjectId;
    private String customerId;
    private BigDecimal purchasePrice;
    private BigDecimal pricePub;
    private String stockSrc;
    private String warrantyDate;
    private BigDecimal clinchPrice;
    private BigDecimal tocPrice;
    private BigDecimal totalPrice;
}
