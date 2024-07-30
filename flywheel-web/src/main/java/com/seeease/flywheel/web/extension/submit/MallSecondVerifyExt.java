package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.RecycleOrderSecondVerifyRequest;
import com.seeease.flywheel.recycle.result.RecycleOrderSecondVerifyResult;
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
 * @Description 商城回收 2.客户第二次确认
 * @Date create in 2023/9/8 15:42
 */
@Service
@Extension(bizId = BizCode.MALL, useCase = UseCase.SECOND_VERIFY)
@Slf4j
public class MallSecondVerifyExt implements SubmitExtPtI<RecycleOrderSecondVerifyRequest, RecycleOrderSecondVerifyResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade recycleOderFacade;

    @Override
    public RecycleOrderSecondVerifyResult submit(SubmitCmd<RecycleOrderSecondVerifyRequest> cmd) {

        return recycleOderFacade.secondVerify(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(RecycleOrderSecondVerifyRequest request, RecycleOrderSecondVerifyResult result) {
        Map<String, Object> workflowVar = new HashMap<>();

        if (result.getVerify()) {
            workflowVar.put(VariateDefinitionKeyEnum.VERIFY.getKey(), 1);
        } else {
            workflowVar.put(VariateDefinitionKeyEnum.VERIFY.getKey(), 0);
        }
        return workflowVar;
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(RecycleOrderSecondVerifyRequest request, RecycleOrderSecondVerifyResult result) {
        return Arrays.asList();
    }

    @Override
    public Class<RecycleOrderSecondVerifyRequest> getRequestClass() {
        return RecycleOrderSecondVerifyRequest.class;
    }

    @Override
    public void validate(SubmitCmd<RecycleOrderSecondVerifyRequest> cmd) {

    }
}
