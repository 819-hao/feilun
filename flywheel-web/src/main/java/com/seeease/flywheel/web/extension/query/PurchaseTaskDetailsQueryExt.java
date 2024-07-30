package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.purchase.IPurchaseTaskFacade;
import com.seeease.flywheel.purchase.request.PurchaseTaskDetailsRequest;
import com.seeease.flywheel.purchase.result.PurchaseTaskDetailsResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 采购详情查询
 *
 * @author trio
 * @date 2023/1/15
 */
@Service
@Extension(bizId = BizCode.PURCHASE_TASK, useCase = UseCase.QUERY_DETAILS)
@Slf4j
public class PurchaseTaskDetailsQueryExt implements QueryExtPtI<PurchaseTaskDetailsRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseTaskFacade purchaseTaskFacade;


    @Override
    public Class<PurchaseTaskDetailsRequest> getRequestClass() {
        return PurchaseTaskDetailsRequest.class;
    }

    @Override
    public void validate(QueryCmd<PurchaseTaskDetailsRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId()) ||
                StringUtils.isNotBlank(cmd.getRequest().getSerialNo()), "id不能为空");
    }

    @Override
    public QueryResult query(QueryCmd<PurchaseTaskDetailsRequest> cmd) {
        PurchaseTaskDetailsResult data = purchaseTaskFacade.details(cmd.getRequest());

        return QuerySingleResult.builder()
                .result(data)
                .task(UserTaskDto.builder()
                        .businessKey(data.getSerialNo())
                        .build())
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<PurchaseTaskDetailsRequest> cmd) {

        LoginUser loginUser = UserContext.getUser();

        List<LoginRole> loginRoleList = loginUser.getRoles();

        if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("admin", "采购员").contains(loginRole.getRoleName()))) {
            return Lists.newArrayList(TaskDefinitionKeyEnum.PURCHASE_CHECK,
                    TaskDefinitionKeyEnum.PURCHASE_CREATE2);
        } else {
            return Lists.newArrayList();
        }
    }
}
