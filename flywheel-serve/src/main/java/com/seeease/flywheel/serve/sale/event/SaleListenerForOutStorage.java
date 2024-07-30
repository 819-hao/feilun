package com.seeease.flywheel.serve.sale.event;


import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLineDto;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.OutStorageEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 销售时候 仓库出库
 */
@Component
public class SaleListenerForOutStorage extends BaseSaleListenerForStoreWork<OutStorageEvent> implements BillHandlerEventListener<OutStorageEvent> {
    @Resource
    private StockService stockService;

    @Override
    public void onApplicationEvent(OutStorageEvent event) {
        super.onApplicationEvent(event.getWorkPreList(), event);
    }

    /**
     * @param saleOrder
     * @param preList
     * @param outStorageEvent
     */
    @Override
    void handler(BillSaleOrder saleOrder, List<BillStoreWorkPre> preList, OutStorageEvent outStorageEvent) {
        List<Integer> stockIdList = preList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList());

        billSaleOrderLineService.updateLineState(BillSaleOrderLineDto.builder()
                        .saleId(saleOrder.getId())
                        .stockIdList(stockIdList)
                        .build()
                , SaleOrderLineStateEnum.TransitionEnum.OUT_STORAGE_TO_QUALITY_TESTING);
        List<Stock> stockList = stockIdList.stream()
                .map(t ->
                        new Stock()
                                .setId(t)
                                .setCkTime(new Date())
                                .setIsUnderselling(StockUndersellingEnum.NOT_ALLOW)
                ).collect(Collectors.toList());
        stockService.updateBatchById(stockList);
    }
}
