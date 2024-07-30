package com.seeease.flywheel.web.common.task;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.TextRobotMessage;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.SaleReturnToTimeoutRequest;
import com.seeease.flywheel.sale.result.SaleReturnToTimeoutResult;
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
public class SaleReturnToTimeoutTask {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleReturnOrderFacade saleReturnOrderFacade;

    @NacosValue(value = "${saleReturn.timeout.day:7}", autoRefreshed = true)
    private Integer day;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;


    @NacosValue(value = "${robot.saleReturnToTimeout:2d798d3a-62a3-458e-b361-b327a6d8df0f}", autoRefreshed = true)
    private String allocateToTimeout;

    @NacosValue(value = "${role.saleReturnToTimeout:33}", autoRefreshed = true)
    private Integer roleId;

    @Scheduled(cron = "0 0 9 * * ?")
    public synchronized void completeTask() {

        List<SaleReturnToTimeoutResult> saleReturnToTimeoutResultList = saleReturnOrderFacade.toTimeout(SaleReturnToTimeoutRequest
                .builder()
                .timeoutDay(day)
                .roleId(roleId)
                .build());

        for (SaleReturnToTimeoutResult saleReturnToTimeoutResult : saleReturnToTimeoutResultList) {
            wxCpMessageFacade.send(TextRobotMessage.builder()
                    .key(allocateToTimeout)
                    .text(TextRobotMessage.Text.builder()
                            .content(StrFormatterUtil.format("【销售退货接收超时提醒】\n【告警原因：销售退货超7天未接收提醒】\n【销售退货单号：{}】\n【接收门店：{}】\n【品牌：{}】\n【系列：{}】\n【型号：{}】\n【表身号：{}】\n【{}】",
                                    saleReturnToTimeoutResult.getSerialNo(), saleReturnToTimeoutResult.getStoreName(), saleReturnToTimeoutResult.getBrandName(), saleReturnToTimeoutResult.getSeriesName(), saleReturnToTimeoutResult.getModel(), saleReturnToTimeoutResult.getStockSn(), saleReturnToTimeoutResult.getTimeoutMsg()))
                            .mentioned_list(
                                    Stream.of(
                                                    CollectionUtils.isEmpty(saleReturnToTimeoutResult.getMsgManList()) ? Lists.newArrayList("@all") : saleReturnToTimeoutResult.getMsgManList()
                                            )
                                            .flatMap(Collection::stream)
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList()))
                            .build())
                    .build());
        }

    }

}
