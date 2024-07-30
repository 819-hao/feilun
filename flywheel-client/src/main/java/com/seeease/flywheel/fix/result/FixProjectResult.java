package com.seeease.flywheel.fix.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/2 17:12
 */
@Data
public class FixProjectResult implements Serializable {

    private Integer id;

    /**
     * 维修项目名称
     */
    private String name;

    private String anticipatedMaintenanceTime;
}
