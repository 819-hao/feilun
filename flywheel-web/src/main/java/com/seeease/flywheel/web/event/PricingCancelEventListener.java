package com.seeease.flywheel.web.event;

import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.executor.CancelCmdExe;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 15:27
 */
@Slf4j
@Component
@EnableAsync
public class PricingCancelEventListener implements ApplicationListener<PricingCancelEvent> {

    @Resource
    private CancelCmdExe cancelCmdExe;

    @Override
    @Async
    public void onApplicationEvent(PricingCancelEvent event) {

        CancelCmd cancelCmd = new CancelCmd();
        cancelCmd.setBizCode(BizCode.PRICING);
        cancelCmd.setUseCase(UseCase.CANCEL);

        event.getPricingCancelRequestList().forEach(request -> {

            cancelCmd.setRequest(request);
            cancelCmdExe.cancel(cancelCmd);
        });
    }
}
