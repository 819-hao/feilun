package com.seeease.flywheel.serve.goods.mq;

import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoSync;
import com.seeease.flywheel.serve.goods.service.GoodsMetaInfoSyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/21
 */
@Slf4j
@Component
public class GoodsMQTask {
    @Resource
    private GoodsMetaInfoSyncProducer producer;
    @Resource
    private GoodsMetaInfoSyncService goodsMetaInfoSyncService;

    @Scheduled(cron = "0/10 * * * * ?")
    public synchronized void goodsSyncMq() {
        try {
            log.debug("============商品同步任务开始============");
            List<GoodsMetaInfoSync> goodsMetaInfoDtoList = goodsMetaInfoSyncService.findPropertyChangeGoods();
            if (CollectionUtils.isEmpty(goodsMetaInfoDtoList)) {
                log.debug("商品未发送变动无需同步");
                return;
            }
            goodsMetaInfoDtoList.forEach(producer::sendMsg);
        } catch (Exception e) {
            log.error("定时同步商品通知异常:{}", e.getMessage(), e);
        }

    }
}
