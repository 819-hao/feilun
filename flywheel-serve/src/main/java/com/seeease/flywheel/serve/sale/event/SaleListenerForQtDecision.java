package com.seeease.flywheel.serve.sale.event;

import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.event.QtDecisionEvent;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLineDto;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 质检判定结果
 * @Date create in 2023/3/13 14:19
 */
@Component
public class SaleListenerForQtDecision implements BillHandlerEventListener<QtDecisionEvent> {

    @Resource
    protected BillSaleOrderService billSaleOrderService;
    @Resource
    protected BillSaleOrderLineService billSaleOrderLineService;

    private static List<BusinessBillTypeEnum> SALE_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TO_C_XS,
            BusinessBillTypeEnum.TO_B_JS,
            BusinessBillTypeEnum.TO_B_XS,
            BusinessBillTypeEnum.TO_C_ON_LINE
    );

    @Override
    public void onApplicationEvent(QtDecisionEvent event) {
        BillSaleOrder billSaleOrder = billSaleOrderService.selectBySerialNo(event.getOriginSerialNo());
        if (ObjectUtils.isEmpty(billSaleOrder) || !SALE_TYPE.contains(billSaleOrder.getSaleSource())) return;

        QualityTestingStateEnum qtState = event.getQtState();

        switch (qtState) {
            case NORMAL:
            case ANOMALY:

                break;
            case RETURN:
                billSaleOrderLineService.updateLineState(BillSaleOrderLineDto.builder()
                                .stockIdList(Lists.newArrayList(event.getStockId()))
                                .saleId(billSaleOrder.getId())
                                .build(),
                        SaleOrderLineStateEnum.TransitionEnum.QUALITY_TESTING_TO_RETURN);
                break;
            case FIX:
//                billSaleOrderLineService.updateLineState(BillSaleOrderLineDto.builder()
//                                .stockIdList(Lists.newArrayList(event.getStockId()))
//                                .whetherMD(Boolean.FALSE)
//                                .whetherChageOrderState(Boolean.FALSE)
//                                .serialNo(event.getOriginSerialNo())
//                                .build(),
//                        SaleOrderLineStateEnum.TransitionEnum.QUALITY_TESTING_TO_IN_FIX_INSPECTION);

        }
    }
}
