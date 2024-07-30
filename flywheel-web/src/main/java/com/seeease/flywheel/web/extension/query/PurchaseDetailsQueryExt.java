package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseDetailsRequest;
import com.seeease.flywheel.purchase.result.PurchaseDetailsResult;
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
 * 采购详情查询
 *
 * @author trio
 * @date 2023/1/15
 */
@Service
@Extension(bizId = BizCode.PURCHASE, useCase = UseCase.QUERY_DETAILS)
public class PurchaseDetailsQueryExt implements QueryExtPtI<PurchaseDetailsRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseFacade purchaseFacade;


    @Override
    public Class<PurchaseDetailsRequest> getRequestClass() {
        return PurchaseDetailsRequest.class;
    }

    @Override
    public void validate(QueryCmd<PurchaseDetailsRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) ||
                StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id不能为空");
    }

    @Override
    public QueryResult query(QueryCmd<PurchaseDetailsRequest> cmd) {
        PurchaseDetailsResult data = purchaseFacade.details(cmd.getRequest());

        return QuerySingleResult.builder()
                .result(data)
                .task(UserTaskDto.builder()
                        .businessKey(data.getSerialNo())
                        .build())
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<PurchaseDetailsRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.UPLOAD_EXPRESS_NUMBER,
                TaskDefinitionKeyEnum.SHOP_RECEIVING,
                TaskDefinitionKeyEnum.CONFIRM_RETURN,
                TaskDefinitionKeyEnum.ACCEPT_REPAIR,
                TaskDefinitionKeyEnum.RETURN_RECEIVE
                );
    }
}
