package com.seeease.flywheel.web.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowToDoListRequest implements Serializable {

    /**
     * 单号
     */
    private String serialNo;
    /**
     * 任务节点
     */
    private List<String> taskDefinitionKeys;
    private int page;
    private int limit;
}
