package com.seeease.flywheel.serve.sale.strategy;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderTypeEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.springframework.context.LoginStore;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * toc销售退货
 */
@Component
public class SaleReturnOrderStrategyToC extends SaleReturnOrderStrategy {

    @Resource
    private StockService stockService;
    @Resource
    private BillSaleOrderService saleOrderService;

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.TO_C_XS_TH;
    }

    @Override
    void preRequestProcessing(SaleReturnOrderCreateRequest request) {
        //门店id
        //设置当前登陆用户的门店 为销售门店
        request.setShopId(Optional.ofNullable(UserContext.getUser())
                .map(LoginUser::getStore)
                .map(LoginStore::getId)
                .orElse(request.getShopId()));

        request.setParentSerialNo(SerialNoGenerator.generateToCSaleReturnOrderSerialNo());
        request.setSaleReturnType(SaleReturnOrderTypeEnum.TO_C_XS_TH.getValue());
        //toc 销售退货退至销售门店
        request.setReturnByOriginalRoute(false);
        //3号楼发货特殊逻辑，3号楼发货退货原路反回
        BillSaleOrder saleOrder = saleOrderService.getById(request.getDetails().get(0).getSaleId());
        if (SaleOrderChannelEnum.T_MALL.equals(saleOrder.getSaleChannel())) {
            request.setReturnByOriginalRoute(false);
        } else if (FlywheelConstant._DF3_SHOP_ID == saleOrder.getDeliveryLocationId()) {
            request.setReturnByOriginalRoute(true);
        }
    }

    @Override
    void checkRequest(SaleReturnOrderCreateRequest request) throws BusinessException {

    }
}
