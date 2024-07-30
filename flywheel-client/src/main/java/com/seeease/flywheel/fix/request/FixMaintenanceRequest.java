package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修结果
 * @Date create in 2023/2/3 11:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixMaintenanceRequest implements Serializable {

    private Integer fixId;

    private String serialNo;

    private Integer maintenanceMasterId;

}
