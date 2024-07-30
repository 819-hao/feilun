package com.seeease.flywheel.serve.purchase.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseLineNotice;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLineDetailsVO;
import com.seeease.flywheel.serve.purchase.entity.PurchaseLineDetailsVO;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseTypeEnum;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @description 针对表【bill_purchase_line】的数据库操作Service实现
 * @createDate 2023-01-07 17:50:06
 */
@Service
@Slf4j
public class BillPurchaseLineServiceImpl extends ServiceImpl<BillPurchaseLineMapper, BillPurchaseLine>
        implements BillPurchaseLineService {

    @Resource
    private BillPurchaseMapper billPurchaseMapper;

    @Resource
    private StockMapper stockMapper;

    @Override
    public List<BillPurchaseLineDetailsVO> selectByPurchaseId(Integer purchaseId) {
        return baseMapper.selectByPurchaseId(purchaseId);
    }

    @Override
    public void noticeListener(PurchaseLineNotice lineNotice) {

        BillPurchase billPurchase;

        if (ObjectUtils.isEmpty(lineNotice.getSerialNo()) && ObjectUtils.isNotEmpty(lineNotice.getPurchaseId())) {
            billPurchase = billPurchaseMapper.selectById(lineNotice.getPurchaseId());
        } else {
            billPurchase = billPurchaseMapper.selectOne(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, lineNotice.getSerialNo()));
        }

        if (Objects.nonNull(billPurchase)) {

            //1。更新
            BillPurchaseLine billPurchaseLine = new BillPurchaseLine();
            billPurchaseLine.setPurchaseLineState(Optional.ofNullable(lineNotice.getLineState()).orElse(null));
            billPurchaseLine.setPlanFixPrice(lineNotice.getPlanFixPrice());
            billPurchaseLine.setFixPrice(lineNotice.getFixPrice());
            //2。查询
            List<BillPurchaseLine> billPurchaseLineList = this.baseMapper.selectList(new LambdaQueryWrapper<BillPurchaseLine>()
                    .eq(BillPurchaseLine::getPurchaseId, billPurchase.getId())
            );

            if (ObjectUtils.isNotEmpty(lineNotice.getPurchaseId())) {
                lineNotice.setStockId(billPurchaseLineList.get(0).getStockId());
            }

            //寄售价已经开发给定价的人员，不在是系统自动计算
//            if (lineNotice.getComputeConsignmentPrice() == WhetherEnum.YES) {
////重新计算寄售价 针对于个人寄售之外的
//                log.info("重新计算寄售价");
//                stockMapper.recalculateConsignmentPrice(Arrays.asList(lineNotice.getStockId()));
//                Stock stock = stockMapper.getConsignmentPrice(Arrays.asList(lineNotice.getStockId())).get(FlywheelConstant.INDEX);
//
//                if (ObjectUtils.isNotEmpty(stock.getTobPrice()) && ObjectUtils.isNotEmpty(stock.getTocPrice()) && !billPurchase.getPurchaseSource().equals(BusinessBillTypeEnum.GR_JS)) {
//
//                    Boolean b = ImmutableRangeMap.<Comparable<BigDecimal>, Boolean>builder()
//                            .put(Range.open(stock.getTobPrice(), stock.getTocPrice()), true)
//                            .put(Range.atLeast(stock.getTocPrice()), false)
//                            .build().get(stock.getConsignmentPrice());
//
//                    if (ObjectUtils.isNotEmpty(b) && b) {
//                        //变更
//                        //记录
//                        Stock s = new Stock();
//                        s.setId(stock.getId());
//                        s.setTobPrice(stock.getConsignmentPrice());
//                        stockMapper.updateById(s);
//                    } else if (ObjectUtils.isNotEmpty(b) && !b) {
//                        //通知
//                        //记录 警告
//                        Stock s = new Stock();
//                        s.setId(stock.getId());
//                        s.setTransitionStateEnum(StockStatusEnum.TransitionEnum.MARKETABLE_WAIT_PRICING);
//                        UpdateByIdCheckState.update(stockMapper, s);
//                    }
//                }
//            }

            if (lineNotice.getComputeBuyBackPrice() == WhetherEnum.YES && billPurchase.getPurchaseType() == PurchaseTypeEnum.GR_HG) {
                //重新个人回购 针对于个人寄售之外的
                log.info("重新计算回购");

                check(lineNotice.getStockId(), billPurchase.getId(), billPurchaseLine.getPlanFixPrice());
            }

            log.info("更新节点状态");
            //新逻辑
            if (billPurchase.getPurchaseType() == PurchaseTypeEnum.GR_HG) {
                billPurchaseLine.setPlanFixPrice(null);
            }
            //更新节点状态
            this.baseMapper.update(billPurchaseLine, new LambdaUpdateWrapper<BillPurchaseLine>().eq(BillPurchaseLine::getPurchaseId, billPurchase.getId()).eq(BillPurchaseLine::getStockId, lineNotice.getStockId()));

            BillPurchase purchase = new BillPurchase();
            purchase.setId(billPurchase.getId());

            //2。更改数据
            Map<Integer, PurchaseLineStateEnum> map = this.baseMapper.selectList(new LambdaQueryWrapper<BillPurchaseLine>()
                    .eq(BillPurchaseLine::getPurchaseId, billPurchase.getId())).stream().collect(Collectors.toMap(BillPurchaseLine::getStockId, BillPurchaseLine::getPurchaseLineState));
            map.put(lineNotice.getStockId(), lineNotice.getLineState());

            if (map.values().stream().allMatch(purchaseLineStateEnum -> Lists.newArrayList(
                    PurchaseLineStateEnum.TO_BE_CONFIRMED
            ).contains(purchaseLineStateEnum))) {
                //待开始
                return;
            } else if (

                    map.values().stream().allMatch(purchaseLineStateEnum -> Lists.newArrayList(
                            //已取消
                            PurchaseLineStateEnum.RETURNED,
                            PurchaseLineStateEnum.ORDER_CANCEL_WHOLE,

                            //已完成
                            PurchaseLineStateEnum.WAREHOUSED,
                            PurchaseLineStateEnum.IN_SETTLED
                    ).contains(purchaseLineStateEnum))) {

                if (map.values().stream().allMatch(purchaseLineStateEnum -> Lists.newArrayList(
                        PurchaseLineStateEnum.RETURNED,
                        PurchaseLineStateEnum.ORDER_CANCEL_WHOLE).contains(purchaseLineStateEnum))) {
                    //已退回 & 定价单 直接变为已取消 TODO

                    //已取消
                    purchase.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNDER_WAY_TO_CANCEL_WHOLE);
                } else {
                    //已完成
                    if (map.values().stream().allMatch(purchaseLineStateEnum -> Lists.newArrayList(
                            PurchaseLineStateEnum.IN_SETTLED).contains(purchaseLineStateEnum)) && billPurchase.getPurchaseState() == BusinessBillStateEnum.COMPLETE) {

                        purchase.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.COMPLETE_TO_COMPLETE);
                        purchase.setIsSettlement(null);
                    } else if (map.values().stream().allMatch(purchaseLineStateEnum -> Lists.newArrayList(
                            PurchaseLineStateEnum.WAREHOUSED).contains(purchaseLineStateEnum)) && billPurchase.getPurchaseState() == BusinessBillStateEnum.COMPLETE) {
                        purchase.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.COMPLETE_TO_COMPLETE);
                        purchase.setIsSettlement(billPurchase.getIsSettlement());
                    } else {
                        purchase.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNDER_WAY_TO_COMPLETE);
                        purchase.setIsSettlement(billPurchase.getIsSettlement());
                    }

