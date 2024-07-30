package com.seeease.flywheel.serve.recycle.rpc;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.customer.request.CustomerUpdateRequest;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentCreateResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentDetailResult;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseDetailsRequest;
import com.seeease.flywheel.purchase.request.PurchaseLoadRequest;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;
import com.seeease.flywheel.purchase.result.PurchaseDetailsResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.*;
import com.seeease.flywheel.recycle.result.*;
import com.seeease.flywheel.sale.request.SaleLoadRequest;
import com.seeease.flywheel.sale.result.SaleOrderCreateResult;
import com.seeease.flywheel.serve.base.BigDecimalUtil;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.dict.entity.DictData;
import com.seeease.flywheel.serve.dict.service.DictDataService;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.SeriesService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.flywheel.serve.recycle.convert.MallRecycleConverter;
import com.seeease.flywheel.serve.recycle.domain.RecycleDomain;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import com.seeease.flywheel.serve.recycle.enums.RecycleOrderTypeEnum;
import com.seeease.flywheel.serve.recycle.enums.RecycleStateEnum;
import com.seeease.flywheel.serve.recycle.service.IRecycleOrderService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderMapper;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 回收、回购订单
 *
 * @Auther Gilbert
 * @Date 2023/9/1 09:49
 */
@Slf4j
@DubboService(version = "1.0.0")
public class RecycleOderFacade implements IRecycleOderFacade {

    private final static String OSS_PATH = "https://seeease.oss-cn-hangzhou.aliyuncs.com/mall/upload/";

    private final static List<Double> DISCOUNT_RANGE = Lists.newArrayList(9.5d, 9d, 8.5d, 8d, 7.5d, 7d, 6.5d, 6d, 5.5d, 5d, 4.5d, 4d);

    @Resource
    private IRecycleOrderService recycleOrderService;

    @Resource
    private RecycleDomain recycleDomain;

    @Resource
    private CustomerService customerService;

    @Resource
    private CustomerContactsService customerContactsService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private BillPurchaseMapper billPurchaseMapper;

    @Resource
    private BillSaleOrderMapper billSaleOrderMapper;

    @Resource
    private IPurchaseFacade iPurchaseFacade;

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;

    @Resource
    private TagService tagService;

    @Resource
    private StockService stockService;
    @Resource
    private SeriesService seriesService;
    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private BrandService brandService;

    @Resource
    private UserService userService;

    private static final Set<String> ROLE_NAMES = ImmutableSet.of("admin", "总部查看", "财务查看");

    private static final List<RecycleStateEnum> BUY_BACK_LIST = Arrays.asList(RecycleStateEnum.MAKE_ORDER,
            RecycleStateEnum.WAIT_UPLOAD_CUSTOMER,
            RecycleStateEnum.COMPLETE,
            RecycleStateEnum.CANCEL_WHOLE,
            RecycleStateEnum.WAIT_DELIVER_LOGISTICS
    );
    private static final List<RecycleStateEnum> RECYCLE_LIST = Arrays.asList(RecycleStateEnum.UN_CONFIRMED,
            RecycleStateEnum.CUSTOMER_RECEIVE,
            RecycleStateEnum.WAY_OFFER,
            RecycleStateEnum.CUSTOMER_RECEIVE_SURE,
            RecycleStateEnum.MAKE_ORDER,
            RecycleStateEnum.COMPLETE,
            RecycleStateEnum.CANCEL_WHOLE);

    @Override
    public RecycleOrderResult orderCreate(MarketRecycleOrderRequest request) {
        //进行保存回收或者回购订单
        return recycleOrderService.create(request);
    }

    @Override
    public BuyBackForSaleResult recycleForSaleDetail(RecycleOrderVerifyRequest request) {
        MallRecyclingOrder mallRecyclingOrder = recycleDomain.asserter.assertRecycle(request.getId());
        return recycleDomain.buyBackForSaleResult(mallRecyclingOrder)
                .setBuyBackForLineResult(detailLine(request))
                .setSaleOrderDetailLineResult(saleDetailLine(new RecycleOrderVerifyRequest().setId(request.getId())))
                .setExpressResult(expressResult(mallRecyclingOrder));
    }

    @Override
    public PageResult<RecyclingListResult> list(RecycleOrderListRequest request) {
        if (Objects.nonNull(UserContext.getUser()) && Objects.nonNull(UserContext.getUser().getStore())) {
            if (CollectionUtils.isNotEmpty(UserContext.getUser().getRoles()) &&
                    UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(ROLE_NAMES::contains)) {
                request.setStoreId(null);
            } else {
                request.setStoreId(UserContext.getUser().getStore().getId());
            }
        }

        if (RecycleOrderTypeEnum.BUY_BACK == RecycleOrderTypeEnum.fromCode(request.getRecycleType())) {

            //查询条件
            if (ObjectUtils.isNotEmpty(request.getState())) {

                List<Integer> collect = BUY_BACK_LIST.stream().filter(r -> r.getRemark().equals(request.getState())).map(r -> r.getValue()).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)) {
                    return PageResult.<RecyclingListResult>builder()
                            .result(Arrays.asList())
                            .totalCount(0)
                            .totalPage(0)
                            .build();
                }
                request.setLineStateList(collect);
                request.setState(null);
            } else {
                request.setState(null);
            }

            Page<RecyclingListResult> recyclingListResultPage = recycleOrderService.listByRequest(request);

            recyclingListResultPage.getRecords().forEach(item -> {
                Map job = (Map) dictDataService.dictData(item.getStockId(), item.getIsCard(), item.getWarrantyDate());
                item.setDictChildList(job.get("dictChildList"));
                item.setAttachmentLabel(job.get("attachmentLabel"));
                if (ObjectUtils.isNotEmpty(item.getLineState())) {

                    RecycleStateEnum recycleStateEnum = RecycleStateEnum.fromCode(Integer.valueOf(item.getLineState()));

                    item.setLineState(recycleStateEnum.getDesc());
                    item.setState(recycleStateEnum.getRemark());
                }
                item.setCreatedTime(DateUtil.formatDateTime(DateUtil.parse(item.getCreatedTime())));
                item.setBuyBackForLineResult(detailLine(new RecycleOrderVerifyRequest().setId(item.getId())));
                item.setSaleOrderDetailLineResult(saleDetailLine(new RecycleOrderVerifyRequest().setId(item.getId())));
                item.setExpressResult(expressResult(new MallRecyclingOrder().setPurchaseId(item.getPurchaseId())));
            });

