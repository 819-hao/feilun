package com.seeease.flywheel.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.web.controller.xianyu.XyRecycleConvert;
import com.seeease.flywheel.web.entity.XyRecycleOrder;
import com.seeease.flywheel.web.entity.XyRecycleOrderStatsVO;
import com.seeease.flywheel.web.entity.XyRecycleOrderVO;
import com.seeease.flywheel.web.entity.enums.XyRecycleOrderStateEnum;
import com.seeease.flywheel.web.entity.request.*;
import com.seeease.flywheel.web.infrastructure.service.XyRecycleOrderService;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 闲鱼回收订单
 *
 * @author Tiro
 * @date 2023/10/20
 */
@Slf4j
@RestController
@RequestMapping("/xyRecycleOrder")
public class XyRecycleOrderController {
    @Resource
    private XyRecycleOrderService orderService;

    /**
     * 挂载问卷
     *
     * @param spuId
     * @param whole
     * @return
     */
    @PostMapping("/recycleSpu")
    public SingleResponse recycleSpu(@RequestParam(value = "spuId", required = false) String spuId
            , @RequestParam(value = "whole", required = false) Boolean whole) {
        orderService.recycleSpu(spuId, whole);
        return SingleResponse.buildSuccess();
    }

    /**
     * 上线最新版本
     *
     * @param spuId
     * @param whole
     * @return
     */
    @PostMapping("/spuOnline")
    public SingleResponse spuOnline(@RequestParam(value = "spuId", required = false) String spuId
            , @RequestParam(value = "whole", required = false) Boolean whole) {
        orderService.spuOnline(spuId, whole);
        return SingleResponse.buildSuccess();
    }

    /**
     * 角标
     *
     * @return
     */
    @PostMapping("/mark")
    public SingleResponse mark() {
        List<XyRecycleOrderStatsVO> statsVOList = orderService.statsByOrderState();

        Map<Integer, Integer> result = statsVOList.stream()
                .collect(Collectors.toMap(XyRecycleOrderStatsVO::getQuoteOrderState, XyRecycleOrderStatsVO::getCountNumber));
        //全部数量
        result.put(NumberUtils.INTEGER_ZERO, (int) statsVOList.stream().mapToInt(XyRecycleOrderStatsVO::getCountNumber).sum());

        return SingleResponse.of(result);
    }

    @PostMapping("/list")
    public SingleResponse list(@RequestBody XyRecycleOrderListRequest request) {

        if (request.getQuoteOrderState() == -1) {
            request.setQuoteOrderState(null);
        }

        LambdaQueryWrapper<XyRecycleOrder> wrapper = Wrappers.<XyRecycleOrder>lambdaQuery()
                .eq(Objects.nonNull(request.getQuoteOrderState()), XyRecycleOrder::getQuoteOrderState, XyRecycleOrderStateEnum.findByValue(request.getQuoteOrderState()));

        if (StringUtils.isNotBlank(request.getKeyword())) {
            wrapper.eq(XyRecycleOrder::getBrandName, request.getKeyword())
                    .or()
                    .eq(XyRecycleOrder::getExpressNumber, request.getKeyword())
                    .or()
                    .eq(XyRecycleOrder::getBizOrderId, request.getKeyword())
                    .or()
                    .like(XyRecycleOrder::getSellerRealName, request.getKeyword())
                    .or()
                    .like(XyRecycleOrder::getSellerPhone, request.getKeyword());
        }

        Page<XyRecycleOrder> result = orderService.page(Page.of(request.getPage(), request.getLimit()), wrapper);

        return SingleResponse.of(PageResult.<XyRecycleOrderVO>builder()
                .result(result.getRecords()
                        .stream()
                        .map(XyRecycleConvert.INSTANCE::convertVO)
                        .collect(Collectors.toList()))
                .totalCount(result.getTotal())
                .totalPage(result.getPages())
                .build());
    }


    @PostMapping("/details")
    public SingleResponse details(@RequestBody XyRecycleOrderDetailsRequest request) {
        return SingleResponse.of(XyRecycleConvert.INSTANCE.convertVO(orderService.getById(request.getId())));
    }

    /**
     * 主动取消回收订单
     *
     * @param request
     * @return
     */
    @PostMapping("/cancel")
    public SingleResponse cancel(@RequestBody XyRecycleOrderCancelRequest request) {
        orderService.cancelOrder(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 第一次报价
     *
     * @param request
     * @return
     */
    @PostMapping("/quote")
    public SingleResponse quote(@RequestBody XyRecycleOrderQuoteRequest request) {
        orderService.quote(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 取件
     *
     * @param request
     * @return
     */
    @PostMapping("/pickUp")
    public SingleResponse pickUp(@RequestBody XyRecycleOrderPickUpRequest request) {
        orderService.pickUp(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 收货
     *
     * @param request
     * @return
     */
    @PostMapping("/received")
    public SingleResponse received(@RequestBody XyRecycleOrderReceivedRequest request) {
        orderService.received(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 质检
     *
     * @param request
     * @return
     */
    @PostMapping("/qt")
    public SingleResponse qt(@RequestBody XyRecycleOrderQtRequest request) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Random random = new Random();
        int number = random.nextInt(999);
        request.setQtCode(String.format("%s%04d", simpleDateFormat.format(new Date()), number));

        orderService.qt(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 打款
     *
     * @param request
     * @return
     */
    @PostMapping("/payment")
    public SingleResponse payment(@RequestBody XyRecycleOrderPaymentRequest request) {
        orderService.payment(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 退回
     *
     * @param request
     * @return
     */
    @PostMapping("/refund")
    public SingleResponse refund(@RequestBody XyRecycleOrderRefundRequest request) {
        orderService.refund(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 质检报告
     *
     * @param qtCode
     * @return
     */
    @GetMapping("/qtReport")
    public SingleResponse qtReport(@RequestParam("qtCode") String qtCode) {
        return SingleResponse.of(orderService.qtReport(qtCode));
    }
}
