package com.seeease.flywheel.goods.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/12/2
 */
@Data
public class StockMarketsInfo implements Serializable {
    /**
     * stock id
     */
    private Integer stockId;

    /**
     * 行情价
     */
    private BigDecimal marketsPrice;

    /**
     * 行情图片
     */
    private List<String> images;
}
