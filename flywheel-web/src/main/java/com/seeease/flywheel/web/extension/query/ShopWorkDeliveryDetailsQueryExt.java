package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.StoreWorkDeliveryDetailRequest;
import com.seeease.flywheel.storework.result.StoreWorkDetailResult;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.common.work.pti.QueryExtPtI;
import com.seeease.flywheel.web.common.work.result.QueryListResult;
import com.seeease.flywheel.web.common.work.result.QueryResult;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 门店发货详情
 *
 * @author Tiro
 * @date 2023/2/3
 */
@Service
@Extension(bizId = BizCode.SHOP, useCase = UseCase.LOGISTICS_DELIVERY_DETAILS)
public class ShopWorkDeliveryDetailsQueryExt implements QueryExtPtI<StoreWorkDeliveryDetailRequest> {
    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkQueryFacade storeWorkQueryFacade;

    @Override
    public QueryResult query(QueryCmd<StoreWorkDeliveryDetailRequest> cmd) {
        StoreWorkDeliveryDetailRequest request = cmd.getRequest();
        //设置登陆门店
        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        request.setStoreComprehensive(true);
        Assert.isTrue(request.getBelongingStoreId() != FlywheelConstant._ZB_ID, "登陆门店id不能为空");

        List<StoreWorkDetailResult> result = storeWorkQueryFacade.deliveryDetail(request);
        List<QuerySingleResult> collect = result
                .stream()
                .map(t -> QuerySingleResult.builder()
                        .result(t)
                        .task(UserTaskDto.builder()
                                .businessKey(t.getSerialNo())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return QueryListResult.builder().resultList(collect).build();

    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<StoreWorkDeliveryDetailRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.SHOP_DELIVERY);
    }

    @Override
    public Class<StoreWorkDeliveryDetailRequest> getRequestClass() {
        return StoreWorkDeliveryDetailRequest.class;
    }

    @Override
    public void validate(QueryCmd<StoreWorkDeliveryDetailRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getOriginSerialNo(), "参数不能为空");

    }
}
