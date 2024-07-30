package com.seeease.flywheel.serve.storework.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/10/30
 */
@Data
public class WmsWorkCapacityDTO implements Serializable {
    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 已经存在的数量
     */
    private Integer inWorkQuantity;

    /**
     * 型号
     */
    private String model;

    /**
     * 库存数量
     */
    private Integer stockQuantity;


    /**
     * 销售受限的
     *
     * @param quantity
     * @return
     */
    public boolean restrictedSale(Integer quantity) {
        return inWorkQuantity + quantity > stockQuantity;
    }
}
