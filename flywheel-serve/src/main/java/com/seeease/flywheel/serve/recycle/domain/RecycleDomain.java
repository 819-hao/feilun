package com.seeease.flywheel.serve.recycle.domain;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.customer.request.CustomerCreateRequest;
import com.seeease.flywheel.financial.IApplyFinancialPaymentFacade;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentAppletCreateRequest;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentCreateResult;
import com.seeease.flywheel.maindata.entity.UserInfo;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;
import com.seeease.flywheel.recycle.request.MarkektRecycleUserBankRequest;
import com.seeease.flywheel.recycle.request.ProtocolSyncRequest;
import com.seeease.flywheel.recycle.request.ReplacementLineRequest;
import com.seeease.flywheel.recycle.request.ReplacementOrRecycleCreateRequest;
import com.seeease.flywheel.recycle.result.BuyBackForLineResult;
import com.seeease.flywheel.recycle.result.BuyBackForSaleResult;
import com.seeease.flywheel.recycle.result.SaleOrderDetailLineResult;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.request.SaleOrderDetailsRequest;
import com.seeease.flywheel.sale.result.SaleOrderCreateResult;
import com.seeease.flywheel.sale.result.SaleOrderDetailsResult;
import com.seeease.flywheel.serve.base.BigDecimalUtil;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.convert.UserConverter;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchasePaymentMethodEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseTypeEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.recycle.convert.MallRecycleConverter;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import com.seeease.flywheel.serve.recycle.enums.RecycleOrderTypeEnum;
import com.seeease.flywheel.serve.recycle.enums.RecycleStateEnum;
import com.seeease.flywheel.serve.recycle.service.IRecycleOrderService;
import com.seeease.flywheel.serve.sale.convert.SaleOrderConverter;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper;
import com.seeease.flywheel.serve.sale.enums.*;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.Tuple2;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Auther Gilbert
 * @Date 2023/9/4 11:14
 */
@Service
public class RecycleDomain {
    static String ossPath = "https://seeease.oss-cn-hangzhou.aliyuncs.com/mall/upload/";
    public Asserter asserter = new Asserter();
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private UserService userService;
    @Resource
    private IRecycleOrderService recycleOrderService;
    @Resource
    private BillSaleOrderService billSaleOrderService;
    @Resource
    private BillPurchaseLineService billPurchaseLineService;
    @Resource
    private BillPurchaseService billPurchaseService;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private TagService tagService;
    @Resource
    private IApplyFinancialPaymentFacade iApplyFinancialPaymentFacade;
    @Resource
    private IPurchaseFacade iPurchaseFacade;
    @Resource
    private ISaleOrderFacade iSaleOrderFacade;
    @Resource
    private BillFixService billFixService;
    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;
    @Resource
    private StockMapper stockMapper;

