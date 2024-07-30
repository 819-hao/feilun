package com.seeease.flywheel.serve.sale.strategy;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.base.StringTools;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * toc销售
 */
@Component
public class SaleOrderStrategyToC extends SaleOrderStrategy {

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.TO_C_XS;
    }

    @Override
    void preRequestProcessing(SaleOrderCreateRequest request) {
        request.setParentSerialNo(SerialNoGenerator.generateToCSaleOrderSerialNo());

        if (Objects.equals(SaleOrderModeEnum.DEPOSIT.getValue(), request.getSaleMode())) {
            request.getDetails().forEach(line -> {
                        line.setWhetherFix(WhetherEnum.NO.getValue());
                        line.setIsCounterPurchase(WhetherEnum.NO.getValue());
                        line.setIsRepurchasePolicy(WhetherEnum.NO.getValue());
                        line.setWhetherFix(WhetherEnum.NO.getValue());
                    }
            );
        }
        request.getDetails().forEach(t -> {
                    if (FlywheelConstant._SJZ == UserContext.getUser().getStore().getId()) {
                        t.setIsUnderselling(StockUndersellingEnum.ALLOW.getValue());
                    }
                    t.setWarrantyPeriod(FlywheelConstant.two);
                }
        );
        //设置当前登陆用户的门店 为销售门店
        request.setShopId(Objects.requireNonNull(UserContext.getUser().getStore().getId()));
    }

    @Override
    void checkRequest(SaleOrderCreateRequest request) throws BusinessException {
        Assert.notNull(request.getFirstSalesman(), "销售人不能为空");
        boolean isDeposit = Objects.equals(SaleOrderModeEnum.DEPOSIT.getValue(), request.getSaleMode());
        if (request.getShopId() == FlywheelConstant._ZB_ID) {
            throw new OperationRejectedException(OperationExceptionCode.ZB_NOT_ALLOWED_TO_CREATE_ORDER);
        }

        if(request.getDetails().stream()
                .collect(Collectors.groupingBy(SaleOrderCreateRequest.BillSaleOrderLineDto::getLocationId)).size() != NumberUtils.INTEGER_ONE){
            throw new OperationRejectedException(OperationExceptionCode.TOC_SALE_LOCATION_ID_ERROR);
        }

        if (isDeposit) {
            Assert.notNull(request.getDeposit(), "销售方式为订金销售时订金金额为空");
        } else {
            Assert.notNull(request.getPaymentMethod(), "非订金销售时付款方式为空");
            Assert.notNull(request.getBuyCause(), "非订金销售时付款方式为空");
        }
        if (SaleOrderChannelEnum.DOU_YIN.getValue() == request.getSaleChannel() && StringTools.isNull(request.getBizOrderCode())) {
            throw new OperationRejectedException(OperationExceptionCode.DOU_YIN_ORDER_NUMBER_IS_REQUIRED);
        }
        request.getDetails().forEach(t -> {
            Assert.notNull(t.getStockId(), "库存商品不存在");
            Assert.notNull(t.getClinchPrice(), "成交价为空");
            Assert.notNull(t.getTocPrice(), "商品无ToC价,请先为定价");
            Assert.notNull(t.getConsignmentPrice(), "库存商品寄售价不存在");
            if (SaleOrderModeEnum.PRESENTED.getValue() == request.getSaleMode()) {
                Assert.isTrue(BigDecimal.ZERO.compareTo(t.getClinchPrice()) == 0, "赠送成交价为0");
            } else {
                saleBreakPriceCheck.toCSaleCheck( t);
            }
            if (!isDeposit) {
                Assert.notNull(t.getIsCounterPurchase(), "非订金销售-是否回购协议为空");
                Assert.notNull(t.getIsRepurchasePolicy(), "非订金销售-是否回购政策为空");
                if (t.getIsCounterPurchase() == 1)
                    Assert.isTrue(Objects.nonNull(t.getStrapMaterial()), "非订金销售-表带类型为空");
                Assert.notNull(t.getWhetherFix(), "收取表带更换费为空");
            } else {
                Assert.isTrue(t.getLocationId() == FlywheelConstant._ZB_ID || t.getLocationId() == request.getShopId(),
                        "定金销售只能选择自己门店商品或者总部商品！！");
            }
        });
    }
}
