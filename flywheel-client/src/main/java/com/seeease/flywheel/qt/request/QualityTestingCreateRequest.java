package com.seeease.flywheel.qt.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 质检单创建
 * @Date create in 2023/1/17 14:06
 */
@Data
@Accessors(chain = true)
public class QualityTestingCreateRequest implements Serializable {

    private Integer qtSource;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    private String storeWorkSerialNo;

    private Integer stockId;

    private Integer workId;

    private Integer customerId;

    private Integer customerContactId;

}
