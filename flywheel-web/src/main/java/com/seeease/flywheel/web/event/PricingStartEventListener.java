package com.seeease.flywheel.web.event;

import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 15:27
 */
@Slf4j
@Component
public class PricingStartEventListener implements ApplicationListener<PricingStartEvent> {

    @Resource
    private CreateCmdExe workCreateCmdExe;

    @Override
    @Async
    public void onApplicationEvent(PricingStartEvent event) {

        CreateCmd createCmd = new CreateCmd();
        createCmd.setBizCode(BizCode.PRICING);
        createCmd.setUseCase(UseCase.PROCESS_CREATE);

        event.getPricingCreateRequestList().forEach(request -> {

            try {
                createCmd.setRequest(request);
                workCreateCmdExe.create(createCmd);
            } catch (Exception e) {
                log.error("定价开启异常,{}", e.getMessage(), e);
            }
        });
    }
}
