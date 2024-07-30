package com.seeease.flywheel.web.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/6/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkCompleteSkipRequest implements Serializable {

    /**
     * 单号
     */
    private String serialNo;
    /**
     * 任务节点
     */
    private String taskDefinitionKey;

    /**
     * 业务id(扩展点使用)
     */
    private String bizCode;

    /**
     * 用例(扩展点使用)
     */
    private String useCase;

    /**
     * 全局参数
     */
    private Map<String, Object> globalVariables;
}
