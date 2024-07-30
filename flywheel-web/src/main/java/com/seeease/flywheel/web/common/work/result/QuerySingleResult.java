package com.seeease.flywheel.web.common.work.result;

import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author trio
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuerySingleResult implements QueryResult {
    /**
     * 业务详情结果
     */
    private Object result;
    /**
     * 用户任务
     */
    private UserTaskDto task;
}
