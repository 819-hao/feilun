package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.ReplacementOrRecycleCreateRequest;
import com.seeease.flywheel.recycle.result.ReplacementOrRecycleCreateResult;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author Mr. Du
 * @Description 商城回购 3.客户经理去建单
 * @Date create in 2023/9/8 15:42
 */
@Service
@Extension(bizId = BizCode.MALL, useCase = UseCase.LOAD_CREATE)
@Slf4j
public class MallLoadCreateExt implements SubmitExtPtI<ReplacementOrRecycleCreateRequest, ReplacementOrRecycleCreateResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade recycleOderFacade;

    @Resource
    private CreateCmdExe workCreateCmdExe;

    /**
     * 回收则执行工作流
     *
     * @param cmd
     * @return
     */
    @Override
    public ReplacementOrRecycleCreateResult submit(SubmitCmd<ReplacementOrRecycleCreateRequest> cmd) {
        ReplacementOrRecycleCreateResult result = recycleOderFacade.replacementOrRecycleCreate(cmd.getRequest());
        log.info("返回数据参数:{}", result);
        try {
            if (Objects.nonNull(result) && Objects.nonNull(result.getProcess().getPurchaseLoadRequest())) {
                CreateCmd createCmd = new CreateCmd();
                createCmd.setBizCode(BizCode.PURCHASE);
                createCmd.setUseCase(UseCase.PROCESS_LOAD);
                createCmd.setRequest(result.getProcess().getPurchaseLoadRequest());
                workCreateCmdExe.create(createCmd);
            }
            if (Objects.nonNull(result) && Objects.nonNull(result.getProcess().getSaleLoadRequest())) {
                CreateCmd createCmd = new CreateCmd();
                createCmd.setBizCode(BizCode.SALE);
                createCmd.setUseCase(UseCase.PROCESS_LOAD);
                createCmd.setRequest(result.getProcess().getSaleLoadRequest());
                workCreateCmdExe.create(createCmd);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Map<String, Object> workflowVar(ReplacementOrRecycleCreateRequest request, ReplacementOrRecycleCreateResult result) {

        return new HashMap<String, Object>() {{
        }};
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(ReplacementOrRecycleCreateRequest request, ReplacementOrRecycleCreateResult result) {
        return Arrays.asList();
    }

    @Override
    public Class<ReplacementOrRecycleCreateRequest> getRequestClass() {
        return ReplacementOrRecycleCreateRequest.class;
    }

    @Override
    public void validate(SubmitCmd<ReplacementOrRecycleCreateRequest> cmd) {

    }
}
