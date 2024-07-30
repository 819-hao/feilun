package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingDetailsRequest;
import com.seeease.flywheel.pricing.result.PricingDetailsResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * @Author Mr. Du
 * @Description 维修详情
 * @Date create in 2023/1/18 17:18
 */
@Service
@Extension(bizId = BizCode.PRICING, useCase = UseCase.QUERY_DETAILS)
public class PricingDetailsQueryExt implements QueryExtPtI<PricingDetailsRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;

    @Override
    public QueryResult query(QueryCmd<PricingDetailsRequest> cmd) {

        PricingDetailsResult data = pricingFacade.details(cmd.getRequest());

        return QuerySingleResult.builder()
                .result(data)
                .task(UserTaskDto.builder()
                        .businessKey(data.getSerialNo())
                        .build())
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<PricingDetailsRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.WAIT_PRICING, TaskDefinitionKeyEnum.WAIT_CHECK);
    }

    @Override
    public Class<PricingDetailsRequest> getRequestClass() {
        return PricingDetailsRequest.class;
    }

    @Override
    public void validate(QueryCmd<PricingDetailsRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) ||
                StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id不能为空");
    }
}
