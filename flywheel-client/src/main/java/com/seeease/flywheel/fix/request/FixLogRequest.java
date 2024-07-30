package com.seeease.flywheel.fix.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修列表
 * @Date create in 2023/1/18 14:58
 */
@Data
public class FixLogRequest extends PageRequest implements Serializable {

    private String serialNo;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 维修来源
     */
    private Integer fixSource;

    /**
     * 流转等级
     */
    private Integer flowGrade;

    /**
     * 是否加急
     */
    private Integer specialExpediting;

    /**
     * 维修状态
     */
    private Integer fixState;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 型号
     */
    private String model;

    /**
     * 是否返修
     */
    private Integer repairFlag;
}
