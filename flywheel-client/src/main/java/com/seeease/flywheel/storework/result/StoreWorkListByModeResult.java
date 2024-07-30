package com.seeease.flywheel.storework.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 列表页返回
 *
 * @Auther Gilbert
 * @Date 2023/1/17 18:48
 */
@Data
public class StoreWorkListByModeResult implements Serializable {

//

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;


    /**
     * 数量
     */
    private Integer number;


    /**
     * 型号
     */
    private String model;


    /**
     * 表身号
     */
    private String stockSn;


}
