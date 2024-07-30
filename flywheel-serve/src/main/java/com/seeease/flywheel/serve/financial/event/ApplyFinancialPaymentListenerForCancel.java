package com.seeease.flywheel.serve.financial.event;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceCmdTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


/**
 * 申请打款取消
 */
@Slf4j
@Component
public class ApplyFinancialPaymentListenerForCancel implements BillHandlerEventListener<ApplyFinancialPaymentCancelEvent> {

    private static List<ApplyFinancialPaymentTypeEnum> TYPE_ENUMS = Lists.newArrayList(
            ApplyFinancialPaymentTypeEnum.BALANCE_REFUND);

    @Resource
    private CustomerBalanceService customerBalanceService;

    @Override
    public void onApplicationEvent(ApplyFinancialPaymentCancelEvent event) {
        log.info("onApplicationEvent function of ApplyFinancialPaymentListenerForCancel start and event = {}", JSON.toJSONString(event));

        if (TYPE_ENUMS.contains(event.getTypePayment())) {

//            if (CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue().equals(event.getRefundType())) {
//                List<CustomerBalanceDetailResult> customerBalanceDetailResultList = queryAccountBalanceDetail(event.getCustomerId());
//                BigDecimal accountbalanceSum = customerBalanceDetailResultList.stream().filter(Objects::nonNull)
//                        .map(e -> e.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
//                Assert.isTrue(accountbalanceSum.compareTo(event.getClinchPrice()) >= 0, "客户余额不足");
//
//            } else {
//                List<CustomerBalanceDetailResult> customerBalanceDetailResultList = queryconsignmentMarginDetail(event.getCustomerId());
//                BigDecimal consignmentMarginSum = customerBalanceDetailResultList.stream().filter(Objects::nonNull)
//                        .map(e -> e.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
//                Assert.isTrue(consignmentMarginSum.compareTo(event.getClinchPrice()) >= 0, "客户寄售保证金不足");
//            }

            //往客户余额中插入一条正向流水
            customerBalanceService.customerBalanceCmd(event.getCustomerId(), event.getContactId(), event.getClinchPrice(),
                    event.getRefundType(), UserContext.getUser().getStore().getId(),
                    CustomerBalanceCmdTypeEnum.ADD.getValue(), event.getUserId(), event.getOriginSerialNo());
        }
    }
//
//    public List<CustomerBalanceDetailResult> queryAccountBalanceDetail(Integer customerId) {
//        List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(customerId, null);
//        Map<Integer, List<CustomerBalance>> customerBalanceMap = customerBalanceList.stream()
//                .filter(e -> e.getType().equals(CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue()))
//                .collect(Collectors.groupingBy(CustomerBalance::getCreatedId));
//
//        List<CustomerBalanceDetailResult> resultList = org.apache.commons.compress.utils.Lists.newArrayList();
//        for (Map.Entry<Integer, List<CustomerBalance>> map : customerBalanceMap.entrySet()) {
//            CustomerBalanceDetailResult customerBalanceDetailResult = new CustomerBalanceDetailResult();
//            customerBalanceDetailResult.setCustomerContactName(map.getValue().get(0).getCreatedBy());
//            customerBalanceDetailResult.setAmount(map.getValue().stream().map(e -> e.getAccountBalance()).reduce(BigDecimal.ZERO, BigDecimal::add));
//            resultList.add(customerBalanceDetailResult);
//        }
//
//        return resultList;
//    }
//
//    public List<CustomerBalanceDetailResult> queryconsignmentMarginDetail(Integer customerId) {
//        List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(customerId, null);
//        Map<Integer, List<CustomerBalance>> customerBalanceMap = customerBalanceList.stream()
//                .filter(e -> e.getType().equals(CustomerBalanceTypeEnum.JS_AMOUNT.getValue()))
//                .collect(Collectors.groupingBy(CustomerBalance::getCreatedId));
//
//        List<CustomerBalanceDetailResult> resultList = org.apache.commons.compress.utils.Lists.newArrayList();
//        for (Map.Entry<Integer, List<CustomerBalance>> map : customerBalanceMap.entrySet()) {
//            CustomerBalanceDetailResult customerBalanceDetailResult = new CustomerBalanceDetailResult();
//            customerBalanceDetailResult.setCustomerContactName(map.getValue().get(0).getCreatedBy());
//            customerBalanceDetailResult.setAmount(map.getValue().stream().map(e -> e.getConsignmentMargin()).reduce(BigDecimal.ZERO, BigDecimal::add));
//            resultList.add(customerBalanceDetailResult);
//        }
//
//        return resultList;
//    }
}
