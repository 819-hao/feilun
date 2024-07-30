package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.FixFinishMsgRequest;
import com.seeease.flywheel.fix.request.FixRepairRequest;
import com.seeease.flywheel.fix.result.FixRepairResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.FixReceiveNotice;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.event.FixFinishMsgEvent;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author Mr. Du
 * @Description 维修接修
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.FIX, useCase = UseCase.REPAIR_RECEIVING)
@Slf4j
public class FixReceiveExt implements SubmitExtPtI<FixRepairRequest, FixRepairResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IFixFacade fixFacade;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public FixRepairResult submit(SubmitCmd<FixRepairRequest> cmd) {

        FixRepairResult result = fixFacade.repair(cmd.getRequest());

        if (Objects.nonNull(result.getFixSource()) && Arrays.asList(101, 102).contains(result.getFixSource()) && ObjectUtils.isNotEmpty(result.getShopId())) {
            wxCpMessageFacade.send(FixReceiveNotice.builder()
                    .createdBy(UserContext.getUser().getUserName())
                    .id(result.getId())
                    .createdTime(new Date())
                    .serialNo(result.getSerialNo())
                    .state(FlywheelConstant.FIX_RECEIVE)
                    .toUserRoleKey(Arrays.asList("shopowner"))
                    .shopId(result.getShopId())
                    .build());
        }

        msg(result);

        return result;
    }

    @Override
    public Map<String, Object> workflowVar(FixRepairRequest request, FixRepairResult result) {

        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.IS_ACCEPT.getKey(), result.getIsAccept());

        if (Objects.nonNull(result.getIsAllot())) {
            workflowVar.put(VariateDefinitionKeyEnum.IS_ALLOT.getKey(), result.getIsAllot());
        }

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(FixRepairRequest request, FixRepairResult result) {
        return Optional.ofNullable(result.getStockId()).map(r -> Arrays.asList(StockLifeCycleResult.builder()
                .stockId(result.getStockId())
                .originSerialNo(result.getSerialNo())
                .operationDesc(OperationDescConst.FIX_RECEIVE)
                .build())).orElse(Arrays.asList());
    }

    @Override
    public Class<FixRepairRequest> getRequestClass() {
        return FixRepairRequest.class;
    }

    @Override
    public void validate(SubmitCmd<FixRepairRequest> cmd) {

        Assert.notNull(cmd.getRequest().getAccept(), "判断条件不存在");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getFixId()) || Objects.nonNull(cmd.getRequest().getSerialNo()), "维修单条件不存在");

        if (WhetherEnum.fromValue(cmd.getRequest().getAccept()) == WhetherEnum.YES) {
            Assert.notNull(cmd.getRequest().getFixDay(), "维修天数不存在");
            Assert.notNull(cmd.getRequest().getContent(), "维修项不存在");
            Assert.isTrue(cmd.getRequest().getContent().stream().allMatch(t -> Objects.nonNull(t.getFixProjectId()) || Objects.nonNull(t.getFixMoney())), "维修项不能为空");
        }
    }

    /**
     * 接修取消通知 自动执行下一步
     *
     * @param
     */
    private void msg(FixRepairResult data) {
        try {
            if (Objects.nonNull(data) && StringUtils.isNotBlank(data.getParentFixSerialNo())) {
                applicationContext.publishEvent(new FixFinishMsgEvent(this, FixFinishMsgRequest.builder().serialNo(data.getParentFixSerialNo()).build()));
            }
        } catch (Exception e) {
            log.error("拒绝维修完成通知异常,{}", e.getMessage(), e);
        }
    }
}
