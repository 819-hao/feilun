package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.financial.IFinancialFacade;
import com.seeease.flywheel.financial.IFinancialTxHistoryFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@RestController
@RequestMapping("/financial")
public class FinancialController {
    @DubboReference(check = false, version = "1.0.0")
    private IFinancialFacade facade;
    @DubboReference(check = false, version = "1.0.0")
    private IFinancialTxHistoryFacade txHistoryFacade;

    @PostMapping("/detail")
    public SingleResponse detail(@RequestBody FinancialDetailsRequest request) {
        return SingleResponse.of(facade.detail(request));
    }


    @PostMapping("/queryAll")
    public SingleResponse all(@RequestBody FinancialQueryAllRequest request) {

        return SingleResponse.of(facade.queryAll(request));
    }

    @PostMapping("/export")
    public SingleResponse export(@RequestBody FinancialQueryAllRequest request) {

        return SingleResponse.of(facade.export(request));
    }

    @PostMapping("/tx/list")
    public SingleResponse txList(@RequestBody TxHistoryQueryRequest request) {
        return SingleResponse.of(txHistoryFacade.page(request));
    }

    @PostMapping("/tx/remove")
    public SingleResponse txRemove(@RequestBody TxHistoryDeleteRequest request) {
        txHistoryFacade.remove(request);
        return SingleResponse.buildSuccess();
    }


    @PostMapping("/jDImport")
    public SingleResponse jDImport(@RequestBody FinancialQueryAllRequest request) {

        return SingleResponse.of(facade.jDImport(request));
    }

    @PostMapping("/newGenerateFinancialOrder")
    public SingleResponse newGenerateFinancialOrder(@RequestBody FinancialGenerateOrderRequest request) {
        facade.newGenerateFinancialOrder(request);
        return SingleResponse.buildSuccess();
    }
}
