package com.seeease.flywheel.fix.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修创建
 * @Date create in 2023/1/17 17:25
 */
@Data
public class FixCreateResult implements Serializable {

    private Integer id;

    /**
     * 维修单号
     */
    private String serialNo;

    private Integer stockId;

    //工作流参数

    /**
     * 门店简码
     */
    private String shortcodes;

    /**
     * 总部字段
     */
    private Integer storeId;


    /**
     * 是否接修
     */
    private Integer isRepair;

    /**
     * 是否分配
     */
    private Integer isAllot;

    //工作流参数
}
