package com.seeease.flywheel.web.extension.query;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.FixDetailsRequest;
import com.seeease.flywheel.fix.result.FixDetailsResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
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
@Extension(bizId = BizCode.FIX, useCase = UseCase.QUERY_DETAILS)
public class FixDetailsQueryExt implements QueryExtPtI<FixDetailsRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IFixFacade fixFacade;

    @Override
    public QueryResult query(QueryCmd<FixDetailsRequest> cmd) {

        FixDetailsResult data = fixFacade.details(cmd.getRequest());

        return QuerySingleResult.builder()
                .result(data)
                .task(UserTaskDto.builder()
                        .businessKey(data.getSerialNo())
                        .build())
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<FixDetailsRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.REPAIR_RECEIVING
                , TaskDefinitionKeyEnum.REPAIR_COMPLETED
                , TaskDefinitionKeyEnum.TASK_ALLOT
                , TaskDefinitionKeyEnum.TASK_DECISION
        );
    }

    @Override
    public Class<FixDetailsRequest> getRequestClass() {
        return FixDetailsRequest.class;
    }

    @Override
    public void validate(QueryCmd<FixDetailsRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) ||
                StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id不能为空");
    }
}
