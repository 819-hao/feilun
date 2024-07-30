package com.seeease.flywheel.goods.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2024/1/18
 */
@Data
public class StockPromotionInfoGetRequest implements Serializable {
    /**
     * 库存商品id
     */
    private Integer stockId;
}
