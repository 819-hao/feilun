package com.seeease.flywheel.web.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单报价
 *
 * @author Tiro
 * @date 2023/10/20
 */
@Data
public class XyRecycleOrderQuoteRequest implements Serializable {

    /**
     * id
     */
    private Integer id;
    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 情况
     */
    private String summary;
}
