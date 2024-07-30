package com.seeease.flywheel.fix.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 维修详情
 *
 * @author dmm
 * @date 2023/1/16
 */
@Data
public class FixDetailsRequest implements Serializable {
    /**
     * 采购单id
     */
    private Integer id;
    /**
     * 采购单号
     */
    private String serialNo;

//    private String storeWorkSerialNo;
}
