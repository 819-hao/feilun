package com.seeease.flywheel.serve.sale.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.event.QtReceiveEvent;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLineDto;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Component
public class SaleReturnListenerForQtReceive implements BillHandlerEventListener<QtReceiveEvent> {

    @Resource
    private BillQualityTestingService billQualityTestingService;
    @Resource
    protected BillSaleReturnOrderLineService billSaleReturnOrderLineService;
    @Resource
    protected BillSaleReturnOrderService billSaleReturnOrderService;
    private static List<BusinessBillTypeEnum> SALE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TO_C_XS_TH,
            BusinessBillTypeEnum.TO_B_XS_TH,
            BusinessBillTypeEnum.TO_C_ON_LINE_TH
    );

    @Override
    public void onApplicationEvent(QtReceiveEvent event) {

        BillSaleReturnOrder billSaleReturnOrder = billSaleReturnOrderService.selectBySriginSerialNo(event.getOriginSerialNo());
        if (ObjectUtils.isEmpty(billSaleReturnOrder) || !SALE_RETURN_TYPE.contains(billSaleReturnOrder.getSaleReturnSource())) return;

        BillQualityTesting one = billQualityTestingService.getOne(Wrappers.<BillQualityTesting>lambdaQuery().
                eq(BillQualityTesting::getOriginSerialNo, event.getOriginSerialNo()).eq(BillQualityTesting::getStockId, event.getStockId()));

        if (one.getFixFlag() == 0) {
            billSaleReturnOrderLineService.updateLineState(BillSaleReturnOrderLineDto.builder()
                    .serialNo(event.getOriginSerialNo())
                    .stockIdList(Lists.newArrayList(event.getStockId()))
                    .whetherChangeOrderState(Boolean.FALSE)
                    .build(), SaleReturnOrderLineStateEnum.TransitionEnum.LOGISTICS_RECEIVING_TO_QUALITY_TESTING);
        } else {
            billSaleReturnOrderLineService.updateLineState(BillSaleReturnOrderLineDto.builder()
                    .serialNo(event.getOriginSerialNo())
                    .stockIdList(Lists.newArrayList(event.getStockId()))
                    .whetherChangeOrderState(Boolean.FALSE)
                    .build(), SaleReturnOrderLineStateEnum.TransitionEnum.QUALITY_TESTING_TO_IN_FIX_INSPECTION);
        }

    }
}
