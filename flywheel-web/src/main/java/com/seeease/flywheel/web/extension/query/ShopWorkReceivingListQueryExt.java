package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.StoreWorkListRequest;
import com.seeease.flywheel.storework.result.StoreWorkListResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryPageResult;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 门店待收货列表
 *
 * @author Tiro
 * @date 2023/2/3
 */
@Service
@Extension(bizId = BizCode.SHOP, useCase = UseCase.RECEIVING_LIST)
public class ShopWorkReceivingListQueryExt implements QueryExtPtI<StoreWorkListRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkQueryFacade storeWorkQueryFacade;

    @Override
    public QueryResult query(QueryCmd<StoreWorkListRequest> cmd) {
        StoreWorkListRequest request = cmd.getRequest();
        //设置登陆门店
        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        //总部降级
        if (request.getBelongingStoreId() == FlywheelConstant._ZB_ID) {
            return QueryPageResult.builder()
                    .resultList(Collections.EMPTY_LIST)
                    .build();
        }

        PageResult<StoreWorkListResult> result = storeWorkQueryFacade.listReceiving(request);

        List<QuerySingleResult> resultList = result.getResult().stream()
                .map(t -> QuerySingleResult.builder()
                        .result(t)
                        .task(UserTaskDto.builder()
                                .businessKey(t.getSerialNo())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return QueryPageResult.builder()
                .totalCount(result.getTotalCount())
                .totalPage(result.getTotalPage())
                .resultList(resultList)
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<StoreWorkListRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.SHOP_RECEIVING);
    }

    @Override
    public Class<StoreWorkListRequest> getRequestClass() {
        return StoreWorkListRequest.class;
    }

    @Override
    public void validate(QueryCmd<StoreWorkListRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(cmd.getRequest().getPage() > 0 && cmd.getRequest().getLimit() > 0, "分页参数异常");
    }
}
