package com.seeease.flywheel.web.common.task;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.FixReceiveNotice;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * @Author Mr. Du
 * @Description 维修超时
 * @Date create in 2023/10/18 10:07
 */
@Slf4j
@Component
public class FixTimeoutTask {

    @DubboReference(check = false, version = "1.0.0")
    private IFixFacade fixFacade;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    @Scheduled(cron = "0 0 10,14,16 * * ?")
    public synchronized void completeTask() {
        fixFacade.allList().stream().filter(Objects::nonNull).
                forEach(result -> wxCpMessageFacade.send(FixReceiveNotice.builder()
                        .createdBy(result.getCreatedBy())
                        .id(result.getId())
                        .createdTime(new Date())
                        .serialNo(result.getSerialNo())
                        .state(FlywheelConstant.FIX_TIMEOUT)
                        .toUserRoleKey(Arrays.asList("shopowner"))
                        .shopId(result.getShopId())
                        .build()));
    }

}
