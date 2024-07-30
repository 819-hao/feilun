package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.purchase.IPurchaseReturnFacade;
import com.seeease.flywheel.purchase.request.PurchaseReturnDetailsRequest;
import com.seeease.flywheel.purchase.result.PurchaseReturnDetailsResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * @author wbh
 * @date 2023/2/3
 */
@Service
@Extension(bizId = BizCode.PURCHASE_RETURN, useCase = UseCase.QUERY_DETAILS)
public class PurchaseReturnDetailsQueryExt implements QueryExtPtI<PurchaseReturnDetailsRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseReturnFacade facade;


    @Override
    public QueryResult query(QueryCmd<PurchaseReturnDetailsRequest> cmd) {
        PurchaseReturnDetailsResult data = facade.details(cmd.getRequest());
        return QuerySingleResult.builder()
                .result(data)
                .task(UserTaskDto.builder()
                        .businessKey(data.getSerialNo())
                        .build())
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<PurchaseReturnDetailsRequest> cmd) {
        return null;
    }

    @Override
    public Class<PurchaseReturnDetailsRequest> getRequestClass() {
        return PurchaseReturnDetailsRequest.class;
    }

    @Override
    public void validate(QueryCmd<PurchaseReturnDetailsRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) ||
                StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id、订单号不能为空");
    }
}
