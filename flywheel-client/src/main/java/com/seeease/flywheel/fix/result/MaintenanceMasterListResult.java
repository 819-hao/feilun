package com.seeease.flywheel.fix.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description 维修师当前
 * @Date create in 2023/11/20 13:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceMasterListResult implements Serializable {

    private Integer maintenanceMasterId;

    /**
     * 维修师
     */
    private String maintenanceMasterName;

    /**
     * 总任务数量
     */
    private Integer currentTask;
//
//    /**
//     * 当前到期数量
//     */
//    private Integer todayTask;
//
//    /**
//     * 紧急数量
//     */
//    private Integer specialTask;
}
