package com.seeease.flywheel.web.common.work.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 用户任务
 *
 * @author trio
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTaskDto implements Serializable {
    /**
     * 业务key-订单号
     */
    private String businessKey;
    /**
     * 父业务key
     */
    private String parentBusinessKey;
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务key
     */
    private String taskDefinitionKey;

    /**
     * 任务名称
     */
    private String taskName;
    /**
     *
     */
    private Map<String, Object> variables;


    /**
     * @return
     */
    public boolean effective() {
        return StringUtils.isNotBlank(this.taskId);
    }

}
