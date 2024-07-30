package com.seeease.flywheel.serve.customer.rpc;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.customer.ICustomerFacade;
import com.seeease.flywheel.customer.entity.CustomerInfo;
import com.seeease.flywheel.customer.request.*;
import com.seeease.flywheel.customer.result.*;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.customer.convert.CustomerConvert;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceTypeEnum;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.CustomerBalance;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/3/1
 */
@Slf4j
@DubboService(version = "1.0.0")
public class CustomerFacade implements ICustomerFacade {

    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private CustomerBalanceService customerBalanceService;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;

    @Override
    public CustomerCreateResult create(CustomerCreateRequest request) {

        CustomerContacts customerContacts = customerContactsService.getOne(new LambdaQueryWrapper<CustomerContacts>()
                .eq(CustomerContacts::getPhone, request.getPhone()));
        if (customerContacts != null)
            new OperationRejectedException(OperationExceptionCode.EXISTING_DATA);
        int customerId = customerService.create(request);
        Integer customerContactsId = customerContactsService.create(request, customerId);
        return CustomerCreateResult.builder().customerId(customerId).customerContactsId(customerContactsId).build();
    }

    @Override
    public void update(CustomerUpdateRequest request) {
        customerService.update(request);

        customerContactsService.update(request);
    }

