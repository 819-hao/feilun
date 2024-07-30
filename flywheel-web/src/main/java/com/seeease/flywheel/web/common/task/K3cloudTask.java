package com.seeease.flywheel.web.common.task;

import com.seeease.flywheel.k3cloud.IK3cloudGlVoucherFacade;
import com.seeease.flywheel.k3cloud.request.K3cloudGlVoucherRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author Mr. Du
 * @Description 凭证导入 定时任务
 * @Date create in 2023/10/18 10:07
 */
@Slf4j
@Component
public class K3cloudTask {

//    @DubboReference(check = false, version = "1.0.0")
    @Resource
    private IK3cloudGlVoucherFacade k3cloudGlVoucherFacade;

//    @Scheduled(cron = "0 0 1 * * ?")
    public synchronized void completeTask() {

        String format = DateFormatUtils.format((DateUtils.addDays(new Date(), -1)), "yyyy-MM-dd");

        k3cloudGlVoucherFacade.executeBillQuery(K3cloudGlVoucherRequest.builder()
                .completeDateStart(format)
                .completeDateEnd(format)
                .isTask(true)
                .build());
    }

}
