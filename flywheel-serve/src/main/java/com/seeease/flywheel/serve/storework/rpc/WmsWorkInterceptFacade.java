package com.seeease.flywheel.serve.storework.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.storework.entity.WmsWorkIntercept;
import com.seeease.flywheel.serve.storework.service.WmsWorkInterceptService;
import com.seeease.flywheel.storework.IWmsWorkInterceptFacade;
import com.seeease.flywheel.storework.request.WmsWorkInterceptRequest;
import com.seeease.flywheel.storework.result.WmsWorkInterceptResult;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author Tiro
 * @date 2023/9/4
 */
@Slf4j
@DubboService(version = "1.0.0")
public class WmsWorkInterceptFacade implements IWmsWorkInterceptFacade {
    @Resource
    private WmsWorkInterceptService wmsWorkInterceptService;
    @Resource
    private BillSaleOrderService billSaleOrderService;

    @Override
    public WmsWorkInterceptResult intercept(WmsWorkInterceptRequest request) {
        BillSaleOrder saleOrder = billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery()
                .eq(BillSaleOrder::getBizOrderCode, request.getBizOrderCode()));
        if (Objects.isNull(saleOrder)) {
            log.warn("第三方销售拦截失败，未找到相应订单:{}", request.getBizOrderCode());
            return null;
        }
        WmsWorkIntercept wmsWorkIntercept = new WmsWorkIntercept();
        wmsWorkIntercept.setOriginSerialNo(saleOrder.getSerialNo());
        wmsWorkIntercept.setBelongingStoreId(saleOrder.getShopId());
        wmsWorkIntercept.setInterceptState(request.isIntercept() ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue());
        wmsWorkInterceptService.saveOrUpdate(wmsWorkIntercept, Wrappers.<WmsWorkIntercept>lambdaQuery()
                .eq(WmsWorkIntercept::getOriginSerialNo, wmsWorkIntercept.getOriginSerialNo()));

        return WmsWorkInterceptResult.builder()
                .originSerialNo(wmsWorkIntercept.getOriginSerialNo())
                .build();
    }

    @Override
    public void checkIntercept(List<String> originSerialNoList) {
        //发货拦截
        wmsWorkInterceptService.checkIntercept(originSerialNoList);
    }
}
