package com.seeease.flywheel.fix.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 质检再维修
 * @Date create in 2023/1/17 17:25
 */
@Data
public class QtFixRequest implements Serializable {

    private Integer fixId;

    private String fixAdvise;

    private Integer fixTimes;

    private Integer repairFlag;

    private Integer fixDay;
}
