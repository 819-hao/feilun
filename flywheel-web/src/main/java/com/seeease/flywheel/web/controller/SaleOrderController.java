package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IStockLifeCycleFacade;
import com.seeease.flywheel.goods.request.StockLifeCycleCreateRequest;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.*;
import com.seeease.flywheel.sale.result.SaleOrderBatchSettlementResult;
import com.seeease.flywheel.sale.result.SaleOrderListForBillResult;
import com.seeease.flywheel.sale.result.SaleOrderListForExportResult;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/sale")
public class SaleOrderController {

    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade facade;
    @DubboReference(check = false, version = "1.0.0")
    private IStockLifeCycleFacade stockLifeCycleFacade;


    /**
     * 查询相同型号的销售历史记录
     * @param wno
     * @return
     */
    @GetMapping("/history")
    public SingleResponse saleHistory(@RequestParam String wno){
        return SingleResponse.of(facade.saleHistory(wno));
    }

    @PostMapping("/queryAll")
    public SingleResponse queryAll(@RequestBody SaleOrderListRequest request) {
        return SingleResponse.of(facade.list(request));
    }

    @PostMapping("/edit")
    public SingleResponse edit(@RequestBody SaleOrderEditRequest request) {

        facade.edit(request);

        return SingleResponse.buildSuccess();
    }

    /**
     * 修改回购政策
     * @param request
     * @return
     */
    @PostMapping("/update")
    public SingleResponse update(@RequestBody SaleOrderUpdateRequest request) {

        facade.update(request);

        return SingleResponse.buildSuccess();
    }

    @PostMapping("/details")
    public SingleResponse details(@RequestBody SaleOrderDetailsRequest request) {

        return SingleResponse.of(facade.details(request));
    }

    @PostMapping("/querySettlementList")
    public SingleResponse querySettlementList(@RequestBody SaleOrderSettlementListRequest request) {
        Assert.notNull(request.getCustomerId(), "客户id不能为空");
        return SingleResponse.of(facade.querySettlementList(request));
    }

    @PostMapping("/batchSettlement")
    public SingleResponse batchSettlement(@RequestBody SaleOrderBatchSettlementRequest request) {
        Assert.notNull(request.getList(), "列表参数不能为空");
        Assert.notNull(request.getCustomerId(), "客户id不能为空");
        request.getList().forEach(t -> {
            Assert.notNull(t.getStockId(), "商品id不能为空");
            Assert.notNull(t.getSaleId(), "销售单号Id不能为空");
        });
        SaleOrderBatchSettlementResult result = facade.batchSettlement(request);
        createLifeCycle(result);
        return SingleResponse.buildSuccess();
    }

    @PostMapping("/tmallSettleAccounts")
    public SingleResponse tmallSettleAccounts(@RequestBody SaleOrderTmallSettleAccountsRequest request) {
        Assert.notNull(request.getPhone(), "手机号码不能为空");
        Assert.notNull(request.getUserName(), "客户userName不能为空");
        Assert.notNull(request.getSaleId(), "saleId不能为空");
        Assert.notNull(request.getUserId(), "userId不能为空");
        facade.tmallSettleAccounts(request);
        return SingleResponse.buildSuccess();
    }

    @PostMapping("/export")
    public SingleResponse export(@RequestBody SaleOrderListRequest request) {
        return SingleResponse.of(facade.export(request));
    }

    /**
     * 销售单已完成的个人销售
     *
     * @param request
     * @return
     */
    @PostMapping("/queryByRecycle")
    public SingleResponse queryByRecycle(@RequestBody SaleOrderRecycleListRequest request) {
        return SingleResponse.of(facade.queryByRecycle(request));
    }

    /**
     * 新增生命周期
     */
    private void createLifeCycle(SaleOrderBatchSettlementResult result) {
        try {
            List<SaleOrderBatchSettlementResult.SaleOrderBatchSettlementLine> list = result.getList();
            List<StockLifeCycleCreateRequest> collect = list.stream().map(lifeCycle -> {

                StockLifeCycleCreateRequest stockLifeCycleCreateRequest = new StockLifeCycleCreateRequest();
                stockLifeCycleCreateRequest.setStockId(lifeCycle.getStockId());
                stockLifeCycleCreateRequest.setOriginSerialNo(lifeCycle.getSerialNo());
                stockLifeCycleCreateRequest.setOperationDesc(OperationDescConst.CONSIGNMENT_CONFIRMED_SALE);
                stockLifeCycleCreateRequest.setStoreId(UserContext.getUser().getStore().getId());
                return stockLifeCycleCreateRequest;
            }).collect(Collectors.toList());

            stockLifeCycleFacade.createBatch(collect);
        } catch (Exception e) {
            log.error("生命周期插入异常，saleOrderBatchSettlementResult={},{}", JSONObject.toJSONString(result), e.getMessage(), e);
        }
    }

    /**
     * 单据合并
     *
     * @param request
     * @return
     */
    @PostMapping("/bill")
    private SingleResponse bill(@RequestBody SaleOrderListRequest request) {

        PageResult<SaleOrderListForExportResult> pageResult = facade.export(request);

        if (CollectionUtils.isEmpty(pageResult.getResult())) {
            return SingleResponse.of(Arrays.asList());
        }

        Map<String, List<SaleOrderListForExportResult>> collect = pageResult.getResult().stream().collect(Collectors.groupingBy(SaleOrderListForExportResult::getSerialNo));

        List<SaleOrderListForBillResult> list = new ArrayList<>();

        for (Map.Entry<String, List<SaleOrderListForExportResult>> entry : collect.entrySet()) {

            SaleOrderListForExportResult result = entry.getValue().get(FlywheelConstant.INDEX);
            SaleOrderListForBillResult build = SaleOrderListForBillResult.builder().build();
            BeanUtils.copyProperties(result, build);
            build.setCIQ(result.getInspectionType());
            build.setLocationName(result.getShopName());
            build.setList(entry.getValue().stream().map(saleOrderListForExportResult -> SaleOrderListForBillResult.StockDTO.builder()
                    .brandName(saleOrderListForExportResult.getBrandName())
                    .model(saleOrderListForExportResult.getModel())
                    .stockSn(saleOrderListForExportResult.getStockSn())
                    .attachment(saleOrderListForExportResult.getAttachment())
                    .clinchPrice(saleOrderListForExportResult.getClinchPrice())
                    .spotCheckCode(saleOrderListForExportResult.getSpotCheckCode())
                    .build()).collect(Collectors.toList()));
            list.add(build);
        }

        return SingleResponse.of(list);
    }
}
