package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.qt.IQualityTestingFacade;
import com.seeease.flywheel.qt.request.QualityTestingDetailsRequest;
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
 * @Description 质检详情
 * @Date create in 2023/1/18 17:18
 */
@Service
@Extension(bizId = BizCode.QT, useCase = UseCase.QUERY_DETAILS)
public class QualityTestingDetailsQueryExt implements QueryExtPtI<QualityTestingDetailsRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IQualityTestingFacade qualityTestingFacade;


    @Override
    public QueryResult query(QueryCmd<QualityTestingDetailsRequest> cmd) {

//        QualityTestingDetailsResult data = qualityTestingFacade.details(cmd.getRequest());

        return QuerySingleResult.builder()
//                .result(data)
                .task(UserTaskDto.builder()
//                        .businessKey(data.getStoreWorkSerialNo())
                        .build())
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<QualityTestingDetailsRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.QT_DETERMINE);
    }

    @Override
    public Class<QualityTestingDetailsRequest> getRequestClass() {
        return QualityTestingDetailsRequest.class;
    }

    @Override
    public void validate(QueryCmd<QualityTestingDetailsRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) ||
                StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id不能为空");
    }
}
