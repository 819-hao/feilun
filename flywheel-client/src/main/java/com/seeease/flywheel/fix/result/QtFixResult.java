package com.seeease.flywheel.fix.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 质检再维修
 * @Date create in 2023/1/17 17:25
 */
@Data
public class QtFixResult implements Serializable {

    private Integer id;

    /**
     * 维修单号
     */
    private String serialNo;
}
