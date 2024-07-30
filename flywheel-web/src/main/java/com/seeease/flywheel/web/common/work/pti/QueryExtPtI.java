package com.seeease.flywheel.web.common.work.pti;

import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.result.QueryResult;

import java.util.List;

/**
 * @author trio
 * @date 2023/1/15
 */
public interface QueryExtPtI<T> extends WorkExtPtI<T, QueryCmd<T>> {

    /**
     * 查询
     *
     * @param cmd
     * @return
     */
    QueryResult query(QueryCmd<T> cmd);

    /**
     * 任务定义key集合
     * <p>
     * 简单来说就是需要查询什么任务
     *
     * @return
     */
    List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<T> cmd);

}
