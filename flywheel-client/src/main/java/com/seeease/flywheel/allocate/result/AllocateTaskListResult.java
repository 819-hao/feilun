package com.seeease.flywheel.allocate.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/8/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateTaskListResult implements Serializable {

    /**
     * 调拨单号
     */
    private String allocateNo;

    /**
     * 任务状态
     */
    private Integer taskState;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 调出方
     */
    private String fromName;

    /**
     * 调入方
     */
    private String toName;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 商品状态
     */
    private Integer stockStatus;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 型号
     */
    private String model;

    /**
     * 主图
     */
    private String image;
}
