package com.seeease.flywheel.storework.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 入库待收货的返货
 *
 * @Auther Gilbert
 * @Date 2023/1/17 17:47
 */
@Data
public class StoreWorkCreateResult implements Serializable {

    //入库待收货的返回id
    private Integer id;
    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 预作业单号
     */
    private String serialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    private Integer goodsId;

    /**
     * 配对标记
     */
    private String mateMark;

    private Integer workSource;
    /**
     * 发货物流单号
     */
    private String deliveryExpressNumber;
}
