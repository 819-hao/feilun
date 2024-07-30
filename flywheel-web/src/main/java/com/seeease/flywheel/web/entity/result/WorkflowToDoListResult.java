package com.seeease.flywheel.web.entity.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author trio
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowToDoListResult implements Serializable {
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务定义key
     */
    private String taskDefinitionKey;
    /**
     * 流程定义key
     */
    private String processDefinitionKey;
    /**
     * 流程名称
     */
    private String processDefinitionName;
    /**
     * 业务key
     */
    private String businessKey;
    /**
     * 父业务建
     */
    private String parentBusinessKey;
    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}
