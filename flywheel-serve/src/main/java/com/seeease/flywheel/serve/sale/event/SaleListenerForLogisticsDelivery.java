package com.seeease.flywheel.serve.sale.event;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmAddRequest;
import com.seeease.flywheel.sale.entity.SaleOrderDeliveryMessage;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.entity.CustomerPO;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.entity.FinancialGenerateDto;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.GpmConfig;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.entity.StoreRelationshipSubject;
import com.seeease.flywheel.serve.maindata.service.GpmConfigService;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLineDto;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderTypeEnum;
import com.seeease.flywheel.serve.sale.mq.SaleOrderDeliveryProducers;
import com.seeease.flywheel.serve.sale.service.RcSaleDeliveryVideoService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.LogisticsDeliveryEvent;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 销售情况下 总部物流发货 / 门店发货出库
 */
@Slf4j
@Component
public class SaleListenerForLogisticsDelivery extends BaseSaleListenerForStoreWork<LogisticsDeliveryEvent>
        implements BillHandlerEventListener<LogisticsDeliveryEvent> {

    @Resource
    private StockService stockService;
    @Resource
    private StoreRelationshipSubjectService storeRelationshipSubjectService;
    @Resource
    private StoreService storeService;
    @Resource
    private GpmConfigService gpmConfigService;
    @Resource
    private FinancialDocumentsService financialDocumentsService;
    @Resource
    private BillPurchaseLineService billPurchaseLineService;
    @Resource
    private BillPurchaseService billPurchaseService;
    @Resource
    private SaleOrderDeliveryProducers saleOrderDeliveryProducers;
    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    private StoreRelationshipSubjectService subjectService;
    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;
    @Resource
    private AccountStockRelationService accountStockRelationService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private CustomerService customerService;
    @Resource
    private RcSaleDeliveryVideoService rcSaleDeliveryVideoService;


    private static final Set<Integer> PURCHASE_TYPE = ImmutableSet.of(BusinessBillTypeEnum.TH_JS.getValue(),
            BusinessBillTypeEnum.GR_JS.getValue());
    private static final Set<Integer> CHANNEL_TYPE = ImmutableSet.of(SaleOrderChannelEnum.OTHER.getValue());

    @Override
    public void onApplicationEvent(LogisticsDeliveryEvent event) {
        super.onApplicationEvent(event.getWorkPreList(), event);
    }

    /**
     * 门店发货后 处理原销售单状态
     *
     * @param sale
     * @param preList
     * @param event
     */
    @Override
    void handler(BillSaleOrder sale, List<BillStoreWorkPre> preList, LogisticsDeliveryEvent event) {
        List<Integer> stockIdList = preList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList());

        Map<Integer, String> stockDeliveryExpressNumberMap = preList.stream()
                .collect(Collectors.toMap(BillStoreWorkPre::getStockId, BillStoreWorkPre::getDeliveryExpressNumber, (k1, k2) -> k2));

        StoreRelationshipSubject subject = subjectService.getByShopId(sale.getShopId());
        StoreRelationshipSubject deliverySubject = subjectService.getByShopId(sale.getDeliveryLocationId());

        List<Stock> stocks = stockService.listByIds(stockIdList);
        boolean whetherZB = sale.getDeliveryLocationId() == FlywheelConstant._ZB_ID;
        boolean whetherToC = BusinessBillTypeEnum.TO_C_XS.equals(sale.getSaleSource()) || BusinessBillTypeEnum.TO_C_ON_LINE.equals(sale.getSaleSource());
        boolean whetherJS = BusinessBillTypeEnum.TO_B_JS.equals(sale.getSaleSource());
        List<Integer> saleApa = null;
        //同行销售 正常 物流/门店发货后，生成自动核销预收单
        if (BusinessBillTypeEnum.TO_B_XS.equals(sale.getSaleSource()) && (SaleOrderModeEnum.NORMAL.equals(sale.getSaleMode()) || SaleOrderModeEnum.RETURN_POINT.equals(sale.getSaleMode()))) {
            //同行销售  预收单自动核销
            List<AccountsPayableAccounting> list = accountingService
                    .selectListByOriginSerialNoAndStatusAndType(sale.getSerialNo(),
                            Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW)
                            , Lists.newArrayList(ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT));
            accountingService.batchAudit(list.stream().filter(a -> stockIdList.contains(a.getStockId()))
                    .map(AccountsPayableAccounting::getId)
                    .collect(Collectors.toList()), FlywheelConstant.DELIVERY_AUDIT, UserContext.getUser().getUserName());
//            //商品是同行寄售的，生成应付单
//            accountingService.createApa(sale.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE,
//                    FinancialStatusEnum.PENDING_REVIEW, stockIdList, sale.getTotalSalePrice(), false);
        } else if (BusinessBillTypeEnum.TO_C_XS.equals(sale.getSaleSource()) || BusinessBillTypeEnum.TO_C_ON_LINE.equals(sale.getSaleSource())) {
            //个人销售单 出库发货后 自动生成应收单 状态：待核销
            saleApa = accountingService.createSaleApa(MapUtil.builder(sale.getSerialNo(), stockIdList).build(),
                    ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE, FinancialStatusEnum.PENDING_REVIEW);
        }
        //初始化
        SaleOrderLineStateEnum.TransitionEnum transitionEnum;
        //同行寄售  个人寄售  销售流程完成后（后方发货/物流发货）
        stocks.forEach(stock -> {
            if (PURCHASE_TYPE.contains(stock.getStockSrc())) {
                BillPurchaseLine purchaseLine = billPurchaseLineService.list(new LambdaQueryWrapper<BillPurchaseLine>()
                                .eq(BillPurchaseLine::getStockId, stock.getId())
                                .in(BillPurchaseLine::getPurchaseLineState,
                                        Lists.newArrayList(PurchaseLineStateEnum.ON_CONSIGNMENT, PurchaseLineStateEnum.WAREHOUSED, PurchaseLineStateEnum.TO_BE_SETTLED))
                                .orderByDesc(BillPurchaseLine::getCreatedTime))
                        .stream().findFirst().orElse(null);
                if (Objects.nonNull(purchaseLine)) {
                    FinancialGenerateDto dto = new FinancialGenerateDto();
                    dto.setId(purchaseLine.getPurchaseId());
                    dto.setStockList(Lists.newArrayList(stock.getId()));
                    dto.setType(stock.getStockSrc());
                    financialDocumentsService.generatePurchase(dto);

//                    if (Objects.equals(BusinessBillTypeEnum.TH_JS.getValue(), stock.getStockSrc())) {
//                        BillPurchase purchase = billPurchaseService.getById(purchaseLine.getPurchaseId());
//                        accountingService.createApa(purchase.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE,
//                                FinancialStatusEnum.PENDING_REVIEW, Lists.newArrayList(stock.getId()), null);
//                    }
                }
            }
        });
        //当发货位置是总部 并且销售是 寄售
        List<BillSaleOrderLine> lines = billSaleOrderLineService.list(new LambdaQueryWrapper<BillSaleOrderLine>()
                .eq(BillSaleOrderLine::getSaleId, sale.getId())
                .in(BillSaleOrderLine::getStockId, stockIdList));
        if (whetherJS) {
            //当前发货位置是总部
            transitionEnum = event.isShopDelivery() ? SaleOrderLineStateEnum.TransitionEnum.WAIT_OUT_STORAGE_TO_ON_CONSIGNMENT
                    : SaleOrderLineStateEnum.TransitionEnum.QUALITY_TESTING_TO_ON_CONSIGNMENT;
            lines.forEach(line -> {
                BillSaleOrderLine up = new BillSaleOrderLine();
                up.setId(line.getId());
                up.setDeliveryTime(new Date());
                up.setExpressNumber(stockDeliveryExpressNumberMap.get(line.getStockId()));
                billSaleOrderLineService.updateById(up);
            });
        } else {
            Date time = new Date();
            transitionEnum = whetherZB ? SaleOrderLineStateEnum.TransitionEnum.QUALITY_TESTING_TO_DELIVERED : SaleOrderLineStateEnum.TransitionEnum.WAIT_OUT_STORAGE_TO_DELIVERED;

            lines.forEach(line -> {
                BillSaleOrderLine up = new BillSaleOrderLine();
                up.setId(line.getId());

                log.info("Setting gmvPerformance in SaleListenerForLogisticsDelivery.handler() at line 158, gmvPerformance={}, saleOrderLine={}",
                        Optional.ofNullable(line.getClinchPrice())
                                .filter(BigDecimalUtil::gtZero)
                                .map(p -> p.subtract(line.getMarketsPrice()).divide(new BigDecimal("0.2"), 2, RoundingMode.HALF_UP))
                                .orElse(BigDecimal.ZERO),
                        JSON.toJSONString(line));

                if (Objects.nonNull(line.getMarketsPrice())) {
                    up.setGmvPerformance(Optional.ofNullable(line.getClinchPrice())
                            .filter(BigDecimalUtil::gtZero)
                            .map(p -> p.subtract(line.getMarketsPrice()).divide(new BigDecimal("0.2"), 2, RoundingMode.HALF_UP))
                            .orElse(BigDecimal.ZERO));
                }else {
                    log.info("Setting gmvPerformance in SaleListenerForLogisticsDelivery.handler() at line 162, gmvPerformance={}, saleOrderLine={}",
                            Optional.ofNullable(line.getClinchPrice())
                                    .filter(BigDecimalUtil::gtZero)
                                    .map(p -> p.subtract(line.getNewSettlePrice()).divide(new BigDecimal("0.2"), 2, RoundingMode.HALF_UP))
                                    .orElse(BigDecimal.ZERO),
                            JSON.toJSONString(line));
                    if (line.getNewSettlePrice() != null){
                        up.setGmvPerformance(Optional.ofNullable(line.getClinchPrice())
                                .filter(BigDecimalUtil::gtZero)
                                .map(p -> p.subtract(line.getNewSettlePrice()).divide(new BigDecimal("0.2"), 2, RoundingMode.HALF_UP))
                                .orElse(BigDecimal.ZERO));
                    }
                }


//                if (Objects.nonNull(line.getMarketsPrice())) {
//                    up.setGmvPerformance(Optional.ofNullable(line.getClinchPrice())
//                            .filter(BigDecimalUtil::gtZero)
//                            .map(p -> p.subtract(line.getMarketsPrice()).divide(new BigDecimal("0.2"), 2, RoundingMode.HALF_UP))
//                            .orElse(BigDecimal.ZERO));
//                } else if (Objects.nonNull(line.getPromotionConsignmentPrice())) {
//                    up.setGmvPerformance(Optional.ofNullable(line.getClinchPrice())
//                            .filter(BigDecimalUtil::gtZero)
//                            .map(p -> p.subtract(line.getPromotionConsignmentPrice()).divide(new BigDecimal("0.2"), 2, RoundingMode.HALF_UP))
//                            .orElse(BigDecimal.ZERO));
//                } else {
//                    Date date = DateUtils.getNowDate();
//                    GpmConfig toC = gpmConfigService.getOne(new LambdaQueryWrapper<GpmConfig>()
//                            .eq(GpmConfig::getToTarget, "ToC")
//                            .ge(GpmConfig::getEndDateTime, date)
//                            .le(GpmConfig::getStartDateTime, date));
//                    up.setGmvPerformance(Optional.ofNullable(line.getClinchPrice())
//                            .filter(BigDecimalUtil::gtZero)
//                            .map(p -> p.subtract(line.getConsignmentPrice()).multiply(new BigDecimal(100)).divide(toC.getGpmTarget(), 2, RoundingMode.HALF_UP))
//                            .orElse(BigDecimal.ZERO));
//                }

                up.setDeliveryTime(time);
                up.setExpressNumber(stockDeliveryExpressNumberMap.get(line.getStockId()));
                up.setProportion(check(line.getRightOfManagement(), sale.getDeliveryLocationId(), subject, deliverySubject) ? new BigDecimal("0.5") : null);
                billSaleOrderLineService.updateById(up);
            });
            BillSaleOrder saleOrder = new BillSaleOrder();
            saleOrder.setId(sale.getId());
            saleOrder.setFinishTime(time);
            billSaleOrderService.updateById(saleOrder);

        }

        billSaleOrderLineService.updateLineState(BillSaleOrderLineDto.builder()
                        .saleId(sale.getId())
                        .stockIdList(stockIdList)
                        .build()
                , transitionEnum);

        Store store = storeService.selectByShopId(sale.getShopId());
        //商家组卖总部商品时候 不更改任何商品基础信息
        List<Stock> stockList = stockIdList.stream()
                .map(t ->
                        new Stock()
                                .setId(t)
                                .setLocationId((whetherToC) ? sale.getShopId() : null) //改变商品位置
                                .setStoreId((whetherToC) ? store.getId() : null) //改变仓库位置
                                .setRightOfManagement((whetherToC && whetherZB) ? subject.getSubjectId() : null)
                                .setStoreRkTime((whetherToC && whetherZB) ? new Date() : null) // 经营权变更门店时间
                                .setCkTime(event.isShopDelivery() ? new Date() : null)
                                .setIsUnderselling(StockUndersellingEnum.NOT_ALLOW)
                ).collect(Collectors.toList());
        stockService.updateBatchById(stockList);

        if (!whetherJS) {
            stocks.forEach(stock -> {
                FinancialGenerateDto dto = new FinancialGenerateDto();
                dto.setId(sale.getId());
                dto.setStockList(Lists.newArrayList(stock.getId()));
                financialDocumentsService.generateSale(dto);
            });
        }

        //关联质检视频
        int count = rcSaleDeliveryVideoService.bindSaleOrder(sale.getId(), stockIdList);
        if (event.isMustQtVideo() && count == NumberUtils.INTEGER_ZERO) {
            throw new OperationRejectedException(OperationExceptionCode.MUST_QT_VIDEO);
        }

        try {
            SaleOrderDeliveryMessage message = SaleOrderDeliveryMessage.builder()
                    .serialNo(sale.getSerialNo())
                    .bizOrderCode(sale.getBizOrderCode())
                    .expressNumber(stockDeliveryExpressNumberMap.values().stream().findFirst().orElse(null))
                    .stockIdList(stockIdList)
                    .build();
            saleOrderDeliveryProducers.sendMsg(message);
        } catch (Exception e) {
            log.error("销售发货通知消息发送异常-{}", e.getMessage(), e);

        }

        //新增确认收款单
        //todo
        if (SaleOrderTypeEnum.TO_C_XS.equals(sale.getSaleType())) {
            Integer confirmAdd = accountReceiptConfirmAdd(sale, lines);
            String serialNo = accountReceiptConfirmService.getById(confirmAdd).getSerialNo();
            if (Objects.nonNull(confirmAdd)) {
                for (Integer i : saleApa) {
                    AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                    accounting.setId(i);
                    accounting.setArcSerialNo(serialNo);
                    accountsPayableAccountingService.updateById(accounting);
                }
            }
        }

    }

    @Resource
    private AccountsPayableAccountingService accountsPayableAccountingService;

    private boolean check(Integer rightOfManagement, Integer locationId, StoreRelationshipSubject subject, StoreRelationshipSubject deliverySubject) {
        if (Objects.isNull(rightOfManagement) || Objects.isNull(locationId) || Objects.isNull(subject) || Objects.isNull(deliverySubject)) {
            return false;
        }
        if ((locationId == FlywheelConstant._ZB_ID ||
                FlywheelConstant.EXCLUDE_SUBJECT_ID.contains(rightOfManagement) ||
                (rightOfManagement.equals(Optional.ofNullable(subject).get().getSubjectId())) ||
                (deliverySubject.getSubjectId() == FlywheelConstant.SUBJECT_NN_ED && subject.getSubjectId() == FlywheelConstant.SUBJECT_NN_YD) ||
                (deliverySubject.getSubjectId() == FlywheelConstant.SUBJECT_NN_YD && subject.getSubjectId() == FlywheelConstant.SUBJECT_NN_ED))) {
            return false;
        }
        return true;
    }

    Integer accountReceiptConfirmAdd(BillSaleOrder saleOrder, List<BillSaleOrderLine> lines) {
        log.info("accountReceiptConfirmAdd function of SaleListenerForLogisticsDelivery start and saleOrder={},lines={}",
                JSON.toJSONString(saleOrder), JSON.toJSONString(lines));
        List<CustomerContacts> customerContactsList = customerContactsService.searchByCustomerId(saleOrder.getCustomerId());
        customerContactsList = customerContactsList.stream().filter(Objects::nonNull)
                .filter(e -> e.getId().equals(saleOrder.getCustomerContactId())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(customerContactsList) || CollectionUtil.isEmpty(lines)) {
            log.info("customerContactsList or lines of accountReceiptConfirmAdd function is empty .");
            return null;
        }

        CustomerPO customerPO = customerService.queryCustomerPO(saleOrder.getCustomerId());

        CustomerContacts customerContacts = customerContactsList.get(0);
        AccountReceiptConfirmAddRequest request = new AccountReceiptConfirmAddRequest();
        request.setCustomerId(saleOrder.getCustomerId());
        request.setCustomerName(null != customerPO && StringUtils.isNotEmpty(customerPO.getCustomerName()) ? customerPO.getCustomerName() : FlywheelConstant.CUSTOMER_CONTACTNAME_VALUE);
        request.setContactId(saleOrder.getCustomerContactId());
        request.setContactName(customerContacts.getName());
        request.setContactAddress(customerContacts.getAddress());
        request.setContactPhone(customerContacts.getPhone());
        request.setShopId(saleOrder.getShopId());
        request.setMiniAppSource(Boolean.FALSE);

        request.setOriginSerialNo(saleOrder.getSerialNo());
        request.setReceivableAmount(saleOrder.getTotalSalePrice());
        request.setWaitAuditPrice(saleOrder.getTotalSalePrice());
        request.setOriginType(OriginTypeEnum.XS.getValue());
        request.setClassification(FinancialClassificationEnum.GR_XS.getValue());
        request.setCollectionType(CollectionTypeEnum.XF_TK.getValue());
        //销售单业务方式都改成 全款
        request.setSalesMethod(FinancialSalesMethodEnum.FULL_PAYMENT.getValue());
        request.setStatus(AccountReceiptConfirmStatusEnum.WAIT.getValue());
        request.setCollectionNature(CollectionNatureEnum.SALE.getValue());
        request.setTotalNumber(lines.size());

        request.setCreatedId(saleOrder.getCreatedId());
        request.setCreatedBy(saleOrder.getCreatedBy());
        Integer accountReceiptConfirmId = accountReceiptConfirmService.accountReceiptConfirmAdd(request).getId();

        for (BillSaleOrderLine saleOrderLine : lines) {
            AccountStockRelation accountStockRelation = new AccountStockRelation();
            accountStockRelation.setArcId(accountReceiptConfirmId);
            accountStockRelation.setOriginSerialNo(saleOrder.getSerialNo());
            accountStockRelation.setStockId(saleOrderLine.getStockId());
            accountStockRelation.setOriginPrice(saleOrderLine.getClinchPrice());

            accountStockRelationService.AccountStockRelationAdd(accountStockRelation);
        }

        return accountReceiptConfirmId;

    }
}