//                    if (billPurchase.getPurchaseType() == PurchaseTypeEnum.GR_HG) {
//                        return;
//                    }
                }
                UpdateByIdCheckState.update(billPurchaseMapper, purchase);

            }
//            else if (map.values().stream().allMatch(purchaseLineStateEnum -> Lists.newArrayList(
//                    PurchaseLineStateEnum.CUSTOMER_HAS_SHIPPED
//            ).contains(purchaseLineStateEnum))) {
//                return;
//            }
            else {
                //进行中
                purchase.setIsSettlement(lineNotice.getIsSettlement());
                if (ObjectUtils.isNotEmpty(purchase.getIsSettlement())) {
                    billPurchaseMapper.updateById(purchase);
                }
                return;
            }
        }
    }

    @Override
    public List<PurchaseLineDetailsVO> listByStockIds(List<Integer> stockIds) {
        if (CollectionUtils.isEmpty(stockIds)) {
            return Collections.EMPTY_LIST;
        }
        return this.baseMapper.listByStockIds(stockIds, BusinessBillTypeEnum.TH_JS.getValue());
    }

    @Override
    public BillPurchaseLine billPurchaseLineQuery(Integer purchaseId, Integer stockId) {
        LambdaQueryWrapper<BillPurchaseLine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillPurchaseLine::getPurchaseId, purchaseId)
                .eq(BillPurchaseLine::getStockId, stockId);

        return this.baseMapper.selectOne(queryWrapper);
    }


    /**
     * 质检判定 需要更新价格区间
     *
     * @param stockId
     * @param purchaseId
     * @param planFixPrice
     */
    public void check(Integer stockId, Integer purchaseId, BigDecimal planFixPrice) {

        BillPurchaseLine billPurchaseLine = this.baseMapper.selectOne(Wrappers.<BillPurchaseLine>lambdaQuery()
                .eq(BillPurchaseLine::getStockId, stockId).eq(BillPurchaseLine::getPurchaseId, purchaseId));

        if (ObjectUtils.isEmpty(billPurchaseLine)) {
            return;
        }

        BillPurchaseLine billPurchaseLineNew = new BillPurchaseLine();

        billPurchaseLineNew.setId(billPurchaseLine.getId());

        BigDecimal referenceBuyBackPriceQuery = billPurchaseLine.getReferenceBuyBackPrice();

        BigDecimal watchbandReplacePriceQuery = billPurchaseLine.getWatchbandReplacePrice();

        BigDecimal consignmentPriceQuery = billPurchaseLine.getConsignmentPrice();

        //实际回购价 = 参考回购价 - 预计维修价 - 表带更换费
        BigDecimal buyBackPrice = referenceBuyBackPriceQuery.subtract(ObjectUtils.isEmpty(planFixPrice) ? BigDecimal.ZERO : planFixPrice).subtract(watchbandReplacePriceQuery);

        //采购价
        //回购服务费
        if (buyBackPrice.compareTo(consignmentPriceQuery) > 0) {
            //
            billPurchaseLineNew.setPurchasePrice(consignmentPriceQuery);
            billPurchaseLineNew.setRecycleServePrice(buyBackPrice.subtract(consignmentPriceQuery));

        } else {
            billPurchaseLineNew.setPurchasePrice(buyBackPrice);
            billPurchaseLineNew.setRecycleServePrice(BigDecimal.ZERO);
        }

        //实际回购价
        billPurchaseLineNew.setBuyBackPrice(buyBackPrice);

        this.baseMapper.updateById(billPurchaseLineNew);

        BillPurchase billPurchase = new BillPurchase();
        billPurchase.setId(purchaseId);
        billPurchase.setTotalPurchasePrice(billPurchaseLineNew.getPurchasePrice());

        billPurchaseMapper.updateById(billPurchase);

        //同时实时更新采购价
        Stock stock = new Stock();
        stock.setId(billPurchaseLine.getStockId());
        stock.setPurchasePrice(billPurchaseLineNew.getPurchasePrice());
        stockMapper.updateById(stock);
    }
}




