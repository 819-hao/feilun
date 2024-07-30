package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderListRequest;
import com.seeease.flywheel.sale.result.SaleOrderListResult;
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
 *
 */
@Service
@Extension(bizId = BizCode.SALE, useCase = UseCase.QUERY_LIST)
public class SaleOrderListQueryExt implements QueryExtPtI<SaleOrderListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade facade;

    @Override
    public Class<SaleOrderListRequest> getRequestClass() {
        return SaleOrderListRequest.class;
    }

    @Override
    public void validate(QueryCmd<SaleOrderListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(cmd.getRequest().getPage() >= 0 && cmd.getRequest().getLimit() > 0, "分页参数异常");
    }

    @Override
    public QueryResult query(QueryCmd<SaleOrderListRequest> cmd) {

        PageResult<SaleOrderListResult> data = facade.list(cmd.getRequest());

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
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<SaleOrderListRequest> cmd) {
        return Lists.newArrayList();
    }
}
