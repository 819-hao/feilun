package com.seeease.flywheel.web.extension.load;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.ShippingReminderNotice;
import com.seeease.flywheel.sale.request.SaleLoadRequest;
import com.seeease.flywheel.sale.result.SaleLoadResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.ProcessDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 销售挂载工作流 //todo
 * @Date create in 2023/9/7 15:36
 */
@Service
@Slf4j
@Extension(bizId = BizCode.SALE, useCase = UseCase.PROCESS_LOAD)
public class SaleLoadExt implements CreateExtPtI<SaleLoadRequest, SaleLoadResult> {

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @Override
    public SaleLoadResult create(CreateCmd<SaleLoadRequest> cmd) {
        return SaleLoadResult.builder().build();
    }

    @Override
    public List<ProcessInstanceStartDto> start(SaleLoadRequest request, SaleLoadResult result) {
        return request.getOrders()
                .stream()
                .map(t -> {
                    Map<String, Object> workflowVar = new HashMap<>();
                    workflowVar.put(VariateDefinitionKeyEnum.OWNER.getKey(), request.getOwner());
                    workflowVar.put(VariateDefinitionKeyEnum.ORDER_CREATE_CODES.getKey(), request.getCreateShortcodes());
                    workflowVar.put(VariateDefinitionKeyEnum.SALE_CONFIRM.getKey(), request.isSaleConfirm() ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue());
                    workflowVar.put(VariateDefinitionKeyEnum.LOCATION_ID.getKey(), t.getDeliveryLocationId());
                    workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), t.getShortcodes());
                    if (CollectionUtils.isNotEmpty(t.getStoreWorkList())) {
                        workflowVar.put(VariateDefinitionKeyEnum.SALE_WORK_SERIAL_NO_LIST.getKey(), t.getStoreWorkList().stream()
                                .map(SaleLoadRequest.StoreWorkDTO::getSerialNo)
                                .collect(Collectors.toList()));
                    }

                    //发送通知
                    try {
                        if (request.getSaleProcess().equals(ProcessDefinitionKeyEnum.TO_C_SALE_ON_LINE)
                                && Objects.nonNull(request.getShopId())
                                && request.getShopId() == 21) {
                            request.getOrders().forEach(order -> wxCpMessageFacade.send(ShippingReminderNotice.builder()
                                    .createdBy(UserContext.getUser().getUserName())
                                    .createdTime(new Date())
                                    .serialNo(order.getSerialNo())
                                    .count(order.getStoreWorkList().size())
                                    .toUserIdList(Lists.newArrayList(283))
                                    .build()));
                        }
                    } catch (Exception e) {
                        log.error("发货提醒发送失败{}", e.getMessage(), e);
                    }

                    return ProcessInstanceStartDto.builder()
                            .serialNo(t.getSerialNo())
                            .process(ProcessDefinitionKeyEnum.fromKey(request.getSaleProcess()))
                            .variables(workflowVar)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(SaleLoadRequest request, SaleLoadResult result) {
        return request.getOrders()
                .stream()
                .map(orderDto -> {
                    String serialNo = orderDto.getSerialNo();
                    return orderDto.getStoreWorkList()
                            .stream()
                            .map(t -> StockLifeCycleResult.builder()
                                    .stockId(t.getStockId())
                                    .originSerialNo(serialNo)
                                    .operationDesc(OperationDescConst.SALE_CREATE)
                                    .build())
                            .collect(Collectors.toList());
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Class<SaleLoadRequest> getRequestClass() {
        return SaleLoadRequest.class;
    }

    @Override
    public void validate(CreateCmd<SaleLoadRequest> cmd) {

    }
}
