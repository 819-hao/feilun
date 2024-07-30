package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.RecycleOrderVerifyRequest;
import com.seeease.flywheel.recycle.result.RecycleOrderVerifyResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr. Du
 * @Description 商城回收 2.客户第一次确认
 * @Date create in 2023/9/8 15:42
 */
@Service
@Extension(bizId = BizCode.MALL, useCase = UseCase.FIRST_VERIFY)
@Slf4j
public class MallFirstVerifyExt implements SubmitExtPtI<RecycleOrderVerifyRequest, RecycleOrderVerifyResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade recycleOderFacade;

//    @Resource
//    private CreateCmdExe workCreateCmdExe;

    @Override
    public RecycleOrderVerifyResult submit(SubmitCmd<RecycleOrderVerifyRequest> cmd) {

        return recycleOderFacade.firstVerify(cmd.getRequest());
//        try {
//            if (Objects.nonNull(result) && Objects.nonNull(result.getProcess().getProcess().getPurchaseLoadRequest())) {
//                CreateCmd createCmd = new CreateCmd();
//                createCmd.setBizCode(BizCode.PURCHASE);
//                createCmd.setUseCase(UseCase.PROCESS_LOAD);
//                createCmd.setRequest(result.getProcess().getProcess().getPurchaseLoadRequest());
//                workCreateCmdExe.create(createCmd);
//            }
//            if (Objects.nonNull(result) && Objects.nonNull(result.getProcess().getProcess().getSaleLoadRequest())) {
//                CreateCmd createCmd = new CreateCmd();
//                createCmd.setBizCode(BizCode.SALE);
//                createCmd.setUseCase(UseCase.PROCESS_LOAD);
//                createCmd.setRequest(result.getProcess().getProcess().getSaleLoadRequest());
//                workCreateCmdExe.create(createCmd);
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }

    }

    @Override
    public Map<String, Object> workflowVar(RecycleOrderVerifyRequest request, RecycleOrderVerifyResult result) {
        Map<String, Object> workflowVar = new HashMap<>(1);

        if (result.getVerify()) {
            workflowVar.put(VariateDefinitionKeyEnum.VERIFY.getKey(), 1);
        } else {
            workflowVar.put(VariateDefinitionKeyEnum.VERIFY.getKey(), 0);
        }
        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(RecycleOrderVerifyRequest request, RecycleOrderVerifyResult result) {
        return Arrays.asList();
    }

    @Override
    public Class<RecycleOrderVerifyRequest> getRequestClass() {
        return RecycleOrderVerifyRequest.class;
    }

    @Override
    public void validate(SubmitCmd<RecycleOrderVerifyRequest> cmd) {

    }
}
