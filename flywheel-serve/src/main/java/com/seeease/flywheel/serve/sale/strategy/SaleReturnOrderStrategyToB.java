package com.seeease.flywheel.serve.sale.strategy;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderTypeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.springframework.exception.e.BusinessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * tob销售退货
 */
@Component
public class SaleReturnOrderStrategyToB extends SaleReturnOrderStrategy {

    @Resource
    protected BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private StockService stockService;

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.TO_B_XS_TH;
    }


    @Override
    void preRequestProcessing(SaleReturnOrderCreateRequest request) {
        //同行销售只能是商家组
        request.setShopId(FlywheelConstant._SJZ);
        request.setParentSerialNo(SerialNoGenerator.generateToBSaleReturnOrderSerialNo());
        request.setSaleReturnType(SaleReturnOrderTypeEnum.TO_B_JS_TH.getValue());
        //tob 销售退货原路退回发货地
        request.setReturnByOriginalRoute(true);
    }

    @Override
    void checkRequest(SaleReturnOrderCreateRequest request) throws BusinessException {

    }
}
