package com.seeease.flywheel.serve.customer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.seeease.flywheel.customer.request.CustomerBalancePageRequest;
import com.seeease.flywheel.customer.result.CustomerBalancePageResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.financial.entity.CustomerBalance;
import com.seeease.flywheel.serve.financial.mapper.CustomerBalanceMapper;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderStateEnum;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CustomerBalanceServiceImpl extends ServiceImpl<CustomerBalanceMapper, CustomerBalance>
        implements CustomerBalanceService {

    @Resource
    private BillSaleOrderMapper billSaleOrderMapper;
    @Resource
    private BillSaleOrderLineMapper billSaleOrderLineMapper;


    @Override
    public List<CustomerBalance> customerBalanceList(Integer customerId, List<Integer> contractsIds) {
        LambdaQueryWrapper<CustomerBalance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomerBalance::getDeleted, 0)
                .eq(null != customerId, CustomerBalance::getCustomerId, customerId)
                .in(CollectionUtil.isNotEmpty(contractsIds), CustomerBalance::getCustomerContactId, contractsIds);
        List<CustomerBalance> customerBalanceList = baseMapper.selectList(queryWrapper);
        return customerBalanceList;
    }

    @Override
    public List<BillSaleOrder> consignmentGoodsQuery(Integer customerId) {

        //查找寄售货值
        LambdaQueryWrapper<BillSaleOrder> billSaleOrderQueryWrapper = new LambdaQueryWrapper<>();
        billSaleOrderQueryWrapper.eq(BillSaleOrder::getCustomerId, customerId)
                .eq(BillSaleOrder::getSaleSource, BusinessBillTypeEnum.TO_B_JS)
                .in(BillSaleOrder::getSaleState, Lists.newArrayList(SaleOrderStateEnum.UN_STARTED, SaleOrderStateEnum.UNDER_WAY));
        List<BillSaleOrder> billSaleOrderList = billSaleOrderMapper.selectList(billSaleOrderQueryWrapper);

        return billSaleOrderList;
    }

    @Override
    public Page<CustomerBalancePageResult> customerBalancePage(CustomerBalancePageRequest request) {
        Page<CustomerBalancePageResult> page = this.baseMapper.
                getCustomerBalancePage(new Page(request.getPage(), request.getLimit()), request);

        //查找寄售货值，并计算保证金余额
        List<Integer> customerIdList = page.getRecords().stream().filter(Objects::nonNull)
                .map(e -> e.getCustomerId()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(customerIdList)) {
            LambdaQueryWrapper<BillSaleOrder> billSaleOrderQueryWrapper = new LambdaQueryWrapper<>();
            billSaleOrderQueryWrapper.in(BillSaleOrder::getCustomerId, customerIdList)
                    .eq(BillSaleOrder::getSaleSource, BusinessBillTypeEnum.TO_B_JS)
                    .in(BillSaleOrder::getSaleState, Lists.newArrayList(SaleOrderStateEnum.UN_STARTED, SaleOrderStateEnum.UNDER_WAY));
            List<BillSaleOrder> billSaleOrderList = billSaleOrderMapper.selectList(billSaleOrderQueryWrapper);
            Map<Integer, List<BillSaleOrder>> billSaleOrderMap = billSaleOrderList.stream().filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(BillSaleOrder::getCustomerId));

            List<CustomerBalancePageResult> customerBalancePageResultList = Lists.newArrayList();
            for (CustomerBalancePageResult customerBalancePageResult : page.getRecords()) {
                BigDecimal consignmentGoods = BigDecimal.ZERO;
                if (!billSaleOrderMap.isEmpty() && CollectionUtil.isNotEmpty(billSaleOrderMap.get(customerBalancePageResult.getCustomerId()))) {
                    List<BillSaleOrder> saleOrderList = billSaleOrderMap.get(customerBalancePageResult.getCustomerId());
                    List<SaleOrderLineStateEnum> saleOrderLineStateEnums = Lists.newArrayList(
                            SaleOrderLineStateEnum.CONSIGNMENT_SETTLED, SaleOrderLineStateEnum.CANCEL_WHOLE, SaleOrderLineStateEnum.RETURN, SaleOrderLineStateEnum.DELIVERED);

                    LambdaQueryWrapper<BillSaleOrderLine> saleOrderLineQueryWrapper = new LambdaQueryWrapper<>();
                    saleOrderLineQueryWrapper.in(BillSaleOrderLine::getSaleId, saleOrderList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList()))
                            .notIn(BillSaleOrderLine::getSaleLineState, saleOrderLineStateEnums);
                    List<BillSaleOrderLine> billSaleOrderLineList = billSaleOrderLineMapper.selectList(saleOrderLineQueryWrapper);

                    consignmentGoods = billSaleOrderLineList.stream()
                            .map(e -> e.getPreClinchPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);


//                    consignmentGoods = billSaleOrderMap.get(customerBalancePageResult.getCustomerId()).stream()
//                            .map(e -> e.getTotalSalePrice()).reduce(BigDecimal.ZERO, BigDecimal::add);

                }

                customerBalancePageResult.setConsignmentGoods(consignmentGoods);
                customerBalancePageResult.setInsuranceFee(customerBalancePageResult.getConsignmentMargin().subtract(consignmentGoods));
                customerBalancePageResultList.add(customerBalancePageResult);
            }

            page.setRecords(customerBalancePageResultList);
        }

        return page;
    }

    @Override
    public void customerBalanceCmd(Integer customerId, Integer contractsId, BigDecimal amount, Integer
            type, Integer shopId, Integer cmdType, Integer createId, String originSerialNo) {
        log.info("customerBalanceCmd function start and customerId = {},contractsId ={},amount ={},type = {},shopId ={},cmdType = {}",
                customerId, contractsId, amount, type, shopId, cmdType);
        CustomerBalance customerBalance = new CustomerBalance();
        customerBalance.setCustomerId(customerId);
        customerBalance.setCustomerContactId(contractsId);
        BigDecimal consignmentMargin = BigDecimal.ZERO;
        BigDecimal accountBalance = BigDecimal.ZERO;
        if (CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue().equals(type)) {
            accountBalance = amount.multiply(new BigDecimal(cmdType));
        } else {
            consignmentMargin = amount.multiply(new BigDecimal(cmdType));
        }
        customerBalance.setType(type);
        customerBalance.setAccountBalance(accountBalance);
        customerBalance.setConsignmentMargin(consignmentMargin);
        customerBalance.setShopId(shopId);
//        customerBalance.setUserId(FlywheelConstant.INTEGER_DAFULT_VALUE);
        customerBalance.setUserId(createId);
        customerBalance.setOriginSerialNo(originSerialNo);
        this.baseMapper.insert(customerBalance);
    }

    @Override
    public void customerBalanceByCreateIdCmd(Integer customerId, Integer contractsId, BigDecimal amount, Integer type, Integer shopId, Integer cmdType, Integer createId) {
        log.info("customerBalanceByCreateIdCmd function start and customerId = {},contractsId ={},amount ={},type = {},shopId ={},cmdType = {},createId= {}",
                customerId, contractsId, amount, type, shopId, cmdType, createId);
        CustomerBalance customerBalance = new CustomerBalance();
        customerBalance.setCustomerId(customerId);
        customerBalance.setCustomerContactId(contractsId);
        BigDecimal consignmentMargin = BigDecimal.ZERO;
        BigDecimal accountBalance = BigDecimal.ZERO;
        if (CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue().equals(type)) {
            accountBalance = amount.multiply(new BigDecimal(cmdType));
        } else {
            consignmentMargin = amount.multiply(new BigDecimal(cmdType));
        }
        customerBalance.setType(type);
        customerBalance.setAccountBalance(accountBalance);
        customerBalance.setConsignmentMargin(consignmentMargin);
        customerBalance.setShopId(shopId);
//        customerBalance.setUserId(FlywheelConstant.INTEGER_DAFULT_VALUE);
        customerBalance.setCreatedId(createId);
        customerBalance.setUserId(createId);
        this.baseMapper.insert(customerBalance);
    }


}
