package com.seeease.flywheel.web.extension.cancel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderCancelRequest;
import com.seeease.flywheel.sale.result.SaleOrderCancelResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.CancelExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderService;
import com.seeease.flywheel.web.infrastructure.service.KuaishouOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/2
 */
@Slf4j
@Service
@Extension(bizId = BizCode.SALE, useCase = UseCase.CANCEL)
public class SaleOrderCancelExt implements CancelExtPtI<SaleOrderCancelRequest, SaleOrderCancelResult> {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade facade;
    @Resource
    private DouYinOrderService douYinOrderService;

    @Resource
    private KuaishouOrderService kuaishouOrderService;

    @Override
    public SaleOrderCancelResult cancel(CancelCmd<SaleOrderCancelRequest> cmd) {
        return facade.cancel(cmd.getRequest());
    }

    @Override
    public String businessKey(SaleOrderCancelResult result) {
        return result.getSerialNo();
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(SaleOrderCancelRequest request, SaleOrderCancelResult result) {
        //回退抖音审核状态
        try {
            douYinOrderService.backUseStep(Optional.ofNullable(result.getSerialNo()).orElse(request.getSerialNo()));
        } catch (Exception e) {
            log.error("回退抖音审核状态异常:{}", e.getMessage(), e);
        }
        //回退快手审核状态
        try {
            kuaishouOrderService.backUseStep(Optional.ofNullable(result.getSerialNo()).orElse(request.getSerialNo()));
        } catch (Exception e) {
            log.error("回退快手审核状态异常:{}", e.getMessage(), e);
        }
        return result.getStockIdList()
                .stream()
                .map(id -> StockLifeCycleResult.builder()
                        .stockId(id)
                        .originSerialNo(result.getSerialNo())
                        .operationDesc(OperationDescConst.SALE_CANCEL)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Class<SaleOrderCancelRequest> getRequestClass() {
        return SaleOrderCancelRequest.class;
    }

    @Override
    public void validate(CancelCmd<SaleOrderCancelRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(Objects.nonNull(cmd.getRequest().getId())
                        || StringUtils.isNotBlank(cmd.getRequest().getSerialNo())
                        || StringUtils.isNotBlank(cmd.getRequest().getBizOrderCode())
                , "id或单号不能为空");
    }
}
