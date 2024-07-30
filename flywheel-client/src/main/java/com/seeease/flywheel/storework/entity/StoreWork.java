package com.seeease.flywheel.storework.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/3/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWork implements Serializable {

    /**
     * 预作业单号
     */
    private String serialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;
    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;

}
