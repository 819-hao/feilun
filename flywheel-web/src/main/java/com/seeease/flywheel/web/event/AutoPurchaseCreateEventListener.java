package com.seeease.flywheel.web.event;

import com.seeease.flywheel.purchase.IPurchaseQueryFacade;
import com.seeease.flywheel.purchase.request.PurchaseLoadRequest;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/10/10 09:43
 */
@Slf4j
@Component
@EnableAsync
public class AutoPurchaseCreateEventListener implements ApplicationListener<AutoPurchaseCreateEvent> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseQueryFacade purchaseQueryFacade;

    @Resource
    private CreateCmdExe workCreateCmdExe;

    @Override
    @Async
    public void onApplicationEvent(AutoPurchaseCreateEvent autoPurchaseCreateEvent) {

        log.info("发货列表{}", autoPurchaseCreateEvent.getAutoPurchaseCreateRequest().getWorkIdList());

        //订单信息 采购订单信息
        for (Integer workId : autoPurchaseCreateEvent.getAutoPurchaseCreateRequest().getWorkIdList()) {

            //创建业务单
            PurchaseCreateListResult purchaseCreateListResult = purchaseQueryFacade.autoPurchaseCreate(workId);

            //挂载工作流
            if (Objects.nonNull(purchaseCreateListResult)) {

                CreateCmd createCmd = new CreateCmd();
                createCmd.setBizCode(BizCode.PURCHASE);
                createCmd.setUseCase(UseCase.PROCESS_LOAD);
                createCmd.setRequest(PurchaseLoadRequest.builder()
                        .shortcodes(purchaseCreateListResult.getShortcodes())
                        .serialNo(purchaseCreateListResult.getSerialNo())
                        .storeId(1)
                        .businessKey(purchaseCreateListResult.getBusinessKey())
                        .line(Arrays.asList(purchaseCreateListResult.getLine().get(0).getWno()))
                        .build());
                workCreateCmdExe.create(createCmd);
            }
        }
    }
}
