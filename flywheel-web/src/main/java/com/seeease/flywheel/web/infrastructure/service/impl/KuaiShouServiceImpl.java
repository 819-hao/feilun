package com.seeease.flywheel.web.infrastructure.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderCancelRequest;
import com.seeease.flywheel.sale.request.SaleOrderDetailsRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleOrderDetailsResult;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CancelCmdExe;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.entity.KuaishouOrder;
import com.seeease.flywheel.web.entity.KuaishouOrderRefund;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.service.KuaiShouService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/12/5 16:54
 */
@Service
@Slf4j
public class KuaiShouServiceImpl implements KuaiShouService {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade saleOrderFacade;

    @Resource
    private CancelCmdExe cancelCmdExe;

    @Resource
    private CreateCmdExe createCmdExe;

    @Override
    public void cancelOrder(KuaishouOrder kuaishouOrder) {
        //查销售单
        SaleOrderDetailsRequest detailsRequest = SaleOrderDetailsRequest.builder()
                .serialNo(kuaishouOrder.getSerialNo())
                .bizOrderCode(String.valueOf(kuaishouOrder.getOrderId()))
                .build();
        SaleOrderDetailsResult details = saleOrderFacade.details(detailsRequest);
        if (Objects.isNull(details)) {
            return;
        }
        //查询是否有售后单介入
        switch (TMallServiceImpl.SaleOrderStateEnum.fromCode(details.getSaleState())) {
            case CANCEL_WHOLE:
                log.info("[飞轮快手订单已取消: order={}]", JSONObject.toJSONString(kuaishouOrder));
                return;
            case UN_CONFIRMED:
            case UN_STARTED:
                SaleOrderCancelRequest request = SaleOrderCancelRequest.builder()
                        .bizOrderCode(String.valueOf(kuaishouOrder.getOrderId()))
                        .build();

                CancelCmd<SaleOrderCancelRequest> cmd = new CancelCmd<>();
                cmd.setBizCode(BizCode.SALE);
                cmd.setUseCase(UseCase.CANCEL);
                cmd.setRequest(request);

                Object res = cancelCmdExe.cancel(cmd);
                log.info("[飞轮快手订单取消: order={}| cmd={} | res={}]", JSONObject.toJSONString(kuaishouOrder), JSONObject.toJSON(cmd), JSONObject.toJSONString(res));
                break;
            //飞轮系统已发货 快手取消订单
            case COMPLETE:
                //创建销退单
                Object reverseOrderRes = reverseOrder(details, String.valueOf(kuaishouOrder.getOrderId()), details.getLines()
                        .stream()
                        .map(SaleOrderDetailsResult.SaleOrderLineVO::getSubOrderCode)
                        .collect(Collectors.toList()));
                log.info("[飞轮快手订单销退订单创建: order={} | res={}]", JSONObject.toJSONString(kuaishouOrder), JSONObject.toJSONString(reverseOrderRes));
                break;

            default:
                throw new RuntimeException("进行中无法取消");
        }
    }

    @Override
    public String refundOrder(KuaishouOrderRefund kuaishouOrderRefund, KuaishouOrder kuaishouOrder) {

        //查销售单
        SaleOrderDetailsRequest detailsRequest = SaleOrderDetailsRequest.builder()
                .serialNo(kuaishouOrder.getSerialNo())
                .bizOrderCode(kuaishouOrderRefund.getOrderId())
                .build();
        SaleOrderDetailsResult details = saleOrderFacade.details(detailsRequest);

        //查询是否有售后单介入
        switch (TMallServiceImpl.SaleOrderStateEnum.fromCode(details.getSaleState())) {
            case CANCEL_WHOLE:
                log.info("[飞轮快手订单已取消: order={}]", JSONObject.toJSONString(kuaishouOrderRefund.getOrderId()));
                break;
            case UN_CONFIRMED:
            case UN_STARTED:
                SaleOrderCancelRequest request = SaleOrderCancelRequest.builder()
                        .bizOrderCode(String.valueOf(kuaishouOrderRefund.getOrderId()))
                        .build();

                CancelCmd<SaleOrderCancelRequest> cmd = new CancelCmd<>();
                cmd.setBizCode(BizCode.SALE);
                cmd.setUseCase(UseCase.CANCEL);
                cmd.setRequest(request);

                Object res = cancelCmdExe.cancel(cmd);
                log.info("[飞轮快手订单取消: order={}| cmd={} | res={}]", JSONObject.toJSONString(kuaishouOrderRefund.getOrderId()), JSONObject.toJSON(cmd), JSONObject.toJSONString(res));
                break;
            //飞轮系统已发货 快手取消订单
            case COMPLETE:
//                //创建销退单
//                Object reverseOrderRes = reverseOrder(details, String.valueOf(kuaishouOrderRefund.getOrderId()), details.getLines()
//                        .stream()
//                        .map(SaleOrderDetailsResult.SaleOrderLineVO::getSubOrderCode)
//                        .collect(Collectors.toList()));
//                log.info("[飞轮快手订单销退订单创建: order={} | res={}]", JSONObject.toJSONString(kuaishouOrderRefund.getOrderId()), JSONObject.toJSONString(reverseOrderRes));
                //创建销退单
                Object reverseOrderRes = reverseOrder(details, kuaishouOrderRefund.getRefundOrderId(), Lists.newArrayList(kuaishouOrderRefund.getOrderSubId()));
                log.info("[快手销退订单创建: douYinOrderRefund={} | res={}]", JSONObject.toJSONString(kuaishouOrderRefund), JSONObject.toJSONString(reverseOrderRes));
                break;

            default:
                throw new RuntimeException("进行中无法取消");
        }
        return null;
    }

    /**
     * 销退订单
     *
     * @param details
     * @param returnBizOrderCode
     * @param subOrderCodeList
     * @return
     */
    private Object reverseOrder(SaleOrderDetailsResult details, String returnBizOrderCode, List<String> subOrderCodeList) {
        SaleReturnOrderCreateRequest request = SaleReturnOrderCreateRequest.builder()
                .saleReturnType(details.getSaleType())
                .bizOrderCode(returnBizOrderCode)
                .creator(SaleReturnOrderCreateRequest.PrescriptiveCreator.builder()
                        .createdId(details.getCreatedId())
                        .createdBy(details.getCreatedBy())
                        .build())
                .shopId(details.getShopId())
                .details(details.getLines()
                        .stream()
                        .filter(t -> subOrderCodeList.contains(t.getSubOrderCode()))
                        .map(t -> SaleReturnOrderCreateRequest.BillSaleReturnOrderLineDto.builder()
                                .saleId(details.getId())
                                .saleLineId(t.getId())
                                .returnPrice(t.getClinchPrice())
                                .build()
                        )
                        .collect(Collectors.toList())
                ).build();

        //组装创建命令
        CreateCmd<SaleReturnOrderCreateRequest> cmd = new CreateCmd<>();
        cmd.setBizCode(BizCode.TO_C_SALE_RETURN);
        cmd.setUseCase(UseCase.PROCESS_CREATE);
        cmd.setRequest(request);

        //创建销退单
        return createCmdExe.create(cmd);
    }
}
