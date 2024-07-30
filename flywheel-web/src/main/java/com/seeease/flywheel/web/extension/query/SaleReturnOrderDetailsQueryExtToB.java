package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.SaleReturnOrderDetailsRequest;
import com.seeease.flywheel.sale.result.SaleReturnOrderDetailsResult;
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
 * 
 */
@Service
@Extension(bizId = BizCode.TO_B_SALE_RETURN, useCase = UseCase.QUERY_DETAILS)
public class SaleReturnOrderDetailsQueryExtToB implements QueryExtPtI<SaleReturnOrderDetailsRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleReturnOrderFacade facade;


    @Override
    public Class<SaleReturnOrderDetailsRequest> getRequestClass() {
        return SaleReturnOrderDetailsRequest.class;
    }

    @Override
    public void validate(QueryCmd<SaleReturnOrderDetailsRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) ||
                StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id不能为空");
    }

    @Override
    public QueryResult query(QueryCmd<SaleReturnOrderDetailsRequest> cmd) {
        SaleReturnOrderDetailsResult data = facade.details(cmd.getRequest());

        return QuerySingleResult.builder()
                .result(data)
                .task(UserTaskDto.builder()
                        .businessKey(data.getSerialNo())
                        .build())
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<SaleReturnOrderDetailsRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.UPLOAD_TO_B_RETURN_EXPRESS_NUMBER);
    }
}
