package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.TextRobotMessage;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.request.StoreWorkInStorageRequest;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.flywheel.storework.result.StoreWorkInStorageListResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.service.DouYinCallbackNotifyService;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.utils.StrFormatterUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther Gilbert
 * @Date 2023/1/19 15:23
 */
@Service
@Extension(bizId = BizCode.STORAGE, useCase = UseCase.IN_STORAGE)
public class StoreWorkInStorageExt implements SubmitExtPtI<StoreWorkInStorageRequest, StoreWorkInStorageListResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @Resource
    private DouYinCallbackNotifyService douYinCallbackNotifyService;

    @NacosValue(value = "${robot.cPriceNotify:2d798d3a-62a3-458e-b361-b327a6d8df0f}", autoRefreshed = true)
    private String robot;

    @NacosValue(value = "${robot.newStockNotify:2d798d3a-62a3-458e-b361-b327a6d8df0f}", autoRefreshed = true)
    private String newStockRobot;

    @Override
    public StoreWorkInStorageListResult submit(SubmitCmd<StoreWorkInStorageRequest> cmd) {
        cmd.getRequest().setStoreId(UserContext.getUser().getStore().getId().intValue());

        StoreWorkInStorageListResult result = storeWorkFacade.inStorage(cmd.getRequest());

        douYinCallbackNotifyService.brandNotify(result.getStoreWorkCreateResultList()
                .stream()
                .map(StoreWorkCreateResult::getStockId)
                .collect(Collectors.toList()));

        if (CollectionUtils.isNotEmpty(result.getStockSnList())) {
            result.getStockSnList().forEach(s -> wxCpMessageFacade.send(TextRobotMessage.builder()
                    .key(robot)
                    .text(TextRobotMessage.Text.builder()
                            .content(StrFormatterUtil.format("【定价异常告警】\n【告警原因：寄售价大于C价】\n【表身号：{}】",
                                    s))
                            .mentioned_list(
                                    Stream.of(Lists.newArrayList("@all"))
                                            .flatMap(Collection::stream)
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList()))
                            .build())
                    .build()));
        }

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
    public Map<String, Object> workflowVar(StoreWorkInStorageRequest request, StoreWorkInStorageListResult result) {
        return null;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(StoreWorkInStorageRequest request, StoreWorkInStorageListResult result) {
        return result.getStoreWorkCreateResultList().stream().map(storeWorkCreateResult -> StockLifeCycleResult.builder()
                .stockId(storeWorkCreateResult.getStockId())
                .originSerialNo(storeWorkCreateResult.getSerialNo())
                .operationDesc(OperationDescConst.IN_STORAGE)
                .build()).collect(Collectors.toList());
    }

    @Override
    public Class<StoreWorkInStorageRequest> getRequestClass() {
        return StoreWorkInStorageRequest.class;
    }

    @Override
    public void validate(SubmitCmd<StoreWorkInStorageRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "参数不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getWorkIds()), "作业id集合不能为空");
        Assert.isTrue(cmd.getRequest().getWorkIds().size() == cmd.getTaskList().size(), "业务数量和任务数量不一致");
    }

}
