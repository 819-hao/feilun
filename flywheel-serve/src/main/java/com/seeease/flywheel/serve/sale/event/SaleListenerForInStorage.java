package com.seeease.flywheel.serve.sale.event;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLineDto;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.InStorageEvent;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 销售时候 质检异常入库
 */
@Component
public class SaleListenerForInStorage extends BaseSaleListenerForStoreWork<InStorageEvent> implements BillHandlerEventListener<InStorageEvent> {
    @Resource
    private StockService stockService;

    @Resource
    private AccountsPayableAccountingService accountingService;

    @Override
    public void onApplicationEvent(InStorageEvent event) {
        super.onApplicationEvent(event.getWorkPreList(), event);
    }

    /**
     * @param saleOrder
     * @param preList
     * @param inStorageEvent
     */
    @Override
    void handler(BillSaleOrder saleOrder, List<BillStoreWorkPre> preList, InStorageEvent inStorageEvent) {
        List<Integer> stockIdList = preList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList());
        billSaleOrderLineService.updateLineState(BillSaleOrderLineDto.builder()
                        .saleId(saleOrder.getId())
                        .stockIdList(stockIdList)
                        .build()
                , SaleOrderLineStateEnum.TransitionEnum.QUALITY_TESTING_TO_CANCEL_WHOLE);

        preList.forEach(billStoreWorkPre ->
                Assert.isTrue(WhetherEnum.YES.equals(billStoreWorkPre.getExceptionMark()), "必须是异常入库"));
        List<AccountsPayableAccounting> payableAccountingList = accountingService.list(Wrappers.<AccountsPayableAccounting>lambdaQuery()
                .eq(AccountsPayableAccounting::getType, ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT)
                .in(AccountsPayableAccounting::getStockId, stockIdList)
                .eq(AccountsPayableAccounting::getStatus, FinancialStatusEnum.PENDING_REVIEW)
        );

        if (CollectionUtils.isNotEmpty(payableAccountingList)){
            accountingService.batchAudit(payableAccountingList.stream().map(AccountsPayableAccounting::getId).collect(Collectors.toList()), FlywheelConstant.CANCEL_ORDER_AUDIT, UserContext.getUser().getUserName());
        }
        List<Stock> stockList = stockService.listByIds(stockIdList);
        stockList.stream().filter(stock -> StockStatusEnum.SOLD_OUT.equals(stock.getStockStatus())).forEach(stock -> {
            //已销售时候 质检异常入库
            stockService.updateStockStatus(Lists.newArrayList(stock.getId()), StockStatusEnum.TransitionEnum.SALE_EXCEPTION);
        });

        stockList.stream().filter(stock -> StockStatusEnum.CONSIGNMENT.equals(stock.getStockStatus())).forEach(stock -> {
            //已寄售时候 质检异常入库
            stockService.updateStockStatus(Lists.newArrayList(stock.getId()), StockStatusEnum.TransitionEnum.CONSIGNMENT_EXCEPTION);
        });
    }
}
