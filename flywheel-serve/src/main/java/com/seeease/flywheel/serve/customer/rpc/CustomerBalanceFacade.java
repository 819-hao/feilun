package com.seeease.flywheel.serve.customer.rpc;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.customer.ICustomerBalanceFacade;
import com.seeease.flywheel.customer.request.CustomerBalancePageRequest;
import com.seeease.flywheel.customer.request.CustomerBalanceRefundRequest;
import com.seeease.flywheel.customer.result.CustomerBalanceDetailResult;
import com.seeease.flywheel.customer.result.CustomerBalancePageResult;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCreateRequest;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentCreateResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.CustomerBalance;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentEnum;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialSalesMethodEnum;
import com.seeease.flywheel.serve.financial.event.ApplyFinancialPaymentCreateEvent;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.maindata.entity.FinancialStatementCompany;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.FinancialStatementCompanyService;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@DubboService(version = "1.0.0")
public class CustomerBalanceFacade implements ICustomerBalanceFacade {

    @Resource
    private CustomerBalanceService customerBalanceService;
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;
    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private FinancialStatementCompanyService statementCompanyService;

    @Override
    public PageResult<CustomerBalancePageResult> customerBalancePageQuery(CustomerBalancePageRequest request) {
        List<Integer> customerIdList = Lists.newArrayList();
        if (null != request && StringUtils.isNotEmpty(request.getCustomerName())) {
            List<Customer> customerList = customerService.searchByName(request.getCustomerName());
            customerIdList = customerList.stream().filter(Objects::nonNull).map(e -> e.getId()).collect(Collectors.toList());

            request.setCustomerIdList(customerIdList);
        }
        request.setShopId(UserContext.getUser().getStore().getId());

        Page<CustomerBalancePageResult> page = customerBalanceService.customerBalancePage(request);


        return PageResult.<CustomerBalancePageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Transactional
    @Override
    public void customerBalanceRefund(CustomerBalanceRefundRequest request) {

        //校验客户余额或者保证金是否充足
        checkCustomerBalanceLeft(request);

        //        List<PurchaseSubject> purchaseSubjectList = purchaseSubjectService.subjectCompanyQryBySubjectPayment(request.getSubjectPayment());
        FinancialStatementCompany company = statementCompanyService.getOne(new LambdaQueryWrapper<FinancialStatementCompany>()
                .eq(FinancialStatementCompany::getCompanyName, request.getSubjectPayment()));
        Assert.isTrue(Objects.nonNull(company), "退款打款主体不存在");
        Integer subjectPayment = Integer.valueOf(Lists.newArrayList(company.getSubjectId().split(",")).stream().findFirst().get());

        ApplyFinancialPaymentCreateRequest createRequest = new ApplyFinancialPaymentCreateRequest();

        createRequest.setCustomerName(request.getCustomerName());
        createRequest.setTypePayment(ApplyFinancialPaymentTypeEnum.BALANCE_REFUND.getValue());
        createRequest.setPricePayment(request.getClinchPrice());
        createRequest.setSubjectPayment(subjectPayment);
        createRequest.setBankName(request.getBankName());
        createRequest.setBankAccount(request.getBankAccount());
        createRequest.setBankCard(request.getBankCard());
        createRequest.setBankCustomerName(request.getBankCustomerName());
        createRequest.setShopId(UserContext.getUser().getStore().getId());

        createRequest.setSalesMethod(CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue().equals(request.getRefundType()) ? FinancialSalesMethodEnum.ACCOUNT_BALANCE.getValue() : FinancialSalesMethodEnum.JS_AMOUNT.getValue());
        createRequest.setManualCreation(WhetherEnum.YES.getValue());
        createRequest.setWhetherUse(WhetherEnum.YES.getValue());
        CustomerContacts contacts = customerContactsService.list(Wrappers.<CustomerContacts>lambdaQuery()
                .eq(CustomerContacts::getCustomerId, request.getCustomerId())).stream().findFirst().orElse(null);
        createRequest.setCustomerContactsId(Objects.nonNull(contacts) ? contacts.getId() : null);
        createRequest.setRefundType(request.getRefundType());
        createRequest.setPayment(ApplyFinancialPaymentEnum.RETURN.getValue());
        ApplyFinancialPaymentCreateResult paymentCreateResult = applyFinancialPaymentService.create(createRequest);

        billHandlerEventPublisher.publishEvent(new ApplyFinancialPaymentCreateEvent(request.getRefundType(),
                request.getCustomerId(), request.getClinchPrice(),
                Objects.nonNull(contacts) ? contacts.getId() : null, ApplyFinancialPaymentTypeEnum.BALANCE_REFUND,
                request.getUserId(), paymentCreateResult.getSerialNo()));
    }

    private void checkCustomerBalanceLeft(CustomerBalanceRefundRequest request) {
        log.info("checkCustomerBalanceLeft function of CustomerBalanceFacade start and request = {}", JSON.toJSONString(request));
//        Integer userid = UserContext.getUser().getId();

        if (CustomerBalanceTypeEnum.JS_AMOUNT.getValue().equals(request.getRefundType())) {
            //退寄售保证金
            List<CustomerBalanceDetailResult> customerBalanceDetailResults = queryconsignmentMarginDetail(request.getCustomerId());
            BigDecimal amountLeft = customerBalanceDetailResults.stream().filter(Objects::nonNull)
//                    .filter(e -> request.getUserId().equals(e.getCreateId()))
                    .filter(e -> request.getUserId().equals(e.getUserId()))
                    .map(CustomerBalanceDetailResult::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            //Assert.isTrue(amountLeft.compareTo(request.getClinchPrice()) >= 0, "客户寄售保证金不足");
            if (amountLeft.compareTo(request.getClinchPrice()) < 0) {
                throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_MERGIN_LEFT_ERROR);
            }

        } else if (CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue().equals(request.getRefundType())) {
            //退客户余额
            List<CustomerBalanceDetailResult> customerBalanceDetailResults = queryAccountBalanceDetail(request.getCustomerId());
            BigDecimal amountLeft = customerBalanceDetailResults.stream().filter(Objects::nonNull)
//                    .filter(e -> request.getUserId().equals(e.getCreateId()))
                    .filter(e -> request.getUserId().equals(e.getUserId()))
                    .map(CustomerBalanceDetailResult::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            //Assert.isTrue(amountLeft.compareTo(request.getClinchPrice()) >= 0, "客户余额不足");
            if (amountLeft.compareTo(request.getClinchPrice()) < 0) {
                throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_BALANCE_ERR);
            }
        }

    }

    @Resource
    private UserService userService;

    @Override
    public List<CustomerBalanceDetailResult> queryAccountBalanceDetail(Integer customerId) {
        List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(customerId, null);
        Map<Integer, List<CustomerBalance>> customerBalanceMap = customerBalanceList.stream()
                .filter(e -> e.getType().equals(CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue()))
//                .collect(Collectors.groupingBy(CustomerBalance::getCreatedId))
                .collect(Collectors.groupingBy(CustomerBalance::getUserId));

        Map<Long, String> collect = new HashMap<>();
        if (CollectionUtils.isNotEmpty(customerBalanceMap.keySet())) {
            collect = userService.listByIds(new ArrayList<>(customerBalanceMap.keySet())).stream().collect(Collectors.toMap(User::getId, User::getName, (k1, k2) -> k1));
        }

        List<CustomerBalanceDetailResult> resultList = Lists.newArrayList();
        for (Map.Entry<Integer, List<CustomerBalance>> map : customerBalanceMap.entrySet()) {
            Integer key = map.getKey();
            CustomerBalanceDetailResult customerBalanceDetailResult = new CustomerBalanceDetailResult();
            if (Objects.isNull(key) || key.equals(0)) {
                customerBalanceDetailResult.setCustomerContactName("未知用户");
            } else {
                customerBalanceDetailResult.setCustomerContactName(collect.getOrDefault(Long.valueOf(key), "未知用户"));
            }
            customerBalanceDetailResult.setAmount(map.getValue().stream().map(e -> e.getAccountBalance()).reduce(BigDecimal.ZERO, BigDecimal::add));
            customerBalanceDetailResult.setCreateId(key);
            customerBalanceDetailResult.setUserId(key);
            resultList.add(customerBalanceDetailResult);
        }

        return resultList;
    }

    /**
     * 清洗userId
     *
     * @param customerId
     * @return
     */
    @Override
    public List<CustomerBalanceDetailResult> queryconsignmentMarginDetail(Integer customerId) {
        List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(customerId, null);
        Map<Integer, List<CustomerBalance>> customerBalanceMap = customerBalanceList.stream()
                .filter(e -> e.getType().equals(CustomerBalanceTypeEnum.JS_AMOUNT.getValue()))
//                .collect(Collectors.groupingBy(CustomerBalance::getCreatedId))
                .collect(Collectors.groupingBy(CustomerBalance::getUserId));

        //查找寄售货值，并计算保证金余额
        List<BillSaleOrder> billSaleOrderList = customerBalanceService.consignmentGoodsQuery(customerId);
        Map<Integer, List<BillSaleOrder>> billSaleOrderMap = billSaleOrderList.stream().filter(Objects::nonNull)
                .collect(Collectors.groupingBy(BillSaleOrder::getFirstSalesman));
        Map<Long, String> collect = new HashMap<>();
        if (CollectionUtils.isNotEmpty(customerBalanceMap.keySet())) {
            collect = userService.listByIds(new ArrayList<>(customerBalanceMap.keySet())).stream().collect(Collectors.toMap(User::getId, User::getName, (k1, k2) -> k1));
        }

        List<CustomerBalanceDetailResult> resultList = Lists.newArrayList();

        for (Map.Entry<Integer, List<CustomerBalance>> entry : customerBalanceMap.entrySet()) {

            Integer createdId = entry.getKey();
            List<CustomerBalance> list = entry.getValue();

            CustomerBalanceDetailResult customerBalanceDetailResult = new CustomerBalanceDetailResult();


            if (Objects.isNull(createdId) || createdId.equals(0)) {
                customerBalanceDetailResult.setCustomerContactName("未知用户");
            } else {
                customerBalanceDetailResult.setCustomerContactName(collect.getOrDefault(Long.valueOf(createdId), "未知用户"));
            }
            customerBalanceDetailResult.setUserId(createdId);

            BigDecimal consignmentMarginSum = list.stream().map(CustomerBalance::getConsignmentMargin)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal billOrderAmount = BigDecimal.ZERO;

            if (billSaleOrderMap.containsKey(createdId)) {
                /*billOrderAmount = billSaleOrderMap.get(createdId).stream().map(BillSaleOrder::getTotalSalePrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);*/
                List<BillSaleOrder> saleOrderList = billSaleOrderMap.get(createdId);

                List<SaleOrderLineStateEnum> saleOrderLineStateEnums = Lists.newArrayList(
                        SaleOrderLineStateEnum.CONSIGNMENT_SETTLED, SaleOrderLineStateEnum.CANCEL_WHOLE, SaleOrderLineStateEnum.RETURN);

                List<BillSaleOrderLine> billSaleOrderLineList = billSaleOrderLineService.selectBySaleIds(saleOrderList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList()));
                billOrderAmount = billSaleOrderLineList.stream()
                        .filter(e -> !saleOrderLineStateEnums.contains(e.getSaleLineState()))
                        .map(e -> e.getPreClinchPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);

            }

            customerBalanceDetailResult.setCreateId(createdId);
            customerBalanceDetailResult.setAmount(consignmentMarginSum.subtract(billOrderAmount));
            resultList.add(customerBalanceDetailResult);
        }

        return resultList;
    }

}

