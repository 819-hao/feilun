package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.seeease.flywheel.web.common.work.flow.WorkflowStateEnum;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 流程启动记录
 *
 * @TableName workflow_start
 */
@TableName(value = "workflow_start", autoResultMap = true)
@Data
public class WorkflowStart implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务code
     */
    private String bizCode;

    /**
     * 用例
     */
    private String useCase;

    /**
     * 业务key
     */
    private String businessKey;

    /**
     * 流程定义key
     */
    private String processDefinitionKey;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 业务状态
     */
    private WorkflowStateEnum bizState;

    /**
     * 工作流状态
     */
    private WorkflowStateEnum workState;

    /**
     * 流程启动参数
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private Map<String, Object> processVariables;

    /**
     * 乐观锁
     */
    @Version
    private Integer revision;

    /**
     * 创建人id
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer createdId;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;

    /**
     * 修改人id
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updatedId;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;

    /**
     * 删除标识
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}