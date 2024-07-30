package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.storework.IWmsWorkCollectFacade;
import com.seeease.flywheel.storework.request.WmsWorkListRequest;
import com.seeease.flywheel.storework.result.WmsWorkListResult;
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
 * @author Tiro
 * @date 2023/9/1
 */
@Service
@Extension(bizId = BizCode.WMS_COLLECT, useCase = UseCase.QUERY_LIST)
public class WmsWorkCollectListQueryExt implements QueryExtPtI<WmsWorkListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IWmsWorkCollectFacade wmsWorkCollectFacade;

    @Override
    public QueryResult query(QueryCmd<WmsWorkListRequest> cmd) {
        PageResult<WmsWorkListResult> result = wmsWorkCollectFacade.listWork(cmd.getRequest());
        return QueryPageResult.builder()
                .totalCount(result.getTotalCount())
                .totalPage(result.getTotalPage())
                .resultList(result.getResult()
                        .stream()
                        .map(t -> QuerySingleResult.builder()
                                .result(t)
                                .task(UserTaskDto.builder()
                                        .businessKey(t.getSerialNo())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<WmsWorkListRequest> cmd) {
        switch (cmd.getRequest().getUseScenario()) {
            case WAIT_DELIVERY:
                return Lists.newArrayList(TaskDefinitionKeyEnum.SHOP_DELIVERY);
            default:
                return null;
        }
    }

    @Override
    public Class<WmsWorkListRequest> getRequestClass() {
        return WmsWorkListRequest.class;
    }

    @Override
    public void validate(QueryCmd<WmsWorkListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getUseScenario(), "场景值不能为空");
    }
}
