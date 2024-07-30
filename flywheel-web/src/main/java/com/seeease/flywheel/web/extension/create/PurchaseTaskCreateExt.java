package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.purchase.IPurchaseTaskFacade;
import com.seeease.flywheel.purchase.request.PurchaseTaskCreateRequest;
import com.seeease.flywheel.purchase.result.PurchaseTaskCreateResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Service
@Slf4j
@Extension(bizId = BizCode.PURCHASE_TASK, useCase = UseCase.PROCESS_CREATE)
public class PurchaseTaskCreateExt implements CreateExtPtI<PurchaseTaskCreateRequest, PurchaseTaskCreateResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseTaskFacade purchaseTaskFacade;

    @Override
    public Class<PurchaseTaskCreateRequest> getRequestClass() {
        return PurchaseTaskCreateRequest.class;
    }

    @Override
    public void validate(CreateCmd<PurchaseTaskCreateRequest> cmd) {

        //采购必要参数校验
        Assert.notNull(cmd, "请求不能为空");
        Assert.notNull(cmd.getRequest(), "请求入参不能为空");
        PurchaseTaskCreateRequest request = cmd.getRequest();


        /**
         * 型号
         */
        if (ObjectUtils.isEmpty(request.getGoodsId())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.MODEL_REQUIRE_NON_NULL);
        }
        /**
         * 成色
         */
        if (StringUtils.isBlank(request.getFiness())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINESS_REQUIRE_NON_NULL);
        }

        /**
         * 附件信息
         */
        if (ObjectUtils.isEmpty(request.getIsCard())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.ATTACHMENT_NON_NULL);
        }
        /**
         * 预估年份
         */
        if (StringUtils.isBlank(request.getDeliveryTimeStart()) || StringUtils.isBlank(request.getDeliveryTimeEnd())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.REQUIRE_NON_NULL);
        }

        /**
         * 预估毛利率
         */

        if (ObjectUtils.isEmpty(request.getClinchRateStart()) || ObjectUtils.isEmpty(request.getClinchRateEnd())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.RATE_NON_NULL);
        }

        /**
         * 数量
         */
        if (ObjectUtils.isEmpty(request.getTaskNumber()) || request.getTaskNumber().compareTo(0) <= 0) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.TASK_NON_NULL);
        }

        /**
         * 售价
         */
        if (ObjectUtils.isEmpty(request.getClinchPrice()) || request.getClinchPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.SALE_PRICE_NON_NULL);
        }

        /**
         * 对接人
         */
        if (ObjectUtils.isEmpty(request.getPurchaseJoinId())) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.PURCHASE_JOIN__NON_NULL);
        }
    }

    @Override
    public PurchaseTaskCreateResult create(CreateCmd<PurchaseTaskCreateRequest> cmd) {

        return purchaseTaskFacade.create(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(PurchaseTaskCreateRequest request, PurchaseTaskCreateResult result) {
        Map<String, Object> workflowVar = new HashMap<>();

        workflowVar.put(VariateDefinitionKeyEnum.PURCHASE_JOIN.getKey(), result.getPurchaseJoin().replaceAll(FlywheelConstant.REGEX, ""));

        return Arrays.asList(ProcessInstanceStartDto.builder()
                .serialNo(result.getSerialNo()).variables(workflowVar)
                .process(ProcessDefinitionKeyEnum.PURCHASE_TASK)
                .build());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseTaskCreateRequest request, PurchaseTaskCreateResult result) {

        return Arrays.asList();
    }
}
