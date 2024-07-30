package com.seeease.flywheel.serve.goods.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/10/30
 */
@Data
public class StockQuantityDTO implements Serializable {


    /**
     * 采购商品
     */
    private Integer goodsId;

    /**
     * 型号
     */
    private String model;

    /**
     * 库存数量
     */
    private Integer stockQuantity;
}
