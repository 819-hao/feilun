package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseListRequest;
import com.seeease.flywheel.purchase.result.PurchaseListResult;
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
 * 采购列表查询
 *
 * @author trio
 * @date 2023/1/15
 */
@Service
@Extension(bizId = BizCode.PURCHASE, useCase = UseCase.QUERY_LIST)
public class PurchaseListQueryExt implements QueryExtPtI<PurchaseListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseFacade purchaseFacade;

    @Override
    public Class<PurchaseListRequest> getRequestClass() {
        return PurchaseListRequest.class;
    }

    @Override
    public void validate(QueryCmd<PurchaseListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(cmd.getRequest().getPage() >= 0 && cmd.getRequest().getLimit() > 0, "分页参数异常");
    }

    @Override
    public QueryResult query(QueryCmd<PurchaseListRequest> cmd) {

        PageResult<PurchaseListResult> data = purchaseFacade.list(cmd.getRequest());

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
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<PurchaseListRequest> cmd) {
        return Lists.newArrayList();
    }
}
