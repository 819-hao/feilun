package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.recycle.IMarketRecycleOderFacade;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.*;
import com.seeease.flywheel.recycle.result.*;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.common.work.executor.QueryCmdExe;
import com.seeease.flywheel.web.common.work.executor.SubmitCmdExe;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商城调用RPC回收、回购通用
 *
 * @Auther Gilbert
 * @Date 2023/9/1 10:41
 */
@Slf4j
@DubboService(version = "1.0.0")
public class MarketRecycleOrderController implements IMarketRecycleOderFacade {

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade iRecycleOderFacade;

    @Resource
    private CreateCmdExe workCreateCmdExe;

    @Resource
    private SubmitCmdExe submitCmdExe;

    @Resource
    private QueryCmdExe queryCmdExe;

    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    @Override
    public SingleResponse<RecycleOrderResult> create(MarketRecycleOrderRequest request) {

        CreateCmd cmd = new CreateCmd();
        cmd.setBizCode(BizCode.MALL);
        cmd.setUseCase(UseCase.PROCESS_CREATE);
        Object result = null;
        try {
            cmd.setRequest(request);
            result = workCreateCmdExe.create(cmd);
            log.info("[商城回购回收订单创建: request={}| cmd={} | res={}]", JSONObject.toJSONString(request), JSONObject.toJSON(cmd), JSONObject.toJSONString(result));
        } catch (Exception e) {
            log.error("商城回购回收订单创建异常,{}", e.getMessage(), e);
        }
        return SingleResponse.of(ObjectUtils.isEmpty(result) ? RecycleOrderResult.builder().build() : (RecycleOrderResult) result);
    }

    /**
     * 上传打款单信息
     *
     * @param request
     * @return
     */
    @Override
    public SingleResponse<RecycleOrderResult> uploadRemit(MarkektRecycleUserBankRequest request) {
        return SingleResponse.of(iRecycleOderFacade.uploadRemit(request));
    }

    /**
     * 付款的前提 肯定有销售单 开启销售流程
     *
     * @param request
     * @return
     */
    @Override
    public SingleResponse<RecycleOrderPayResult> clientPay(MarkektRecyclePayRequest request) {

        RecycleOrderPayResult result = iRecycleOderFacade.clientPay(request);

        MarkektRecycleGetSaleProcessResult process = iRecycleOderFacade.getStartSaleProcess(MarkektRecycleGetSaleProcessRequest.builder().recycleId(request.getRecycleId()).build());

        try {
            if (ObjectUtils.isNotEmpty(process) && ObjectUtils.isNotEmpty(process.getSaleLoadRequest())) {
                CreateCmd cmd = new CreateCmd();
                cmd.setBizCode(BizCode.SALE);
                cmd.setUseCase(UseCase.PROCESS_LOAD);
                cmd.setRequest(process.getSaleLoadRequest());
                workCreateCmdExe.create(cmd);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return SingleResponse.of(result);
    }

    @Override
    public SingleResponse<Boolean> buyBackExits(MarketRecycleOrderRequest request) {
        return SingleResponse.of(iRecycleOderFacade.buyBackExits(request));
    }

    /**
     * 查询回购中采购、销售行信息
     *
     * @param request
     * @return
     */
    @Override
    public SingleResponse<BuyBackForSaleResult> recycleForSaleDetail(RecycleOrderVerifyRequest request) {
        return SingleResponse.of(iRecycleOderFacade.recycleForSaleDetail(request));
    }

    @Override
    public List<RecyclingListResult> buyBackList(RecycleOrderListRequest request) {
        PageResult<RecyclingListResult> list = iRecycleOderFacade.list(request);

        return list.getResult();
    }

    @Override
    public SingleResponse<MallRecycleOrderDetailResult> recycleDetail(RecycleOrderDetailsRequest request) {
        return SingleResponse.of(iRecycleOderFacade.mallRecycleDetail(request));
    }

    @Override
    public SingleResponse<Boolean> protocolSync(ProtocolSyncRequest request) {
        return SingleResponse.of(iRecycleOderFacade.protocolSync(request));
    }
}
