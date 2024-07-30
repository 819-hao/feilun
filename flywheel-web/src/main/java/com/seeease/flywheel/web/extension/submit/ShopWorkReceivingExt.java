package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.TextRobotMessage;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.request.StoreWorkReceivedRequest;
import com.seeease.flywheel.storework.result.StoreWorkReceivedListResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.utils.StrFormatterUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 门店收货
 *
 * @author Tiro
 * @date 2023/3/13
 */
@Service
@Extension(bizId = BizCode.SHOP, useCase = UseCase.LOGISTICS_RECEIVING)
public class ShopWorkReceivingExt implements SubmitExtPtI<StoreWorkReceivedRequest, StoreWorkReceivedListResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @NacosValue(value = "${robot.newStockNotify:2d798d3a-62a3-458e-b361-b327a6d8df0f}", autoRefreshed = true)
    private String newStockRobot;

    @Override
    public StoreWorkReceivedListResult submit(SubmitCmd<StoreWorkReceivedRequest> cmd) {
        StoreWorkReceivedRequest request = cmd.getRequest();
        request.setShopReceived(true);

        StoreWorkReceivedListResult result = storeWorkFacade.logisticsReceiving(request);

        if (CollectionUtils.isNotEmpty(result.getList()) && result.getList().stream().allMatch(Objects::nonNull)) {
            result.getList().forEach(s -> wxCpMessageFacade.send(TextRobotMessage.builder()
                    .key(newStockRobot)
                    .text(TextRobotMessage.Text.builder()
                            .content(StrFormatterUtil.format("【价格异常告警】\n【告警原因：新表价格不一致】\n【品牌：{}】\n【系列：{}】\n【型号：{}】\n【成色：S级/99新】\n【{}】",
                                    s.getBrandName(), s.getSeriesName(), s.getModel(), s.getLineMsg()))
                            .mentioned_list(
                                    Stream.of(Lists.newArrayList("@all"))
                                            .flatMap(Collection::stream)
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList()))
                            .build())
                    .build()));
        }
        return result;
    }

    @Override
    public Map<String, Object> workflowVar(StoreWorkReceivedRequest request, StoreWorkReceivedListResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        workflowVar.put(VariateDefinitionKeyEnum.LOGISTICS_REJECT_STATE.getKey(), request.getLogisticsRejectState());

        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(StoreWorkReceivedRequest request, StoreWorkReceivedListResult result) {
        return result.getStoreWorkCreateResultList().stream()
                .map(storeWorkCreateResult -> StockLifeCycleResult.builder()
                        .stockId(storeWorkCreateResult.getStockId())
                        .originSerialNo(storeWorkCreateResult.getSerialNo())
                        .operationDesc(request.getLogisticsRejectState().intValue() == OperationDescConst.LOGISTICS_RECEIVING_NO_VALUE ?
                                String.format(OperationDescConst.SHOP_LOGISTICS_RECEIVING, OperationDescConst.LOGISTICS_RECEIVING_NO) : String.format(OperationDescConst.SHOP_LOGISTICS_RECEIVING, OperationDescConst.LOGISTICS_RECEIVING_YES))
                        .build()).collect(Collectors.toList());
    }

    @Override
    public Class<StoreWorkReceivedRequest> getRequestClass() {
        return StoreWorkReceivedRequest.class;
    }

    @Override
    public void validate(SubmitCmd<StoreWorkReceivedRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.notNull(cmd.getRequest().getLogisticsRejectState(), "收货选择不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getWorkIds()), "作业id集合不能为空");
        Assert.isTrue(cmd.getRequest().getWorkIds().size() == cmd.getTaskList().size(), "业务数量和任务数量不一致");
    }

}

