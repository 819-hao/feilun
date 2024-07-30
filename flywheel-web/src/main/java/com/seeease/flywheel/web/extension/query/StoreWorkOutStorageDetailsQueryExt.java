package com.seeease.flywheel.web.extension.query;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.StoreWorkOutStorageDetailRequest;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 仓库出库详情
 *
 * @author Tiro
 * @date 2023/2/3
 */
@Service
@Extension(bizId = BizCode.STORAGE, useCase = UseCase.OUT_STORAGE_DETAILS)
public class StoreWorkOutStorageDetailsQueryExt implements QueryExtPtI<StoreWorkOutStorageDetailRequest> {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkQueryFacade storeWorkQueryFacade;

    @Override
    public QueryResult query(QueryCmd<StoreWorkOutStorageDetailRequest> cmd) {
        StoreWorkOutStorageDetailRequest request = cmd.getRequest();

        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        Assert.isTrue(request.getBelongingStoreId() == FlywheelConstant._ZB_ID, "登陆门店异常");

        List<StoreWorkDetailResult> resultList = storeWorkQueryFacade.outStorageDetails(request);

        return QueryListResult.builder()
                .resultList(resultList
                        .stream()
                        .map(t -> QuerySingleResult.builder()
                                .result(t)
                                .task(UserTaskDto.builder()
                                        .businessKey(t.getSerialNo())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<TaskDefinitionKeyEnum> needQueryTaskKeys(QueryCmd<StoreWorkOutStorageDetailRequest> cmd) {
        return Lists.newArrayList(TaskDefinitionKeyEnum.OUT_STORAGE);
    }

    @Override
    public Class<StoreWorkOutStorageDetailRequest> getRequestClass() {
        return StoreWorkOutStorageDetailRequest.class;
    }

    @Override
    public void validate(QueryCmd<StoreWorkOutStorageDetailRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(ObjectUtils.isNotEmpty(cmd.getRequest().getOriginSerialNo()), "作业单不能为空");
    }
}
