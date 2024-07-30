package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderConfirmRequest;
import com.seeease.flywheel.sale.result.SaleOrderConfirmResult;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.entity.request.DouYinCustomerDecryptionRequest;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.service.DouYinService;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/27
 */
@Service
@Extension(bizId = BizCode.SALE, useCase = UseCase.SALE_ORDER_CONFIRM)
public class SaleOrderConfirmExt implements SubmitExtPtI<SaleOrderConfirmRequest, SaleOrderConfirmResult> {
    @Resource
    private DouYinService douYinService;
    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade facade;

    @Override
    public SaleOrderConfirmResult submit(SubmitCmd<SaleOrderConfirmRequest> cmd) {
        //抖音订单客户重解密
//        douYinService.customerDecryption(DouYinCustomerDecryptionRequest.builder()
//                .douYinOrderId(cmd.getRequest().getBizOrderCode())
//                .customerId(cmd.getRequest().getCustomerId())
//                .customerContactsId(cmd.getRequest().getCustomerContactsId())
//                .build());
        return facade.saleConfirm(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(SaleOrderConfirmRequest request, SaleOrderConfirmResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.LOCATION_ID.getKey(), result.getDeliveryLocationId());
        workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), result.getShortcodes());
        workflowVar.put(VariateDefinitionKeyEnum.SALE_WORK_SERIAL_NO_LIST.getKey(), result.getStoreWorkList().stream()
                .map(StoreWorkCreateResult::getSerialNo)
                .collect(Collectors.toList()));
        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(SaleOrderConfirmRequest request, SaleOrderConfirmResult result) {
        return result.getStoreWorkList().stream().map(storeWorkCreateResult ->
                StockLifeCycleResult.builder()
                        .stockId(storeWorkCreateResult.getStockId())
                        .operationDesc(OperationDescConst.SALE_CONFIRM)
                        .originSerialNo(result.getSerialNo())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public Class<SaleOrderConfirmRequest> getRequestClass() {
        return SaleOrderConfirmRequest.class;
    }

    @Override
    public void validate(SubmitCmd<SaleOrderConfirmRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getOrderId(), "id不能为空");

        Assert.isTrue(CollectionUtils.isEmpty(cmd.getRequest().getDetails()) ||
                        cmd.getRequest()
                                .getDetails()
                                .stream()
                                .allMatch(t -> Objects.nonNull(t.getId()) && Objects.nonNull(t.getStockId()))
                , "订单行商品参数不能为空");

        List<Integer> salesmanList = Lists.newArrayList(cmd.getRequest().getFirstSalesman()
                        , cmd.getRequest().getSecondSalesman()
                        , cmd.getRequest().getThirdSalesman())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (salesmanList.stream().distinct().count() != salesmanList.size()) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.SALESMAN_FAILED);
        }

    }
}
