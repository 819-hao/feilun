package com.seeease.flywheel.qt.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 质检列表
 * @Date create in 2023/1/18 14:43
 */
@Data
public class QualityTestingWaitDeliverListResult implements Serializable {


    /**
     * 源头单据单号
     */
    private String originSerialNo;

    private String storeWorkSerialNo;

    private Integer id;

    private Integer stockId;

    private Integer goodsId;

    private String brandName;

    private String seriesName;

    private String model;

    private String stockSn;

    private String attachment;

    /**
     * 转交方
     */
    private Integer deliverTo;

    /**
     * 转交状态
     */
    private Integer deliver;


    /**
     * 埋点时间
     */
    private String taskArriveTime;
}
