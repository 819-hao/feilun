package com.seeease.flywheel.serve.sale.event;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.common.biz.buyBackPolicy.BuyBackPolicyBO;
import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.entity.*;
import com.seeease.flywheel.serve.goods.enums.StockPromotionEnum;
import com.seeease.flywheel.serve.goods.service.*;
import com.seeease.flywheel.serve.sale.convert.SaleOrderConverter;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.OutStorageSupplyStockEvent;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 销售监听-补充表身号事件
 *
 * @author Tiro
 * @date 2023/3/16
 */
@Slf4j
@Component
public class SaleListenerForSupplyStock extends BaseSaleListenerForStoreWork<OutStorageSupplyStockEvent> implements BillHandlerEventListener<OutStorageSupplyStockEvent> {
    @Resource
    private BuyBackPolicyService buyBackPolicyService;
    @Resource
    private StockService stockService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private BillLifeCycleService billLifeCycleService;
    @Resource
    private StockMarketsService stockMarketsService;
    @Resource
    private StockPromotionService stockPromotionService;

    @Override
    public void onApplicationEvent(OutStorageSupplyStockEvent event) {
        super.onApplicationEvent(event.getOutWorkList(), event);
    }


    @Override
    void handler(BillSaleOrder saleOrder, List<BillStoreWorkPre> workPreList, OutStorageSupplyStockEvent event) {
        Map<Integer, Stock> stockMap = stockService.listByIds(workPreList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Stock::getId, Function.identity()));

        Map<Integer, GoodsWatch> goodsMap = goodsWatchService.listByIds(stockMap.values().stream().map(Stock::getGoodsId).distinct().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(GoodsWatch::getId, Function.identity()));

        Map<Integer, StockMarkets> marketsMap = stockMarketsService.listByStockId(workPreList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(StockMarkets::getStockId, Function.identity()));


        //查销售行
        Map<Integer, BillSaleOrderLine> lineMap = billSaleOrderLineService.list(Wrappers.<BillSaleOrderLine>lambdaQuery()
                        .eq(BillSaleOrderLine::getSaleId, saleOrder.getId()))
                .stream()
                .collect(Collectors.toMap(BillSaleOrderLine::getId, Function.identity()));

        Date nowDate = DateUtils.getNowDate();
        Map<Integer, StockPromotion> promotionMap = stockPromotionService.list(new LambdaQueryWrapper<StockPromotion>()
                        .in(StockPromotion::getStockId, workPreList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList()))
                        .eq(StockPromotion::getStatus, StockPromotionEnum.ITEM_UP_SHELF)
                        .le(StockPromotion::getStartTime, nowDate)
                        .ge(StockPromotion::getEndTime, nowDate)
                        .eq(StockPromotion::getDeleted, WhetherEnum.NO.getValue()))
                .stream().collect(Collectors.toMap(StockPromotion::getStockId, Function.identity()));

        workPreList.forEach(t -> {
            BillSaleOrderLine line = lineMap.get(Objects.requireNonNull(this.getLineId(t.getMateMark())));
            if (Objects.isNull(line)) {
                throw new BusinessException(ExceptionCode.SALE_ORDER_BILL_NOT_EXIST);
            }
            if (line.getSaleLineState() != SaleOrderLineStateEnum.WAIT_OUT_STORAGE) {
                throw new OperationRejectedException(OperationExceptionCode.ORDER_INFO_CHANGE);
            }
            Stock stock = Objects.requireNonNull(stockMap.get(t.getStockId()));
            GoodsWatch goods = Objects.requireNonNull(goodsMap.get(stock.getGoodsId()));

            List<BuyBackPolicyInfo> buyBackPolicy = buyBackPolicyService.getStockBuyBackPolicy(BuyBackPolicyBO.builder()
                    .finess(stock.getFiness())
                    .sex(goods.getSex())
                    .brandId(goods.getBrandId())
                    .clinchPrice(line.getClinchPrice())
                    .build());

            BillSaleOrderLine up = new BillSaleOrderLine();
            up.setId(line.getId());
            up.setStockId(stock.getId());
            up.setGoodsId(stock.getGoodsId());
            up.setTobPrice(stock.getTobPrice());
            up.setTocPrice(stock.getTocPrice());
            up.setTagPrice(stock.getTagPrice());
            up.setTotalPrice(stock.getTotalPrice());
            up.setPricePub(goods.getPricePub());
            up.setConsignmentPrice(stock.getConsignmentPrice());
            up.setRightOfManagement(stock.getRightOfManagement());
            up.setIsCounterPurchase(WhetherEnum.YES.getValue());
            StockPromotion stockPromotion = promotionMap.get(t.getStockId());
            if (Objects.nonNull(stockPromotion)) {
                up.setPromotionPrice(stockPromotion.getPromotionPrice());
                up.setPromotionConsignmentPrice(stockPromotion.getPromotionConsignmentPrice());
            }
            up.setIsRepurchasePolicy(CollectionUtils.isNotEmpty(buyBackPolicy) ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue());
            up.setBuyBackPolicy(SaleOrderConverter.INSTANCE.convertBuyBackPolicyMapper(buyBackPolicy));
            up.setStrapReplacementPrice(new BigDecimal(0));

            up.setMarginPrice(Optional.ofNullable(marketsMap.get(stock.getId())).map(StockMarkets::getMarketsPrice).orElse(null));

            if (!billSaleOrderLineService.update(up, Wrappers.<BillSaleOrderLine>lambdaUpdate()
                    .eq(BillSaleOrderLine::getId, line.getId())
                    .eq(BillSaleOrderLine::getSaleLineState, line.getSaleLineState()))) {
                throw new OperationRejectedException(OperationExceptionCode.ORDER_INFO_CHANGE);
            }

            //补充生命周期
            try {
                BillLifeCycle billLifeCycle = new BillLifeCycle();
                billLifeCycle.setStockId(t.getStockId());
                billLifeCycle.setOriginSerialNo(saleOrder.getSerialNo());
                billLifeCycle.setOperationDesc("新建销售单");
                billLifeCycle.setOperationTime(saleOrder.getCreatedTime().getTime());
                billLifeCycle.setCreatedId(saleOrder.getCreatedId());
                billLifeCycle.setCreatedBy(saleOrder.getCreatedBy());
                billLifeCycleService.save(billLifeCycle);
            } catch (Exception e) {
                log.error("保存表身号补充生命周期异常，saleOrder={}", JSONObject.toJSONString(saleOrder));
            }
        });

    }

    /**
     * @param mateMark
     * @return
     */
    private Integer getLineId(String mateMark) {
        try {
            return Integer.valueOf(mateMark.split("-")[2]);
        } catch (Exception e) {
            log.error("解析配对标记异常:{}", e.getMessage(), e);
            return null;
        }
    }
}