            return PageResult.<RecyclingListResult>builder()
                    .result(recyclingListResultPage.getRecords())
                    .totalCount(recyclingListResultPage.getTotal())
                    .totalPage(recyclingListResultPage.getPages())
                    .build();
        } else if (RecycleOrderTypeEnum.RECYCLE == RecycleOrderTypeEnum.fromCode(request.getRecycleType())) {

            //查询条件
            if (ObjectUtils.isNotEmpty(request.getState())) {
                List<Integer> collect = RECYCLE_LIST.stream().filter(r -> r.getRemark().equals(request.getState())).map(r -> r.getValue()).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect)) {
                    return PageResult.<RecyclingListResult>builder()
                            .result(Arrays.asList())
                            .totalCount(0)
                            .totalPage(0)
                            .build();
                }
                request.setLineStateList(collect);
                request.setState(null);
            } else {
                request.setState(null);
            }

            Page<RecyclingListResult> page = recycleOrderService.listByRequestByRecycle(request);
            List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));
            for (RecyclingListResult result : page.getRecords()) {

                //封装最新价格 回收价和置换价
                result.setLatestRecyclePrice(StringUtils.isEmpty(result.getValuationPriceTwo()) ? StringUtils.isEmpty(result.getValuationPrice()) ? BigDecimal.ZERO : Arrays.stream(StringUtils.split(result.getValuationPrice(), ",")).map(BigDecimal::new).collect(Collectors.toList()).get(0) : Arrays.stream(StringUtils.split(result.getValuationPriceTwo(), ",")).map(BigDecimal::new).collect(Collectors.toList()).get(0));
                result.setLatestReplacePrice(StringUtils.isEmpty(result.getValuationPriceTwo()) ? StringUtils.isEmpty(result.getValuationPrice()) ? BigDecimal.ZERO : Arrays.stream(StringUtils.split(result.getValuationPrice(), ",")).map(BigDecimal::new).collect(Collectors.toList()).get(1) : Arrays.stream(StringUtils.split(result.getValuationPriceTwo(), ",")).map(BigDecimal::new).collect(Collectors.toList()).get(1));
                //附件处理
                result.setAttachment(convert(dataList, JSONObject.parseArray(result.getAttachmentList(), Integer.class), result.getIsCard(), result.getWarrantyDate(),result.getStockId()));
                if (ObjectUtils.isNotEmpty(result.getLineState())) {

                    RecycleStateEnum recycleStateEnum = RecycleStateEnum.fromCode(Integer.valueOf(result.getLineState()));

                    result.setLineState(recycleStateEnum.getDesc());
                    result.setState(recycleStateEnum.getRemark());
                }
            }
            return PageResult.<RecyclingListResult>builder()
                    .result(page.getRecords())
                    .totalCount(page.getTotal())
                    .totalPage(page.getPages())
                    .build();
        }
        return PageResult.<RecyclingListResult>builder()
                .result(Arrays.asList())
                .totalCount(0)
                .totalPage(0)
                .build();
    }

    /**
     * 更改单子状态
     *
     * @param id
     */
    @Override
    public void updateRecycleStatus(@NonNull Integer id) {
        MallRecyclingOrder r = new MallRecyclingOrder();
        r.setId(id);
//        r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.WAIT_UPLOAD_BANK);
//        recycleOrderService.updateRecycleStatus(r);
    }

    /**
     * 进行上传打款信息
     *
     * @param request
     * @return
     */
    @Override
    public RecycleOrderResult uploadRemit(MarkektRecycleUserBankRequest request) {

        MallRecyclingOrder mallRecyclingOrder = recycleDomain.asserter.assertRecycle(request.getRecycleId());

        MallRecyclingOrder r = new MallRecyclingOrder();

        if (StringUtils.isNotEmpty(request.getBankName()) && StringUtils.contains(request.getBankName(), "银行")) {
            Matcher m = Pattern.compile("(?<bank>[^银行]+银行|[^信用社]+信用社)?(?<address>.*)").matcher(request.getBankName());
            if (m.matches()) {
                r.setAccountName(m.group("bank"));
                r.setBank(m.group("address"));
            }

        } else {
            //不存在
            r.setAccountName(request.getBankName());
            r.setBank(request.getBankName());
        }
        if (StringUtils.isEmpty(r.getBank())) {
            r.setBank(request.getBankName());
        }
        //保存银行卡信息
        customerService.update(CustomerUpdateRequest.builder()
                .customerId(mallRecyclingOrder.getCustomerId())
                .customerName(request.getAccountName())
                .accountName(request.getAccountName())
                .bank(request.getBankName())
                .bankAccount(request.getAccount())
                .identityCard(request.getIdCard())
                .identityCardImage(OSS_PATH + request.getFrontImg() + "," + OSS_PATH + request.getBackImg()).build());

        r.setId(mallRecyclingOrder.getId());

        r.setTransitionStateEnum(mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.RECYCLE ? RecycleStateEnum.TransitionEnum.WAIT_UPLOAD_CUSTOMER_MAKE_ORDER : RecycleStateEnum.TransitionEnum.WAIT_UPLOAD_CUSTOMER_COMPLETE);

        r.setAgreement(Objects.nonNull(request.getAgreement()) ? OSS_PATH + request.getAgreement().getContent() : null);

        r.setBankAccount(request.getAccount());
        r.setBankCustomerName(request.getAccountName());
        //身份证正反面
        r.setFrontIdentityCard(OSS_PATH + request.getFrontImg());
        r.setReverseIdentityCard(OSS_PATH + request.getBackImg());
        r.setAgreementTransfer(Objects.nonNull(request.getAgreement()) ? OSS_PATH + request.getAgreement().getContent() : null);

        if (mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.BUY_BACK) {
            recycleOrderService.updateRecycleStatus(r);
            //在采购单中也存一份银行信息
            recycleDomain.purchaseUpdate(request, mallRecyclingOrder,r);
        } else if (mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.RECYCLE) {
            r.setTransitionStateEnum(null);
            recycleOrderService.updateById(r);
        }
        return RecycleOrderResult.builder().serialNo(mallRecyclingOrder.getSerial()).recycleId(mallRecyclingOrder.getId()).build();
    }


    /**
     * 查询采购行信息
     *
     * @param request
     * @return
     */
    @Override
    public BuyBackForLineResult detailLine(RecycleOrderVerifyRequest request) {
        //判断回收、回购单是否存在
        MallRecyclingOrder mallRecyclingOrder = recycleDomain.asserter.assertRecycle(request.getId());

        //判断采购单id是否存在，如果存在则查询采购单信息。如果不存在则查询原销售单信息
        if (mallRecyclingOrder.getPurchaseId() != null) {
            return purchaseOrder(mallRecyclingOrder);
        }
        return originSaleOrder(mallRecyclingOrder);
    }

    /**
     * 查询销售行数据信息
     *
     * @param request
     * @return
     */
    public SaleOrderDetailLineResult saleDetailLine(RecycleOrderVerifyRequest request) {
        MallRecyclingOrder mallRecyclingOrder = recycleDomain.asserter.assertRecycle(request.getId());
        if (mallRecyclingOrder.getSaleId() != null) {
            return recycleDomain.saleOrderDetailLineResult(mallRecyclingOrder);
        }
        return null;
    }

    /**
     * 查询物流单号
     */
    public BuyBackExpressResult expressResult(MallRecyclingOrder mallRecyclingOrder) {
        if (mallRecyclingOrder.getPurchaseId() != null) {
            BillPurchase purchase = recycleDomain.asserter.purchase(mallRecyclingOrder.getPurchaseId());
            BillStoreWorkPre one = billStoreWorkPreService.getOne(Wrappers.<BillStoreWorkPre>lambdaQuery().eq(BillStoreWorkPre::getOriginSerialNo, purchase.getSerialNo()).last("limit 1"));
            if (Objects.nonNull(one)) {
                return new BuyBackExpressResult()
                        .setExpressNumber(one.getExpressNumber())
                        .setDeliveryExpressNumber(one.getDeliveryExpressNumber())
                        .setCreatedTime(one.getCreatedTime());
            }
        }

        return null;
    }

    public BuyBackForLineResult originSaleOrder(MallRecyclingOrder mallRecyclingOrder) {

        if (mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.RECYCLE && Objects.isNull(mallRecyclingOrder.getSaleSerialNo())) {
            return new BuyBackForLineResult();
        }

        //1。销售单是否存在
        BillSaleOrder billSaleOrder = recycleDomain.asserter.assertBillSaleOrder(mallRecyclingOrder.getSaleSerialNo());
        //查询表是否存在回购
        //recycleDomain.asserter.buyBackExits(recycling.getStockId(), recycling.getSaleSerialNo());
        //销售单行号是否存在
        BillSaleOrderLine billSaleOrderLine = recycleDomain.asserter.saleOrderLine(billSaleOrder.getId(), mallRecyclingOrder.getStockId());
        //3。回购政策是否满足
        List<BuyBackPolicyMapper> mappers = billSaleOrderLine.getBuyBackPolicy().stream().sorted(Comparator.comparing(BuyBackPolicyMapper::getBuyBackTime)).collect(Collectors.toList());

        //关联销售成交价
        BigDecimal clinchPrice = billSaleOrderLine.getClinchPrice();
        //仅置换
        ImmutableRangeMap.Builder<Comparable<Date>, BigDecimal> builderIn = ImmutableRangeMap.<Comparable<Date>, BigDecimal>builder();

        //仅回收
        ImmutableRangeMap.Builder<Comparable<Date>, BigDecimal> builderRecycle = ImmutableRangeMap.<Comparable<Date>, BigDecimal>builder();
        //差值 初始者
        AtomicReference<Integer> difference = new AtomicReference<>(0);
        //回购加点值
        mappers.forEach(buyBackPolicyMapper -> {
            // 确定 到天 前开后闭
            //YYYY_MM_DD ？？？？ 建单时间 初始时间
            Date date = DateUtils.stepMonth(DateUtil.parse(DateUtil.formatDate(billSaleOrder.getCreatedTime())), difference.get());

            //递增值
            Date month = DateUtils.stepMonth(DateUtil.parse(DateUtil.formatDate(billSaleOrder.getCreatedTime())), buyBackPolicyMapper.getBuyBackTime());

            //置换
            builderIn.put(Range.openClosed(date, month), buyBackPolicyMapper.getDiscount().add(buyBackPolicyMapper.getReplacementDiscounts()));

            //回收
            builderRecycle.put(Range.openClosed(date, month), buyBackPolicyMapper.getDiscount());

            difference.set(buyBackPolicyMapper.getBuyBackTime());
        });
        //建单时间 参考回购折扣 置换
        BigDecimal referenceBuyBackDiscount = Optional.ofNullable(builderIn.build().get(DateUtil.parse(DateUtil.today()).toJdkDate())).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.REFERENCE_BUY_BACK_PRICE));
        //建单时间 参考回购价 置换
        BigDecimal referenceBuyBackInPrice = BigDecimalUtil.multiplyRoundHalfUp(clinchPrice,
                //值点
                referenceBuyBackDiscount.divide(BigDecimal.valueOf(FlywheelConstant.MULTIPLIER_10), 4, BigDecimal.ROUND_HALF_UP));

        //建单时间 参考回购折扣 回收
        BigDecimal referenceBuyBackRecycleDiscount = Optional.ofNullable(builderRecycle.build().get(DateUtil.parse(DateUtil.today()).toJdkDate())).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.REFERENCE_BUY_BACK_PRICE));
        //建单时间 参考回购价 回收
        BigDecimal referenceBuyBackRecyclePrice = BigDecimalUtil.multiplyRoundHalfUp(clinchPrice,
                //值点
                referenceBuyBackRecycleDiscount.divide(BigDecimal.valueOf(FlywheelConstant.MULTIPLIER_10), 4, BigDecimal.ROUND_HALF_UP));

        if (ObjectUtils.isEmpty(referenceBuyBackInPrice) || ObjectUtils.isEmpty(referenceBuyBackRecyclePrice)
                || referenceBuyBackInPrice.compareTo(BigDecimal.ZERO) <= 0 || referenceBuyBackRecyclePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperationRejectedException(OperationExceptionCode.REFERENCE_BUY_BACK_PRICE);
        }

        WatchDataFusion watchDataFusion = recycleDomain.asserter.watchDataFusion(mallRecyclingOrder.getStockId());
        return recycleDomain.detailLine(watchDataFusion,
                billSaleOrderLine,
                billSaleOrder,
                referenceBuyBackRecyclePrice,
                referenceBuyBackRecycleDiscount,
                referenceBuyBackInPrice,
                referenceBuyBackDiscount,
                DISCOUNT_RANGE);
    }


    public BuyBackForLineResult purchaseOrder(MallRecyclingOrder mallRecyclingOrder) {
        BillPurchase purchase = recycleDomain.asserter.purchase(mallRecyclingOrder.getPurchaseId());
        PurchaseDetailsRequest purchaseDetailsRequest = new PurchaseDetailsRequest();
        purchaseDetailsRequest.setId(purchase.getId());
        purchaseDetailsRequest.setSerialNo(purchase.getSerialNo());
        PurchaseDetailsResult details = iPurchaseFacade.details(purchaseDetailsRequest);
        if (Objects.nonNull(details)) {
            //获取行数据
            PurchaseDetailsResult.PurchaseLineVO purchaseLineVO = details.getLines().get(FlywheelConstant.INDEX);
            purchaseLineVO.setSerialNo(purchase.getSerialNo());
            return MallRecycleConverter.INSTANCE.convertLine(purchaseLineVO);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    public ReplacementOrRecycleCreateResult replacementOrRecycleCreate(ReplacementOrRecycleCreateRequest request) {
        //查询商城回收单
        MallRecyclingOrder mallRecyclingOrder = recycleDomain.asserter.assertRecycle(request.getId());

        Optional.ofNullable(mallRecyclingOrder).orElseThrow(() -> new BusinessException(ExceptionCode.APPLY_FINANCIAL_RECYCLE_EXIST2));

        BillSaleOrder saleOrder = null;
        SaleOrderCreateResult saleOrderCreateResult = null;

        ReplacementOrRecycleCreateResult.ReplacementOrRecycleCreateResultBuilder replacementOrRecycleCreateResultBuilder = ReplacementOrRecycleCreateResult.builder();

        BigDecimal recyclePrice = StringUtils.isBlank(mallRecyclingOrder.getValuationPriceTwo()) ? BigDecimal.ZERO : Arrays.stream(mallRecyclingOrder.getValuationPriceTwo().split(",")).map(BigDecimal::new).collect(Collectors.toList()).get(0);

        //如果是置换需要创建销售单
        if (PurchaseModeEnum.DISPLACE == PurchaseModeEnum.fromCode(request.getType())) {
            //创建销售单
            saleOrderCreateResult = recycleDomain.saleCreate(mallRecyclingOrder, request.getReplacementLineDtoList().get(FlywheelConstant.INDEX));
            //返回单号进行查询
            saleOrder = recycleDomain.asserter.assertBillSaleOrder(saleOrderCreateResult.getOrders().get(FlywheelConstant.INDEX).getSerialNo());
            recyclePrice = StringUtils.isBlank(mallRecyclingOrder.getValuationPriceTwo()) ? BigDecimal.ZERO : Arrays.stream(mallRecyclingOrder.getValuationPriceTwo().split(",")).map(BigDecimal::new).collect(Collectors.toList()).get(1);
        }

        //更改状态到客户是否接受
        MallRecyclingOrder r = new MallRecyclingOrder();
        r.setId(request.getId());
        r.setSaleId(Objects.nonNull(saleOrderCreateResult) && CollectionUtils.isNotEmpty(saleOrderCreateResult.getOrders()) ? saleOrderCreateResult.getOrders().get(0).getId() : null);
        r.setType(PurchaseModeEnum.fromCode(request.getType()));

        //回收
        if (RecycleOrderTypeEnum.RECYCLE == mallRecyclingOrder.getRecycleType()) {

            List<BigDecimal> offerList = Arrays.stream(StringUtils.split(mallRecyclingOrder.getValuationPriceTwo(), ",")).map(BigDecimal::new).collect(Collectors.toList());

            //回收价
            BigDecimal totalPurchasePrice = offerList.get(0);

            //置换价
            if (PurchaseModeEnum.DISPLACE == PurchaseModeEnum.fromCode(request.getType())) {
                totalPurchasePrice = offerList.get(1);
            }
            //销售价
            BigDecimal totalSalePrice = BigDecimal.ZERO;

            if (ObjectUtils.isNotEmpty(saleOrder) && ObjectUtils.isNotEmpty(saleOrderCreateResult)) {
                totalSalePrice = saleOrderCreateResult.getTotalSalePrice();
            }

            //差额
            BigDecimal balance = totalSalePrice.subtract(totalPurchasePrice);

            r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.MAKE_ORDER_COMPLETE);
            r.setSymbol(balance.signum());
            r.setBalance(balance);

            if (balance.signum() < 0) {

                //创建申请打款单 未填写打款信息
                if (StringUtils.isBlank(mallRecyclingOrder.getBankAccount()) || StringUtils.isBlank(mallRecyclingOrder.getBankAccount()) ||
                        StringUtils.isBlank(mallRecyclingOrder.getBankAccount()) || StringUtils.isBlank(mallRecyclingOrder.getBankAccount())) {
                    throw new BusinessException(ExceptionCode.MALL_FINANCIAL_RECYCLE_EXIST2);
                }

                //创建打款单 差额
                if (PurchaseModeEnum.RECYCLE == PurchaseModeEnum.fromCode(request.getType())) {
                    ApplyFinancialPaymentCreateResult applyFinancialPaymentCreateResult = recycleDomain.applyFinancialPaymentCreate(mallRecyclingOrder, balance.abs(), request.getPurchaseSubjectId());
                    request.setApplyPaymentSerialNo(applyFinancialPaymentCreateResult.getSerialNo());
                }

                //封装打款信息
                request.setApplyFinancialPaymentDetailResult(ApplyFinancialPaymentDetailResult
                        .builder()
                        .pricePayment(balance.abs())
                        .subjectPayment(request.getPurchaseSubjectId())
                        .build());
            }

            //创建采购单 必须创建采购单
            request.getBillPurchaseLineDtoList().get(0).setRecyclePrice(recyclePrice);

            PurchaseCreateListResult purchase = recycleDomain.createPurchase(request, mallRecyclingOrder, saleOrder);

            ReplacementOrRecycleCreateResult.ProcessDTO.ProcessDTOBuilder builder = ReplacementOrRecycleCreateResult.ProcessDTO.builder();

            //开启采购参数
            if (ObjectUtils.isNotEmpty(purchase)) {
                //加载采购
                builder.purchaseLoadRequest(PurchaseLoadRequest.builder()
                        .businessKey(purchase.getBusinessKey())
                        .serialNo(purchase.getSerialNo())
                        .storeId(purchase.getStoreId())
                        .shortcodes(purchase.getShortcodes())
                        .line(purchase.getLine().stream().map(PurchaseDetailsResult.PurchaseLineVO::getWno).collect(Collectors.toList()))
                        .build());
                r.setPurchaseId(purchase.getId());
            }

            if (balance.signum() <= 0 && ObjectUtils.isNotEmpty(saleOrder) && ObjectUtils.isNotEmpty(saleOrderCreateResult)) {
                builder.saleLoadRequest(SaleLoadRequest.builder()
                        .saleProcess("toCSale")
                        .shopId(UserContext.getUser().getStore().getId())
                        .createShortcodes(saleOrderCreateResult.getCreateShortcodes())
                        .saleConfirm(Boolean.FALSE)
                        .owner("")
                        .orders(saleOrderCreateResult.getOrders().stream().map(saleOrderDto -> {
                            return SaleLoadRequest.SaleOrderDTO.builder()
                                    .deliveryLocationId(saleOrderDto.getDeliveryLocationId())
                                    .serialNo(saleOrderDto.getSerialNo())
                                    .shortcodes(saleOrderDto.getShortcodes())
                                    .storeWorkList(saleOrderDto.getStoreWorkList().stream().map(storeWorkCreateResult -> {
                                        return SaleLoadRequest.StoreWorkDTO.builder().stockId(storeWorkCreateResult.getStockId()).serialNo(storeWorkCreateResult.getSerialNo()).build();
                                    }).collect(Collectors.toList())).build();

                        }).collect(Collectors.toList())).build());

                r.setSaleId(saleOrderCreateResult.getOrders().get(0).getId());
            }

            replacementOrRecycleCreateResultBuilder.process(builder.build());

            recycleOrderService.updateRecycleStatus(r);

            return replacementOrRecycleCreateResultBuilder.build();
            //回购
        } else if (RecycleOrderTypeEnum.BUY_BACK == mallRecyclingOrder.getRecycleType()) {

            //创建采购单
            PurchaseCreateListResult purchase = recycleDomain.createPurchase(request, mallRecyclingOrder, saleOrder);

            ReplacementOrRecycleCreateResult.ProcessDTO.ProcessDTOBuilder builder = ReplacementOrRecycleCreateResult.ProcessDTO.builder();

            if (ObjectUtils.isNotEmpty(purchase)) {
                //加载采购
                builder.purchaseLoadRequest(PurchaseLoadRequest.builder()
                        .businessKey(purchase.getBusinessKey())
                        .serialNo(purchase.getSerialNo())
                        .storeId(purchase.getStoreId())
                        .shortcodes(purchase.getShortcodes())
                        .line(purchase.getLine().stream().map(PurchaseDetailsResult.PurchaseLineVO::getWno).collect(Collectors.toList()))
                        .build());
            }

            r.setPurchaseId(purchase.getId());

            if (ObjectUtils.isNotEmpty(saleOrder) && ObjectUtils.isNotEmpty(saleOrderCreateResult)) {
                //不改状态 只更新单子
                r.setType(PurchaseModeEnum.fromCode(request.getType()));
                recycleOrderService.updateById(r);
            } else {
                r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.MAKE_ORDER_WAIT_UPLOAD_BANK);
                r.setBalance(purchase.getTotalPurchasePrice().negate());
                r.setSymbol(-1);
                r.setType(PurchaseModeEnum.fromCode(request.getType()));
                r.setPurchaseId(purchase.getId());
                recycleOrderService.updateRecycleStatus(r);
            }

            replacementOrRecycleCreateResultBuilder.process(builder.build());

            return replacementOrRecycleCreateResultBuilder.build();
        }

        return replacementOrRecycleCreateResultBuilder.build();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecycleOrderVerifyResult firstVerify(RecycleOrderVerifyRequest request) {

        MallRecyclingOrder mallRecyclingOrder = recycleOrderService.getById(request.getId());

        Optional.ofNullable(mallRecyclingOrder).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.BUY_BACK_RECYCLE_EXIST));

        MallRecyclingOrder r = new MallRecyclingOrder();
        r.setId(request.getId());
        //回收
        if (RecycleOrderTypeEnum.RECYCLE == mallRecyclingOrder.getRecycleType()) {
            if (WhetherEnum.fromValue(request.getAccept()) == WhetherEnum.YES) {
                r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.CUSTOMER_RECEIVE_WAY_OFFER);
                r.setExpressNumber(request.getExpressNumber());
                recycleOrderService.updateRecycleStatus(r);
                return RecycleOrderVerifyResult.builder().verify(Boolean.TRUE).build();
            } else if (WhetherEnum.fromValue(request.getAccept()) == WhetherEnum.NO) {
                r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.CUSTOMER_RECEIVE_CANCEL_ORDER);
                recycleOrderService.updateRecycleStatus(r);
                return RecycleOrderVerifyResult.builder().verify(Boolean.FALSE).build();
            }
            return RecycleOrderVerifyResult.builder().build();
        }

        return RecycleOrderVerifyResult.builder().build();
    }

    @Override
    public RecycleOrderSecondVerifyResult secondVerify(RecycleOrderSecondVerifyRequest request) {

        MallRecyclingOrder r = new MallRecyclingOrder();
        r.setId(request.getId());
        //客户不接受。直接流程结束。默认为接受
        if (WhetherEnum.fromValue(request.getAccept()) == WhetherEnum.YES) {
            r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.CUSTOMER_RECEIVE_SURE_MAKE_ORDER);
            recycleOrderService.updateRecycleStatus(r);
            return RecycleOrderSecondVerifyResult.builder().verify(Boolean.TRUE).build();
        } else if (WhetherEnum.fromValue(request.getAccept()) == WhetherEnum.NO) {
            r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.CUSTOMER_RECEIVE_SURE_CANCEL_WHOLE);
            r.setDeliveryExpressNumber(request.getDeliveryExpressNumber());
            recycleOrderService.updateRecycleStatus(r);
            return RecycleOrderSecondVerifyResult.builder().verify(Boolean.FALSE).build();
        }
        return RecycleOrderSecondVerifyResult.builder().build();
    }

    @Override
    public RecycleOrderDetailsResult details(RecycleOrderDetailsRequest request) {

        MallRecyclingOrder mallRecyclingOrder = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> recycleOrderService.getOne(Wrappers.<MallRecyclingOrder>lambdaQuery()
                        .eq(MallRecyclingOrder::getId, t.getId())
                        .or().eq(MallRecyclingOrder::getSerial, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.BUYBACK_FINANCIAL_RECYCLE_EXIST2));

        List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));

        RecycleOrderDetailsResult result = MallRecycleConverter.INSTANCE.convertSaleOrderDetailsResult(mallRecyclingOrder);

        //封装附件字符串
        result.setAttachment(convert(dataList, mallRecyclingOrder.getAttachmentList(), result.getIsCard(), result.getWarrantyDate(),result.getStockId()));
        //封装最新价格 回收价和置换价
        result.setLatestRecyclePrice(StringUtils.isEmpty(result.getValuationPriceTwo()) ? StringUtils.isEmpty(result.getValuationPrice()) ? BigDecimal.ZERO : Arrays.stream(StringUtils.split(result.getValuationPrice(), ",")).map(BigDecimal::new).collect(Collectors.toList()).get(0) : Arrays.stream(StringUtils.split(result.getValuationPriceTwo(), ",")).map(BigDecimal::new).collect(Collectors.toList()).get(0));
        result.setLatestReplacePrice(StringUtils.isEmpty(result.getValuationPriceTwo()) ? StringUtils.isEmpty(result.getValuationPrice()) ? BigDecimal.ZERO : Arrays.stream(StringUtils.split(result.getValuationPrice(), ",")).map(BigDecimal::new).collect(Collectors.toList()).get(1) : Arrays.stream(StringUtils.split(result.getValuationPriceTwo(), ",")).map(BigDecimal::new).collect(Collectors.toList()).get(1));
        result.setLatestValuationImage(StringUtils.isEmpty(mallRecyclingOrder.getValuationImageTwo()) ? StringUtils.isEmpty(mallRecyclingOrder.getValuationImage()) ? "" : mallRecyclingOrder.getValuationImage() : mallRecyclingOrder.getValuationImageTwo());
        result.setLatestValuationRemark(StringUtils.isEmpty(mallRecyclingOrder.getValuationPriceTwoRemark()) ? StringUtils.isEmpty(mallRecyclingOrder.getValuationRemark()) ? "" : mallRecyclingOrder.getValuationRemark() : mallRecyclingOrder.getValuationPriceTwoRemark());

        if (StringUtils.isNotEmpty(mallRecyclingOrder.getFrontIdentityCard()) && StringUtils.isNotEmpty(mallRecyclingOrder.getReverseIdentityCard())) {
            result.setIdentityCard(StringUtils.join(Arrays.asList(mallRecyclingOrder.getFrontIdentityCard(), mallRecyclingOrder.getReverseIdentityCard()), ","));
        }
        //商城传递过来的图片肯定不为空
        if (StringUtils.isNotEmpty(mallRecyclingOrder.getShopImage())) {
            result.setOtherPicture(StringUtils.join(Arrays.asList(mallRecyclingOrder.getShopImage()), ","));
        }
        if (StringUtils.isNotEmpty(mallRecyclingOrder.getValuationImage())) {
            result.setOtherPicture(StringUtils.join(Arrays.asList(result.getOtherPicture(), mallRecyclingOrder.getValuationImage()), ","));
        }

        if (StringUtils.isNotEmpty(mallRecyclingOrder.getValuationImageTwo())) {
            result.setOtherPicture(StringUtils.join(Arrays.asList(result.getOtherPicture(), mallRecyclingOrder.getValuationImageTwo()), ","));
        }

        if (StringUtils.isNotEmpty(mallRecyclingOrder.getAgreement())) {
            result.setOtherPicture(StringUtils.join(Arrays.asList(result.getOtherPicture(), mallRecyclingOrder.getAgreement()), ","));
        }

        if (ObjectUtils.isEmpty(mallRecyclingOrder.getGoodsId())) {
            Brand brand = brandService.getById(mallRecyclingOrder.getBrandId());
            if (ObjectUtils.isNotEmpty(brand)) {
                result.setBrandName(brand.getName());
            }
        } else {
            List<WatchDataFusion> list = goodsWatchService.getWatchDataFusionListByGoodsIds(Arrays.asList(mallRecyclingOrder.getGoodsId()));

            if (CollectionUtils.isNotEmpty(list)) {
                WatchDataFusion watchDataFusion = list.get(0);
                result.setBrandName(watchDataFusion.getBrandName());
                result.setSeriesName(watchDataFusion.getSeriesName());
                result.setModel(watchDataFusion.getModel());
                result.setPricePub(watchDataFusion.getPricePub());
                result.setWatchSize(watchDataFusion.getWatchSize());
                result.setSeriesId(watchDataFusion.getSeriesId());
            }
        }
        if (StringUtils.isNotEmpty(result.getRemark()) && StringUtils.isNotEmpty(result.getValuationRemark())) {
            result.setRemark(StringUtils.join(Arrays.asList(result.getRemark(), mallRecyclingOrder.getValuationRemark()), ","));
        }
        if (StringUtils.isEmpty(result.getRemark()) && StringUtils.isNotEmpty(result.getValuationRemark())) {
            result.setRemark(result.getValuationRemark());
        }


        //客户信息
        //客户信息
        Customer customer = Optional.ofNullable(mallRecyclingOrder.getCustomerId()).map(customerService::getById).orElse(null);
        if (Objects.nonNull(customer)) {
            result.setCustomerName(customer.getCustomerName());
            //相同字段发生置换
//            result.setAccountName(customer.getAccountName());
//            result.setBank(customer.getBank());
//            result.setBankAccount(customer.getBankAccount());
        }

        //联系人信息
        CustomerContacts customerContacts = Optional.ofNullable(mallRecyclingOrder.getCustomerContactId()).map(customerContactsService::getById).orElse(null);
        if (Objects.nonNull(customerContacts)) {
            result.setContactName(customerContacts.getName());
            result.setContactPhone(customerContacts.getPhone());
            result.setContactAddress(customerContacts.getAddress());
            result.setContactId(customerContacts.getId());
        }

        //需求门店
        Tag tag = Optional.ofNullable(mallRecyclingOrder.getDemandId()).map(tagService::selectByStoreManagementId)
                .orElse(null);
        if (Objects.nonNull(tag)) {
            result.setDemanderStoreName(tag.getTagName());
        }

        User user = Optional.ofNullable(mallRecyclingOrder.getEmployeeId()).map(userService::getById).orElse(null);

        if (Objects.nonNull(user)) {
            result.setEmployeeName(user.getName());
        }

        //返回附件的格式 回显附件
        Map map = new HashMap<>();
        dataList.stream().collect(Collectors.groupingBy(DictData::getDictType)).entrySet().stream().forEach((r) -> {
            String key = r.getKey();
            List<DictData> dictDataList = r.getValue();
            List list = new ArrayList<>();
            for (DictData dictData : dictDataList) {
                for (Integer integer : mallRecyclingOrder.getAttachmentList()) {
                    if (integer.equals(dictData.getDictCode().intValue())) {
                        list.add(Integer.valueOf(dictData.getDictValue()));
                    }
                }
            }
            map.put(key, list);
        });

        if (ObjectUtils.isNotEmpty(mallRecyclingOrder.getPurchaseId())) {
            BillPurchase billPurchase = billPurchaseMapper.selectById(mallRecyclingOrder.getPurchaseId());
            if (ObjectUtils.isNotEmpty(billPurchase)) {
                result.setPurchaseSerialNo(billPurchase.getSerialNo());
            }
        }
        result.setCreatedBy("二手表小程序");
        result.setAttachmentMap(map);
        result.setCounselSource("稀蜴商城");

        result.setLineState(mallRecyclingOrder.getState().getDesc());
        result.setState(mallRecyclingOrder.getState().getRemark());

        return result;
    }

    /**
     * 客户付款
     *
     * @param request
     * @return
     */
    @Override
    public RecycleOrderPayResult clientPay(MarkektRecyclePayRequest request) {

        MallRecyclingOrder mallRecyclingOrder = recycleDomain.asserter.assertRecycle(request.getRecycleId());
        MallRecyclingOrder r = new MallRecyclingOrder();
        r.setId(request.getRecycleId());
        r.setSymbol(0);
        recycleOrderService.updateById(r);

        return RecycleOrderPayResult.builder().serialNo(mallRecyclingOrder.getSerial()).recycleId(mallRecyclingOrder.getId()).build();
    }

    @Override
    public MarkektRecycleGetSaleProcessResult getStartSaleProcess(MarkektRecycleGetSaleProcessRequest request) {

        MarkektRecycleGetSaleProcessResult.MarkektRecycleGetSaleProcessResultBuilder builder = MarkektRecycleGetSaleProcessResult.builder().recycleId(request.getRecycleId());

        MallRecyclingOrder mallRecyclingOrder = recycleOrderService.getById(request.getRecycleId());

        BillSaleOrder billSaleOrder = billSaleOrderMapper.selectById(mallRecyclingOrder.getSaleId());

        if (ObjectUtils.isNotEmpty(billSaleOrder)) {

            Tag tag = tagService.selectByStoreManagementId(billSaleOrder.getShopId());

            List<BillSaleOrderLine> billSaleOrderLineList = billSaleOrderLineService.list(Wrappers.<BillSaleOrderLine>lambdaQuery().eq(BillSaleOrderLine::getSaleId, billSaleOrder.getId()));

            if (Objects.nonNull(tag) && CollectionUtils.isNotEmpty(billSaleOrderLineList)) {
                List<Stock> stockList = stockService.listByIds(billSaleOrderLineList.stream()
                        .map(BillSaleOrderLine::getStockId)
                        .collect(Collectors.toList()));

                Map<Integer, List<Stock>> collect = stockList.stream().collect(Collectors.groupingBy(Stock::getLocationId));

                List<BillStoreWorkPre> billStoreWorkPreList = billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaQuery().eq(BillStoreWorkPre::getOriginSerialNo, billSaleOrder.getSerialNo()));

                List<SaleLoadRequest.SaleOrderDTO> saleOrderDTOList = collect.entrySet().stream().map(entry -> {

                    SaleLoadRequest.SaleOrderDTO.SaleOrderDTOBuilder saleOrderDTOBuilder = SaleLoadRequest.SaleOrderDTO.builder()
                            .serialNo(billSaleOrder.getSerialNo())
                            .shortcodes(tagService.selectByStoreManagementId(billSaleOrder.getDeliveryLocationId()).getShortcodes())
                            .deliveryLocationId(billSaleOrder.getDeliveryLocationId());

                    List<SaleLoadRequest.StoreWorkDTO> storeWorkDTOList = new ArrayList<>();

                    for (Stock stock : entry.getValue()) {
                        BillStoreWorkPre billStoreWorkPre = billStoreWorkPreList.stream().filter(e -> stock.getId().equals(e.getStockId())).findAny().orElse(null);
                        if (ObjectUtils.isEmpty(billStoreWorkPre)) {
                            continue;
                        }
                        storeWorkDTOList.add(SaleLoadRequest.StoreWorkDTO.builder().serialNo(billStoreWorkPre.getSerialNo()).stockId(stock.getId()).build());
                    }
                    saleOrderDTOBuilder.storeWorkList(storeWorkDTOList);
                    return saleOrderDTOBuilder.build();
                }).collect(Collectors.toList());

                builder.saleLoadRequest(SaleLoadRequest.builder()
                        .saleProcess("toCSale")
                        .shopId(billSaleOrder.getShopId())
                        .createShortcodes(tag.getShortcodes())
                        .saleConfirm(Boolean.FALSE)
                        .owner("")
                        .orders(saleOrderDTOList)
                        .build());
            }
        }

        return builder.build();
    }

    @Override
    public Boolean buyBackExits(MarketRecycleOrderRequest request) {

        return recycleDomain.asserter.buyBackExits(request.getStockId(), request.getSerialNo(), request.getPhone());
    }


