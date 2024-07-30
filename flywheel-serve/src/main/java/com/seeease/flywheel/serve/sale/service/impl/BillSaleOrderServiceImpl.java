package com.seeease.flywheel.serve.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.biz.buyBackPolicy.BuyBackPolicyBO;
import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.sale.request.*;
import com.seeease.flywheel.sale.result.DouYinSaleOrderListResult;
import com.seeease.flywheel.sale.result.SaleOrderListForExportResult;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.StringTools;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.goods.entity.*;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.flywheel.serve.goods.enums.StockPromotionEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.goods.mapper.GoodsWatchMapper;
import com.seeease.flywheel.serve.goods.mapper.SeriesMapper;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.mapper.StockPromotionMapper;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.goods.service.BuyBackPolicyService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockMarketsService;
import com.seeease.flywheel.serve.maindata.entity.StoreRelationshipSubject;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.mapper.StoreRelationshipSubjectMapper;
import com.seeease.flywheel.serve.maindata.mapper.UserMapper;
import com.seeease.flywheel.serve.sale.convert.SaleOrderConverter;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderDTO;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderTypeEnum;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderMapper;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.sale.strategy.SaleBreakPriceCheck;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.exception.ValidationUtil;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【bill_sale】的数据库操作Service实现
 * @createDate 2023-03-06 10:38:19
 */
