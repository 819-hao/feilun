package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingCreateByStockRequest;
import com.seeease.flywheel.pricing.request.PricingCreateRequest;
import com.seeease.flywheel.pricing.request.PricingListRequest;
import com.seeease.flywheel.pricing.request.PricingLogListRequest;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 14:59
 */
@Slf4j
@RestController
@RequestMapping("/pricing")
public class PricingController {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;

    @Resource
    private CreateCmdExe workCreateCmdExe;

    @PostMapping("/list")
    public SingleResponse listGoods(@RequestBody PricingListRequest request) {
        return SingleResponse.of(pricingFacade.list(request));
    }

    @PostMapping("/log/list")
    public SingleResponse listGoods(@RequestBody PricingLogListRequest request) {
        return SingleResponse.of(pricingFacade.logList(request));
    }

    /**
     * @param request
     * @return
     */
    @PostMapping("/batchCreate")
    public SingleResponse batchCreate(@RequestBody PricingCreateByStockRequest request) {

        ArrayList<CreateCmd> objects = new ArrayList<>();

        for (Integer item : request.getStockIdList()) {

            CreateCmd createCmd = new CreateCmd();
            createCmd.setBizCode(BizCode.PRICING);
            createCmd.setUseCase(UseCase.PROCESS_CREATE);

            PricingCreateRequest pricingCreateRequest = new PricingCreateRequest();
            pricingCreateRequest.setStockId(item);
            pricingCreateRequest.setAgain(false);
            pricingCreateRequest.setCancel(false);
            pricingCreateRequest.setCreatedBy(UserContext.getUser().getUserName());
            pricingCreateRequest.setCreatedId(UserContext.getUser().getId());
            pricingCreateRequest.setUpdatedId(UserContext.getUser().getId());
            pricingCreateRequest.setUpdatedBy(UserContext.getUser().getUserName());

            createCmd.setRequest(pricingCreateRequest);

            workCreateCmdExe.create(createCmd);
        }

        return SingleResponse.of(objects);
    }
}
