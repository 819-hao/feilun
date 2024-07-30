package com.seeease.flywheel.serve.sale.event;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceCmdTypeEnum;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.service.*;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.sale.entity.*;
import com.seeease.flywheel.serve.sale.enums.*;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.enums.StoreWorkLogisticsRejectStateEnum;
import com.seeease.flywheel.serve.storework.event.LogisticsReceivingEvent;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 退货情况下 总部物流收货 / 门店收货入库
 */
@Slf4j
@Component
public class SaleReturnListenerForLogisticsReceiving extends BaseSaleReturnListenerForStoreWork<LogisticsReceivingEvent>
        implements BillHandlerEventListener<LogisticsReceivingEvent> {

    @Resource
    private BillSaleOrderService billSaleOrderService;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;
    @Resource
    private FinancialDocumentsService financialDocumentsService;
    @Resource
    private StockService stockService;
    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    private CustomerBalanceService customerBalanceService;
    @Resource
    private FinancialInvoiceService invoiceService;
    @Resource
    private FinancialInvoiceStockService invoiceStockService;
    @Resource
    private FinancialInvoiceReverseService invoiceReverseService;
    private static final Set<SaleOrderReturnFlagEnum> TYPE = ImmutableSet.of(SaleOrderReturnFlagEnum.CDTH,
            SaleOrderReturnFlagEnum.TK);

    @Override
    public void onApplicationEvent(LogisticsReceivingEvent event) {
        super.onApplicationEvent(event.getWorkPreList(), event);
    }

    /**
     * 门店收货后处理退货单状态
     *
     * @param saleReturnOrder
     * @param preList
     * @param e
     */
    @Override
    void handler(BillSaleReturnOrder saleReturnOrder, List<BillStoreWorkPre> preList, LogisticsReceivingEvent e) {
        log.info("handler of SaleReturnListenerForLogisticsReceiving start and saleReturnOrder = {}", JSON.toJSONString(saleReturnOrder));
        List<Integer> stockIdList = preList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList());

        //销售退货单只要是拒绝收货
        if (e.getLogisticsRejectState().equals(StoreWorkLogisticsRejectStateEnum.REJECT)) {
            billSaleReturnOrderLineService.updateLineState(BillSaleReturnOrderLineDto.builder()
                            .saleReturnId(saleReturnOrder.getId())
                            .stockIdList(stockIdList)
                            .whetherChangeOrderState(e.isShopReceived())
                            .build(),
                    SaleReturnOrderLineStateEnum.TransitionEnum.UPLOAD_EXPRESS_NUMBER_TO_RETURNED
            );
            return;
        }

        //销售退货详情更改信息
        billSaleReturnOrderLineService.updateLineState(BillSaleReturnOrderLineDto.builder()
                        .saleReturnId(saleReturnOrder.getId())
                        .stockIdList(stockIdList)
                        .whetherChangeOrderState(e.isShopReceived())
                        .build(),
                e.isShopReceived() ? SaleReturnOrderLineStateEnum.TransitionEnum.UPLOAD_EXPRESS_NUMBER_TO_IN_STORAGE
                        : SaleReturnOrderLineStateEnum.TransitionEnum.UPLOAD_EXPRESS_NUMBER_TO_LOGISTICS_RECEIVING);

        //个人销售-退货财务 总部物流收货/门店后方入库后 应收单自动核销 状态：已核销
        if (e.isShopReceived() && !TYPE.contains(saleReturnOrder.getRefundFlag())) {
            List<AccountsPayableAccounting> list = accountingService
                    .selectListByOriginSerialNoAndStatusAndType(saleReturnOrder.getSerialNo(),
                            Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.RETURN_PENDING_REVIEW)
                            , Lists.newArrayList(ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT));
            accountingService.batchAudit(list.stream().filter(a -> stockIdList.contains(a.getStockId()))
                    .map(AccountsPayableAccounting::getId)
                    .collect(Collectors.toList()), FlywheelConstant.RETURN_AUDIT, UserContext.getUser().getUserName());
        }

        BillSaleOrder saleOrder = billSaleOrderService.getById(saleReturnOrder.getSaleId());

        if (saleOrder.getSaleType() == SaleOrderTypeEnum.TO_C_XS && saleOrder.getSaleChannel() != SaleOrderChannelEnum.STORE) {

            List<AccountsPayableAccounting> list = accountingService
                    .selectListByOriginSerialNoAndStatusAndType(saleReturnOrder.getSerialNo(),
                            Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.RETURN_PENDING_REVIEW)
                            , Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE));
            accountingService.batchAudit(list.stream()
                            .map(AccountsPayableAccounting::getId)
                            .collect(Collectors.toList()),
                    FlywheelConstant.RETURN_AUDIT, UserContext.getUser().getUserName());
        }

        if (e.isShopReceived()) {
            //如果是个人销售退货 将原销售单状态变成已退货
            billSaleOrderLineService.updateLineState(BillSaleOrderLineDto.builder()
                            .saleId(saleReturnOrder.getSaleId())
                            .stockIdList(stockIdList)
                            .build()
                    , SaleOrderLineStateEnum.TransitionEnum.IN_RETURN_TO_RETURN);


            //门店销售退货  后方收货&入库后
            FinancialGenerateDto dto = new FinancialGenerateDto();
            dto.setId(saleReturnOrder.getId());
            dto.setStockList(stockIdList);
            if (ObjectUtils.isNotEmpty(saleOrder.getTransferCustomerId()))
                financialDocumentsService.generateSaleReturnBalance(dto);
            else
                financialDocumentsService.generateSaleReturn(dto);
            List<Stock> stockList = stockService.listByIds(stockIdList);
            Map<Integer, StockStatusEnum> stockMap = stockList.stream()
                    .collect(Collectors.toMap(Stock::getId, Stock::getStockStatus));
            //入库流转商品状态
            preList.stream()
                    .collect(Collectors.groupingBy(BillStoreWorkPre::getExceptionMark))
                    .forEach((k, v) -> {
                        v.forEach(billStoreWorkPre -> {
                            StockStatusEnum stockStatusEnum = stockMap.get(billStoreWorkPre.getStockId());
                            StockStatusEnum.TransitionEnum transitionEnum;
                            switch (k) {
                                case YES:
                                    transitionEnum = StockStatusEnum.SOLD_OUT.equals(stockStatusEnum) ?
                                            StockStatusEnum.TransitionEnum.SALE_EXCEPTION :
                                            StockStatusEnum.TransitionEnum.CONSIGNMENT_EXCEPTION;
                                    break;
                                default:
                                    transitionEnum = StockStatusEnum.SOLD_OUT.equals(stockStatusEnum) ?
                                            StockStatusEnum.TransitionEnum.SALE_MARKETABLE :
                                            StockStatusEnum.TransitionEnum.CONSIGNMENT_MARKETABLE;
                            }
                            stockService.updateStockStatus(Lists.newArrayList(billStoreWorkPre.getStockId()), transitionEnum);
                        });
                    });
            stockService.cleanCkTimeById(stockIdList);

            if (SaleOrderTypeEnum.TO_B_JS.equals(saleOrder.getSaleType()) &&
                    !SaleOrderModeEnum.RETURN_POINT.equals(saleOrder.getSaleMode())) {

                List<SaleOrderLineStateEnum> saleOrderLineStateEnums = Lists.newArrayList(
                        SaleOrderLineStateEnum.CONSIGNMENT_SETTLED, SaleOrderLineStateEnum.DELIVERED);
                List<BillSaleReturnOrderLine> billSaleReturnOrderLines = billSaleReturnOrderLineService.
                        saleReturnOrderLineQry(saleReturnOrder.getId(), stockIdList, saleOrderLineStateEnums);

                customerBalanceCmd(saleReturnOrder, billSaleReturnOrderLines);
            }
            //在红冲列表添加数据
            List<BillSaleReturnOrderLineDetailsVO> detailsVOList = billSaleReturnOrderLineService.selectBySaleReturnId(saleReturnOrder.getId())
                    .stream()
                    .filter(a -> stockIdList.contains(a.getStockId()))
                    .filter(a -> FinancialInvoiceStateEnum.INVOICE_COMPLETE.getValue().equals(a.getWhetherInvoice()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(detailsVOList)) {
                Map<Integer, BigDecimal> priceMap = detailsVOList.stream()
                        .collect(Collectors.toMap(BillSaleReturnOrderLineDetailsVO::getStockId, BillSaleReturnOrderLineDetailsVO::getReturnPrice, (k1, k2) -> k2));
                Map<Integer, Integer> lineMap = detailsVOList.stream()
                        .collect(Collectors.toMap(BillSaleReturnOrderLineDetailsVO::getStockId, BillSaleReturnOrderLineDetailsVO::getId, (k1, k2) -> k2));
                List<FinancialInvoiceStock> invoiceStocks = detailsVOList
                        .stream()
                        .map(a -> invoiceStockService.getOneByStockIdAndLineId(a.getStockId(), a.getSaleLineId()))
                        .collect(Collectors.toList());

                Map<Integer, FinancialInvoice> invoiceMap = invoiceService.listByIds(invoiceStocks.stream().map(FinancialInvoiceStock::getFinancialInvoiceId).collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(FinancialInvoice::getId, Function.identity()));

                List<FinancialInvoiceReverse> reverseList = invoiceStocks.stream()
                        .map(a -> {
                            FinancialInvoice invoice = invoiceMap.get(a.getFinancialInvoiceId());
                            return FinancialInvoiceReverse.builder()
                                    .fiId(invoice.getId())
                                    .fiSerialNo(invoice.getSerialNo())
                                    .invoiceNumber(invoice.getInvoiceNumber())
                                    .invoiceSubject(invoice.getInvoiceSubject())
                                    .originPrice(priceMap.get(a.getStockId()).negate())
                                    .originSerialNo(saleReturnOrder.getSerialNo())
                                    .lineId(lineMap.get(a.getStockId()))
                                    .stockId(a.getStockId())
                                    .state(WhetherEnum.NO.getValue())
                                    .shopId(invoice.getShopId())
                                    .build();
                        }).collect(Collectors.toList());
                invoiceReverseService.saveBatch(reverseList);
                log.info("退货入库查询退货详情中 红冲数据: {}", reverseList);
            }
        }


    }

    public void customerBalanceCmd(BillSaleReturnOrder saleReturnOrder, List<BillSaleReturnOrderLine> billSaleReturnOrderLines) {
        log.info("customerBalanceCmd function of SaleReturnListenerForLogisticsReceiving start and saleReturnOrder = {},billSaleReturnOrderLines = {} ",
                JSON.toJSONString(saleReturnOrder), JSON.toJSONString(billSaleReturnOrderLines));

        BigDecimal totalCustomerBalance = billSaleReturnOrderLines.stream().filter(Objects::nonNull)
                .filter(e -> SaleOrderLineStateEnum.DELIVERED.getValue().equals(e.getSaleLineState()))
                .map(BillSaleReturnOrderLine::getReturnPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalConsignAmount = billSaleReturnOrderLines.stream().filter(Objects::nonNull)
                .filter(e -> SaleOrderLineStateEnum.CONSIGNMENT_SETTLED.getValue().equals(e.getSaleLineState()))
                .map(BillSaleReturnOrderLine::getReturnPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalCustomerBalance.compareTo(BigDecimal.ZERO) > 0) {
            customerBalanceService.customerBalanceByCreateIdCmd(saleReturnOrder.getCustomerId(), saleReturnOrder.getCustomerContactId()
                    , totalCustomerBalance,
                    CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue(), saleReturnOrder.getShopId(),
                    CustomerBalanceCmdTypeEnum.ADD.getValue(), saleReturnOrder.getCreatedId());
        }

        if (totalConsignAmount.compareTo(BigDecimal.ZERO) > 0) {
            customerBalanceService.customerBalanceByCreateIdCmd(saleReturnOrder.getCustomerId(), saleReturnOrder.getCustomerContactId()
                    , totalConsignAmount,
                    CustomerBalanceTypeEnum.JS_AMOUNT.getValue(), saleReturnOrder.getShopId(),
                    CustomerBalanceCmdTypeEnum.ADD.getValue(), saleReturnOrder.getCreatedId());
        }

    }
}
