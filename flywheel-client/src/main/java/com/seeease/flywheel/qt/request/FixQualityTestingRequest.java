package com.seeease.flywheel.qt.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修再质检
 * @Date create in 2023/1/17 14:06
 */
@Data
public class FixQualityTestingRequest implements Serializable {

    private Integer fixId;

}
