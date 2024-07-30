package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.FixListRequest;
import com.seeease.flywheel.fix.result.FixListResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryPageResult;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 维修列表
 * @Date create in 2023/1/18 17:18
 */
@Service
@Extension(bizId = BizCode.FIX, useCase = UseCase.QUERY_LIST)
public class FixListQueryExt implements QueryExtPtI<FixListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IFixFacade fixFacade;

    @Override
    public QueryResult query(QueryCmd<FixListRequest> cmd) {

        PageResult<FixListResult> data = fixFacade.list(cmd.getRequest());

        List<QuerySingleResult> dataList = data.getResult()
                .stream()
                .map(t -> QuerySingleResult.builder()
                        .result(t)
                        .task(UserTaskDto.builder()
                                .businessKey(t.getSerialNo())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return QueryPageResult.builder()
                .totalCount(data.getTotalCount())
                .totalPage(data.getTotalPage())
                .resultList(dataList)
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<FixListRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.REPAIR_RECEIVING
                , TaskDefinitionKeyEnum.REPAIR_COMPLETED
                , TaskDefinitionKeyEnum.TASK_ALLOT
                , TaskDefinitionKeyEnum.TASK_DECISION
        );
    }

    @Override
    public Class<FixListRequest> getRequestClass() {
        return FixListRequest.class;
    }

    @Override
    public void validate(QueryCmd<FixListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(cmd.getRequest().getPage() >= 0 && cmd.getRequest().getLimit() > 0, "分页参数异常");
    }
}
