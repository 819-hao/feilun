package com.seeease.flywheel.storework.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/14 10:49
 */
@Data
public class StoreWorkLogResult implements Serializable {


    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 型号
     */
    private String model;

    /**
     * 表身号
     */
    private String stockSn;

    private Integer stockId;

    /**
     * 机芯类型
     */
    private String movement;

    private String attachment;

    private String originSerialNo;

    private String expressNumber;

    private String taskArriveTime;

    private String createdTime;

    private Integer workType;

    private Integer workSource;

    private String createdBy;

    private Integer goodsId;

    private String finess;

    private Integer exceptionMark;
    private Integer optType;


}
