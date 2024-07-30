package com.seeease.flywheel.helper.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BreakPriceAuditSubmitRequest implements Serializable {
    private Integer id;
    /**
     * 商品id
     */
    private Integer stockId;
    /**
     * 成交价
     */
    private BigDecimal clinchPrice;
    /**
     * 原因
     */
    private String reason;


}
