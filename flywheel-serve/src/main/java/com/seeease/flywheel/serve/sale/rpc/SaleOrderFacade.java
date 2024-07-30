package com.seeease.flywheel.serve.sale.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.allocate.result.AllocateStockQueryImportResult;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.*;
import com.seeease.flywheel.sale.result.*;
import com.seeease.flywheel.serve.base.*;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceCmdTypeEnum;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceTypeEnum;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.entity.FinancialGenerateDto;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.*;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import com.seeease.flywheel.serve.recycle.service.IRecycleOrderService;
import com.seeease.flywheel.serve.sale.convert.SaleOrderConverter;
import com.seeease.flywheel.serve.sale.convert.SaleReturnOrderConverter;
import com.seeease.flywheel.serve.sale.entity.*;
import com.seeease.flywheel.serve.sale.enums.*;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.sale.service.RcSaleDeliveryVideoService;
import com.seeease.flywheel.serve.sale.strategy.SaleOrderContext;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCollect;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.flywheel.serve.storework.enums.WmsWorkCollectWorkStateEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.serve.storework.service.WmsWorkCollectService;
import com.seeease.flywheel.storework.request.StoreWorKCreateRequest;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/3/6
 */
@Slf4j
@DubboService(version = "1.0.0")
public class SaleOrderFacade implements ISaleOrderFacade {
    @Resource
    private BillSaleOrderService billSaleOrderService;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;
    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    private TagService tagService;
    @Resource
    private PurchaseSubjectService subjectService;
    @Resource
    private SaleOrderContext saleOrderContext;
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private UserService userService;
    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    private StockService stockService;
    @Resource
    private FinancialDocumentsService financialService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private WmsWorkCollectService wmsWorkCollectService;
    @Resource
    private RcSaleDeliveryVideoService rcSaleDeliveryVideoService;
    @Resource
    private IRecycleOrderService recycleOrderService;
    @Resource
    private CustomerBalanceService customerBalanceService;

    @NacosValue(value = "${saleOrder.roleNames}", autoRefreshed = true)
    private List<String> ROLE_NAMES;
    @NacosValue(value = "${saleOrder.ipRoleName}", autoRefreshed = true)
    private List<String> IP_ROLE_NAMES;
    @NacosValue(value = "${saleOrder.ipShopId}", autoRefreshed = true)
    private List<Integer> IP_SHOP_ID;
    @NacosValue(value = "${saleOrder.sjzShopId}", autoRefreshed = true)
    private List<String> SJZ_ROLE_NAMES;

    @NacosValue(value = "${saleOrder.printShopId:-1}", autoRefreshed = true)
    private List<Integer> PRINT_SHOP_ID;

    @Override
    public SaleOrderCreateResult create(SaleOrderCreateRequest request) {
        SaleOrderCreateResult result = saleOrderContext.create(request);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrderCancelResult cancel(SaleOrderCancelRequest request) {
        //客户上传了打款信息。不可以进行取消
        LambdaQueryWrapper<MallRecyclingOrder> mallRecyclingOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        mallRecyclingOrderLambdaQueryWrapper.eq(MallRecyclingOrder::getSaleId, request.getId());
        //查销售单
        MallRecyclingOrder recyclingOrder = recycleOrderService.getOne(mallRecyclingOrderLambdaQueryWrapper);
        if (Objects.nonNull(recyclingOrder)) {
            throw new OperationRejectedException(OperationExceptionCode.MALL_BUY_BACK_SALE_CANCEL);
        }
        BillSaleOrder order = Optional.ofNullable(request.getId())
                .filter(id -> id > 0)
                .map(billSaleOrderService::getById)
                .orElseGet(() -> billSaleOrderService.getOne(new LambdaQueryWrapper<BillSaleOrder>()
                        .eq(BillSaleOrder::getSerialNo, request.getSerialNo())
                        .or().eq(BillSaleOrder::getBizOrderCode, request.getBizOrderCode())));

        if (Objects.isNull(order)) {
            throw new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE);
        }

        List<BillSaleOrderLine> orderLines = billSaleOrderLineService.list(Wrappers.<BillSaleOrderLine>lambdaQuery()
                .eq(BillSaleOrderLine::getSaleId, order.getId()));

        if (!orderLines.stream().allMatch(a -> FinancialInvoiceStateEnum.NO_INVOICED.equals(a.getWhetherInvoice()))) {
            throw new BusinessException(ExceptionCode.FINANCIAL_INVOICE_STATE_NOT_ALL_NO_INVOICED);
        }

        List<Integer> stockIds = orderLines.stream()
                .map(BillSaleOrderLine::getStockId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //销售取消  预收单自动核销 状态：已核销
        List<AccountsPayableAccounting> list = accountingService
                .selectListByOriginSerialNoAndStatusAndType(order.getSerialNo(),
                        Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.RETURN_PENDING_REVIEW)
                        , Lists.newArrayList(ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT));
        accountingService.batchAudit(list.stream().filter(a -> stockIds.contains(a.getStockId()))
                .map(AccountsPayableAccounting::getId)
                .collect(Collectors.toList()), FlywheelConstant.CANCEL_ORDER_AUDIT, UserContext.getUser().getUserName());


        SaleOrderLineStateEnum.TransitionEnum transitionEnum;
        switch (order.getSaleState()) {
            //确认前取消
            case UN_CONFIRMED:
                transitionEnum = SaleOrderLineStateEnum.TransitionEnum.WAIT_CONFIRM_TO_CANCEL_WHOLE;
                break;
            //开始前取消
            case UN_STARTED:
                transitionEnum = SaleOrderLineStateEnum.TransitionEnum.WAIT_OUT_STORAGE_TO_CANCEL_WHOLE;
                //取消库存锁定
                stockService.updateStockStatus(stockIds,
                        BusinessBillTypeEnum.TO_B_JS.equals(order.getSaleSource()) ?
                                StockStatusEnum.TransitionEnum.SALE_CONSIGNMENT_CANCEL :
                                StockStatusEnum.TransitionEnum.SALE_CANCEL);
                break;
            //确认后取消
            default:
                throw new OperationRejectedException(OperationExceptionCode.SALE_NOT_CANCEL);
        }

        stockService.updateUnderselling(stockIds, StockUndersellingEnum.NOT_ALLOW);

        //取消订单，改变订单行状态
        billSaleOrderLineService.updateLineState(BillSaleOrderLineDto.builder()
                        .saleId(order.getId())
                        .saleLineIdList(orderLines.stream().map(BillSaleOrderLine::getId).collect(Collectors.toList()))
                        .stockIdList(stockIds)
                        .build()
                , transitionEnum);

        //取消出库单
        billStoreWorkPreService.cancel(order.getSerialNo());

        //取消集单工作
        WmsWorkCollect wmsWorkCollect = wmsWorkCollectService.getOne(Wrappers.<WmsWorkCollect>lambdaQuery()
                .eq(WmsWorkCollect::getOriginSerialNo, order.getSerialNo()));

        if (Objects.nonNull(wmsWorkCollect)) {
            WmsWorkCollectWorkStateEnum.TransitionEnum te;
            switch (wmsWorkCollect.getWorkState()) {
                case WAIT_PRINT:
                    te = WmsWorkCollectWorkStateEnum.TransitionEnum.WAIT_PRINT_CANCEL;
                    break;
                case WAIT_DELIVERY:
                    te = WmsWorkCollectWorkStateEnum.TransitionEnum.WAIT_DELIVERY_CANCEL;
                    break;
                default:
                    throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
            }
            //更新集单状态
            wmsWorkCollectService.updateCollectWorkState(Lists.newArrayList(wmsWorkCollect), te);
        }

        if (SaleOrderTypeEnum.TO_B_JS.equals(order.getSaleType()) &&
                SaleOrderModeEnum.NORMAL.equals(order.getSaleMode())) {
            customerBalanceAdd(order, orderLines);
        }

        return SaleOrderCancelResult.builder()
                .serialNo(order.getSerialNo())
                .stockIdList(stockIds)
                .build();
    }