    /**
     * 断言
     */
    public class Asserter {
        /**
         * 断言回购回收单
         *
         * @param id
         * @return
         */
        public MallRecyclingOrder assertRecycle(@NonNull Integer id) {
            MallRecyclingOrder mallRecyclingOrder = recycleOrderService.getById(id);
            Optional.ofNullable(mallRecyclingOrder).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.BUY_BACK_RECYCLE_EXIST));
            return mallRecyclingOrder;
        }

        public MallRecyclingOrder assertRecycleByPurchaseId(@NonNull Integer purchaseId) {
            LambdaQueryWrapper<MallRecyclingOrder> recyclingLambdaQueryWrapper = new LambdaQueryWrapper<>();
            recyclingLambdaQueryWrapper.eq(MallRecyclingOrder::getPurchaseId, purchaseId)
                    .eq(MallRecyclingOrder::getDeleted, Boolean.FALSE);
            MallRecyclingOrder one = recycleOrderService.getOne(recyclingLambdaQueryWrapper);
            return one;
        }


        /**
         * 断言销售单
         *
         * @param serialNo
         * @return
         */
        public BillSaleOrder assertBillSaleOrder(@NonNull String serialNo) {
            BillSaleOrder billSaleOrder = billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery()
                    .eq(BillSaleOrder::getSerialNo, serialNo)
                    .or()
                    .eq(BillSaleOrder::getBizOrderCode, serialNo));
            Optional.ofNullable(billSaleOrder).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.SALE_EXIST));
            return billSaleOrder;
        }

        /**
         * 根据销售单id查询销售单信息
         *
         * @param id
         * @return
         */
        public BillSaleOrder assertBillSaleOrderById(@NonNull Integer id) {
            BillSaleOrder billSaleOrder = billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery()
                    .eq(BillSaleOrder::getId, id));
            Optional.ofNullable(billSaleOrder).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.SALE_EXIST));
            return billSaleOrder;
        }

        /**
         * 断言表是否存在回购
         */
        public Boolean buyBackExits(@NonNull Integer stockId, @NonNull String serialNo, String phone) {
            //查询表是否存在回购
            List<BillPurchaseLine> purchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery()
                    .eq(BillPurchaseLine::getOriginStockId, stockId));

            BillSaleOrder billSaleOrder = billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery()
                    .eq(BillSaleOrder::getSerialNo, serialNo)
                    .or()
                    .eq(BillSaleOrder::getBizOrderCode, serialNo)
                    .ne(BillSaleOrder::getSaleState, SaleOrderStateEnum.CANCEL_WHOLE)
            );
            if (Objects.isNull(billSaleOrder)) {
                return Boolean.TRUE;
            }
            //销售单行好是否存在是否可以做回购政策
            BillSaleOrderLine billSaleOrderLine = billSaleOrderLineService.getOne(Wrappers.<BillSaleOrderLine>lambdaQuery()
                    .eq(BillSaleOrderLine::getSaleId, billSaleOrder.getId())
                    .eq(BillSaleOrderLine::getStockId, stockId)
                    .eq(BillSaleOrderLine::getIsCounterPurchase, 1)
                    .eq(BillSaleOrderLine::getIsRepurchasePolicy, 1)
            );
            if (Objects.isNull(billSaleOrderLine)) {
                return Boolean.TRUE;
            }
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
            BigDecimal referenceBuyBackDiscount = builderIn.build().get(DateUtil.parse(DateUtil.today()).toJdkDate());
            if (referenceBuyBackDiscount == null) {
                return Boolean.TRUE;
            }
            //建单时间 参考回购价 置换
            BigDecimal referenceBuyBackInPrice = BigDecimalUtil.multiplyRoundHalfUp(clinchPrice,
                    //值点
                    referenceBuyBackDiscount.divide(BigDecimal.valueOf(FlywheelConstant.MULTIPLIER_10), 4, BigDecimal.ROUND_HALF_UP));

            //建单时间 参考回购折扣 回收
            BigDecimal referenceBuyBackRecycleDiscount = builderRecycle.build().get(DateUtil.parse(DateUtil.today()).toJdkDate());
            if (referenceBuyBackRecycleDiscount == null) {
                return Boolean.TRUE;
            }
            //建单时间 参考回购价 回收
            BigDecimal referenceBuyBackRecyclePrice = BigDecimalUtil.multiplyRoundHalfUp(clinchPrice,
                    //值点
                    referenceBuyBackRecycleDiscount.divide(BigDecimal.valueOf(FlywheelConstant.MULTIPLIER_10), 4, BigDecimal.ROUND_HALF_UP));

            if (ObjectUtils.isEmpty(referenceBuyBackInPrice) || ObjectUtils.isEmpty(referenceBuyBackRecyclePrice)
                    || referenceBuyBackInPrice.compareTo(BigDecimal.ZERO) <= 0 || referenceBuyBackRecyclePrice.compareTo(BigDecimal.ZERO) <= 0) {
                return Boolean.TRUE;
            }

            //是否存在相同的回购单
            if (CollectionUtils.isNotEmpty(purchaseLineList)) {
                List<BillPurchase> collect = billPurchaseService.listByIds(purchaseLineList.stream().map(BillPurchaseLine::getPurchaseId).collect(Collectors.toList()))
                        .stream().filter(billPurchase -> serialNo.equals(billPurchase.getOriginSaleSerialNo()) && !billPurchase.getPurchaseState().equals(BusinessBillStateEnum.CANCEL_WHOLE)).collect(Collectors.toList());

                if (!collect.isEmpty()) {
                    return Boolean.TRUE;
                }
            }
            //判断是否可以申请回购。流程中不可以在进行申请回购
            List<CustomerContacts> customerContacts = customerContactsService.searchByNameOrPhone(null, phone);
            if (CollectionUtil.isNotEmpty(customerContacts)) {
                //如果存在,返回客户主键id
                LambdaQueryWrapper<MallRecyclingOrder> recyclingLambdaQueryWrapper = new LambdaQueryWrapper<>();
                recyclingLambdaQueryWrapper.eq(MallRecyclingOrder::getCustomerContactId, customerContacts.get(0).getId())
                        .eq(MallRecyclingOrder::getSaleSerialNo, serialNo)
                        .notIn(MallRecyclingOrder::getState, RecycleStateEnum.COMPLETE.getValue(), RecycleStateEnum.CANCEL_WHOLE.getValue());
                MallRecyclingOrder one = recycleOrderService.getOne(recyclingLambdaQueryWrapper);
                if (Objects.nonNull(one)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }

        /**
         * 判断销售行是否存在
         *
         * @param saleId
         * @param stockId
         * @return
         */
        public BillSaleOrderLine saleOrderLine(@NonNull Integer saleId, @NonNull Integer stockId) {
            //2。销售单行好是否存在
            BillSaleOrderLine billSaleOrderLine = billSaleOrderLineService.getOne(Wrappers.<BillSaleOrderLine>lambdaQuery()
                    .eq(BillSaleOrderLine::getSaleId, saleId)
                    .eq(BillSaleOrderLine::getStockId, stockId)
                    .eq(BillSaleOrderLine::getIsCounterPurchase, 1)
                    .eq(BillSaleOrderLine::getIsRepurchasePolicy, 1)
            );

            Optional.ofNullable(billSaleOrderLine).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER));
            return billSaleOrderLine;
        }

        /**
         * 商品表的基本数据信息
         *
         * @param stockId
         */
        public WatchDataFusion watchDataFusion(@NonNull Integer stockId) {
            //基本信息
            List<WatchDataFusion> watchDataFusionList = goodsWatchService.getWatchDataFusionListByStockIds(Arrays.asList(stockId));
            Assert.notEmpty(watchDataFusionList, "基本信息为空");
            WatchDataFusion watchDataFusion = watchDataFusionList.get(0);
            return watchDataFusion;
        }

        /**
         * 查询采购信息
         *
         * @param purchaseId
         * @return
         */
        public BillPurchase purchase(@NonNull Integer purchaseId) {
            LambdaQueryWrapper<BillPurchase> billPurchaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            billPurchaseLambdaQueryWrapper.eq(BillPurchase::getId, purchaseId);
            BillPurchase one = billPurchaseService.getOne(billPurchaseLambdaQueryWrapper);
            Optional.ofNullable(one).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.PURCHASE_NO_EXIST));
            return one;
        }

        /**
         * 查询维修单信息
         */
        public BillFix queryBillFix(@NonNull String purchaseCode) {
            LambdaQueryWrapper<BillFix> billFixLambdaQueryWrapper = new LambdaQueryWrapper<>();
            billFixLambdaQueryWrapper.eq(BillFix::getOriginSerialNo, purchaseCode)
                    .last("limit 1");
            return billFixService.getOne(billFixLambdaQueryWrapper);
        }

        /**
         * 查询申请打款表
         */
        public ApplyFinancialPayment queryApplyFinancialPayment(@NonNull String serialNo) {
            LambdaQueryWrapper<ApplyFinancialPayment> applyFinancialPaymentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            applyFinancialPaymentLambdaQueryWrapper.eq(ApplyFinancialPayment::getSerialNo, serialNo);
            ApplyFinancialPayment one = applyFinancialPaymentService.getOne(applyFinancialPaymentLambdaQueryWrapper);
            Optional.ofNullable(one).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.PAYMENT_MATERIAL_NON_NULL));
            return one;
        }
    }

    public Tuple2<Integer, Integer> customerCreateOrQuery(String customerName, @NonNull String customerPhone) {
        //查询客户姓名和手机号再系统内存不存在
        List<CustomerContacts> customerContacts = customerContactsService.searchByNameOrPhone(customerName, customerPhone);
        if (CollectionUtil.isNotEmpty(customerContacts)) {
            //如果存在,返回客户主键id
            return Tuple2.of(customerContacts.get(0).getCustomerId(), customerContacts.get(0).getId());
        }
        //不存在进行新增
        int customerId = customerService.create(new CustomerCreateRequest()
                .setType(CustomerTypeEnum.INDIVIDUAL.getValue())
                .setPhone(customerPhone));

        //新增明细表
        Integer customerContactId = customerContactsService.create(new CustomerCreateRequest().setName(customerName).setPhone(customerPhone)
                , customerId);
        return Tuple2.of(customerId, customerContactId);
    }

    public Map<String, UserInfo> queryUser(String employeeId) {
        //查询用户信息
        List<UserInfo> collect1 = userService.list(Wrappers.<User>lambdaQuery()
                        .in(User::getUserid, Arrays.asList(employeeId)))
                .stream()
                .map(UserConverter.INSTANCE::convertUserInfo)
                .collect(Collectors.toList());

        return collect1.stream()
                .collect(Collectors.toMap(UserInfo::getUserid, Function.identity(), (k1, k2) -> k2));
    }

    //查询客户信息
    public CustomerContacts customerContacts(@NonNull Integer customerContactId) {
        //查询客户信息
        return customerContactsService.queryCustemerContactByCustomerId(customerContactId);
    }

    //查询用户信息
    public User user(@NonNull Integer id) {
        return userService.getById(id);
    }

    public User user(@NonNull String id) {
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUserid, id));
    }

    public BuyBackForSaleResult buyBackForSaleResult(MallRecyclingOrder mallRecyclingOrder) {
        //查询客户信息
        CustomerContacts customerContacts = customerContacts(mallRecyclingOrder.getCustomerContactId());

        //查询银行卡信息信息
        Customer customer = customerService.queryCustomerById(mallRecyclingOrder.getCustomerId());
        //查询需求门店信息
        Tag tag = tagService.selectByStoreManagementId(mallRecyclingOrder.getDemandId());
        //查询用户信息
        User user = user(mallRecyclingOrder.getEmployeeId());
        BillSaleOrder billSaleOrder = null;
        //查询原销售价格
        if (StringUtils.isNotEmpty(mallRecyclingOrder.getSaleSerialNo())) {
            billSaleOrder = asserter.assertBillSaleOrder(mallRecyclingOrder.getSaleSerialNo());
        }
        String maintenanceMasterName = null;

        //沙雕需求。关联4张表进行拿一个维修员的名称，艹！！！！！！！！！
        if (mallRecyclingOrder.getPurchaseId() != null) {
            BillPurchase purchase = asserter.purchase(mallRecyclingOrder.getPurchaseId());
            //获取采购的code进行查询维修单
            BillFix billFix = asserter.queryBillFix(purchase.getSerialNo());
            if (Objects.nonNull(billFix) && billFix.getMaintenanceMasterId() != null) {
                User userMaintenance = user(billFix.getMaintenanceMasterId());
                maintenanceMasterName = userMaintenance.getName();
            }
        }
        //查询图片
        List<StockExt> stockExts = stockMapper.selectByStockIdList(Arrays.asList(mallRecyclingOrder.getStockId()));
        //最终返回的数据
        return new BuyBackForSaleResult()
                .setId(mallRecyclingOrder.getId())
                .setStockId(mallRecyclingOrder.getStockId())
                .setRecycleType(mallRecyclingOrder.getRecycleType().getValue())
                .setCustomerId(mallRecyclingOrder.getCustomerId())
                .setCustomerContactId(mallRecyclingOrder.getCustomerContactId())
                .setCustomerName(Objects.nonNull(customerContacts) ? customerContacts.getName() : null)
                .setCustomerPhone(Objects.nonNull(customerContacts) ? customerContacts.getPhone() : null)
                .setType(mallRecyclingOrder.getType() != null ? mallRecyclingOrder.getType().getValue() : null)
                .setAddress(Objects.nonNull(customerContacts) ? customerContacts.getAddress() : null)
                .setPayee(Objects.nonNull(customer) ? customer.getCustomerName() : null)
                .setBank(Objects.nonNull(customer) ? customer.getBank() : null)
                .setBankAccount(Objects.nonNull(customer) ? customer.getBankAccount() : null)
                .setAccountName(Objects.nonNull(customer) ? customer.getAccountName() : null)
                .setIdentityCardImage(Objects.nonNull(customer) ? customer.getIdentityCardImage() : null)
                .setShopImage(mallRecyclingOrder.getShopImage())
                .setCreatedTime(mallRecyclingOrder.getCreatedTime())
                .setCreatedBy(mallRecyclingOrder.getCreatedBy())
                .setState(mallRecyclingOrder.getState().getValue())
                .setStatusDesc(mallRecyclingOrder.getState().getDesc())
                .setDemandId(mallRecyclingOrder.getDemandId())
                .setDemandName(Objects.nonNull(tag) ? tag.getTagName() : null)
                .setEmployeeId(mallRecyclingOrder.getEmployeeId())
                .setQwId(Objects.nonNull(user) ? user.getUserid() : null)
                .setOriginalClinchPrice(Objects.nonNull(billSaleOrder) ? billSaleOrder.getTotalSalePrice() : null)
                .setSymbol(mallRecyclingOrder.getSymbol())
                .setBalance(mallRecyclingOrder.getBalance())
                .setMaintenanceMasterName(maintenanceMasterName)
                .setEmployeeName(Objects.nonNull(user) ? user.getName() : null)
                .setGoodsImage(CollectionUtil.isNotEmpty(stockExts) ? stockExts.get(FlywheelConstant.INDEX).getImage() : null);
    }

    public BuyBackForLineResult detailLine(WatchDataFusion watchDataFusion,
                                           BillSaleOrderLine billSaleOrderLine,
                                           BillSaleOrder billSaleOrder,
                                           BigDecimal referenceBuyBackRecyclePrice,
                                           BigDecimal referenceBuyBackRecycleDiscount,
                                           BigDecimal referenceBuyBackInPrice,
                                           BigDecimal referenceBuyBackDiscount,
                                           List<Double> DISCOUNT_RANGE) {
        //组装行中表的数据
        return new BuyBackForLineResult()
                .setStockId(watchDataFusion.getStockId())
                .setSerialNo(billSaleOrder.getSerialNo())
                .setOriginStockId(watchDataFusion.getStockId())
                .setStockSn(watchDataFusion.getStockSn())
                .setBrandName(watchDataFusion.getBrandName())
                .setSeriesName(watchDataFusion.getSeriesName())
                .setGoodsId(watchDataFusion.getGoodsId())
                .setModel(watchDataFusion.getModel())
                .setFiness(watchDataFusion.getFiness())
                .setAttachment(watchDataFusion.getAttachment())
                .setPricePub(watchDataFusion.getPricePub())
                .setWno(watchDataFusion.getWno())
                .setStrapMaterial(billSaleOrderLine.getStrapMaterial())
                .setWatchSection(billSaleOrderLine.getWatchSection())
                .setReferenceBuyBackRecyclePrice(referenceBuyBackRecyclePrice)
                .setReferenceBuyBackInPrice(referenceBuyBackInPrice)
                .setWatchbandReplacePrice(billSaleOrderLine.getStrapReplacementPrice())
                .setClinchPrice(billSaleOrderLine.getClinchPrice())
                .setDisplaceDiscountRange(DISCOUNT_RANGE.stream()
                        .filter(t -> t <= referenceBuyBackDiscount.doubleValue())
                        .map(Objects::toString)
                        .collect(Collectors.toList()))
                .setRecycleDiscountRange(DISCOUNT_RANGE.stream()
                        .filter(t -> t <= referenceBuyBackRecycleDiscount.doubleValue())
                        .map(Objects::toString)
                        .collect(Collectors.toList()));
    }

    //创建申请打款单
    public ApplyFinancialPaymentCreateResult applyFinancialPaymentCreate(MallRecyclingOrder mallRecyclingOrder
            , BigDecimal pricePayment
            , Integer purchaseSubjectId
    ) {

        ApplyFinancialPaymentAppletCreateRequest applyFinancialPaymentAppletCreateRequest = ApplyFinancialPaymentAppletCreateRequest.builder()
                .customerContactsId(mallRecyclingOrder.getCustomerContactId())
                .customerName(mallRecyclingOrder.getAccountName())
                .pricePayment(pricePayment)
                .subjectPayment(purchaseSubjectId)
                .frontIdentityCard(mallRecyclingOrder.getFrontIdentityCard())
                .reverseIdentityCard(mallRecyclingOrder.getReverseIdentityCard())
                .bankAccount(mallRecyclingOrder.getBank())
                .bankCard(mallRecyclingOrder.getBankAccount())
                .bankCustomerName(mallRecyclingOrder.getBankCustomerName())
                .demanderStoreId(mallRecyclingOrder.getDemandId())
                .typePayment(mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.RECYCLE ? ApplyFinancialPaymentTypeEnum.PERSONAL_RECYCLING.getValue() : ApplyFinancialPaymentTypeEnum.BUY_BACK.getValue()).
                build();
        return iApplyFinancialPaymentFacade.create(applyFinancialPaymentAppletCreateRequest);
    }

    //创建采购单
    public PurchaseCreateListResult createPurchase(ReplacementOrRecycleCreateRequest request, MallRecyclingOrder mallRecyclingOrder, BillSaleOrder billSaleOrder) {
        //创建采购单 默认转换回收
        PurchaseCreateRequest purchaseCreateRequest = MallRecycleConverter.INSTANCE.convert(request);
        //创建采购单需要这些信息
        purchaseCreateRequest.setBank(mallRecyclingOrder.getBank());
        purchaseCreateRequest.setAccountName(mallRecyclingOrder.getAccountName());
        purchaseCreateRequest.setBankAccount(mallRecyclingOrder.getBankAccount());
        purchaseCreateRequest.setBankCustomerName(mallRecyclingOrder.getBankCustomerName());
        //默认回收
        purchaseCreateRequest.setPurchaseType(PurchaseTypeEnum.GR_HS.getValue());
        //默认仅回收枚举值
        purchaseCreateRequest.setPurchaseMode(PurchaseModeEnum.RECYCLE.getValue());
        if (RecycleOrderTypeEnum.BUY_BACK == RecycleOrderTypeEnum.fromCode(request.getRecycleType())) {
            //回购
            purchaseCreateRequest.setPurchaseType(PurchaseTypeEnum.GR_HG.getValue());
        }
        //仅回收
        if (PurchaseModeEnum.DISPLACE == PurchaseModeEnum.fromCode(request.getType())) {
            //置换枚举值
            purchaseCreateRequest.setPurchaseMode(PurchaseModeEnum.DISPLACE.getValue());
        }

        if (purchaseCreateRequest.getPurchaseType().equals(PurchaseTypeEnum.GR_HG.getValue())) {
            if (purchaseCreateRequest.getPurchaseMode().equals(PurchaseModeEnum.RECYCLE.getValue())) {
                purchaseCreateRequest.setPaymentMethod(PurchasePaymentMethodEnum.FK_QK.getValue());
            }

            if (purchaseCreateRequest.getPurchaseMode().equals(PurchaseModeEnum.DISPLACE.getValue())) {
                purchaseCreateRequest.setPaymentMethod(PurchasePaymentMethodEnum.FK_CE.getValue());
            }
        }

        //门店id
        purchaseCreateRequest.setStoreId(UserContext.getUser().getStore().getId());
        purchaseCreateRequest.setDemanderStoreId(request.getDemandId());
        //用来跳过申请打款单。商城回购是后置打款的 todo
        purchaseCreateRequest.setMallUser(Boolean.FALSE);
        purchaseCreateRequest.setDetails(request.getBillPurchaseLineDtoList());
        purchaseCreateRequest.setOriginSaleSerialNo(mallRecyclingOrder.getSaleSerialNo());
        //置换的时候是先创建销售单在创建采购单
        purchaseCreateRequest.setSaleSerialNo(Objects.nonNull(billSaleOrder) ? billSaleOrder.getSerialNo() : null);

        purchaseCreateRequest.setOriginSaleOrderDetailsResult(Objects.nonNull(mallRecyclingOrder.getSaleSerialNo()) ? SaleOrderConverter.INSTANCE.convertSaleOrderDetailsResult(asserter.assertBillSaleOrder(mallRecyclingOrder.getSaleSerialNo())) : null);

        purchaseCreateRequest.setSaleOrderDetailsResult(Objects.nonNull(billSaleOrder) ? SaleOrderConverter.INSTANCE.convertSaleOrderDetailsResult(asserter.assertBillSaleOrder(billSaleOrder.getSerialNo())) : null);

        purchaseCreateRequest.setApplyFinancialPaymentDetailResult(request.getApplyFinancialPaymentDetailResult());
        purchaseCreateRequest.setApplyPaymentSerialNo(request.getApplyPaymentSerialNo());
//        PurchaseCreateRequest.BillPurchaseLineDto billPurchaseLineDto = purchaseCreateRequest.getDetails().get(0);
//        billPurchaseLineDto.setAttachmentMap();

        PurchaseCreateListResult purchaseCreateListResult = iPurchaseFacade.create(purchaseCreateRequest);
        return purchaseCreateListResult;
    }

    //更新采购单
    public void purchaseUpdate(MarkektRecycleUserBankRequest request, MallRecyclingOrder mallRecyclingOrder, MallRecyclingOrder r) {
        BillPurchase billPurchase = new BillPurchase();
        billPurchase.setAccountName(r.getAccountName());
        billPurchase.setBankAccount(request.getAccount());
        billPurchase.setBank(request.getBankName());
        billPurchase.setBankCustomerName(request.getAccountName());
        //采购单中保存身份证图片
        billPurchase.setFrontIdentityCard(Arrays.asList(ossPath + request.getFrontImg()));
        billPurchase.setReverseIdentityCard(Arrays.asList(ossPath + request.getBackImg()));
//        billPurchase.setApplyPaymentSerialNo(Objects.nonNull(applyFinancialPaymentCreateResul) ? applyFinancialPaymentCreateResul.getSerialNo() : null);
        billPurchase.setId(mallRecyclingOrder.getPurchaseId());
        billPurchase.setIsSettlement(WhetherEnum.YES);
        billPurchaseService.updateById(billPurchase);
    }

    //更新采购单
    public void purchaseUpdateAgreement(Integer purchaseId, String agreementTransfer, WhetherEnum isSettlement) {
        BillPurchase billPurchase = new BillPurchase();
        billPurchase.setId(purchaseId);
        billPurchase.setAgreementTransfer(Arrays.asList(agreementTransfer));
        billPurchase.setIsSettlement(isSettlement);
        billPurchaseService.updateById(billPurchase);
    }

    //更新申请打款单转让协议
    public void applyFinancialPayment(ProtocolSyncRequest request, ApplyFinancialPayment applyFinancialPayment) {
        applyFinancialPaymentService.updateById(ApplyFinancialPayment.builder().id(applyFinancialPayment.getId()).agreementTransfer(request.getProtocol()).build());
    }

    //创建销售单
    public SaleOrderCreateResult saleCreate(MallRecyclingOrder mallRecyclingOrder, ReplacementLineRequest replacementLineDtoList) {
        //商城的数据允许破价进行销售
        replacementLineDtoList.setIsUnderselling(StockUndersellingEnum.ALLOW.getValue());
        //查询客户信息
        CustomerContacts customerContacts = customerContacts(mallRecyclingOrder.getCustomerContactId());
        List<SaleOrderCreateRequest.BillSaleOrderLineDto> billSaleOrderLineDtoList = new ArrayList<>();
        billSaleOrderLineDtoList.add(MallRecycleConverter.INSTANCE.convertSaleOrderLine(replacementLineDtoList));
        SaleOrderCreateRequest request = SaleOrderCreateRequest.builder()
                .saleType(SaleOrderTypeEnum.TO_C_XS.getValue())
                .saleMode(SaleOrderModeEnum.ON_LINE.getValue())
                .saleChannel(SaleOrderChannelEnum.XI_YI_SHOP.getValue())
                .receiverInfo(SaleOrderCreateRequest.ReceiverInfo.builder()
                        .receiverName(customerContacts.getName())
                        .receiverMobile(customerContacts.getPhone())
                        .receiverAddress(customerContacts.getAddress())
                        .build())
                .paymentMethod(SaleOrderPaymentMethodEnum.WECHAT.getValue())
                .owner(user(mallRecyclingOrder.getEmployeeId()).getUserid())
                .firstSalesman(user(mallRecyclingOrder.getEmployeeId()).getId().intValue())
                .creator(SaleOrderCreateRequest.PrescriptiveCreator.builder()
                        .build()
                )
                .shopId(Objects.requireNonNull(mallRecyclingOrder.getDemandId())) //下单门店
                .totalSalePrice(replacementLineDtoList.getClinchPrice())
                .details(billSaleOrderLineDtoList)
                .build();
        //组装创建参数
        SaleOrderCreateResult saleOrderCreateResult = iSaleOrderFacade.create(request);
        saleOrderCreateResult.setTotalSalePrice(request.getTotalSalePrice());
        return saleOrderCreateResult;
    }

    //查询销售行信息
    public SaleOrderDetailLineResult saleOrderDetailLineResult(MallRecyclingOrder mallRecyclingOrder) {
        BillSaleOrder billSaleOrder = asserter.assertBillSaleOrderById(mallRecyclingOrder.getSaleId());
        SaleOrderDetailsResult details = iSaleOrderFacade.details(SaleOrderDetailsRequest.builder()
                .id(billSaleOrder.getId())
                .serialNo(billSaleOrder.getSerialNo())
                .build());
        //判断销售对象不为空
        if (Objects.nonNull(details)) {
            //判断销售行数据不为空
            if (CollectionUtil.isNotEmpty(details.getLines())) {
                SaleOrderDetailsResult.SaleOrderLineVO saleOrderLineVO = details.getLines().get(FlywheelConstant.INDEX);
                saleOrderLineVO.setSerialNo(billSaleOrder.getSerialNo());
                //进行对象转换
                return MallRecycleConverter.INSTANCE.convertSaleLineResult(saleOrderLineVO);
            }
        }
        return null;
    }

}