    @Override
    public PageResult<CustomerPageResult> query(CustomerQueryRequest request) {
        Page<CustomerPageResult> page = customerContactsService.query(request);
        return PageResult.<CustomerPageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PageResult<CustomerPageQueryResult> query(CustomerPageQueryRequest request) {

        LambdaQueryWrapper<Customer> query = Wrappers.<Customer>lambdaQuery().eq(Customer::getType, CustomerTypeEnum.ENTERPRISE);

        if (ObjectUtils.isNotEmpty(request.getCustomerName())) {
            query.like(Customer::getCustomerName, request.getCustomerName());
        }

        Page<Customer> page = customerService.page(new Page<>(request.getPage(), request.getLimit()), query);

        List<CustomerPageQueryResult> collect = page.getRecords().stream().
                map(customer -> CustomerConvert.INSTANCE.convertCustomerPageQueryResult(customer)).collect(Collectors.toList());

        return PageResult.<CustomerPageQueryResult>builder()
                .result(collect)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PageResult<ContactsPageQueryResult> query(ContactsPageQueryRequest request) {

        Optional.ofNullable(request.getCustomerId()).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER));

        LambdaQueryWrapper<CustomerContacts> query = Wrappers.<CustomerContacts>lambdaQuery().eq(CustomerContacts::getCustomerId, request.getCustomerId());

        if (ObjectUtils.isNotEmpty(request.getContactsName())) {
            query.likeRight(CustomerContacts::getName, request.getContactsName());
        }

        Page<CustomerContacts> page = customerContactsService.page(new Page<>(request.getPage(), request.getLimit()), query);

        List<ContactsPageQueryResult> collect = page.getRecords().stream().
                map(customer -> CustomerConvert.INSTANCE.convertContactsPageQueryResult(customer)).collect(Collectors.toList());

        return PageResult.<ContactsPageQueryResult>builder()
                .result(collect)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public List<CustomerInfo> findByCustomerName(List<String> customerNameList) {
        if (CollectionUtils.isEmpty(customerNameList)) {
            return Collections.EMPTY_LIST;
        }
        List<Customer> customerList = customerService.list(Wrappers.<Customer>lambdaQuery()
                .in(Customer::getCustomerName, customerNameList));

        if (CollectionUtils.isEmpty(customerList)) {
            return Collections.EMPTY_LIST;
        }

        List<CustomerInfo> result = CustomerConvert.INSTANCE.convertCustomerInfo(customerList);

        Map<Integer, List<CustomerContacts>> customerContactsMap = customerContactsService.list(Wrappers.<CustomerContacts>lambdaQuery()
                        .in(CustomerContacts::getCustomerId, customerList.stream().map(Customer::getId).collect(Collectors.toList())))
                .stream()
                .collect(Collectors.groupingBy(CustomerContacts::getCustomerId));

        result.forEach(t -> {
            if (customerContactsMap.containsKey(t.getId())) {
                t.setContactsInfoList(CustomerConvert.INSTANCE.convertCustomerContactsInfo(customerContactsMap.get(t.getId())));
            }
        });

        return result;
    }


    @Override
    public PageResult<CustomerAndContractsPageQueryResult> customerAndContractsPageQry(CustomerAndContactsPageQueryRequest request) {

        Page<CustomerAndContractsPageQueryResult> page = customerContactsService.customerAndContractPageQry(request);

        if (null == page || CollectionUtil.isEmpty(page.getRecords())) {
            return PageResult.<CustomerAndContractsPageQueryResult>builder()
                    .result(page.getRecords())
                    .totalCount(page.getTotal())
                    .totalPage(page.getPages())
                    .build();
        }

        //查找customerBalance中的金额记录
        Integer userId = UserContext.getUser().getId();
        List<CustomerAndContractsPageQueryResult> pageQueryResultList = Lists.newArrayList();
        for (CustomerAndContractsPageQueryResult pageQueryResult : page.getRecords()) {
            List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(pageQueryResult.getCustomerId(), null);

            //查找寄售货值，并计算保证金余额
            List<BillSaleOrder> billSaleOrderList = customerBalanceService.consignmentGoodsQuery(pageQueryResult.getCustomerId());
            Map<Integer, List<BillSaleOrder>> billSaleOrderMap = billSaleOrderList.stream().filter(a -> Objects.nonNull(a.getFirstSalesman()))
                    .collect(Collectors.groupingBy(BillSaleOrder::getFirstSalesman));

            BigDecimal billOrderAmount = BigDecimal.ZERO;
            if (billSaleOrderMap.containsKey(userId)) {
//                billOrderAmount = billSaleOrderMap.get(userId).stream().map(BillSaleOrder::getTotalSalePrice)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<BillSaleOrder> saleOrderList = billSaleOrderMap.get(userId);
                List<SaleOrderLineStateEnum> saleOrderLineStateEnums = Lists.newArrayList(
                        SaleOrderLineStateEnum.CONSIGNMENT_SETTLED, SaleOrderLineStateEnum.CANCEL_WHOLE, SaleOrderLineStateEnum.RETURN);

                List<BillSaleOrderLine> billSaleOrderLineList = billSaleOrderLineService.selectBySaleIds(saleOrderList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList()));
                billOrderAmount = billSaleOrderLineList.stream()
                        .filter(e -> !saleOrderLineStateEnums.contains(e.getSaleLineState()))
                        .map(e -> e.getPreClinchPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);


            }

            BigDecimal accountBalance = customerBalanceList.stream().filter(Objects::nonNull)
                    .filter(e -> CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue().equals(e.getType()))
                    .filter(e -> e.getUserId().equals(userId))
//                    .filter(e -> e.getCreatedId().equals(userId))
                    .map(e -> e.getAccountBalance()).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal consignmentMargin = customerBalanceList.stream().filter(Objects::nonNull)
                    .filter(e -> CustomerBalanceTypeEnum.JS_AMOUNT.getValue().equals(e.getType()))
                    .filter(e -> e.getUserId().equals(userId))
//                    .filter(e -> e.getCreatedId().equals(userId))
                    .map(e -> e.getConsignmentMargin()).reduce(BigDecimal.ZERO, BigDecimal::add);
            pageQueryResult.setConsignmentMargin(consignmentMargin.subtract(billOrderAmount));
            pageQueryResult.setAccountBalance(accountBalance);
            pageQueryResultList.add(pageQueryResult);
        }

        page.setRecords(pageQueryResultList);
        log.info("pageQueryResultList function of CustomerFacade = {}", JSON.toJSONString(pageQueryResultList));

        return PageResult.<CustomerAndContractsPageQueryResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

}
