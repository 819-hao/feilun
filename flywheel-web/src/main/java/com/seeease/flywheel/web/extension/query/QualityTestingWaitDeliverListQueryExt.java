package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.qt.IQualityTestingFacade;
import com.seeease.flywheel.qt.request.QualityTestingWaitDeliverListRequest;
import com.seeease.flywheel.qt.result.QualityTestingWaitDeliverListResult;
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
 * @Description 待质检转交列表
 * @Date create in 2023/1/18 17:18
 */
@Service
@Extension(bizId = BizCode.QT, useCase = UseCase.WAIT_DELIVER)
public class QualityTestingWaitDeliverListQueryExt implements QueryExtPtI<QualityTestingWaitDeliverListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IQualityTestingFacade qualityTestingFacade;

    @Override
    public QueryResult query(QueryCmd<QualityTestingWaitDeliverListRequest> cmd) {

        PageResult<QualityTestingWaitDeliverListResult> data = qualityTestingFacade.qtWaitDeliver(cmd.getRequest());

        List<QuerySingleResult> dataList = data.getResult()
                .stream()
                .map(t -> QuerySingleResult.builder()
                        .result(t)
                        .task(UserTaskDto.builder()
                                .businessKey(t.getStoreWorkSerialNo())
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
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<QualityTestingWaitDeliverListRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.QT_WAIT_DELIVER);
    }

    @Override
    public Class<QualityTestingWaitDeliverListRequest> getRequestClass() {
        return QualityTestingWaitDeliverListRequest.class;
    }

    @Override
    public void validate(QueryCmd<QualityTestingWaitDeliverListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(cmd.getRequest().getPage() >= 0 && cmd.getRequest().getLimit() > 0, "分页参数异常");
    }
}