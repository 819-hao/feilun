package com.seeease.flywheel.web.common.task;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.seeease.flywheel.allocate.IAllocateFacade;
import com.seeease.flywheel.allocate.request.AllocateToTimeoutRequest;
import com.seeease.flywheel.allocate.result.AllocateToTimeoutResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.TextRobotMessage;
import com.seeease.springframework.utils.StrFormatterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author Mr. Du
 * @Description 调拨接收方超时提醒
 * @Date create in 2023/10/18 10:07
 */
@Slf4j
@Component
public class AllocateToTimeoutTask {

    @DubboReference(check = false, version = "1.0.0")
    private IAllocateFacade allocateFacade;

    @NacosValue(value = "${allocateTo.timeout.day:7}", autoRefreshed = true)
    private Integer day;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @NacosValue(value = "${robot.allocateToTimeout:a992b413-8034-4221-bff4-4e881e1638fa}", autoRefreshed = true)
    private String allocateToTimeout;

    @NacosValue(value = "${role.allocateToTimeout:33}", autoRefreshed = true)
    private Integer roleId;

    @Scheduled(cron = "0 0 9 * * ?")
    public synchronized void completeTask() {

        List<AllocateToTimeoutResult> allocateToTimeoutResultList = allocateFacade.toTimeout(AllocateToTimeoutRequest
                .builder()
                .timeoutDay(day)
                .roleId(roleId)
                .build());

        for (AllocateToTimeoutResult allocateToTimeoutResult : allocateToTimeoutResultList) {
            wxCpMessageFacade.send(TextRobotMessage.builder()
                    .key(allocateToTimeout)
                    .text(TextRobotMessage.Text.builder()
                            .content(StrFormatterUtil.format("【调拨接收超时提醒】\n【告警原因：调拨超7天未接收提醒】\n【调拨单号：{}】\n【接收门店：{}】\n【品牌：{}】\n【系列：{}】\n【型号：{}】\n【表身号：{}】\n【{}】",
                                    allocateToTimeoutResult.getSerialNo(), allocateToTimeoutResult.getStoreName(), allocateToTimeoutResult.getBrandName(), allocateToTimeoutResult.getSeriesName(), allocateToTimeoutResult.getModel(), allocateToTimeoutResult.getStockSn(), allocateToTimeoutResult.getTimeoutMsg()))
                            .mentioned_list(
                                    Stream.of(
                                                    CollectionUtils.isEmpty(allocateToTimeoutResult.getMsgManList()) ? Lists.newArrayList("@all") : allocateToTimeoutResult.getMsgManList()
                                            )
                                            .flatMap(Collection::stream)
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList()))
                            .build())
                    .build());
        }

    }

}