//    @Override
//    public RecycleRefuseReturnResult refuseReturn(RecycleRefuseReturnRequest request) {
//        MallRecyclingOrder mallRecyclingOrder = recycleOrderService.getById(request.getId());
//
//        MallRecyclingOrder r = new MallRecyclingOrder();
//        r.setId(request.getId());
//        r.setDeliveryExpressNumber(request.getDeliveryExpressNumber());
//        r.setTransitionStateEnum(mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.RECYCLE ? RecycleStateEnum.TransitionEnum.CUSTOMER_RECEIVE_SURE_WAIT_LOGISTICS_REFUSE : RecycleStateEnum.TransitionEnum.CUSTOMER_RECEIVE_WAIT_LOGISTICS_REFUSE);
//
//        recycleOrderService.updateRecycleStatus(r);
//
//        return RecycleRefuseReturnResult.builder().id(request.getId()).build();
//    }

    @Override
    public RecycleOrderClientCancelResult clientCancel(RecycleOrderClientCancelRequest request) {

        MallRecyclingOrder r = new MallRecyclingOrder();
        r.setId(request.getId());
        r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.UN_CONFIRMED_CANCEL_WHOLE);
        try {
            recycleOrderService.updateRecycleStatus(r);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            return RecycleOrderClientCancelResult.builder().id(r.getId()).msg("不允许取消").build();
        }
        MallRecyclingOrder mallRecyclingOrder = recycleOrderService.getById(request.getId());

        return RecycleOrderClientCancelResult.builder().id(r.getId()).msg("取消成功").serialNo(mallRecyclingOrder.getSerial()).build();
    }

    @Override
    public RecycleOrderFirstOfferResult firstOffer(RecycleOrderFirstOfferRequest request) {

        //字典处理
        List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));
        //封装附件id
        List<Integer> convert = convert(dataList, request.getAttachmentMap());
        request.setAttachmentList(convert);

        MallRecyclingOrder r = new MallRecyclingOrder();
        r.setId(request.getId());
        r.setGoodsId(request.getGoodsId());
        r.setFiness(request.getFiness());
        r.setStockSn(request.getStockSn());
        r.setStrapMaterial(request.getStrapMaterial());
        r.setWatchSection(request.getWatchSection());
        r.setWatchSize(request.getWatchSize());
        r.setAttachmentList(convert);
        r.setValuationPrice(StringUtils.join(Arrays.asList(request.getRecyclePrice(), request.getReplacementPrice()), ","));
        r.setValuationPriceId(UserContext.getUser().getId());
        r.setValuationImage(request.getValuationImage());
        r.setValuationRemark(request.getValuationRemark());
        r.setValuationTime(new Date());
        r.setIsCard(request.getIsCard());
        r.setWarrantyDate(request.getWarrantyDate());
        r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.UN_CONFIRMED_CUSTOMER_ACCEPT);
        recycleOrderService.updateRecycleStatus(r);

        return RecycleOrderFirstOfferResult.builder().id(request.getId()).build();
    }

    @Override
    public RecycleOrderSecondOfferResult secondOffer(RecycleOrderSecondOfferRequest request) {
        //字典处理
        List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));
        //封装附件id
        List<Integer> convert = convert(dataList, request.getAttachmentMap());
        request.setAttachmentList(convert);

        MallRecyclingOrder r = new MallRecyclingOrder();
        r.setId(request.getId());
        r.setGoodsId(request.getGoodsId());
        r.setFiness(request.getFiness());
        r.setStockSn(request.getStockSn());
        r.setStrapMaterial(request.getStrapMaterial());
        r.setWatchSection(request.getWatchSection());
        r.setWatchSize(request.getWatchSize());
        r.setAttachmentList(convert);
        r.setValuationPriceTwo(StringUtils.join(Arrays.asList(request.getRecyclePrice(), request.getReplacementPrice()), ","));
        r.setValuationPriceTwoId(UserContext.getUser().getId());
        r.setValuationImageTwo(request.getValuationImage());
        r.setValuationPriceTwoRemark(request.getValuationRemark());
        r.setValuationTimeTwo(new Date());
        r.setIsCard(request.getIsCard());
        r.setWarrantyDate(request.getWarrantyDate());
        r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.WAY_OFFER_CUSTOMER_RECEIVE_SURE);
        recycleOrderService.updateRecycleStatus(r);

        return RecycleOrderSecondOfferResult.builder().id(request.getId()).build();
    }

    @Override
    public MallRecycleOrderDetailResult mallRecycleDetail(RecycleOrderDetailsRequest request) {
        MallRecyclingOrder mallRecyclingOrder = recycleDomain.asserter.assertRecycle(request.getId());

        MallRecycleOrderDetailResult mallRecycleOrderDetailResult = MallRecycleConverter.INSTANCE.mallDetails(details(request));
        if (ObjectUtils.isEmpty(mallRecyclingOrder.getGoodsId())) {
            Brand brand = brandService.getById(mallRecyclingOrder.getBrandId());
            if (ObjectUtils.isNotEmpty(brand)) {
                mallRecycleOrderDetailResult.setBrandName(brand.getName());
            }
        } else {
            List<WatchDataFusion> list = goodsWatchService.getWatchDataFusionListByGoodsIds(Arrays.asList(mallRecyclingOrder.getGoodsId()));

            if (CollectionUtils.isNotEmpty(list)) {
                WatchDataFusion watchDataFusion = list.get(0);
                mallRecycleOrderDetailResult.setBrandName(watchDataFusion.getBrandName());
                mallRecycleOrderDetailResult.setSeriesName(watchDataFusion.getSeriesName());
                mallRecycleOrderDetailResult.setModel(watchDataFusion.getModel());
                mallRecycleOrderDetailResult.setPricePub(watchDataFusion.getPricePub());
                mallRecycleOrderDetailResult.setWatchSize(watchDataFusion.getWatchSize());
                mallRecycleOrderDetailResult.setSeriesId(watchDataFusion.getSeriesId());
            }
        }
        //查询用户信息
        User user = recycleDomain.user(mallRecyclingOrder.getEmployeeId());
        mallRecycleOrderDetailResult.setQwId(Objects.nonNull(user) ? user.getUserid() : null);
        mallRecycleOrderDetailResult.setBuyBackForLineResult(detailLine(new RecycleOrderVerifyRequest().setId(request.getId())))
                .setSaleOrderDetailLineResult(saleDetailLine(new RecycleOrderVerifyRequest().setId(request.getId())))
                .setExpressResult(expressResult(mallRecyclingOrder));
        //把预计维修员塞到里面。
        //mallRecycleOrderDetailResult.setMaintenanceMasterName()
        return mallRecycleOrderDetailResult;
    }

    @Override
    public RecycleReplaceUserResult replaceUser(RecycleReplaceUserRequest request) {

        MallRecyclingOrder mallRecyclingOrder = recycleDomain.asserter.assertRecycle(request.getId());
        //原客户经理
        User fromUser = recycleDomain.user(mallRecyclingOrder.getEmployeeId());
        //
        User user = recycleDomain.user(request.getUserId());
        MallRecyclingOrder recyclingOrder = new MallRecyclingOrder();
        recyclingOrder.setId(request.getId());
        recyclingOrder.setDemandId(request.getDemandId());
        recyclingOrder.setEmployeeId(request.getUserId());
        recycleOrderService.updateById(recyclingOrder);

        return RecycleReplaceUserResult.builder().id(request.getId()).fromUserId(fromUser.getUserid()).userId(user.getUserid()).build();
    }


    @Override
    public List<MarkektRecycleGetSaleProcessResult> intercept(Integer purchaseId) {

        return recycleOrderService.intercept(purchaseId).stream().map(id -> getStartSaleProcess(MarkektRecycleGetSaleProcessRequest.builder().recycleId(id).build())).collect(Collectors.toList());
    }

    @Override
    public Boolean protocolSync(ProtocolSyncRequest request) {
        MallRecyclingOrder recyclingOrder = recycleDomain.asserter.assertRecycle(request.getRecycleId());
        if(recyclingOrder.getPurchaseId() !=null){
            BillPurchase purchase = recycleDomain.asserter.purchase(recyclingOrder.getPurchaseId());
            if(StringUtils.isNotEmpty(purchase.getApplyPaymentSerialNo())){
                ApplyFinancialPayment applyFinancialPayment = recycleDomain.asserter.queryApplyFinancialPayment(purchase.getApplyPaymentSerialNo());
                recycleDomain.applyFinancialPayment(request,applyFinancialPayment);
            }
            if(Objects.nonNull(purchase)){
                recycleDomain.purchaseUpdateAgreement(purchase.getId(),request.getProtocol(),purchase.getIsSettlement());
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 字典list
     *
     * @param dataList
     * @param map
     * @return
     */
    private List<Integer> convert(List<DictData> dataList, Map<String, List<Integer>> map) {
        if (Objects.isNull(map) || map.isEmpty()) {
            return Arrays.asList();
        }
        List<Integer> list = new ArrayList<Integer>();
        Map<String, List<DictData>> collect = dataList.stream().collect(Collectors.groupingBy(DictData::getDictType));
        map.forEach((k, v) -> {
            List<DictData> dictDataList = collect.get(k);
            if (ObjectUtils.isNotEmpty(v)) {
                v.forEach(item -> {
                    DictData data = dictDataList.stream().filter(dictData -> (item == Integer.parseInt(dictData.getDictValue()))).findAny().orElse(null);
                    if (ObjectUtils.isNotEmpty(data)) {
                        list.add(data.getDictCode().intValue());
                    }
                });
            }
        });
        return list;
    }

    private String convert(List<DictData> dataList, List<Integer> itemList, Integer isCard, String warrantyDate,Integer stockId) {
        String card = "空白保卡";
        String cardDate = "保卡(date)";
        if (Objects.nonNull(stockId)) {
            Integer seriesType = seriesService.getSeriesTypeByStockId(stockId);
            if (Objects.nonNull(seriesType) && SeriesTypeEnum.BAGS.getValue().equals(seriesType)) {
                card = "空白身份卡";
                cardDate = "身份卡(date)";
            }
        }

        String join = ObjectUtils.isEmpty(isCard) ? StringUtils.EMPTY : (isCard.equals(1) ?
                StringUtils.replace(cardDate, "date", warrantyDate) : isCard.equals(0) ? StringUtils.EMPTY : card);

        String attachment = StringUtils.EMPTY;

        if (CollectionUtils.isNotEmpty(dataList)) {

            List<String> collect = null;
            if (CollectionUtils.isNotEmpty(itemList)) {
                collect = itemList.stream().flatMap(item -> dataList.stream().filter(dictData -> item.intValue() == (dictData.getDictCode().intValue()))).map(dictData -> dictData.getDictLabel()).collect(Collectors.toList());
            }
            attachment = CollectionUtils.isEmpty(itemList) ? StringUtils.EMPTY + (ObjectUtils.isEmpty(join) ? StringUtils.EMPTY : join) : StringUtils.join(collect, "/") + (ObjectUtils.isEmpty(join) ? StringUtils.EMPTY : "/" + join);

        } else {
            return join;
        }

        return attachment;
    }
}
