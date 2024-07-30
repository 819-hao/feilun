package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.FixForeignRequest;
import com.seeease.flywheel.fix.result.FixForeignResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.event.FixForeignMsgEvent;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @Author Mr. Du
 * @Description 维修送外 //todo 监听通知
 * @Date create in 2023/1/19 09:49
 */
@Service
@Extension(bizId = BizCode.FIX, useCase = UseCase.FOREIGN_RECEIVING)
@Slf4j
public class FixForeignExt implements SubmitExtPtI<FixForeignRequest, FixForeignResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IFixFacade fixFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public FixForeignResult submit(SubmitCmd<FixForeignRequest> cmd) {

        FixForeignResult result = fixFacade.foreign(cmd.getRequest());

        foreign(result);

        return result;
    }

    @Override
    public Map<String, Object> workflowVar(FixForeignRequest request, FixForeignResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.IS_LOCAL.getKey(), result.getIsLocal());
        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(FixForeignRequest request, FixForeignResult result) {
        return Optional.ofNullable(result.getStockId()).map(r -> Arrays.asList(StockLifeCycleResult.builder()
                .stockId(result.getStockId())
                .originSerialNo(result.getSerialNo())
                .operationDesc(OperationDescConst.FIX_CHECK)
                .build())).orElse(Arrays.asList());
    }

    @Override
    public Class<FixForeignRequest> getRequestClass() {
        return FixForeignRequest.class;
    }

    @Override
    public void validate(SubmitCmd<FixForeignRequest> cmd) {

        Assert.notNull(cmd.getRequest().getTagType(), "判断条件不存在");

        if (WhetherEnum.YES.getValue().equals(cmd.getRequest().getTagType())) {
            Assert.notNull(cmd.getRequest().getFixSiteId(), "站点不存在");
            Assert.isTrue(StringUtils.isNotBlank(cmd.getRequest().getDeliverExpressNo()), "快递不存在");
        }
    }

    /**
     * 维修内部送内维修
     *
     * @param
     */
    private void foreign(FixForeignResult data) {
        try {
            if (Objects.nonNull(data) && Objects.nonNull(data.getFixCreateRequest())) {

                applicationContext.publishEvent(new FixForeignMsgEvent(this, data.getFixCreateRequest()));
            }
        } catch (Exception e) {
            log.error("维修送外通知异常,{}", e.getMessage(), e);
        }
    }
}
