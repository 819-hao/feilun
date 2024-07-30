package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.RecycleOrderListRequest;
import com.seeease.flywheel.recycle.result.RecyclingListResult;
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
 * 采购详情查询
 *
 * @author trio
 * @date 2023/1/15
 */
@Service
@Extension(bizId = BizCode.MALL, useCase = UseCase.QUERY_LIST)
public class MallListQueryExt implements QueryExtPtI<RecycleOrderListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade recycleOderFacade;


    @Override
    public Class<RecycleOrderListRequest> getRequestClass() {
        return RecycleOrderListRequest.class;
    }

    @Override
    public void validate(QueryCmd<RecycleOrderListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");

    }

    @Override
    public QueryResult query(QueryCmd<RecycleOrderListRequest> cmd) {
        PageResult<RecyclingListResult> data = recycleOderFacade.list(cmd.getRequest());

        List<QuerySingleResult> dataList = data.getResult()
                .stream()
                .map(t -> QuerySingleResult.builder()
                        .result(t)
                        .task(UserTaskDto.builder()
                                .businessKey(t.getSerial())
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
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<RecycleOrderListRequest> cmd) {
        return Lists.newArrayList(
                TaskDefinitionKeyEnum.PURCHASE_CREATE,
                TaskDefinitionKeyEnum.FIRST_CLIENT_VERIFY,
                TaskDefinitionKeyEnum.FIRST_OFFER,
                TaskDefinitionKeyEnum.SECOND_OFFER,
                TaskDefinitionKeyEnum.SECOND_CLIENT_VERIFY
        );
    }
}
