package com.seeease.flywheel.fix.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 维修列表
 * @Date create in 2023/1/18 14:43
 */
@Data
public class FixLogResult implements Serializable {

    private Integer id;

    private String serialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    private String storeWorkSerialNo;

    /**
     * 流转等级
     */
    private Integer flowGrade;

    /**
     * 维修来源
     */
    private Integer fixSource;

    /**
     * 是否返修
     */
    private Integer repairFlag;

    /**
     * 是否加急
     */
    private Integer specialExpediting;

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
     * 维修状态
     */
    private Integer fixState;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 客户名称
     */
    private String customerCustomerName;

    /**
     * 创建时间
     */
    private String createdTime;
    private String taskArriveTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 维修时间
     */
    private Integer fixDay;

    /**
     * 维修费用
     */
    private BigDecimal fixMoney;

    /**
     * 维修建议
     */
    private String fixAdvise;

    /**
     * 备注
     */
    private String remark;
}
