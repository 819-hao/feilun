package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.FixFinishMsgRequest;
import com.seeease.flywheel.fix.request.FixFinishRequest;
import com.seeease.flywheel.fix.result.FixFinishResult;
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
 * //todo 监听通知 是否是送来订单
 * @Description 维修完成(1.一步完成)
 * 维修完成(2.送外维修和送内维修的完成)
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.FIX, useCase = UseCase.REPAIR_COMPLETED)
@Slf4j
public class FixFinishExt implements SubmitExtPtI<FixFinishRequest, FixFinishResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IFixFacade fixFacade;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public FixFinishResult submit(SubmitCmd<FixFinishRequest> cmd) {

        FixFinishResult result = fixFacade.finish(cmd.getRequest());

        if (Objects.nonNull(result.getFixSource()) && Arrays.asList(101, 102).contains(result.getFixSource()) && ObjectUtils.isNotEmpty(result.getShopId())) {

            wxCpMessageFacade.send(FixReceiveNotice.builder()
                    .createdBy(UserContext.getUser().getUserName())
                    .id(result.getId())
                    .createdTime(new Date())
                    .serialNo(result.getSerialNo())
                    .state(FlywheelConstant.FIX_FINISH)
                    .toUserRoleKey(Arrays.asList("shopowner"))
                    .shopId(result.getShopId())
                    .build());
        }

        msg(result);

        return result;
    }

    @Override
    public Map<String, Object> workflowVar(FixFinishRequest request, FixFinishResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.IS_LOCAL.getKey(), 0);
        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(FixFinishRequest request, FixFinishResult result) {
        return Arrays.asList(StockLifeCycleResult.builder()
                .originSerialNo(result.getSerialNo())
                .stockId(result.getStockId())
                .operationDesc(OperationDescConst.FIX_FINISH)
                .build());
    }

    @Override
    public Class<FixFinishRequest> getRequestClass() {
        return FixFinishRequest.class;
    }

    @Override
    public void validate(SubmitCmd<FixFinishRequest> cmd) {

        Assert.notNull(cmd.getRequest().getFinishType(), "判断条件不存在");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getFixId()) || Objects.nonNull(cmd.getRequest().getSerialNo()), "维修单条件不存在");

        if (WhetherEnum.fromValue(cmd.getRequest().getFinishType()) == WhetherEnum.YES) {
//            Assert.notNull(cmd.getRequest().getFixDay(), "维修天数不存在");
            Assert.notNull(cmd.getRequest().getContent(), "维修项不存在");
            Assert.isTrue(cmd.getRequest().getContent().stream().allMatch(t -> Objects.nonNull(t.getFixProjectId()) || Objects.nonNull(t.getFixMoney())), "维修项不能为空");
        }

    }

    /**
     * 维修完成通知 自动执行下一步
     *
     * @param
     */
    private void msg(FixFinishResult data) {
        try {
            if (Objects.nonNull(data) && StringUtils.isNotBlank(data.getParentFixSerialNo())) {
                applicationContext.publishEvent(new FixFinishMsgEvent(this, FixFinishMsgRequest.builder().serialNo(data.getParentFixSerialNo()).build()));
            }
        } catch (Exception e) {
            log.error("维修完成通知异常,{}", e.getMessage(), e);
        }
    }
}