    private void customerBalanceAdd(BillSaleOrder saleOrder, List<BillSaleOrderLine> billSaleOrderLineList) {
        log.info("customerBalanceAdd function SaleOrderFacade start and saleOrder = {},billSaleOrderLineList = {}",
                JSON.toJSONString(saleOrder), JSON.toJSONString(billSaleOrderLineList));

        Integer type = CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue();
        BigDecimal amount = saleOrder.getTotalSalePrice();
        if (SaleOrderModeEnum.CONSIGN_FOR_SALE.equals(saleOrder.getSaleMode())) {
            type = CustomerBalanceTypeEnum.JS_AMOUNT.getValue();
        }

        customerBalanceService.customerBalanceCmd(saleOrder.getCustomerId(), saleOrder.getCustomerContactId(),
                amount, type,
                saleOrder.getShopId(), CustomerBalanceCmdTypeEnum.ADD.getValue(), saleOrder.getCreatedId(), saleOrder.getSerialNo());

    }

    @Override
    public void edit(SaleOrderEditRequest request) {
        billSaleOrderService.edit(request);
    }

    @Override
    public PageResult<SaleOrderListResult> list(SaleOrderListRequest request) {
        if (CollectionUtils.isEmpty(UserContext.getUser().getRoles())) {
            return PageResult.<SaleOrderListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        request.setSaleType(Optional.ofNullable(request.getSaleType())
                .filter(v -> v != -1)
                .orElse(null));

        request.setSaleMode(Optional.ofNullable(request.getSaleMode())
                .filter(v -> v != -1)
                .orElse(null));
        request.setSaleState(Optional.ofNullable(request.getSaleState())
                .filter(v -> v != -1)
                .orElse(null));

        if (request.getSaleType() == SaleOrderTypeEnum.TO_C_XS.getValue()) {
            return toCList(request);
        }
        return toBList(request);
    }

    /**
     * 个人销售单列表查询
     *
     * @param request
     * @return
     */
    private PageResult<SaleOrderListResult> toCList(SaleOrderListRequest request) {
        if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(ROLE_NAMES::contains)) {
            request.setShopIds(ObjectUtils.isNotEmpty(request.getShopId()) ? Lists.newArrayList(request.getShopId()) : null);
        } else if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains)) {
            request.setShopIds(IP_SHOP_ID);
        } else {
            request.setShopIds(Lists.newArrayList(UserContext.getUser().getStore().getId()));
        }
        List<CustomerContacts> customerContactsList = new ArrayList<>();
        if (StringTools.isNotNull(request.getCustomerName()) || StringTools.isNotNull(request.getCustomerPhone())) {
            //客户信息查询条件
            customerContactsList = customerContactsService
                    .searchByNameOrPhone(request.getCustomerName(), request.getCustomerPhone());
            if (customerContactsList.size() == 0) {
                return PageResult.<SaleOrderListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(0)
                        .totalPage(0)
                        .build();
            }
            request.setCustomerContactsIdList(customerContactsList.stream()
                    .map(CustomerContacts::getId)
                    .collect(Collectors.toList()));
        }
        Page<BillSaleOrder> page = billSaleOrderService.listByRequest(request);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<SaleOrderListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }

        Map<Integer, CustomerContacts> customerMap = Optional.ofNullable(customerContactsList.size() > 0 ? customerContactsList : null)
                .orElseGet(() -> customerContactsService.listByIds(page.getRecords()
                        .stream()
                        .map(BillSaleOrder::getCustomerContactId)
                        .collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(CustomerContacts::getId, Function.identity()));
        return PageResult.<SaleOrderListResult>builder()
                .result(Optional.ofNullable(page.getRecords())
                        .orElse(Lists.newArrayList())
                        .stream()
                        .map(t -> {
                            SaleOrderListResult r = SaleOrderConverter.INSTANCE.convertSaleOrderListResult(t);
                            r.setCustomerName(Optional.ofNullable(customerMap.get(t.getCustomerContactId()))
                                    .map(CustomerContacts::getName).orElse(null));
                            r.setWhetherTH(billSaleOrderLineService.countStateBySaleId(t.getId()) != 0);
                            return r;
                        })
                        .collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    /**
     * 同行销售单列表查询
     *
     * @param request
     * @return
     */
    private PageResult<SaleOrderListResult> toBList(SaleOrderListRequest request) {
        request.setShopIds(Lists.newArrayList(FlywheelConstant._SJZ));
        List<Customer> customerList = new ArrayList<>();
        if (StringTools.isNotNull(request.getCustomerName()) || StringTools.isNotNull(request.getCustomerPhone())) {
            //企业客户信息查询条件
            customerList = customerService.searchByNameOrPhone(request.getCustomerName(), request.getCustomerPhone());
            if (customerList.size() == 0) {
                return PageResult.<SaleOrderListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(0)
                        .totalPage(0)
                        .build();
            }
            request.setCustomerIdList(customerList.stream()
                    .map(Customer::getId)
                    .collect(Collectors.toList()));
        }
        Page<BillSaleOrder> page = billSaleOrderService.listByRequest(request);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<SaleOrderListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        Map<Integer, Customer> customerMap = Optional.ofNullable(customerList.size() > 0 ? customerList : null)
                .orElseGet(() -> customerService.listByIds(page.getRecords()
                        .stream()
                        .map(BillSaleOrder::getCustomerId)
                        .collect(Collectors.toList())))
                .stream()
                .distinct()
                .collect(Collectors.toMap(Customer::getId, c -> c, ((key1, key2) -> key1)));
        return PageResult.<SaleOrderListResult>builder()
                .result(Optional.ofNullable(page.getRecords())
                        .orElse(Lists.newArrayList())
                        .stream()
                        .map(t -> {
                            SaleOrderListResult r = SaleOrderConverter.INSTANCE.convertSaleOrderListResult(t);
                            r.setCustomerName(Optional.ofNullable(customerMap.get(t.getCustomerId()))
                                    .map(Customer::getCustomerName).orElse(null));
                            if (StringUtils.isNotBlank(r.getFinishTime()))
                                r.setSaleTime(DateUtils.dateDiff(r.getFinishTime(), DateUtils.getNowDate(DateUtils.YYYY_MM_DD_HH_MM_SS), DateUtils.YYYY_MM_DD_HH_MM_SS));
                            return r;
                        })
                        .collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public SaleOrderDetailsResult details(SaleOrderDetailsRequest request) {
        //查销售单
        BillSaleOrder saleOrder = Optional.ofNullable(request.getId())
                .filter(id -> id > 0)
                .map(billSaleOrderService::getById)
                .orElseGet(() -> Optional.ofNullable(billSaleOrderService.getOne(new LambdaQueryWrapper<BillSaleOrder>()
                                .eq(BillSaleOrder::getSerialNo, request.getSerialNo())))
                        .orElseGet(() -> billSaleOrderService.getOne(new LambdaQueryWrapper<BillSaleOrder>()
                                .eq(BillSaleOrder::getBizOrderCode, request.getBizOrderCode()))));

        if (Objects.isNull(saleOrder)) {
            throw new BusinessException(ExceptionCode.SALE_ORDER_BILL_NOT_EXIST);
        }

        SaleOrderDetailsResult result = SaleOrderConverter.INSTANCE.convertSaleOrderDetailsResult(saleOrder);

        //联系人信息
        CustomerContacts customerContacts = Optional.ofNullable(saleOrder.getCustomerContactId())
                .map(customerContactsService::getById)
                .orElse(null);
        if (Objects.nonNull(customerContacts)) {
            result.setCustomerId(customerContacts.getCustomerId());
            result.setContactName(customerContacts.getName());
            result.setContactPhone(customerContacts.getPhone());
            result.setContactAddress(customerContacts.getAddress());
            result.setContactEncrypted(SaleOrderChannelEnum.DOU_YIN.equals(saleOrder.getSaleChannel())
                    && Optional.ofNullable(customerContacts.getPhone())
                    .filter(c -> !c.contains("*")) // 手机号不含星号
                    .map(t -> false)
                    .orElse(true)
            );
        }
        if (SaleOrderTypeEnum.TO_B_JS.equals(saleOrder.getSaleType())) {
            Customer customer = Optional.ofNullable(saleOrder.getCustomerId())
                    .map(customerService::getById)
                    .orElse(null);
            if (Objects.nonNull(customer)) {
                result.setCustomerId(customer.getId());
                result.setCustomerName(customer.getCustomerName());
            }
        }

        //订单来源
        Tag shopTag = Optional.ofNullable(saleOrder.getShopId())
                .map(tagService::selectByStoreManagementId)
                .orElse(null);

        if (Objects.nonNull(shopTag)) {
            result.setShopName(shopTag.getTagName());
            result.setShopAddress(storeManagementService.getById(saleOrder.getShopId()).getAddress());
        }

        //填充第一第二第三销售人
        Map<Long, String> userMap = userService.list().stream().collect(Collectors.toMap(User::getId, User::getName));
        if (ObjectUtils.isNotNull(result.getFirstSalesman()))
            result.setFirstSalesmanName(userMap.get(result.getFirstSalesman().longValue()));
        if (ObjectUtils.isNotNull(result.getSecondSalesman()))
            result.setSecondSalesmanName(userMap.get(result.getSecondSalesman().longValue()));
        if (ObjectUtils.isNotNull(result.getThirdSalesman()))
            result.setThirdSalesmanName(userMap.get(result.getThirdSalesman().longValue()));

        Map<Integer, String> map = subjectService.list().stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));

        //发货位置
        Tag tag = Optional.ofNullable(saleOrder.getDeliveryLocationId())
                .map(tagService::selectByStoreManagementId)
                .orElse(null);


        //查质检视频
        Map<Integer, List<RcSaleDeliveryVideo>> videoMap = rcSaleDeliveryVideoService.list(Wrappers.<RcSaleDeliveryVideo>lambdaQuery()
                        .eq(RcSaleDeliveryVideo::getSaleId, saleOrder.getId()))
                .stream()
                .collect(Collectors.groupingBy(RcSaleDeliveryVideo::getStockId));

        Map<Integer, String> subjectUrlMap = FlywheelConstant.SUBJECT_URL_MAP;
        List<BillSaleOrderLineDetailsVO> lineDetailsVOList = billSaleOrderLineService.selectBySaleId(saleOrder.getId());
        List<SaleOrderDetailsResult.SaleOrderLineVO> lineVOS = SaleOrderConverter.INSTANCE.convertSaleOrderLineVO(lineDetailsVOList);
        lineVOS.forEach(vo -> {
            if (Objects.nonNull(vo.getBelongId())) {
                if ((saleOrder.getShopId() == FlywheelConstant.SHOP_NN_YD || saleOrder.getShopId() == FlywheelConstant.SHOP_NN_ED) &&
                        (vo.getBelongId() == FlywheelConstant._XY || vo.getBelongId() == FlywheelConstant._XY_KJ)) {
                    vo.setSubjectUrl(subjectUrlMap.get(FlywheelConstant.SHOP_NN_ED));
                } else if (subjectUrlMap.containsKey(vo.getBelongId())) {
                    vo.setSubjectUrl(subjectUrlMap.get(vo.getBelongId()));
                }
                vo.setBelongName(map.get(vo.getBelongId()));
            }
            if (videoMap.containsKey(vo.getStockId())) {
                vo.setRcData(videoMap.get(vo.getStockId())
                        .stream()
                        .map(RcSaleDeliveryVideo::getRcData)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));
            }
            if (Objects.nonNull(tag)) {
                vo.setLocationName(tag.getTagName());
            }
            vo.setLocationId(saleOrder.getDeliveryLocationId());
            if (StringTools.isNotEmpty(vo.getBuyBackPolicy())) {
                List<BuyBackPolicyMapper> list = JSON.parseArray(vo.getBuyBackPolicy(), BuyBackPolicyMapper.class)
                        .stream().sorted(Comparator.comparing(BuyBackPolicyMapper::getBuyBackTime)).collect(Collectors.toList());
                vo.setList(SaleOrderConverter.INSTANCE.convertBuyBackPolicyList(list));
            }
            vo.setRightOfManagementName(map.getOrDefault(vo.getRightOfManagement(), "未知经营权"));
            if (StringTools.isEmpty(vo.getSerialNo())) {
                vo.setSerialNo(result.getSerialNo());
            }
        });
        result.setLines(lineVOS);
        if (result.getSaleType() == SaleOrderTypeEnum.TO_C_XS.getValue()) {
            String expressNumber = lineVOS.stream().map(SaleOrderDetailsResult.SaleOrderLineVO::getExpressNumber).distinct().collect(Collectors.joining(","));
            result.setExpressNumber(StringTools.isEmpty(expressNumber) ? null : expressNumber);
        }
        return result;
    }

    @Override
    public PageResult<SaleOrderSettlementListResult> querySettlementList(SaleOrderSettlementListRequest request) {

        if (request.getWhetherTH())
            request.setLineStateList(Lists.newArrayList(SaleOrderLineStateEnum.ON_CONSIGNMENT.getValue(),
                    SaleOrderLineStateEnum.CONSIGNMENT_SETTLED.getValue(), SaleOrderLineStateEnum.DELIVERED.getValue()));
        else
            request.setLineStateList(Lists.newArrayList(SaleOrderLineStateEnum.ON_CONSIGNMENT.getValue()));
        Page<BillSaleOrderLineSettlementVO> page = billSaleOrderLineService.querySettlementList(request);
        Map<Integer, String> map = subjectService.list().stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
        //发货位置
        Map<Integer, String> tagMap = tagService.selectListByStoreManagement().stream().collect(Collectors.toMap(TagPo::getStoreId, TagPo::getTagName));
        List<SaleOrderSettlementListResult> result = page.getRecords().stream()
                .map(t -> {
                    SaleOrderSettlementListResult r = SaleOrderConverter.INSTANCE.convertSaleOrderSettlementListResult(t);
                    r.setRightOfManagementName(map.getOrDefault(r.getRightOfManagement(), "未知经营权"));
                    r.setLocationName(tagMap.getOrDefault(r.getDeliveryLocationId(), "未知商品位置"));
                    return r;
                })
                .collect(Collectors.toList());

        return PageResult.<SaleOrderSettlementListResult>builder()
                .result(result)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrderBatchSettlementResult batchSettlement(SaleOrderBatchSettlementRequest request) {
        return billSaleOrderLineService.batchSettlement(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrderConfirmResult saleConfirm(SaleOrderConfirmRequest request) {
        //确认销售
        BillSaleOrderDTO orderDTO = billSaleOrderService.saleConfirm(request);

        BillSaleOrder order = orderDTO.getOrder();
        List<BillSaleOrderLine> orderLines = orderDTO.getLines();

        List<Integer> stockIds = orderLines
                .stream()
                .map(BillSaleOrderLine::getStockId)
                .collect(Collectors.toList());
        //锁定库存
        stockService.updateStockStatus(stockIds,
                StockStatusEnum.TransitionEnum.SALE);

        //修改销售行状态
        billSaleOrderLineService.updateLineState(BillSaleOrderLineDto.builder()
                .stockIdList(stockIds)
                .saleId(order.getId())
                .build(), SaleOrderLineStateEnum.TransitionEnum.SALE_CONFIRM);

        //创建出库
        List<StoreWorKCreateRequest> shopWorKList = orderLines.stream()
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
                        .build())
                .collect(Collectors.toList());

        //创建出库作业
        List<StoreWorkCreateResult> workList = billStoreWorkPreService.create(shopWorKList);

        return SaleOrderConfirmResult.builder()
                .orderId(order.getId())
                .deliveryLocationId(order.getDeliveryLocationId())
                .shortcodes(tagService.selectByStoreManagementId(order.getDeliveryLocationId()).getShortcodes())
                .storeWorkList(workList)
                .serialNo(order.getSerialNo())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void tmallSettleAccounts(SaleOrderTmallSettleAccountsRequest request) {
        BillSaleOrder saleOrder = billSaleOrderService.getById(request.getSaleId());
        if (ObjectUtils.isEmpty(saleOrder)) throw new BusinessException(ExceptionCode.SALE_ORDER_BILL_NOT_EXIST);
        if (!SaleOrderChannelEnum.T_MALL.equals(saleOrder.getSaleChannel()))
            throw new BusinessException(ExceptionCode.OPERATION_DATA_DOES_NOT_MATCH_DATABASE_DATA);
        if (ObjectUtils.isEmpty(saleOrder.getTransferCustomerId())) {
            Integer customerId;
            CustomerContacts customerContacts = customerContactsService.queryCustomerContactsByNameAndPhone(request.getUserName(), request.getPhone());

            if (customerContacts == null) {
                List<Customer> customers = customerService.searchByNameOrPhone(request.getUserName(), request.getPhone());
                if (customers.size() == 0) {
                    customerContacts = new CustomerContacts();
                    Customer customer = new Customer();
                    customer.setType(CustomerTypeEnum.INDIVIDUAL);
                    customer.setCustomerName(request.getUserName());
                    customerService.save(customer);
                    customerContacts.setCustomerId(customer.getId());
                    customerContacts.setName(request.getUserName());
                    customerContacts.setPhone(request.getPhone());
                    customerContacts.setAddress(request.getUserName());
                    customerContactsService.save(customerContacts);
                    customerId = customer.getId();
                } else {
                    customerId = customers.stream().filter(customer -> CustomerTypeEnum.INDIVIDUAL.equals(customer.getType())).findFirst().get().getId();
                }
            } else {
                customerId = customerContacts.getCustomerId();
            }

            //去更新流转人员信息
            BillSaleOrder order = new BillSaleOrder();
            order.setId(saleOrder.getId());
            order.setTransferCustomerId(customerId);
            billSaleOrderService.updateById(order);

            FinancialGenerateDto dto = new FinancialGenerateDto();
            dto.setId(saleOrder.getId());
            dto.setStockList(request.getStockIdList());
            financialService.generateSaleBalance(dto);

        }
    }

    @Override
    public ImportResult<SaleStockQueryImportResult> stockQueryImport(SaleStockQueryImportRequest request) {
        Map<String, SaleStockQueryImportRequest.ImportDto> stockSnMap = request.getDataList().stream()
                .collect(Collectors.toMap(SaleStockQueryImportRequest.ImportDto::getStockSn, Function.identity()));

        List<String> stockSnList = stockSnMap.keySet().stream().collect(Collectors.toList());

        Map<String, StockBaseInfo> stockMap = Lists.partition(stockSnList, 500)
                .stream()
                .map(snList -> stockService.listSaleableStockBySn(snList, null))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(StockBaseInfo::getStockSn, Function.identity()));

        //校验不可改的数据
        Collection<String> errorStock = CollectionUtils.subtract(stockSnList, stockMap.keySet().stream().collect(Collectors.toList()));

        List<StockBaseInfo> resultList = stockMap.values().stream().collect(Collectors.toList());

        if (CollectionUtils.isEmpty(resultList)) {
            return ImportResult.<AllocateStockQueryImportResult>builder()
                    .successList(Collections.EMPTY_LIST)
                    .errList(errorStock.stream().collect(Collectors.toList()))
                    .build();
        }

        //商品位置
        Map<Integer, String> shopMap = storeManagementService.selectInfoByIds(resultList
                        .stream()
                        .map(StockBaseInfo::getLocationId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));

        //经营权
        Map<Integer, String> rightOfManagementMap = purchaseSubjectService.listByIds(resultList
                        .stream()
                        .map(StockBaseInfo::getRightOfManagement)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));

        return ImportResult.<SaleStockQueryImportResult>builder()
                .successList(
                        resultList.stream()
                                .map(t -> {
                                    SaleStockQueryImportRequest.ImportDto dto = stockSnMap.get(t.getStockSn());
                                    SaleStockQueryImportResult r = SaleOrderConverter.INSTANCE.convertSaleStockQueryImportResult(t);
                                    r.setLocationName(shopMap.get(t.getLocationId()));
                                    r.setRightOfManagementName(rightOfManagementMap.get(t.getRightOfManagement()));
                                    r.setRemarks(dto.getRemarks());
                                    r.setPreClinchPrice(dto.getPreClinchPrice());
                                    r.setClinchPrice(dto.getClinchPrice());
                                    return r;
                                })
                                .collect(Collectors.toList()))
                .errList(errorStock.stream().collect(Collectors.toList()))
                .build();
    }

    @Override
    public ImportResult<SaleSellteStockQueryImportResult> stockQueryImport(SaleSettleStockQueryImportRequest request) {
        Map<String, SaleSettleStockQueryImportRequest.ImportDto> stockSnMap = request.getDataList().stream()
                .collect(Collectors.toMap(SaleSettleStockQueryImportRequest.ImportDto::getStockSn, Function.identity()));

        //查询的表身号条件
        List<String> stockSnList = new ArrayList<>(stockSnMap.keySet());
        //查询的行状态条件
        List<Integer> stateList = Lists.newArrayList(SaleOrderLineStateEnum.ON_CONSIGNMENT.getValue());
        Map<String, BillSaleOrderLineSettlementVO> map = Lists.partition(stockSnList, 500)
                .stream()
                .map(snList -> billSaleOrderLineService.listStockBySnAndState(snList, stateList, request.getCustomerId()))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(BillSaleOrderLineSettlementVO::getStockSn, Function.identity()));

        //校验不可改的数据
        Collection<String> errorStock = CollectionUtils.subtract(stockSnList, new ArrayList<>(map.keySet()));

        List<BillSaleOrderLineSettlementVO> resultList = new ArrayList<>(map.values());

        if (CollectionUtils.isEmpty(resultList)) {
            return ImportResult.<SaleSellteStockQueryImportResult>builder()
                    .successList(Collections.EMPTY_LIST)
                    .errList(new ArrayList<>(errorStock))
                    .build();
        }

        //商品位置
        Map<Integer, String> shopMap = storeManagementService.getStoreMap();

        //经营权
        Map<Integer, String> rightOfManagementMap = subjectService.listByIds(resultList
                        .stream()
                        .map(BillSaleOrderLineSettlementVO::getRightOfManagement)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));

        return ImportResult.<SaleSellteStockQueryImportResult>builder()
                .successList(
                        resultList.stream()
                                .map(t -> {
                                    SaleSettleStockQueryImportRequest.ImportDto dto = stockSnMap.get(t.getStockSn());
                                    SaleSellteStockQueryImportResult r = SaleReturnOrderConverter.INSTANCE.convertSaleSettleStockQueryImportResult(t);
                                    r.setLocationName(shopMap.get(t.getDeliveryLocationId()));
                                    r.setRightOfManagementName(rightOfManagementMap.get(t.getRightOfManagement()));
                                    r.setClinchPrice(dto.getClinchPrice());
                                    return r;
                                })
                                .collect(Collectors.toList()))
                .errList(new ArrayList<>(errorStock))
                .build();
    }

    @Override
    public PageResult<SaleOrderListForExportResult> export(SaleOrderListRequest request) {
        request.setSaleType(Optional.ofNullable(request.getSaleType())
                .filter(v -> v != -1)
                .orElse(null));

        request.setSaleMode(Optional.ofNullable(request.getSaleMode())
                .filter(v -> v != -1)
                .orElse(null));
        request.setSaleState(Optional.ofNullable(request.getSaleState())
                .filter(v -> v != -1)
                .orElse(null));
        //导出手选择项
        if (Objects.nonNull(request.getDocBatchIds()) && request.getDocBatchIds().size() == 0) {
            request.setDocBatchIds(null);
        }

        List<CustomerContacts> customerContactsList = null;
        List<Customer> customerList = null;
        //客户信息查询条件
        if (Objects.equals(request.getSaleType(), SaleOrderTypeEnum.TO_C_XS.getValue())) {
            if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(ROLE_NAMES::contains)) {
                request.setShopIds(ObjectUtils.isNotEmpty(request.getShopId()) ? Lists.newArrayList(request.getShopId()) : null);
            } else if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains)) {
                request.setShopIds(IP_SHOP_ID);
            } else {
                request.setShopIds(Lists.newArrayList(UserContext.getUser().getStore().getId()));
            }
            customerContactsList = customerContactsService.searchByNameOrPhone(request.getCustomerName(), request.getCustomerPhone());
            if (customerContactsList.size() > 0)
                request.setCustomerContactsIdList(customerContactsList.stream()
                        .map(CustomerContacts::getId)
                        .collect(Collectors.toList()));
        } else {
            request.setShopIds(Lists.newArrayList(FlywheelConstant._SJZ));
            //企业客户信息查询条件
            customerList = customerService.searchByNameOrPhone(request.getCustomerName(), request.getCustomerPhone());
            if (customerList.size() > 0)
                request.setCustomerIdList(customerList.stream()
                        .map(Customer::getId)
                        .collect(Collectors.toList()));
        }

        Page<SaleOrderListForExportResult> page = billSaleOrderService.export(request);

        //为空直接返回
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<SaleOrderListForExportResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(page.getTotal())
                    .totalPage(page.getPages())
                    .build();
        }
        //联系人
        Map<Integer, CustomerContacts> customerContactsMap = Optional.ofNullable(customerContactsList)
                .filter(CollectionUtils::isNotEmpty)
                .orElseGet(() -> customerContactsService.listByIds(page.getRecords().stream()
                        .map(SaleOrderListForExportResult::getCustomerContactId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(CustomerContacts::getId, Function.identity()));
        //客户
        Map<Integer, Customer> customerMap = SaleOrderTypeEnum.TO_B_JS.getValue().equals(request.getSaleType()) ?
                Optional.ofNullable(customerList)
                        .filter(CollectionUtils::isNotEmpty)
                        .orElseGet(() -> customerService.listByIds(page.getRecords().stream()
                                .map(SaleOrderListForExportResult::getCustomerId)
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.toList())))
                        .stream().collect(Collectors.toMap(Customer::getId, Function.identity(), (a, b) -> a)) : Collections.EMPTY_MAP;
        //查商品
        Map<Integer, WatchDataFusion> goodsWatchMap = goodsWatchService.getWatchDataFusionListByStockIds(page.getRecords()
                        .stream()
                        .map(SaleOrderListForExportResult::getStockId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity()));
        //填充第一第二第三销售人
        Map<Long, String> userMap = userService.list().stream().collect(Collectors.toMap(User::getId, User::getName));
        //采购主体
        Map<Integer, String> map = subjectService.list().stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
        //发货位置
        Map<Integer, String> tagMap = tagService.selectListByStoreManagement().stream().collect(Collectors.toMap(TagPo::getStoreId, TagPo::getTagName));

        page.getRecords().forEach(result -> {
            //联系人信息
            CustomerContacts customerContacts = customerContactsMap.get(result.getCustomerContactId());
            if (Objects.nonNull(customerContacts)) {
                result.setContactName(customerContacts.getName());
                result.setContactPhone(customerContacts.getPhone());
                result.setContactAddress(customerContacts.getAddress());
            }
            if (SaleOrderTypeEnum.TO_B_JS.getValue().equals(result.getSaleType())) {
                Customer customer = customerMap.get(result.getCustomerId());
                if (Objects.nonNull(customer)) {
                    result.setCustomerName(customer.getCustomerName());
                }
            }
            //订单来源
            result.setShopName(tagMap.get(result.getShopId()));

            if (ObjectUtils.isNotNull(result.getFirstSalesman()))
                result.setFirstSalesmanName(userMap.get(result.getFirstSalesman().longValue()));
            if (ObjectUtils.isNotNull(result.getSecondSalesman()))
                result.setSecondSalesmanName(userMap.get(result.getSecondSalesman().longValue()));
            if (ObjectUtils.isNotNull(result.getThirdSalesman()))
                result.setThirdSalesmanName(userMap.get(result.getThirdSalesman().longValue()));
            result.setLocationId(result.getDeliveryLocationId());
            result.setLocationName(tagMap.get(result.getLocationId()));

            result.setRightOfManagementName(map.getOrDefault(result.getRightOfManagement(), "未知经营权"));

            WatchDataFusion goods = goodsWatchMap.getOrDefault(result.getStockId(), new WatchDataFusion());
            result.setFiness(goods.getFiness());
            result.setBrandName(goods.getBrandName());
            result.setSeriesName(goods.getSeriesName());
            result.setModel(goods.getModel());
            result.setStockSn(goods.getStockSn());
            result.setWno(goods.getWno());
            result.setAttachment(goods.getAttachment());
            result.setBelongName(map.get(goods.getBelongId()));
        });

        return PageResult.<SaleOrderListForExportResult>builder()
                .result(page.getRecords().stream().sorted(Comparator.comparing(SaleOrderListForExportResult::getCreatedTime)).collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PageResult<SaleOrderRecycleListResult> queryByRecycle(SaleOrderRecycleListRequest request) {

        LambdaQueryWrapper<BillSaleOrder> queryWrapper = Wrappers.<BillSaleOrder>lambdaQuery()
                .eq(BillSaleOrder::getSaleType, SaleOrderTypeEnum.TO_C_XS)
                .notIn(BillSaleOrder::getSaleState, BusinessBillStateEnum.CANCEL_WHOLE);

        if (ObjectUtils.isNotEmpty(request.getSerialNo())) {
            queryWrapper.likeRight(BillSaleOrder::getSerialNo, request.getSerialNo());
        }

        Page<BillSaleOrder> page = billSaleOrderService.page(new Page<>(request.getPage(), request.getLimit()), queryWrapper);

        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<SaleOrderRecycleListResult>builder()
                    .result(Arrays.asList())
                    .totalCount(page.getTotal())
                    .totalPage(page.getPages())
                    .build();
        }
        return PageResult.<SaleOrderRecycleListResult>builder()
                .result(page.getRecords().stream().map(billSaleOrder -> {
                    SaleOrderRecycleListResult result = new SaleOrderRecycleListResult();
                    result.setSerialNo(billSaleOrder.getSerialNo());
                    return result;
                }).collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PrintOptionResult printOption(String serialNo) {

        BillSaleOrder saleOrder = billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery()
                        .eq(BillSaleOrder::getSerialNo, serialNo)
//                .eq(BillSaleOrder::getSaleType, SaleOrderTypeEnum.TO_C_XS)
//                .eq(BillSaleOrder::getSaleChannel, SaleOrderChannelEnum.DOU_YIN)
//                .notIn(BillSaleOrder::getShopId, PRINT_SHOP_ID)
//                .eq(BillSaleOrder::getSaleMode, SaleOrderModeEnum.ON_LINE)
        );

        if (ObjectUtils.isEmpty(saleOrder)) {
            //禁止打印
            return PrintOptionResult.builder().printOption(-1).build();
        }
        List<BillSaleOrderLine> saleOrderLineList = billSaleOrderLineService.list(Wrappers.<BillSaleOrderLine>lambdaQuery()
                .eq(BillSaleOrderLine::getSaleId, saleOrder.getId()));
        if (CollectionUtils.isEmpty(saleOrderLineList)) {
            return PrintOptionResult.builder().printOption(-1).build();
        }

        List<StockExt> stockExtList = stockService.selectByStockIdList(saleOrderLineList.stream().map(BillSaleOrderLine::getStockId).collect(Collectors.toList()));

        if (SaleOrderInspectionTypeEnum.ONLINE.equals(saleOrder.getInspectionType())) {

            return PrintOptionResult.builder().printOption(saleOrder.getInspectionType().getValue())
                    .douYinOption(saleOrderLineList.get(FlywheelConstant.INDEX).getScInfoId())
                    .remarks(saleOrder.getRemarks())
                    .bizOrderCode(saleOrder.getBizOrderCode())
                    .shopId(saleOrder.getShopId())
                    //国检码
                    .spotCheckCode(StringUtils.join(saleOrderLineList.stream().map(BillSaleOrderLine::getSpotCheckCode).collect(Collectors.toList()), ","))
                    .printProductName(StringUtils.join(stockExtList.stream().map(stockExt -> StringUtils.join(Arrays.asList(stockExt.getWno(), stockExt.getStockSn()), "\t")).collect(Collectors.toList()), "\n"))
                    .serialNo(saleOrder.getSerialNo())
                    .build();
        }

        return PrintOptionResult.builder().printOption(saleOrder.getInspectionType().getValue())
                .remarks(saleOrder.getRemarks())
                .bizOrderCode(saleOrder.getBizOrderCode())
                .shopId(saleOrder.getShopId())
                .printProductName(StringUtils.join(stockExtList.stream().map(stockExt -> StringUtils.join(Arrays.asList(stockExt.getWno(), stockExt.getStockSn()), "\t")).collect(Collectors.toList()), "\n"))
                .serialNo(saleOrder.getSerialNo())
                .build();
    }

    @Override
    public PrintOptionResult printOptionByDd(String serialNo) {

        BillSaleOrder saleOrder = billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery()
                .eq(BillSaleOrder::getSerialNo, serialNo)
                .notIn(BillSaleOrder::getShopId, PRINT_SHOP_ID)
        );

        if (ObjectUtils.isEmpty(saleOrder)) {
            //禁止打印
            return PrintOptionResult.builder().printOption(-1).build();
        }

        List<BillSaleOrderLine> saleOrderLineList = billSaleOrderLineService.list(Wrappers.<BillSaleOrderLine>lambdaQuery()
                .eq(BillSaleOrderLine::getSaleId, saleOrder.getId()));
        if (CollectionUtils.isEmpty(saleOrderLineList)) {
            return PrintOptionResult.builder().printOption(-1).build();
        }

        List<StockExt> stockExtList = stockService.selectByStockIdList(saleOrderLineList.stream().map(BillSaleOrderLine::getStockId).collect(Collectors.toList()));

        List<WatchDataFusion> watchDataFusionList = goodsWatchService.getWatchDataFusionListByGoodsIds(saleOrderLineList.stream().map(BillSaleOrderLine::getGoodsId).collect(Collectors.toList()));

        if (StringUtils.isBlank(saleOrder.getBizOrderCode())) {
            return PrintOptionResult.builder().printOption(SaleOrderInspectionTypeEnum.OFF.getValue())
                    .remarks(saleOrder.getRemarks())
                    .shopId(saleOrder.getShopId())
                    .printProductName(StringUtils.join(stockExtList.stream().map(stockExt -> StringUtils.join(Arrays.asList(stockExt.getWno()), "\t")).collect(Collectors.toList()), "\n"))
                    .serialNo(saleOrder.getSerialNo())
                    .stockIdList(saleOrderLineList.stream().map(BillSaleOrderLine::getStockId).collect(Collectors.toList()))
                    .model(StringUtils.join(watchDataFusionList.stream().map(WatchDataFusion::getModel).distinct().collect(Collectors.toList()), "\t"))
                    .build();
        } else if (StringUtils.isNotBlank(saleOrder.getBizOrderCode()) && (
                SaleOrderTypeEnum.TO_C_XS == saleOrder.getSaleType() && SaleOrderChannelEnum.DOU_YIN == saleOrder.getSaleChannel() && SaleOrderModeEnum.ON_LINE == saleOrder.getSaleMode()
        )) {
            if (SaleOrderInspectionTypeEnum.ONLINE.equals(saleOrder.getInspectionType())) {

                return PrintOptionResult.builder().printOption(saleOrder.getInspectionType().getValue())
                        .douYinOption(saleOrderLineList.get(FlywheelConstant.INDEX).getScInfoId())
                        .remarks(saleOrder.getRemarks())
                        .bizOrderCode(saleOrder.getBizOrderCode())
                        .shopId(saleOrder.getShopId())
                        //国检码
                        .spotCheckCode(StringUtils.join(saleOrderLineList.stream().map(BillSaleOrderLine::getSpotCheckCode).collect(Collectors.toList()), ","))
                        .serialNo(saleOrder.getSerialNo())
                        .stockIdList(saleOrderLineList.stream().map(BillSaleOrderLine::getStockId).collect(Collectors.toList()))
                        .model(StringUtils.join(watchDataFusionList.stream().map(WatchDataFusion::getModel).distinct().collect(Collectors.toList()), "\t"))
                        .build();
            }

            return PrintOptionResult.builder().printOption(saleOrder.getInspectionType().getValue())
                    .remarks(saleOrder.getRemarks())
                    .bizOrderCode(saleOrder.getBizOrderCode())
                    .shopId(saleOrder.getShopId())
                    .printProductName(StringUtils.join(stockExtList.stream().map(stockExt -> StringUtils.join(Arrays.asList(stockExt.getWno()), "\t")).collect(Collectors.toList()), "\n"))
                    .serialNo(saleOrder.getSerialNo())
                    .stockIdList(saleOrderLineList.stream().map(BillSaleOrderLine::getStockId).collect(Collectors.toList()))
                    .model(StringUtils.join(watchDataFusionList.stream().map(WatchDataFusion::getModel).distinct().collect(Collectors.toList()), "\t"))
                    .build();
        }
        return null;
    }

    @Override
    public List<DouYinSaleOrderListResult> queryDouYinSaleOrder(DouYinSaleOrderListRequest request) {

        return billSaleOrderService.queryDouYinSaleOrder(request);
    }

    @Override
    public void warrantyPeriodUpdate(SaleOrderWarrantyPeriodUpdateRequest request) {
        if (Objects.isNull(request))
            return;
        BillSaleOrder billSaleOrder = billSaleOrderService.selectBySerialNo(request.getSerialNo());
        if (Objects.isNull(billSaleOrder))
            return;
        BillSaleOrderLine orderLine = billSaleOrderLineService.getOne(new LambdaQueryWrapper<BillSaleOrderLine>()
                .eq(BillSaleOrderLine::getStockId, request.getStockId())
                .eq(BillSaleOrderLine::getSaleId, billSaleOrder.getId()));
        if (Objects.isNull(orderLine))
            return;
        billSaleOrderLineService.updateWarrantyPeriod(orderLine.getId());
    }

    @Override
    public SingleSaleOrderResult queryBySerialNo(String seriesNo) {
        return SaleOrderConverter.INSTANCE.to(billSaleOrderService.selectBySerialNo(seriesNo));
    }


    @Override
    public void update(SaleOrderUpdateRequest request) {
        if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch("admin"::equals))
            billSaleOrderService.updateLine(request);
    }

    @Override
    public List<SaleHistoryResult> saleHistory(String wno) {
        LambdaQueryWrapper<Stock> wq = Wrappers.<Stock>lambdaQuery().eq(Stock::getWno, wno).last("limit 1");
        Stock stock = stockService.getOne(wq);
        if (stock == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BillSaleOrderLine> saleQw = Wrappers.<BillSaleOrderLine>lambdaQuery()
                .eq(BillSaleOrderLine::getGoodsId, stock.getGoodsId());
        List<BillSaleOrderLine> saleLines = billSaleOrderLineService.list(saleQw);
        if (saleLines.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> saleIds = saleLines.stream().map(BillSaleOrderLine::getSaleId).collect(Collectors.toList());
        List<BillSaleOrder> billSaleOrders = billSaleOrderService.listByIds(saleIds);


        return saleLines.stream().map(line -> {
                    for (BillSaleOrder billSaleOrder : billSaleOrders) {
                        if (line.getSaleId().equals(billSaleOrder.getId())) {
                            return SaleHistoryResult.builder()
                                    .salePrice(line.getClinchPrice())
                                    .createdTime(line.getCreatedTime())
                                    .type(billSaleOrder.getSaleType().getValue())
                                    .attachment(stock.getAttachment())
                                    .build();
                        }
                    }
                    return null;
                }).filter(v -> Objects.nonNull(v) && v.getType().equals(SaleOrderTypeEnum.TO_C_XS.getValue()))
                .sorted((o1, o2) -> o2.getCreatedTime().compareTo(o1.getCreatedTime()))
                .collect(Collectors.toList());
    }

}
