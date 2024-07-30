package com.seeease.flywheel.web.event;

import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Mr. Du
 * @Description 维修新建内部流程通知
 * @Date create in 2023/3/21 15:21
 */
@Slf4j
@Component
public class FixForeignMsgEventListener implements ApplicationListener<FixForeignMsgEvent> {

    @Resource
    private CreateCmdExe workCreateCmdExe;

    @Override
    public void onApplicationEvent(FixForeignMsgEvent fixForeignMsgEvent) {

        CreateCmd createCmd = new CreateCmd();
        createCmd.setBizCode(BizCode.FIX);
        createCmd.setUseCase(UseCase.PROCESS_CREATE);

        try {
            createCmd.setRequest(fixForeignMsgEvent.getFixCreateRequest());
            workCreateCmdExe.create(createCmd);
        } catch (Exception e) {
            log.error("自动开启维修异常,{}", e.getMessage(), e);
        }
    }
}
