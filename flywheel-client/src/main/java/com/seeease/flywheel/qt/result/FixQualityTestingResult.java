package com.seeease.flywheel.qt.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修再质检
 * @Date create in 2023/1/17 15:48
 */
@Data
public class FixQualityTestingResult implements Serializable {

    private Integer id;

    /**
     * 质检单号
     */
    private String serialNo;
}
