package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPromotionInfo implements Serializable {

    private Integer stockId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 活动价格
     */
    private BigDecimal promotionPrice;

    /**
     * 活动寄售价格
     */
    private BigDecimal promotionConsignmentPrice;

    private Date startTime;

    private Date endTime;
}
