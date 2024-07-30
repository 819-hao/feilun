package com.seeease.flywheel.web.controller;


import com.seeease.flywheel.purchase.IPurchaseDemandProcessFacade;

import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderCancelRequest;

import com.seeease.flywheel.sale.result.SingleSaleOrderResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;

import com.seeease.flywheel.web.common.work.executor.CancelCmdExe;

import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


/**
 * @Author Mr. Du
 * @Description 总部采购列表
 * @Date create in 2023/2/14 13:49
 */
@Slf4j
@DubboService(version = "1.0.0")
public class PurchaseRpcController implements IPurchaseDemandProcessFacade {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade iSaleOrderFacade;


    @Resource
    private CancelCmdExe cancelCmdExe;


    @Override
    public void cancelTailOrder(String seriesNo) {
        SingleSaleOrderResult singleSaleOrderResult = iSaleOrderFacade.queryBySerialNo(seriesNo);
        CancelCmd<SaleOrderCancelRequest> cmd = new CancelCmd<>();
        cmd.setBizCode(BizCode.SALE);
        cmd.setUseCase(UseCase.QUERY_DETAILS);
        SaleOrderCancelRequest req = new SaleOrderCancelRequest();
        req.setId(singleSaleOrderResult.getId());
        req.setSerialNo(seriesNo);
        cmd.setRequest(req);
        cancelCmdExe.cancel(cmd);
    }
}
