package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 分配
 * @Date create in 2023/11/13 14:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixAllotRequest implements Serializable {

    /**
     * 接修id
     */
    private Integer fixId;

    /**
     * 维修师
     */
    private Integer maintenanceMasterId;

    /**
     * 维修单号
     */
    private String serialNo;
}
