package com.seeease.flywheel.web.common.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.enums.WhetherNotifyEnum;
import com.seeease.flywheel.web.infrastructure.service.DouYinCallbackNotifyService;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/7/5
 */
@Slf4j
@Component
public class DouYinTask {

    @Resource
    private DouYinOrderService douYinOrderService;
    @Resource
    private DouYinCallbackNotifyService douYinCallbackNotifyService;

    /**
     * 每5分钟执行一次
     */
    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public synchronized void completeTask() {
        try {
            List<DouYinOrder> saleOrderList = douYinOrderService.list(new LambdaQueryWrapper<DouYinOrder>()
                    .eq(DouYinOrder::getWhetherNotify, WhetherNotifyEnum.FAIL.getValue())
                    .isNotNull(DouYinOrder::getSerialNo));
            if (CollectionUtils.isEmpty(saleOrderList))
                return;

            saleOrderList.stream()
                    .collect(Collectors.groupingBy(DouYinOrder::getShopId))
                    .forEach((shopId, list) -> list.stream()
                            .collect(Collectors.groupingBy(DouYinOrder::getSerialNo))
                            .forEach((serialNo, orderList) -> {
                                String expressNumber = douYinOrderService.selectExpressNumberBySerialNo(serialNo);
                                if (StringUtils.isNotBlank(expressNumber)) {
                                    douYinCallbackNotifyService.deliveryNotify(orderList, expressNumber);
                                    //更新发货通知状态
                                    douYinOrderService.updateBatchById(orderList.stream()
                                            .map(dyo -> {
                                                DouYinOrder douYinOrder = new DouYinOrder();
                                                douYinOrder.setId(dyo.getId());
                                                douYinOrder.setWhetherNotify(WhetherNotifyEnum.SUCCESS);
                                                return douYinOrder;
                                            }).collect(Collectors.toList()));
                                }
                            })
                    );
        } catch (Exception e) {
            log.error("告知抖音物流发货异常，{},{}", e.getMessage(), e);
        }
    }

}
