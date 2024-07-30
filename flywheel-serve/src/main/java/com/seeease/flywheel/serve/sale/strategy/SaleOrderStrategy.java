package com.seeease.flywheel.serve.sale.strategy;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.common.biz.buyBackPolicy.BuyBackPolicyBO;
import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleOrderCreateResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.template.Bill;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceCmdTypeEnum;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.financial.entity.CustomerBalance;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.goods.entity.*;
import com.seeease.flywheel.serve.goods.enums.StockPromotionEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.goods.service.*;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseDemandService;
import com.seeease.flywheel.serve.sale.convert.SaleOrderConverter;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderDTO;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderTypeEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.storework.request.StoreWorKCreateRequest;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.type.TransactionalUtil;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.exception.e.SeeeaseBaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
public abstract class SaleOrderStrategy implements Bill<SaleOrderCreateRequest, SaleOrderCreateResult> {

    @Resource
    protected BillSaleOrderService saleOrderService;
    @Resource
    protected TransactionalUtil transactionalUtil;
    @Resource
    protected BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    protected StockService stockService;
    @Resource
    protected GoodsWatchService goodsWatchService;
    @Resource
    protected TagService tagService;
    @Resource
    protected CustomerContactsService customerContactsService;
    @Resource
    protected StockPromotionService promotionService;
    @Resource
    protected SaleBreakPriceCheck saleBreakPriceCheck;
    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    protected CustomerBalanceService customerBalanceService;
    @Resource
    protected BillPurchaseDemandService purchaseDemandService;
    @Resource
    protected BrandService brandService;
    @Resource
    protected StockMarketsService stockMarketsService;
    @Resource
    private BuyBackPolicyService buyBackPolicyService;

    /**
     * 前置处理
     * 1、参数转换
     * 2、参数填充
     *
     * @param request
     */
    abstract void preRequestProcessing(SaleOrderCreateRequest request);

    /**
     * 业务校验
     * 1、必要参数校验
     * 2、金额校验
     * 3、业务可行性校验
     *
     * @param request
     * @throws BusinessException
     */
    abstract void checkRequest(SaleOrderCreateRequest request) throws BusinessException;

