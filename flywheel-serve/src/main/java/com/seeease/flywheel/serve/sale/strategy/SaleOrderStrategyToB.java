package com.seeease.flywheel.serve.sale.strategy;

import com.alibaba.fastjson.JSON;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.financial.entity.CustomerBalance;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * tob正常销售
 */
@Slf4j
@Component
public class SaleOrderStrategyToB extends SaleOrderStrategy {

    @Resource
    protected BillStoreWorkPreService billStoreWorkPreService;


    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.TO_B_XS;
    }

    @Override
    void preRequestProcessing(SaleOrderCreateRequest request) {
        if (request.getSaleChannel() == null) {
            request.setSaleChannel(SaleOrderChannelEnum.OTHER.getValue());
        }
        request.setParentSerialNo(SerialNoGenerator.generateToBSaleOrderSerialNo());
        request.getDetails().forEach(t -> {
            t.setPreClinchPrice(null);
            t.setWhetherFix(WhetherEnum.NO.getValue());
        });
        //同行销售只能是商家组
        request.setShopId(FlywheelConstant._SJZ);
    }

    @Override
    void checkRequest(SaleOrderCreateRequest request) throws BusinessException {
        Assert.notNull(request.getFirstSalesman(), "销售人不能为空");
        request.getDetails().forEach(t -> {
            Assert.notNull(t.getStockId(), "库存商品不存在");
            Assert.notNull(t.getTobPrice(), "商品无ToB价,请先为定价");
            Assert.notNull(t.getClinchPrice(), "成交价为空");
            Assert.notNull(t.getConsignmentPrice(), "库存商品寄售价不存在");
//            if (BigDecimalUtil.lt(t.getClinchPrice(), t.getTotalPrice())) {
//                throw new OperationRejectedException(OperationExceptionCode.NO_BREAKING_TOTAL_PRICE_IS_ALLOWED);
//            }
//            if (StockUndersellingEnum.ALLOW_TO_B.getValue().intValue() != t.getIsUnderselling()
//                    && t.getClinchPrice().compareTo(t.getTobPrice()) < 0)
//                throw new OperationRejectedException(OperationExceptionCode.NO_BREAKING_PRICE_IS_ALLOWED);
            saleBreakPriceCheck.toBSaleCheck(t);
        });

        if (!SaleOrderModeEnum.RETURN_POINT.getValue().equals(request.getSaleMode()))
            checkCustomerBalance(request.getCustomerId(), request.getCustomerContactId(), request.getDetails());
    }

    /**
     * 个人销售---总金额必须小于客户的可用余额
     *
     * @param customerId
     * @param customerContactId
     */
    void checkCustomerBalance(Integer customerId, Integer customerContactId, List<SaleOrderCreateRequest.BillSaleOrderLineDto> billSaleOrderLineDtoList) {
        log.info("checkCustomerBalance function of SaleOrderStrategyToB start and customerId = {},customerContactId = {},billSaleOrderLineDtoList = {}",
                customerId, customerContactId, JSON.toJSONString(billSaleOrderLineDtoList));
        //不按联系人统计
        List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(customerId, new ArrayList<>());

        BigDecimal accountBalanceSum = customerBalanceList.stream().filter(Objects::nonNull)
                .filter(e -> e.getUserId().equals(UserContext.getUser().getId()))
                .map(e -> e.getAccountBalance())
                .reduce(BigDecimal.ZERO, (b1, b2) -> b1.add(b2));

        BigDecimal clinchPriceSum = billSaleOrderLineDtoList.stream()
                .map(e -> e.getClinchPrice()).reduce(BigDecimal.ZERO, (b1, b2) -> b1.add(b2));
        //Assert.isTrue(accountBalanceSum.compareTo(BigDecimal.ZERO) > 0, "客户可用余额为0");
        //Assert.isTrue(accountBalanceSum.compareTo(clinchPriceSum) >= 0, "总金额必须小于客户的可用余额");
        if (accountBalanceSum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_BALANCE_LEFT_ZERO);
        }
        if (accountBalanceSum.compareTo(clinchPriceSum) < 0) {
            throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_BALANCE_GT_CHICKPRICE);
        }

    }
}
