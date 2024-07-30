package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.FixCreateRequest;
import com.seeease.flywheel.fix.result.FixCreateResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 维修创建
 *
 * @author Tiro
 * @date 2023/3/8
 */
@Service
@Slf4j
@Extension(bizId = BizCode.FIX, useCase = UseCase.PROCESS_CREATE)
public class FixCreateExt implements CreateExtPtI<FixCreateRequest, FixCreateResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IFixFacade fixFacade;

    @Override
    public FixCreateResult create(CreateCmd<FixCreateRequest> cmd) {
        cmd.getRequest().setOrderType(1);

        if (Objects.nonNull(cmd.getRequest()) && Objects.nonNull(cmd.getRequest().getParentStoreId())) {
        } else {
            cmd.getRequest().setStoreId(UserContext.getUser().getStore().getId());
        }

        if (Objects.nonNull(cmd.getRequest()) && Objects.nonNull(cmd.getRequest().getFlowGrade())) {
        } else {
            cmd.getRequest().setFlowGrade(1);
        }

        return fixFacade.create(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(FixCreateRequest request, FixCreateResult result) {

        Map<String, Object> workflowVar = new HashMap<>();
        //总部 && 门店
        switch (request.getStoreId()) {
            case FlywheelConstant._ZB_ID:
                break;
            default:
                workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), result.getShortcodes());
                break;
        }

        workflowVar.put(VariateDefinitionKeyEnum.IS_REPAIR.getKey(), result.getIsRepair());
        workflowVar.put(VariateDefinitionKeyEnum.IS_ALLOT.getKey(), result.getIsAllot());

        return Arrays.asList(ProcessInstanceStartDto.builder()
                .serialNo(result.getSerialNo()).variables(workflowVar)
                .process(ProcessDefinitionKeyEnum.FIX_TASK)
                .build());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(FixCreateRequest request, FixCreateResult result) {

        return Optional.ofNullable(result.getStockId()).map(t -> Arrays.asList(StockLifeCycleResult.builder()
                .stockId(t)
                .originSerialNo(result.getSerialNo())
                .operationDesc(OperationDescConst.FIX_CREATE)
                .build())).orElse(Arrays.asList());
    }

    @Override
    public Class<FixCreateRequest> getRequestClass() {
        return FixCreateRequest.class;
    }

    @Override
    public void validate(CreateCmd<FixCreateRequest> cmd) {
        Assert.notNull(cmd, "参数不能为空");
        Assert.notNull(cmd.getRequest().getBrandId(), "品牌不能为空");
        Assert.notNull(cmd.getRequest().getStockSn(), "表身号不能为空");
        Assert.notNull(cmd.getRequest().getCustomerName(), "客户名称不能为空");
        Assert.notNull(cmd.getRequest().getCustomerPhone(), "联系电话不能为空");

        if (!Objects.nonNull(cmd.getRequest().getParentStoreId())) {
            Assert.notNull(cmd.getRequest().getStrapMaterial(), "表带类型不能为空");
//            Assert.notNull(cmd.getRequest().getWatchSection(), "表节数不能为空");
            Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getContent()), "详情不能为空");
            Assert.isTrue(cmd.getRequest().getContent().stream().allMatch(t -> Objects.nonNull(t.getFixProjectId()) || Objects.nonNull(t.getFixMoney())), "维修项不能为空");
            //        Assert.notNull(cmd.getRequest().getFlowGrade(), "维修等级不能为空");
        }

    }

}
