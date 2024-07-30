package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.request.StoreWorkOutStorageRequest;
import com.seeease.flywheel.storework.result.StoreWorkOutStorageResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/4
 */
@Service
@Extension(bizId = BizCode.STORAGE, useCase = UseCase.OUT_STORAGE)
public class StoreWorkOutStorageExt implements SubmitExtPtI<StoreWorkOutStorageRequest, StoreWorkOutStorageResult> {
    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    @Override
    public StoreWorkOutStorageResult submit(SubmitCmd<StoreWorkOutStorageRequest> cmd) {
        return storeWorkFacade.outStorage(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(StoreWorkOutStorageRequest request, StoreWorkOutStorageResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.OUT_STORAGE_NEED_QT.getKey(), result.getNeedQt());

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(StoreWorkOutStorageRequest request, StoreWorkOutStorageResult result) {
        return result.getStoreWorkCreateResultList().stream().map(storeWorkCreateResult -> StockLifeCycleResult.builder()
                .stockId(storeWorkCreateResult.getStockId())
                .originSerialNo(storeWorkCreateResult.getSerialNo())
                .operationDesc(OperationDescConst.OUT_STORAGE)
                .build()).collect(Collectors.toList());
    }

    @Override
    public Class<StoreWorkOutStorageRequest> getRequestClass() {
        return StoreWorkOutStorageRequest.class;
    }

    @Override
    public void validate(SubmitCmd<StoreWorkOutStorageRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getWorkIds()), "作业单不能为空");
        Assert.isTrue(cmd.getRequest().getWorkIds().size() == cmd.getTaskList().size(), "业务数量和任务数量不一致");

        //判断如果是商品定金销售商品是当前是否能发货
        Assert.isTrue(storeWorkFacade.validateCanDoIfMallOrder(cmd.getRequest().getWorkIds()),"商城订单未支付完成，暂时无法发货");

    }
}