    @Override
    public void preProcessing(SaleOrderCreateRequest request) {
        //设置客户id
        request.setCustomerId(Optional.ofNullable(request.getCustomerContactId())
                .map(customerContactsService::getById)
                .map(CustomerContacts::getCustomerId)
                .orElse(request.getCustomerId()));

        //销售来源
        request.setSaleSource(this.getType().getValue());
        //补充库存信息 防止前端缓存的旧数据
        List<SaleOrderCreateRequest.BillSaleOrderLineDto> stockDetails = request.getDetails()
                .stream()
                .filter(t -> Objects.nonNull(t.getStockId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(stockDetails)) {
            List<Stock> stockList = stockService.listByIds(stockDetails.stream()
                    .map(SaleOrderCreateRequest.BillSaleOrderLineDto::getStockId)
                    .collect(Collectors.toList()));
            Map<Integer, Stock> stockMap = stockList.stream()
                    .collect(Collectors.toMap(Stock::getId, Function.identity()));

            Map<Integer, StockMarkets> marketsMap = stockMarketsService.listByStockId(stockDetails
                            .stream()
                            .map(SaleOrderCreateRequest.BillSaleOrderLineDto::getStockId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()))
                    .stream()
                    .collect(Collectors.toMap(StockMarkets::getStockId, Function.identity()));


            List<GoodsWatch> goodsList = goodsWatchService.listByIds(stockList.stream()
                    .map(Stock::getGoodsId)
                    .collect(Collectors.toList()));
            Map<Integer, GoodsWatch> goodsMap = goodsList.stream().collect(Collectors.toMap(GoodsWatch::getId, Function.identity()));
            Date nowDate = DateUtils.getNowDate();
            Map<Integer, StockPromotion> promotionMap = promotionService.list(new LambdaQueryWrapper<StockPromotion>()
                            .in(StockPromotion::getStockId, stockMap.keySet())
                            .eq(StockPromotion::getStatus, StockPromotionEnum.ITEM_UP_SHELF)
                            .le(StockPromotion::getStartTime, nowDate)
                            .ge(StockPromotion::getEndTime, nowDate)
                            .eq(StockPromotion::getDeleted, WhetherEnum.NO.getValue()))
                    .stream().collect(Collectors.toMap(StockPromotion::getStockId, Function.identity()));

            Map<Integer, Brand> brandMap = brandService.listByIds(goodsList.stream().map(GoodsWatch::getBrandId).distinct().collect(Collectors.toList()))
                    .stream()
                    .collect(Collectors.toMap(Brand::getId, Function.identity()));
            //补充商品信息
            stockDetails.forEach(t -> {
                Stock stock = stockMap.get(t.getStockId());
                GoodsWatch goods = Objects.requireNonNull(goodsMap.get(stock.getGoodsId()));
                StockPromotion stockPromotion = promotionMap.get(t.getStockId());
                if (Objects.nonNull(stockPromotion)) {
                    t.setPromotionConsignmentPrice(stockPromotion.getPromotionConsignmentPrice());
                    t.setPromotionPrice(stockPromotion.getPromotionPrice());
                }
                t.setGoodsId(stock.getGoodsId());
                t.setFiness(stock.getFiness());
                t.setTobPrice(stock.getTobPrice());
                t.setTocPrice(stock.getTocPrice());
                t.setTagPrice(stock.getTagPrice());
                t.setTotalPrice(stock.getTotalPrice());
                t.setPricePub(goods.getPricePub());
                t.setConsignmentPrice(stock.getConsignmentPrice());
                t.setLocationId(stock.getLocationId());
                t.setRightOfManagement(stock.getRightOfManagement());
                t.setIsUnderselling(Optional.ofNullable(stock.getIsUnderselling()).map(StockUndersellingEnum::getValue).orElse(StockUndersellingEnum.NOT_ALLOW.getValue()));
                t.setLockDemand(stock.getLockDemand());
                t.setBrandBusinessType(brandMap.get(goods.getBrandId()).getBusinessType().getValue());

                StockMarkets markets = marketsMap.get(t.getStockId());
                if (Objects.nonNull(markets)) {
                    t.setMarketsPrice(markets.getMarketsPrice());
                }

                //重新覆盖回购政策
                List<BuyBackPolicyInfo> buyBackPolicyInfoList = buyBackPolicyService.getStockBuyBackPolicy(BuyBackPolicyBO.builder()
                        .finess(stock.getFiness())
                        .sex(goods.getSex())
                        .brandId(goods.getBrandId())
                        .clinchPrice(t.getClinchPrice())
                        .build());
                t.setIsRepurchasePolicy(CollectionUtils.isNotEmpty(buyBackPolicyInfoList) ? WhetherEnum.YES.getValue() : WhetherEnum.NO.getValue());
                t.setBuyBackPolicy(buyBackPolicyInfoList);
            });
        }

        //特殊参数处理
        this.preRequestProcessing(request);

        //转换表带更换费用
        request.getDetails().forEach(line -> {
                    line.setStrapReplacementPrice(Objects.isNull(line.getWhetherFix())
                            || line.getWhetherFix().equals(WhetherEnum.NO.getValue()) ? new BigDecimal(0) :
                            (line.getClinchPrice().compareTo(new BigDecimal(20000)) >= 0 ? new BigDecimal(1200) : new BigDecimal(600)));
                }
        );
    }

    @Override
    public void bizCheck(SaleOrderCreateRequest request) throws SeeeaseBaseException {
        Assert.notNull(request.getSaleSource(), "销售来源不能为空");
        Assert.notNull(request.getSaleType(), "销售类型不能为空");
        Assert.notNull(request.getSaleChannel(), "销售渠道不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getDetails()), "销售详情不能为空");
        Assert.notNull(request.getCustomerContactId(), "客户联系人ID不能为空");
        Assert.notNull(request.getCustomerId(), "客户ID不能为空");
        Assert.notNull(request.getShopId(), "销售门店不能为空");
        request.getDetails().forEach(t -> {
            Assert.notNull(t.getLocationId(), "发货位置不能为空");
            Assert.isTrue(Objects.nonNull(t.getGoodsId()) && t.getGoodsId() > 0, "销售商品异常");
            Assert.isTrue((t.isDelayStockSn() && Objects.isNull(t.getStockId())) || !t.isDelayStockSn(), "滞后确认表身号场景异常");
            //定金校验
            saleBreakPriceCheck.checkDeposit(request.getShopId(), request.getSaleChannel(), request.getSaleMode(), t);
        });

        if (StringUtils.isNotEmpty(request.getBizOrderCode()) && saleOrderService.count(Wrappers.<BillSaleOrder>lambdaQuery()
                .in(BillSaleOrder::getSaleState, Arrays.asList(SaleOrderStateEnum.UN_CONFIRMED, SaleOrderStateEnum.UN_STARTED, SaleOrderStateEnum.UNDER_WAY))
                .eq(BillSaleOrder::getBizOrderCode, request.getBizOrderCode())) > 0) {
            throw new OperationRejectedException(OperationExceptionCode.BIZ_ORDER_CODE_EXISTS);
        }

        this.checkRequest(request);
    }

    /**
     * 是否允许确认
     *
     * @return
     */
    boolean allowSaleConfirm() {
        return false;
    }

    @Override
    public SaleOrderCreateResult save(SaleOrderCreateRequest request) {
        log.info("save function of SaleOrderStrategy start and  request = {}", JSON.toJSONString(request));
        return transactionalUtil.transactional(() -> {
            //step1:锁定库存
            boolean lockState = lockStock(request);

            //step2:校验，锁定库存失败且不允许销售确认场景
            if (!lockState && !allowSaleConfirm()) {
                throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
            }

            //step3:需要确认参数设置； 锁定库存失败需要确认
            request.setSaleConfirm(!lockState);

            //step4:创建销售单
            List<BillSaleOrderDTO> saleOrderDTOList = saleOrderService.create(request);

            SaleOrderCreateResult result = SaleOrderCreateResult.builder()
                    .saleConfirm(request.isSaleConfirm())
                    .orders(saleOrderDTOList.stream()
                            .map(BillSaleOrderDTO::getOrder)
                            .map(SaleOrderConverter.INSTANCE::convertSaleOrderDto)
                            .collect(Collectors.toList()))
                    .owner(request.getOwner())
                    .createShortcodes(tagService.selectByStoreManagementId(request.getShopId()).getShortcodes())
                    .build();

            if (CollectionUtils.isNotEmpty(request.getDouYinOrderIds())) {
                //默认抖音合单只有一个销售单
                BillSaleOrderDTO dto = saleOrderDTOList.stream().findFirst().orElse(null);
                if (Objects.nonNull(dto))
                    saleOrderService.updateDouYinOrder(request.getDouYinOrderIds(), WhetherEnum.YES.getValue(), new Date(), dto.getOrder().getSerialNo());
            }

            if (CollectionUtils.isNotEmpty(request.getKuaiShouOrderIds())) {
                //默认抖音合单只有一个销售单
                BillSaleOrderDTO dto = saleOrderDTOList.stream().findFirst().orElse(null);
                if (Objects.nonNull(dto))
                    saleOrderService.updateKuaiShouOrder(request.getKuaiShouOrderIds(), WhetherEnum.YES.getValue(), new Date(), dto.getOrder().getSerialNo());
            }

            //需要确认订单直接返回
            if (result.isSaleConfirm()) {
                return result;
            }

            //step5:创建出库作业
            Map<String, SaleOrderCreateResult.SaleOrderDto> resultMap = result.getOrders()
                    .stream()
                    .collect(Collectors.toMap(SaleOrderCreateResult.SaleOrderDto::getSerialNo, Function.identity()));

            //创建出库
            saleOrderDTOList.forEach(res -> {
                BillSaleOrder order = res.getOrder();
                //创建出库作业
                final List<StoreWorkCreateResult> storeWorkList = this.createWork(res);

                //补充返回结果
                resultMap.get(order.getSerialNo())
                        .setShortcodes(tagService.selectByStoreManagementId(order.getDeliveryLocationId()).getShortcodes())
                        .setStoreWorkList(storeWorkList);
            });

            //同行销售 自动生成预收单
            if (SaleOrderTypeEnum.TO_B_JS.getValue().equals(request.getSaleType())) {
                Map<String, List<Integer>> map = new HashMap<>();
                result.getOrders().forEach(t -> map.put(t.getSerialNo(), t.getStoreWorkList()
                        .stream()
                        .map(StoreWorkCreateResult::getStockId)
                        .collect(Collectors.toList())
                ));

                accountingService.createSaleApa(map, ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT, FinancialStatusEnum.PENDING_REVIEW);

                if (!SaleOrderModeEnum.RETURN_POINT.getValue().equals(request.getSaleMode()) &&
                        !SaleOrderModeEnum.CONSIGN_FOR_SALE.getValue().equals(request.getSaleMode())) {
                    //同行销售单，提交订单后，扣除对应金额
                    customerBalanceCmd(request);
                }

            }

            //商品定金销售订单推送
            if (request.getSaleChannel().equals(SaleOrderChannelEnum.XI_YI_SHOP.getValue()) &&
                    request.getSaleMode().equals(SaleOrderModeEnum.DEPOSIT.getValue())
            ) {
                purchaseDemandService.pushMallRealOrder(saleOrderDTOList, request.getCustomerContactId());
            }

            return result;
        });
    }

    void customerBalanceCmd(SaleOrderCreateRequest request) {
        log.info("customerBalanceCmd function of SaleOrderStrategy start and request = {}", JSON.toJSONString(request));
        List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(request.getCustomerId(), null);
        Integer userId = UserContext.getUser().getId();

        BigDecimal amount = BigDecimal.ZERO;
        Integer customerBalanceType = CustomerBalanceTypeEnum.JS_AMOUNT.getValue();
        if (!request.getSaleSource().equals(BusinessBillTypeEnum.TO_B_JS.getValue())) {
            BigDecimal accountBalanceSumLeft = customerBalanceList.stream().filter(Objects::nonNull)
                    .filter(e -> e.getUserId().equals(userId))
                    .map(CustomerBalance::getAccountBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (BigDecimal.ZERO.compareTo(accountBalanceSumLeft.subtract(amount)) >= 0) {
                throw new BusinessException(ExceptionCode.CUSTOMER_ACCOUNT_BALANCE_LEFT_ERR);
            }

            amount = request.getDetails().stream().map(e -> e.getClinchPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
            customerBalanceType = CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue();

        } else if (request.getSaleSource().equals(BusinessBillTypeEnum.TO_B_JS.getValue())) {
            BigDecimal consignmentMarginSumLeft = customerBalanceList.stream().filter(Objects::nonNull)
                    .filter(e -> e.getUserId().equals(userId))
                    .map(CustomerBalance::getConsignmentMargin)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (BigDecimal.ZERO.compareTo(consignmentMarginSumLeft.subtract(amount)) >= 0) {
                throw new BusinessException(ExceptionCode.CUSTOMER_ACCOUNT_CONSIGNMENTMARGIN_LEFT_ERR);
            }
            amount = request.getDetails().stream().map(e -> e.getPreClinchPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);

        }

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            log.info("amount is zero of customerBalanceCmd SaleOrderStrategy function ");
        }

        customerBalanceService.customerBalanceCmd(request.getCustomerId(), request.getCustomerContactId(),
                amount, customerBalanceType, request.getShopId(), CustomerBalanceCmdTypeEnum.MINUS.getValue(),
                userId
                , null);
    }

    /**
     * 锁定库存
     *
     * @param request
     * @return
     * @throws RuntimeException
     */
    boolean lockStock(SaleOrderCreateRequest request) throws RuntimeException {
        List<Integer> stockIdList = request.getDetails()
                .stream()
                .filter(t -> !t.isDelayStockSn()) // 过滤滞后确认表身号
                .map(SaleOrderCreateRequest.BillSaleOrderLineDto::getStockId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(stockIdList)) {
            return true;
        }

        if (stockIdList.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException(ExceptionCode.GOODS_NOT_SUPPORT);
        }
        //可售商品，解决占库存失败事务回滚问题
        long marketableCount = stockService.count(Wrappers.<Stock>lambdaQuery()
                .eq(Stock::getStockStatus, StockStatusEnum.MARKETABLE)
                .in(Stock::getId, stockIdList));

        if (marketableCount != stockIdList.size()) {
            throw new BusinessException(ExceptionCode.GOODS_NOT_SUPPORT);
        }
        //占库存
        stockService.updateStockStatus(stockIdList,
                BusinessBillTypeEnum.TO_B_JS.getValue().intValue() == request.getSaleSource() ?
                        StockStatusEnum.TransitionEnum.SALE_CONSIGNMENT :
                        StockStatusEnum.TransitionEnum.SALE);
        return true;
    }

    /**
     * @param orderDTO
     * @return
     */
    private List<StoreWorkCreateResult> createWork(BillSaleOrderDTO orderDTO) {
        //创建出库
        BillSaleOrder order = orderDTO.getOrder();
        List<StoreWorKCreateRequest> shopWorKList = orderDTO.getLines().stream()
                .map(t -> StoreWorKCreateRequest.builder()
                        .stockId(t.getStockId())
                        .goodsId(t.getGoodsId())
                        .originSerialNo(order.getSerialNo())
                        .workSource(order.getSaleSource().getValue())
                        //发货
                        .workType(StoreWorkTypeEnum.OUT_STORE.getValue())
                        .customerId(order.getCustomerId())
                        .customerContactId(order.getCustomerContactId())
                        .belongingStoreId(order.getDeliveryLocationId())
                        .mateMark(order.getSerialNo() + "-" + t.getId())
                        .build())
                .collect(Collectors.toList());

        //创建出库作业
        return billStoreWorkPreService.create(shopWorKList);
    }

}
