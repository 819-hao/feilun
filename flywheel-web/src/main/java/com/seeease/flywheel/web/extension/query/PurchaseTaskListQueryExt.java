package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.IPurchaseTaskFacade;
import com.seeease.flywheel.purchase.request.PurchaseTaskPageRequest;
import com.seeease.flywheel.purchase.result.PurchaseTaskPageResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryPageResult;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 采购列表查询
 *
 * @author trio
 * @date 2023/1/15
 */
@Service
@Extension(bizId = BizCode.PURCHASE_TASK, useCase = UseCase.QUERY_LIST)
public class PurchaseTaskListQueryExt implements QueryExtPtI<PurchaseTaskPageRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseTaskFacade purchaseTaskFacade;

    @Override
    public Class<PurchaseTaskPageRequest> getRequestClass() {
        return PurchaseTaskPageRequest.class;
    }

    @Override
    public void validate(QueryCmd<PurchaseTaskPageRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(cmd.getRequest().getPage() >= 0 && cmd.getRequest().getLimit() > 0, "分页参数异常");
    }

    @Override
    public QueryResult query(QueryCmd<PurchaseTaskPageRequest> cmd) {

        PageResult<PurchaseTaskPageResult> data = purchaseTaskFacade.list(cmd.getRequest());

        List<QuerySingleResult> dataList = data.getResult()
                .stream()
                .map(t -> QuerySingleResult.builder()
                        .result(t)
                        .task(UserTaskDto.builder()
                                .businessKey(t.getSerialNo())
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
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<PurchaseTaskPageRequest> cmd) {

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
