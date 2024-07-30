package com.seeease.flywheel.serve.sale.strategy;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.financial.entity.CustomerBalance;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * tob寄售销售
 */
@Component
public class SaleOrderStrategyToBJs extends SaleOrderStrategy {

    @Resource
    protected BillStoreWorkPreService billStoreWorkPreService;

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.TO_B_JS;
    }

    @Override
    void preRequestProcessing(SaleOrderCreateRequest request) {
        if (request.getSaleChannel() == null){
            request.setSaleChannel(SaleOrderChannelEnum.OTHER.getValue());
        }
        request.setParentSerialNo(SerialNoGenerator.generateToBSaleOrderSerialNo());
        request.getDetails().forEach(t -> {
            t.setClinchPrice(null);
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
            Assert.notNull(t.getPreClinchPrice(), "预计成交价为空");
            Assert.notNull(t.getTobPrice(), "商品无ToB价,请先为定价");
//            if (BigDecimalUtil.lt(t.getPreClinchPrice(), t.getTotalPrice())) {
//                throw new OperationRejectedException(OperationExceptionCode.NO_BREAKING_TOTAL_PRICE_IS_ALLOWED);
//            }
//            if (StockUndersellingEnum.ALLOW_TO_B.getValue().intValue() != t.getIsUnderselling() && t.getPreClinchPrice().compareTo(t.getTobPrice()) < 0)
//                throw new OperationRejectedException(OperationExceptionCode.NO_BREAKING_PRICE_IS_ALLOWED);
            saleBreakPriceCheck.toBSaleCheck(t);
            Assert.notNull(t.getConsignmentPrice(), "库存商品寄售价不存在");
        });

        checkTbJsCustomerBalance(request.getCustomerId(), request.getCustomerContactId(), request.getDetails());
    }

    /**
     * 同行销售---寄售小于寄售保证金
     *
     * @param customerId
     * @param customerContactId
     */
    void checkTbJsCustomerBalance(Integer customerId, Integer customerContactId, List<SaleOrderCreateRequest.BillSaleOrderLineDto> billSaleOrderLineDtoList) {
        List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(customerId, null);
        BigDecimal consignmentMarginSum = customerBalanceList.stream().filter(Objects::nonNull)
                .filter(e -> e.getUserId().equals(UserContext.getUser().getId()))
                .map(e -> e.getConsignmentMargin())
                .reduce(BigDecimal.ZERO, (b1, b2) -> b1.add(b2));

        BigDecimal clinchPriceSum = billSaleOrderLineDtoList.stream()
                .map(e -> e.getPreClinchPrice())
                .reduce(BigDecimal.ZERO, (b1, b2) -> b1.add(b2));
        //Assert.isTrue(consignmentMarginSum.compareTo(BigDecimal.ZERO) > 0, "客户寄售保证金为0");
        //Assert.isTrue(consignmentMarginSum.compareTo(clinchPriceSum) >= 0, "寄售金额必须小于寄售保证金");
        if (consignmentMarginSum.compareTo(BigDecimal.ZERO) <= 0){
            throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_MERGIN_LEFT_ZERO);
        }
        if (consignmentMarginSum.compareTo(clinchPriceSum) < 0){
            throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_MERGIN_GT_CHICKPRICE);
        }
    }

}
