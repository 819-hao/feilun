package com.seeease.flywheel.recycle.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 回购物流返回
 */
@Data
@Accessors(chain=true)
public class BuyBackExpressResult implements Serializable {
    /**
     * 物流单号
     */
    private String expressNumber;

    /**
     * 发货物流单号
     */
    private String deliveryExpressNumber;

    private Date createdTime;
}
