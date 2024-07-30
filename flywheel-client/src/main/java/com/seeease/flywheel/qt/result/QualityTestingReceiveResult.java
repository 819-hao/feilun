package com.seeease.flywheel.qt.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 质检收货
 * @Date create in 2023/1/17 14:00
 */
@Data
public class QualityTestingReceiveResult implements Serializable {
    private Integer id;

    /**
     * 质检单号
     */
    private String serialNo;
}
