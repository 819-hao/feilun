package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSON;
import com.seeease.flywheel.customer.ICustomerBalanceFacade;
import com.seeease.flywheel.customer.request.CustomerBalanceDetailRequest;
import com.seeease.flywheel.customer.request.CustomerBalancePageRequest;
import com.seeease.flywheel.customer.request.CustomerBalanceRefundRequest;
import com.seeease.flywheel.customer.result.CustomerBalanceDetailResult;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 客户余额相关接口
 */
@Slf4j
@RestController
@RequestMapping("/customerBalance")
public class CustomerBalanceController {

    @DubboReference(check = false, version = "1.0.0")
    private ICustomerBalanceFacade iCustomerBalanceFacade;

    @PostMapping("/pageQuery")
    public SingleResponse pageQuery(@RequestBody CustomerBalancePageRequest request) {
        log.info("pageQuery function of CustomerBalanceController start and requets = {}", JSON.toJSONString(request));

        return SingleResponse.of(iCustomerBalanceFacade.customerBalancePageQuery(request));
    }

    /**
     * 客户余额申请退款
     *
     * @param request
     * @return
     */
    @PostMapping("/refund")
    public SingleResponse refund(@RequestBody CustomerBalanceRefundRequest request) {
        log.info("refund function of CustomerBalanceController start and request and iApplyFinancialPaymentFacade create = {}", JSON.toJSONString(request));

        iCustomerBalanceFacade.customerBalanceRefund(request);
        return SingleResponse.buildSuccess();
    }

    @PostMapping("/accountBalanceDetail")
    public SingleResponse accountBalanceDetail(@RequestBody CustomerBalanceDetailRequest request) {
        log.info("accountBalanceDetail function of CustomerBalanceController start and requets = {}", JSON.toJSONString(request));

        List<CustomerBalanceDetailResult> list = iCustomerBalanceFacade.queryAccountBalanceDetail(request.getCustomerId());

        return SingleResponse.of(list);

    }

    @PostMapping("/consignmentMarginDetail")
    public SingleResponse consignmentMarginDetail(@RequestBody CustomerBalanceDetailRequest request) {
        log.info("consignmentMarginDetail function of CustomerBalanceController start and requets = {}", JSON.toJSONString(request));

        List<CustomerBalanceDetailResult> list = iCustomerBalanceFacade.queryconsignmentMarginDetail(request.getCustomerId());

        return SingleResponse.of(list);

    }


}
