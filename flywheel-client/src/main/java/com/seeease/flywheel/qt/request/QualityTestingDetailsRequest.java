package com.seeease.flywheel.qt.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 质检详情
 *
 * @author dmmasxmf
 * @date 2023/1/16
 */
@Data
public class QualityTestingDetailsRequest implements Serializable {
    /**
     * 采购单id
     */
    private Integer id;
    /**
     * 采购单号
     */
    private String serialNo;
}
