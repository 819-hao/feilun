package com.seeease.flywheel.allocate.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 调拨任务列表
 *
 * @author Tiro
 * @date 2023/8/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateTaskListRequest extends PageRequest implements Serializable {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 调出方
     */
    private Integer fromId;

    /**
     * 调入方
     */
    private Integer toId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 任务状态
     */
    private Integer taskState;

    /**
     * 调拨单号
     */
    private String allocateNo;
}
