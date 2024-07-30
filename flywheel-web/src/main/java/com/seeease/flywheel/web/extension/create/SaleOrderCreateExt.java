package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.ShippingReminderNotice;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleOrderCreateResult;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@Extension(bizId = BizCode.SALE, useCase = UseCase.PROCESS_CREATE)
public class SaleOrderCreateExt implements CreateExtPtI<SaleOrderCreateRequest, SaleOrderCreateResult> {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade facade;
    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @Override
    public Class<SaleOrderCreateRequest> getRequestClass() {
        return SaleOrderCreateRequest.class;
    }

    @Override
    public void validate(CreateCmd<SaleOrderCreateRequest> cmd) {
        Assert.notNull(cmd, "参数不能为空");
        SaleOrderCreateRequest request = cmd.getRequest();

        Assert.notNull(request, "请求入参不能为空");
        Assert.notNull(request.getSaleType(), "销售类型不能为空");
        Assert.notNull(request.getSaleMode(), "销售方式不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getDetails()), "详情不能为空");
    }

    @Override
    public SaleOrderCreateResult create(CreateCmd<SaleOrderCreateRequest> cmd) {
        return facade.create(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(SaleOrderCreateRequest request, SaleOrderCreateResult result) {
        return result.getOrders()
                .stream()
                .map(t -> {
                    Map<String, Object> workflowVar = new HashMap<>();
                    workflowVar.put(VariateDefinitionKeyEnum.OWNER.getKey(), result.getOwner());
                    workflowVar.put(VariateDefinitionKeyEnum.ORDER_CREATE_CODES.getKey(), result.getCreateShortcodes());
                    workflowVar.put(VariateDefinitionKeyEnum.SALE_CONFIRM.getKey(), result.isSaleConfirm() ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue());
                    workflowVar.put(VariateDefinitionKeyEnum.LOCATION_ID.getKey(), t.getDeliveryLocationId());
                    workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), t.getShortcodes());
                    if (CollectionUtils.isNotEmpty(t.getStoreWorkList())) {
                        workflowVar.put(VariateDefinitionKeyEnum.SALE_WORK_SERIAL_NO_LIST.getKey(), t.getStoreWorkList().stream()
                                .map(StoreWorkCreateResult::getSerialNo)
                                .collect(Collectors.toList()));
                    }

                    ProcessDefinitionKeyEnum keyEnum = SaleType.getKey(request.getSaleType(), request.getSaleMode());

                    //发送通知
//                    try {
//                        if (keyEnum.equals(ProcessDefinitionKeyEnum.TO_C_SALE_ON_LINE)
//                                && Objects.nonNull(request.getShopId())
//                                && request.getShopId() == 21) {
//                            result.getOrders().forEach(order -> wxCpMessageFacade.send(ShippingReminderNotice.builder()
//                                    .createdBy(UserContext.getUser().getUserName())
//                                    .createdTime(new Date())
//                                    .serialNo(order.getSerialNo())
//                                    .count(order.getStoreWorkList().size())
//                                    .toUserIdList(Lists.newArrayList(283))
//                                    .build()));
//                        }
//                    } catch (Exception e) {
//                        log.error("发货提醒发送失败{}", e.getMessage(), e);
//                    }

                    return ProcessInstanceStartDto.builder()
                            .serialNo(t.getSerialNo())
                            .process(keyEnum)
                            .variables(workflowVar)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(SaleOrderCreateRequest request, SaleOrderCreateResult result) {
        return result.getOrders()
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

    @Getter
    @AllArgsConstructor
    enum SaleType {
        /**
         * 同行销售
         */
        TOB(1, i -> true, ProcessDefinitionKeyEnum.TO_B_SALE),
        /**
         * 个人销售
         */
        TOC(2, i -> i != 5, ProcessDefinitionKeyEnum.TO_C_SALE),
        /**
         * 线上销售
         */
        TOC_ON_LINE(2, i -> i == 5, ProcessDefinitionKeyEnum.TO_C_SALE_ON_LINE),
        ;
        private Integer value;
        private Function<Integer, Boolean> condition;
        private ProcessDefinitionKeyEnum key;

        public static ProcessDefinitionKeyEnum getKey(int saleType, int saleMode) {
            return Arrays.stream(SaleType.values())
                    .filter(t -> saleType == t.getValue() && t.getCondition().apply(saleMode))
                    .map(SaleType::getKey)
                    .findFirst()
                    .orElse(null);
        }
    }
}
