package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.MarketRecycleOrderRequest;
import com.seeease.flywheel.recycle.result.RecycleOrderResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城回购 1.建单
 *
 * @author Tiro
 * @date 2023/3/8
 */
@Service
@Slf4j
@Extension(bizId = BizCode.MALL, useCase = UseCase.PROCESS_CREATE)
public class MallCreateExt implements CreateExtPtI<MarketRecycleOrderRequest, RecycleOrderResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade recycleFacade;

    @Override
    public RecycleOrderResult create(CreateCmd<MarketRecycleOrderRequest> cmd) {
        return recycleFacade.orderCreate(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(MarketRecycleOrderRequest request, RecycleOrderResult result) {

        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.COUNSELOR_USER.getKey(), result.getUserId());
        workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), result.getShortcodes());

        return Arrays.asList(ProcessInstanceStartDto.builder()
                .serialNo(result.getSerialNo())
                .process(request.getRecycleType().equals(1) ? ProcessDefinitionKeyEnum.SHOP_RECYCLE : ProcessDefinitionKeyEnum.SHOP_BUY_BACK)
                .variables(workflowVar)
                .build());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(MarketRecycleOrderRequest request, RecycleOrderResult result) {
        return Arrays.asList();
    }

    @Override
    public Class<MarketRecycleOrderRequest> getRequestClass() {
        return MarketRecycleOrderRequest.class;
    }

    @Override
    public void validate(CreateCmd<MarketRecycleOrderRequest> cmd) {
        Assert.notNull(cmd, "参数不能为空");
        Assert.notNull(cmd.getRequest(), "request不能为空");
        Assert.notNull(cmd.getRequest().getRecycleType(), "request不能为空");
        Assert.isTrue(Arrays.asList(1, 2).contains(cmd.getRequest().getRecycleType()), "不存在的类型");
    }
}
