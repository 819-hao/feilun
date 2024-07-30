package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.qt.IQualityTestingFacade;
import com.seeease.flywheel.qt.request.QualityTestingListRequest;
import com.seeease.flywheel.qt.result.QualityTestingListResult;
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
 * @Description 质检列表
 * @Date create in 2023/1/18 17:18
 */
@Service
@Extension(bizId = BizCode.QT, useCase = UseCase.QUERY_LIST)
public class QualityTestingListQueryExt implements QueryExtPtI<QualityTestingListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IQualityTestingFacade qualityTestingFacade;

    @Override
    public QueryResult query(QueryCmd<QualityTestingListRequest> cmd) {

        PageResult<QualityTestingListResult> data = qualityTestingFacade.list(cmd.getRequest());

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
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<QualityTestingListRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.QT_DETERMINE, TaskDefinitionKeyEnum.REPAIR_RESULT_DETERMINE);
    }

    @Override
    public Class<QualityTestingListRequest> getRequestClass() {
        return QualityTestingListRequest.class;
    }

    @Override
    public void validate(QueryCmd<QualityTestingListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(cmd.getRequest().getPage() >= 0 && cmd.getRequest().getLimit() > 0, "分页参数异常");
    }
}
