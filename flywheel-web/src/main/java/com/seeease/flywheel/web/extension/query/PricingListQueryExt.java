package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingListRequest;
import com.seeease.flywheel.pricing.result.PricingListResult;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 定价列表
 * @Date create in 2023/1/18 17:18
 */
@Service
@Extension(bizId = BizCode.PRICING, useCase = UseCase.QUERY_LIST)
public class PricingListQueryExt implements QueryExtPtI<PricingListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;

    @Override
    public QueryResult query(QueryCmd<PricingListRequest> cmd) {

        PageResult<PricingListResult> data = pricingFacade.list(cmd.getRequest());

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
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<PricingListRequest> cmd) {
        switch (Optional.ofNullable(cmd.getRequest())
                .map(PricingListRequest::getPricingState)
                .orElse(0)) {
            case 1:
                return Lists.newArrayList(TaskDefinitionKeyEnum.WAIT_PRICING);
            case 2:
                return Lists.newArrayList(TaskDefinitionKeyEnum.WAIT_CHECK);
            default:
                return Lists.newArrayList();
        }
    }

    @Override
    public Class<PricingListRequest> getRequestClass() {
        return PricingListRequest.class;
    }

    @Override
    public void validate(QueryCmd<PricingListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(cmd.getRequest().getPage() >= 0 && cmd.getRequest().getLimit() > 0, "分页参数异常");
    }
}