@Service
@Slf4j
public class BillSaleOrderServiceImpl extends ServiceImpl<BillSaleOrderMapper, BillSaleOrder>
        implements BillSaleOrderService {

    @Resource
    private BillSaleOrderLineMapper billSaleOrderLineMapper;
    @Resource
    private StockMapper stockMapper;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private StockPromotionMapper promotionMapper;
    @Resource
    private SeriesMapper seriesMapper;
    @Resource
    private BuyBackPolicyService buyBackPolicyService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private StoreRelationshipSubjectMapper subjectMapper;
    @Resource
    protected SaleBreakPriceCheck saleBreakPriceCheck;
    @Resource
    protected BrandService brandService;
    @Resource
    private StockMarketsService stockMarketsService;
    @Resource
    private GoodsWatchMapper goodsWatchMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BillSaleOrderDTO> create(SaleOrderCreateRequest request) {
        List<Integer> stockIdList = request.getDetails().stream()
                .map(SaleOrderCreateRequest.BillSaleOrderLineDto::getStockId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Assert.isTrue(stockIdList.stream().distinct().count() == stockIdList.size(), "销售商品行数据异常");

        List<BillSaleOrderDTO> resultList = new ArrayList<>();
        Map<Integer, List<SaleOrderCreateRequest.BillSaleOrderLineDto>> groupMap = request.getDetails()
                .stream()
                .collect(Collectors.groupingBy(SaleOrderCreateRequest.BillSaleOrderLineDto::getLocationId));

        StoreRelationshipSubject subject = subjectMapper.selectOne(new LambdaQueryWrapper<StoreRelationshipSubject>()
                .eq(StoreRelationshipSubject::getStoreManagementId, request.getShopId()));
        StoreRelationshipSubject deliverySubject = subjectMapper.selectOne(new LambdaQueryWrapper<StoreRelationshipSubject>()
                .eq(StoreRelationshipSubject::getStoreManagementId, request.getDeliveryLocationId()));
        AtomicInteger num = new AtomicInteger(groupMap.size());

        groupMap.forEach((locationId, list) -> {
            log.info("同行销售传入参数为：{}", request);
            BillSaleOrder saleOrder = SaleOrderConverter.INSTANCE.convertBillSaleOrder(request);
            log.info("同行销售转换保存参数为：{}", saleOrder);
            saleOrder.setSaleState(request.isSaleConfirm() ? SaleOrderStateEnum.UN_CONFIRMED : SaleOrderStateEnum.UN_STARTED);
            saleOrder.setSaleNumber(list.size());
            saleOrder.setSerialNo(StringTools.dataSplicing(request.getParentSerialNo(), num.getAndDecrement()));
            if (Objects.nonNull(request.getCreator())) {
                saleOrder.setCreatedId(request.getCreator().getCreatedId());
                saleOrder.setCreatedBy(request.getCreator().getCreatedBy());
            }
            //总销售价格
            saleOrder.setTotalSalePrice(list.stream()
                    .map(t -> {
                        if (SaleOrderTypeEnum.TO_B_JS.equals(saleOrder.getSaleType())
                                && SaleOrderModeEnum.CONSIGN_FOR_SALE.equals(saleOrder.getSaleMode())) {
                            return t.getPreClinchPrice();
                        } else {
                            return t.getClinchPrice();
                        }
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            saleOrder.setDeliveryLocationId(locationId);
            baseMapper.insert(saleOrder);

            List<BillSaleOrderLine> saleOrderLineList = list.stream()
                    .map(SaleOrderConverter.INSTANCE::convertBillSaleOrderLine)
                    .peek(t -> {
                        if (t.getStockId() != null) {
                            Stock stock = stockMapper.selectById(t.getStockId());
                            GoodsWatch goodsWatch = goodsWatchService.getById(stock.getGoodsId());
                            Series series = seriesMapper.selectById(goodsWatch.getSeriesId());
                            if (series.getSeriesType() != SeriesTypeEnum.ACCESSORY && request.getSaleMode() != 3) {

                                if (Objects.equals(request.getSaleType(), SaleOrderTypeEnum.TO_C_XS.getValue())) {
                                    if (stock.getFiness().equals("S级/99新") || stock.getFiness().equals("SA级/98新")) {
                                        if (stock.getIsUnderselling() == StockUndersellingEnum.ALLOW) {
                                            if (!(t.getClinchPrice().compareTo(stock.getConsignmentPrice()) > -1)) {
                                                throw new OperationRejectedException(OperationExceptionCode.STEP1);

                                            }

                                        } else {
                                            if (!(t.getClinchPrice().compareTo(stock.getTocPrice()) > -1)) {
                                                throw new OperationRejectedException(OperationExceptionCode.STEP2);
                                            }
                                        }
                                    } else if (stock.getFiness().equals("A级/95新") || stock.getFiness().equals("AB级/90新")) {
                                        if (stock.getIsUnderselling() == StockUndersellingEnum.ALLOW) {
                                            if (!(t.getClinchPrice().compareTo(stock.getConsignmentPrice()) > -1)) {
                                                throw new OperationRejectedException(OperationExceptionCode.STEP3);
                                            }

                                        } else {
                                            if (!(t.getClinchPrice().compareTo(stock.getNewSettlePrice()) > -1)) {
                                                throw new OperationRejectedException(OperationExceptionCode.STEP4);
                                            }

                                        }
                                    }
                                }
                            }


                            if (null != stock.getNewSettlePrice() && !BigDecimal.ZERO.equals(stock.getNewSettlePrice())) {
                                t.setNewSettlePrice(stock.getNewSettlePrice());
                            } else {
                                t.setNewSettlePrice(stock.getConsignmentPrice());
                            }
                        }
                        t.setWhetherInvoice(FinancialInvoiceStateEnum.NO_INVOICED);
                        t.setCreatedId(saleOrder.getCreatedId());
                        t.setCreatedBy(saleOrder.getCreatedBy());
                        t.setSaleId(saleOrder.getId());
                        t.setSaleLineState(request.isSaleConfirm() ? SaleOrderLineStateEnum.WAIT_CONFIRM : SaleOrderLineStateEnum.WAIT_OUT_STORAGE);
                        t.setProportion(check(t.getRightOfManagement(), locationId, subject, deliverySubject) ? new BigDecimal("0.5") : null);
                    }).collect(Collectors.toList());
            billSaleOrderLineMapper.insertBatchSomeColumn(saleOrderLineList);

            resultList.add(BillSaleOrderDTO.builder()
                    .order(saleOrder)
                    .lines(saleOrderLineList)
                    .build());
        });
        return resultList;
    }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(SaleOrderEditRequest request) {

        this.baseMapper.updateById(SaleOrderConverter.INSTANCE.convertBillSaleOrder(request));

        request.getDetails().forEach(billSaleOrderLineDto ->
                billSaleOrderLineMapper.updateById(SaleOrderConverter.INSTANCE.convertBillSaleOrderLine(request))
        );

    }

    @Override
    public BillSaleOrder selectBySerialNo(String originSerialNo) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<BillSaleOrder>().eq(BillSaleOrder::getSerialNo, originSerialNo));
    }

    @Override
    public BillSaleOrder selectBySaleLineId(Integer saleLineId) {
        return this.baseMapper.selectBySaleLineId(saleLineId);
    }

    @Override
    public Page<BillSaleOrder> listByRequest(SaleOrderListRequest request) {
        return baseMapper.listByRequest(Page.of(request.getPage(), request.getLimit()), request);
    }


    /**
     * 销售订单确认
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillSaleOrderDTO saleConfirm(SaleOrderConfirmRequest request) {
        BillSaleOrder order = baseMapper.selectById(Objects.requireNonNull(request.getOrderId()));
        if (Objects.isNull(order)) {
            throw new BusinessException(ExceptionCode.SALE_ORDER_BILL_NOT_EXIST);
        }

        //销售人校验
        List<Integer> userList = userMapper.listByShop(order.getShopId())
                .stream()
                .map(User::getId)
                .map(Long::intValue)
                .collect(Collectors.toList());

        Arrays.asList(request.getFirstSalesman(), request.getSecondSalesman(), request.getThirdSalesman())
                .stream()
                .filter(Objects::nonNull)
                .filter(id -> !userList.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
                });

        //更新stockId
        if (CollectionUtils.isNotEmpty(request.getDetails())) {
            request.getDetails()
                    .forEach(t -> {
                        BillSaleOrderLine up = new BillSaleOrderLine();
                        up.setId(t.getId());
                        up.setStockId(t.getStockId());
                        up.setStrapMaterial(t.getStrapMaterial());
                        up.setWatchSection(t.getWatchSection());
                        up.setIsCounterPurchase(t.getIsCounterPurchase());
                        up.setRemarks(t.getRemarks());
                        billSaleOrderLineMapper.updateById(up);
                    });
        }

        //查所有的行，重新计算
        List<BillSaleOrderLine> orderLines = billSaleOrderLineMapper.selectList(Wrappers.<BillSaleOrderLine>lambdaQuery()
                .eq(BillSaleOrderLine::getSaleId, order.getId()));

        //订单行id
        List<Integer> lineIds = orderLines.stream()
                .map(BillSaleOrderLine::getId)
                .collect(Collectors.toList());

        //校验修改的行，商品不重复
        Assert.isTrue(request.getDetails().stream().allMatch(t -> lineIds.contains(t.getId().intValue())), "修改数据异常");
        Assert.isTrue(orderLines.stream().map(BillSaleOrderLine::getStockId).distinct().count() == orderLines.size(), "商品数据异常");

        //查商品
        List<Stock> stockList = stockMapper.selectBatchIds(orderLines.stream()
                .map(BillSaleOrderLine::getStockId)
                .collect(Collectors.toList()));

        //商品可售校验
        List<String> notSaleList = stockList.stream()
                .filter(t -> StockStatusEnum.MARKETABLE.getValue().intValue() != t.getStockStatus().getValue())
                .map(Stock::getSn)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(notSaleList)) {
            throw new OperationRejectedException(OperationExceptionCode.GOODS_NOT_SALE, notSaleList.stream().collect(Collectors.joining(",")));
        }
        //发货位置唯一校验
        if (stockList.stream().map(Stock::getLocationId).distinct().count() != NumberUtils.LONG_ONE) {
            throw new OperationRejectedException(OperationExceptionCode.GOODS_LOCATION_SALE_NOT_UNIQUE);
        }

        //更新发货位置
        BillSaleOrder upOrder = new BillSaleOrder();
        upOrder.setId(order.getId());
        upOrder.setDeliveryLocationId(stockList.get(0).getLocationId());
        //确认销售人
        upOrder.setFirstSalesman(Objects.isNull(order.getFirstSalesman()) ? request.getFirstSalesman() : null);
        upOrder.setSecondSalesman(Objects.isNull(order.getSecondSalesman()) ? request.getSecondSalesman() : null);
        upOrder.setThirdSalesman(Objects.isNull(order.getThirdSalesman()) ? request.getThirdSalesman() : null);
        upOrder.setRemarks(request.getRemarks()); //修改备注
        baseMapper.updateById(upOrder);

        List<GoodsWatch> goodsList = goodsWatchMapper.selectBatchIds(stockList.stream()
                .map(Stock::getGoodsId)
                .distinct()
                .collect(Collectors.toList()));

        Map<Integer, GoodsWatch> goodsMap = goodsList
                .stream()
                .collect(Collectors.toMap(GoodsWatch::getId, Function.identity()));

        Map<Integer, Brand> brandMap = brandService.listByIds(goodsList.stream().map(GoodsWatch::getBrandId).distinct().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Brand::getId, Function.identity()));

        Map<Integer, Stock> stockMap = stockList
                .stream()
                .collect(Collectors.toMap(Stock::getId, Function.identity()));
        Date nowDate = DateUtils.getNowDate();
        Map<Integer, StockPromotion> promotionMap = promotionMapper.selectList(new LambdaQueryWrapper<StockPromotion>()
                        .in(StockPromotion::getStockId, stockMap.keySet())
                        .eq(StockPromotion::getStatus, StockPromotionEnum.ITEM_UP_SHELF)
                        .le(StockPromotion::getStartTime, nowDate)
                        .ge(StockPromotion::getEndTime, nowDate)
                        .eq(StockPromotion::getDeleted, WhetherEnum.NO.getValue()))
                .stream().collect(Collectors.toMap(StockPromotion::getStockId, Function.identity()));

        Map<Integer, StockMarkets> marketsMap = stockMarketsService.listByStockId(stockMap.keySet()
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(StockMarkets::getStockId, Function.identity()));

        //过滤收取表带维修费的行
        List<Integer> whetherFixList = request.getDetails()
                .stream()
                .filter(t -> Objects.nonNull(t.getWhetherFix()) && t.getWhetherFix().intValue() == WhetherEnum.YES.getValue())
                .map(SaleOrderConfirmRequest.SaleOrderConfirmLineDto::getId)
                .collect(Collectors.toList());
        //重设商品信息
        orderLines.forEach(t -> {
            Stock stock = Objects.requireNonNull(stockMap.get(t.getStockId()));
            GoodsWatch goods = Objects.requireNonNull(goodsMap.get(stock.getGoodsId()));

            //破价校验
            SaleOrderCreateRequest.BillSaleOrderLineDto dto = SaleOrderConverter.INSTANCE.convertBillSaleOrderLineDto(t);
            dto.setTobPrice(stock.getTobPrice());
            dto.setTocPrice(stock.getTocPrice());
            dto.setFiness(stock.getFiness());
            dto.setIsUnderselling(stock.getIsUnderselling().getValue());
            StockPromotion stockPromotion = promotionMap.get(t.getStockId());
            if (Objects.nonNull(stockPromotion)) {
                dto.setPromotionConsignmentPrice(stockPromotion.getPromotionConsignmentPrice());
                dto.setPromotionPrice(stockPromotion.getPromotionPrice());
            }
            dto.setTotalPrice(stock.getTotalPrice());
            dto.setClinchPrice(t.getClinchPrice());
            dto.setBrandBusinessType(brandMap.get(goods.getBrandId()).getBusinessType().getValue());

            saleBreakPriceCheck.checkDeposit(order.getShopId(), order.getSaleChannel().getValue(), order.getSaleMode().getValue(), dto);
            saleBreakPriceCheck.toCSaleCheck(dto);


            List<BuyBackPolicyInfo> buyBackPolicy = buyBackPolicyService.getStockBuyBackPolicy(BuyBackPolicyBO.builder()
                    .finess(stock.getFiness())
                    .sex(goods.getSex())
                    .brandId(goods.getBrandId())
                    .clinchPrice(t.getClinchPrice())
                    .build());

            BillSaleOrderLine up = new BillSaleOrderLine();
            up.setId(t.getId());
            up.setStockId(stock.getId());
            up.setGoodsId(stock.getGoodsId());
            up.setTobPrice(stock.getTobPrice());
            up.setTocPrice(stock.getTocPrice());
            up.setTagPrice(stock.getTagPrice());
            up.setTotalPrice(stock.getTotalPrice());
            up.setPricePub(goods.getPricePub());
            up.setConsignmentPrice(stock.getConsignmentPrice());
            up.setPromotionConsignmentPrice(dto.getPromotionConsignmentPrice());
            up.setPromotionPrice(dto.getPromotionPrice());
            up.setRightOfManagement(stock.getRightOfManagement());
            up.setIsRepurchasePolicy(CollectionUtils.isNotEmpty(buyBackPolicy) ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue());
            up.setBuyBackPolicy(SaleOrderConverter.INSTANCE.convertBuyBackPolicyMapper(buyBackPolicy));
            up.setStrapReplacementPrice(whetherFixList.contains(t.getId().intValue()) ?
                    (t.getClinchPrice().compareTo(new BigDecimal(20000)) >= 0
                            ? new BigDecimal(1200) : new BigDecimal(600))
                    : new BigDecimal(0));
            up.setMarginPrice(Optional.ofNullable(marketsMap.get(stock.getId())).map(StockMarkets::getMarketsPrice).orElse(null));
            billSaleOrderLineMapper.updateById(up);
        });

        //返回修改后的订单
        return BillSaleOrderDTO.builder()
                .order(baseMapper.selectById(order.getId()))
                .lines(billSaleOrderLineMapper.selectBatchIds(orderLines.stream().map(BillSaleOrderLine::getId).collect(Collectors.toList())))
                .build();
    }

    @Override
    public Page<SaleOrderListForExportResult> export(SaleOrderListRequest request) {
        return baseMapper.export(Page.of(request.getPage(), request.getLimit()), request);
    }


    @Override
    public List<BillSaleOrder> queryToCOrderByOffset(Integer currentOffset, Integer limit) {
        return baseMapper.queryToCOrderByOffset(currentOffset, limit);
    }

    @Override
    public List<BillSaleOrder> queryToCOrderByFinisTime(Date saleTime) {
        return baseMapper.queryToCOrderByFinisTime(saleTime);
    }

    @Override
    public List<BillSaleOrder> queryToCOrderByRequest(SaleOrderAccuracyQueryRequest request) {
        if (Objects.isNull(request) || CollectionUtils.isEmpty(request.getThirdOrderNoList())) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.queryToCOrderByRequest(request);
    }

    @Override
    public Integer maxToCOrderByOffset(Integer currentOffset) {
        return baseMapper.maxToCOrderByOffset(currentOffset);
    }

    @Override
    public void updateDouYinOrder(List<Integer> douYinOrderIds, Integer i, Date d, String serialNo) {
        baseMapper.updateDouYinOrder(douYinOrderIds, i, d, serialNo);
    }

    @Override
    public void updateKuaiShouOrder(List<Integer> kuaiShouOrderIds, Integer i, Date d, String serialNo) {
        baseMapper.updateKuaiShouOrder(kuaiShouOrderIds, i, d, serialNo);
    }

    @Override
    public List<DouYinSaleOrderListResult> queryDouYinSaleOrder(DouYinSaleOrderListRequest request) {
        return baseMapper.queryDouYinSaleOrder(request);
    }

    @Override
    public List<BillSaleOrder> selectBySerialNoList(List<String> xsSale) {
        if (CollectionUtils.isEmpty(xsSale)) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.selectList(new LambdaQueryWrapper<BillSaleOrder>().in(BillSaleOrder::getSerialNo, xsSale));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLine(SaleOrderUpdateRequest request) {
        request.getDetails().forEach(dto -> {
            BillSaleOrderLine line = new BillSaleOrderLine();
            line.setId(dto.getId());
            line.setIsRepurchasePolicy(dto.getIsRepurchasePolicy());
            line.setIsCounterPurchase(dto.getIsCounterPurchase());
            line.setBuyBackPolicy(SaleOrderConverter.INSTANCE.convert(dto.getBuyBackPolicy()));
            billSaleOrderLineMapper.updateById(line);
        });
    }

    @Override
    public Integer selectDouYinOrderBySerialNo(String assocSerialNumber) {
        return baseMapper.selectDouYinOrderBySerialNo(assocSerialNumber);
    }


}




