package com.seeease.flywheel.serve.sale.strategy;

import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.goods.enums.BrandBusinessTypeEnum;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wbh
 * @date 2023/7/25
 */
@Component
public class SaleBreakPriceCheck {


    /**
     * 定金校验
     *
     * @param shopId
     * @param saleChannel
     * @param dto
     */
    public void checkDeposit(Integer shopId, Integer saleChannel, Integer saleMode, SaleOrderCreateRequest.BillSaleOrderLineDto dto) {
        if (Objects.isNull(dto.getLockDemand())
                || dto.getLockDemand() == 0) {
            return;
        }
        //订金商品，其他门店不可售卖
        if (shopId.intValue() != dto.getLockDemand()) {
            throw new OperationRejectedException(OperationExceptionCode.LOCK_DEMAND_NOT_ALLOWED_TO_SALE);
        }
        //商城定价特殊逻辑
        if (SaleOrderChannelEnum.XI_YI_SHOP.getValue().intValue() == saleChannel) {
            return;
        }
        //销售方式必须是订金销售
        if (SaleOrderModeEnum.DEPOSIT.getValue().intValue() != saleMode &&
                SaleOrderModeEnum.ON_LINE.getValue().intValue() != saleMode) {
            throw new OperationRejectedException(OperationExceptionCode.LOCK_DEMAND_MUST_DEPOSIT);
        }

    }


    /**
     * 价格校验
     *
     * @param t
     */
    public void toCSaleCheck(SaleOrderCreateRequest.BillSaleOrderLineDto t) {
//        if (BrandBusinessTypeEnum.SMALL_WATCH.getValue().intValue() == t.getBrandBusinessType().intValue()) {
//            return;
//        }
//
//        //有活动价规则校验，成交价>=活动价 允许销售
//        if (Objects.nonNull(t.getPromotionPrice())
//                && BigDecimalUtil.ge(t.getClinchPrice(), t.getPromotionPrice())) {
//            return;
//        }

        //破价逻辑
//        if (Objects.equals(StockUndersellingEnum.ALLOW.getValue(), t.getIsUnderselling())) {
//            if (BigDecimalUtil.lt(t.getClinchPrice(), t.getConsignmentPrice())) {
//                throw new OperationRejectedException(OperationExceptionCode.NO_BREAKING_CONSIGNMENT_PRICE_IS_ALLOWED);
//            }
//        } else if (BigDecimalUtil.lt(t.getClinchPrice(), t.getTocPrice())) {
//            throw new OperationRejectedException(OperationExceptionCode.UNP_FINESS_NEW_NO_BREAKING_PRICE_IS_ALLOWED_C);
//        }

    }

    public void toBSaleCheck(SaleOrderCreateRequest.BillSaleOrderLineDto t) {
        BigDecimal price = Optional.ofNullable(t.getClinchPrice()).orElse(t.getPreClinchPrice());
        //破价逻辑
        if (Objects.equals(StockUndersellingEnum.ALLOW.getValue(), t.getIsUnderselling())) {
            if (BigDecimalUtil.lt(price, t.getConsignmentPrice())) {
                throw new OperationRejectedException(OperationExceptionCode.NO_BREAKING_CONSIGNMENT_PRICE_IS_ALLOWED);
            }
        } else if (BigDecimalUtil.lt(price, t.getTobPrice())) {
            throw new OperationRejectedException(OperationExceptionCode.NO_BREAKING_B_PRICE_IS_ALLOWED);
        }
    }
}
