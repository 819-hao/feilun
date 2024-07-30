package com.seeease.flywheel.serve.sale.event;

import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.event.QtDecisionEvent;
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
public class SaleReturnListenerForQtDecision implements BillHandlerEventListener<QtDecisionEvent> {

    @Resource
    protected BillSaleReturnOrderService billSaleReturnOrderService;
    @Resource
    protected BillSaleReturnOrderLineService billSaleReturnOrderLineService;
    @Resource
    protected StockService stockService;

    private static List<BusinessBillTypeEnum> SALE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TO_C_XS_TH,
            BusinessBillTypeEnum.TO_B_XS_TH,
            BusinessBillTypeEnum.TO_C_ON_LINE_TH
    );

    @Override
    public void onApplicationEvent(QtDecisionEvent event) {
        BillSaleReturnOrder billSaleReturnOrder = billSaleReturnOrderService.selectBySriginSerialNo(event.getOriginSerialNo());
        if (ObjectUtils.isEmpty(billSaleReturnOrder) || !SALE_RETURN_TYPE.contains(billSaleReturnOrder.getSaleReturnSource()))
            return;

        QualityTestingStateEnum qtState = event.getQtState();

        Integer state = billSaleReturnOrderLineService.selectStateByReturnIdAndStockId(billSaleReturnOrder.getId(), event.getStockId());
        boolean flag = SaleReturnOrderLineStateEnum.LOGISTICS_RECEIVING.getValue().equals(state);
        switch (qtState) {
            case NORMAL:
            case ANOMALY:
                if (flag)
                    billSaleReturnOrderLineService.updateLineState(BillSaleReturnOrderLineDto.builder()
                            .serialNo(event.getOriginSerialNo())
                            .stockIdList(Lists.newArrayList(event.getStockId()))
                            .whetherChangeOrderState(Boolean.FALSE)
                            .build(), SaleReturnOrderLineStateEnum.TransitionEnum.LOGISTICS_RECEIVING_TO_QUALITY_TESTING);
                break;

            case FIX:
                billSaleReturnOrderLineService.updateLineState(BillSaleReturnOrderLineDto.builder()
                        .serialNo(event.getOriginSerialNo())
                        .stockIdList(Lists.newArrayList(event.getStockId()))
                        .whetherChangeOrderState(Boolean.FALSE)
                        .build(), flag ? SaleReturnOrderLineStateEnum.TransitionEnum.LOGISTICS_RECEIVING_TO_IN_FIX_INSPECTION
                        : SaleReturnOrderLineStateEnum.TransitionEnum.QUALITY_TESTING_TO_IN_FIX_INSPECTION);
                break;

            case RETURN:
                billSaleReturnOrderLineService.updateLineState(BillSaleReturnOrderLineDto.builder()
                        .serialNo(event.getOriginSerialNo())
                        .stockIdList(Lists.newArrayList(event.getStockId()))
                        .whetherChangeOrderState(Boolean.TRUE)
                        .build(), flag ? SaleReturnOrderLineStateEnum.TransitionEnum.LOGISTICS_RECEIVING_TO_RETURNED :
                        SaleReturnOrderLineStateEnum.TransitionEnum.QUALITY_TESTING_TO_RETURNED);
                break;
        }
    }
}
