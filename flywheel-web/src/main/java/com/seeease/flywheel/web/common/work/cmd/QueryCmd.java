package com.seeease.flywheel.web.common.work.cmd;

import lombok.Data;

/**
 * @author trio
 * @date 2023/1/15
 */
@Data
public class QueryCmd<T> extends BaseCmd<T> {
    /**
     * 是否查询任务
     */
    private boolean queryTask;
}
