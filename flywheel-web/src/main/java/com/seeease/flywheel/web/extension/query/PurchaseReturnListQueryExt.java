package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.IPurchaseReturnFacade;
import com.seeease.flywheel.purchase.request.PurchaseReturnListRequest;
import com.seeease.flywheel.purchase.result.PurchaseReturnListResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryPageResult;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/3
 */
@Extension(bizId = BizCode.PURCHASE_RETURN, useCase = UseCase.QUERY_LIST)
@Service
public class PurchaseReturnListQueryExt implements QueryExtPtI<PurchaseReturnListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseReturnFacade facade;

    @Override
    public QueryResult query(QueryCmd<PurchaseReturnListRequest> cmd) {
        PageResult<PurchaseReturnListResult> data = facade.list(cmd.getRequest());
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
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<PurchaseReturnListRequest> cmd) {
        return null;
    }

    @Override
    public Class<PurchaseReturnListRequest> getRequestClass() {
        return PurchaseReturnListRequest.class;
    }

    @Override
    public void validate(QueryCmd<PurchaseReturnListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(cmd.getRequest().getPage() >= 0 && cmd.getRequest().getLimit() > 0, "分页参数异常");
    }
}
