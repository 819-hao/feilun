package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 20:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPromotionLogResult implements Serializable {


    private String stockSn;
    private String createdBy;
    private String createdTime;
    /**
     * 活动价格
     */
    private BigDecimal promotionPrice;

    /**
     * 活动寄售价格
     */
    private BigDecimal promotionConsignmentPrice;

    /**
     * 寄售比例
     */
    private BigDecimal consignmentRatio;

    private Date startTime;

    private Date endTime;
}
