package com.seeease.flywheel.serve.goods.tasks;

import com.seeease.flywheel.serve.goods.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 库龄刷新任务
 *
 * @author Tiro
 * @date 2023/5/10
 */
@Slf4j
@Component
public class StorageAgeTask {
    @Resource
    private StockService stockService;

    @Scheduled(cron = "1 0 0 * * ?")
    public synchronized void refreshStorageAge() {
        try {
            log.debug("============库龄刷新任务开始============");
            stockService.refreshStorageAge(null);
            log.debug("============库龄刷新任务完成============");
        } catch (Exception e) {
            log.error("库龄刷新任务异常:{}", e.getMessage(), e);
        }

    }
}
