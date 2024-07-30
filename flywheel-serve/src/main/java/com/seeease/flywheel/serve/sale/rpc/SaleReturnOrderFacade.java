package com.seeease.flywheel.serve.sale.rpc;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCreateRequest;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentCreateResult;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.entity.SaleReturnOrderDeliveryMessage;
import com.seeease.flywheel.sale.request.*;
import com.seeease.flywheel.sale.result.*;
import com.seeease.flywheel.serve.base.*;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceCmdTypeEnum;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.entity.AccountReceiptConfirm;
import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.*;
import com.seeease.flywheel.serve.maindata.mapper.StoreManagementMapper;
import com.seeease.flywheel.serve.maindata.service.*;
import com.seeease.flywheel.serve.sale.convert.SaleReturnOrderConverter;
import com.seeease.flywheel.serve.sale.entity.*;
import com.seeease.flywheel.serve.sale.enums.*;
import com.seeease.flywheel.serve.sale.mq.SaleReturnOrderDeliveryProducers;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderService;
import com.seeease.flywheel.serve.sale.strategy.SaleReturnOrderContext;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.storework.request.StoreWorKCreateRequest;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/3/6
 */
@DubboService(version = "1.0.0")
@Slf4j
public class SaleReturnOrderFacade implements ISaleReturnOrderFacade {
    @Resource
    private SaleReturnOrderContext context;
    @Resource
    private BillSaleReturnOrderService returnOrderService;
    @Resource
    private BillSaleReturnOrderLineService billSaleReturnOrderLineService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private CustomerService customerService;
    @Resource
    private TagService tagService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;
    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    private BillSaleOrderService billSaleOrderService;
    @Resource
    private AccountStockRelationService stockRelationService;
    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;
    @Resource
    private BillSaleReturnOrderService billSaleReturnOrderService;
    @Resource
    private UserService userService;
    @Resource
    private PurchaseSubjectService subjectService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;
    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    private SaleReturnOrderDeliveryProducers saleReturnOrderDeliveryProducers;
    @Resource
    private CustomerBalanceService customerBalanceService;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private FinancialStatementCompanyService statementCompanyService;
    @Resource
    private StoreRelationshipSubjectService relationshipSubjectService;
    @NacosValue(value = "${saleOrder.roleNames}", autoRefreshed = true)
    private List<String> ROLE_NAMES;
    @NacosValue(value = "${saleOrder.ipRoleName}", autoRefreshed = true)
    private List<String> IP_ROLE_NAMES;
    @NacosValue(value = "${saleOrder.ipShopId}", autoRefreshed = true)
    private List<Integer> IP_SHOP_ID;
    @NacosValue(value = "${saleOrder.sjzShopId}", autoRefreshed = true)
    private List<String> SJZ_ROLE_NAMES;
    @NacosValue(value = "${saleOrder.b3ShopId}", autoRefreshed = true)
    private List<Integer> b3ShopId;


    @Override
    public SaleReturnOrderCreateResult create(SaleReturnOrderCreateRequest request) {
        return context.create(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleReturnOrderCancelResult cancel(SaleReturnOrderCancelRequest request) {
        log.info("cancel function SaleReturnOrderFacade start request = {}", JSON.toJSONString(request));

        SaleReturnOrderCancelResult result = returnOrderService.cancel(request);

        if (!result.getLine().stream().allMatch(a -> FinancialInvoiceStateEnum.NO_INVOICED.getValue().equals(a.getWhetherInvoice()))) {
            throw new BusinessException(ExceptionCode.FINANCIAL_INVOICE_STATE_NOT_ALL_NO_INVOICED);
        }

        billStoreWorkPreService.cancel(result.getSerialNo());
        //退货取消  预收单自动核销 状态：已核销
        List<AccountsPayableAccounting> list = accountingService
                .selectListByOriginSerialNoAndStatusAndType(result.getSerialNo(),
                        Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.RETURN_PENDING_REVIEW)
                        , Lists.newArrayList(ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT));
        accountingService.batchAudit(list.stream().filter(a -> result.getLine()
                        .stream()
                        .map(SaleReturnOrderDetailsResult.SaleReturnOrderLineVO::getStockId)
                        .collect(Collectors.toList()).contains(a.getStockId()))
                .map(AccountsPayableAccounting::getId)
                .collect(Collectors.toList()), FlywheelConstant.CANCEL_ORDER_AUDIT, UserContext.getUser().getUserName());

        customerBalanceAdd(request.getSerialNo(), result.getLine());
        return result;
    }

    /**
     * 同行销售单取消，客户余额回滚
     */
    private void customerBalanceAdd(String serialNo, List<SaleReturnOrderDetailsResult.SaleReturnOrderLineVO> line) {
        log.info("customerBalanceAdd function SaleReturnOrderFacade start and serialNo = {},line = {}", serialNo, JSON.toJSONString(line));
        BillSaleReturnOrder billSaleReturnOrder = returnOrderService.selectBySriginSerialNo(serialNo);

        if (!SaleReturnOrderTypeEnum.TO_B_JS_TH.equals(billSaleReturnOrder.getSaleReturnType())) {
            return;
        }
        List<BillSaleOrderLine> billSaleOrderLineList = billSaleOrderLineService.listByIds(line.stream().map(SaleReturnOrderDetailsResult.SaleReturnOrderLineVO::getSaleLineId).collect(Collectors.toList()));
        List<Integer> saleIdList = billSaleOrderLineList.stream().map(BillSaleOrderLine::getSaleId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(saleIdList))
            return;
        List<BillSaleOrder> saleOrderList = billSaleOrderService.listByIds(saleIdList);
        Map<Integer, BillSaleOrder> billSaleOrderMap = saleOrderList.stream()
                .filter(a -> !SaleOrderModeEnum.RETURN_POINT.equals(a.getSaleMode()))
                .collect(Collectors.toMap(BillSaleOrder::getId, Function.identity()));

        for (BillSaleOrderLine saleOrderLine : billSaleOrderLineList) {
            if (billSaleOrderMap.containsKey(saleOrderLine.getSaleId())) {
                BillSaleOrder saleOrder = billSaleOrderMap.get(saleOrderLine.getSaleId());

                Integer type = CustomerBalanceTypeEnum.ACCOUNT_BALANCE.getValue();
                BigDecimal amount = saleOrderLine.getClinchPrice();
                if (SaleOrderModeEnum.CONSIGN_FOR_SALE.equals(saleOrder.getSaleMode())) {
                    type = CustomerBalanceTypeEnum.JS_AMOUNT.getValue();
                    amount = saleOrderLine.getPreClinchPrice();
                }

                customerBalanceService.customerBalanceCmd(billSaleReturnOrder.getCustomerId(), billSaleReturnOrder.getCustomerContactId(),
                        amount, type,
                        billSaleReturnOrder.getShopId(), CustomerBalanceCmdTypeEnum.MINUS.getValue(), saleOrder.getCreatedId(), serialNo);
            }
        }
    }


    @Override
    public PageResult<SaleReturnOrderListResult> list(SaleReturnOrderListRequest request) {
        if (CollectionUtils.isEmpty(UserContext.getUser().getRoles())) {
            return PageResult.<SaleReturnOrderListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }

        request.setSaleReturnType(Optional.ofNullable(request.getSaleReturnType())
                .filter(v -> v != -1)
                .orElse(null));

        if (Objects.equals(request.getSaleReturnType(), SaleReturnOrderTypeEnum.TO_C_XS_TH.getValue())) {
            return tocReturnList(request);
        }

        return tobReturnList(request);
    }

    /**
     * @param request
     * @return
     */
    private PageResult<SaleReturnOrderListResult> tobReturnList(SaleReturnOrderListRequest request) {
        request.setShopIds(Lists.newArrayList(FlywheelConstant._SJZ));
        List<Customer> customerList = new ArrayList<>();
        if (StringTools.isNotNull(request.getCustomerName()) || StringTools.isNotNull(request.getCustomerPhone())) {
            //企业客户信息查询条件
            customerList = customerService.searchByNameOrPhone(request.getCustomerName(), request.getCustomerPhone());
            if (customerList.size() == 0) {
                return PageResult.<SaleReturnOrderListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(0)
                        .totalPage(0)
                        .build();
            }
            request.setCustomerIdList(customerList.stream()
                    .map(Customer::getId)
                    .collect(Collectors.toList()));
        }

        Page<BillSaleReturnOrder> page = returnOrderService.listByRequest(request);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<SaleReturnOrderListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        Map<Integer, Customer> customerMap = Optional.ofNullable(customerList.size() > 0 ? customerList : null)
                .orElseGet(() -> customerService.listByIds(page.getRecords()
                        .stream()
                        .map(BillSaleReturnOrder::getCustomerId)
                        .collect(Collectors.toList())))
                .stream()
                .distinct()
                .collect(Collectors.toMap(Customer::getId, c -> c, ((key1, key2) -> key1)));

        Map<Integer, Customer> finalCustomerMap = customerMap;
        return PageResult.<SaleReturnOrderListResult>builder()
                .result(Optional.ofNullable(page.getRecords())
                        .orElse(Lists.newArrayList())
                        .stream()
                        .map(t -> {
                            SaleReturnOrderListResult r = SaleReturnOrderConverter.INSTANCE.convertSaleReturnOrderListResult(t);
                            r.setCustomerName(Optional.ofNullable(finalCustomerMap.get(t.getCustomerId())).map(Customer::getCustomerName).orElse(null));
                            return r;
                        })
                        .collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    /**
     * @param request
     * @return
     */
    private PageResult<SaleReturnOrderListResult> tocReturnList(SaleReturnOrderListRequest request) {
        if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(ROLE_NAMES::contains))
            request.setShopIds(ObjectUtils.isNotEmpty(request.getShopId()) ? Lists.newArrayList(request.getShopId()) : null);
        else if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains)) {
            request.setShopIds(IP_SHOP_ID);
        } else if (UserContext.getUser().getStore().getId().equals(FlywheelConstant._ZB_ID) && UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(SJZ_ROLE_NAMES::contains)) {
            request.setShopIds(Lists.newArrayList(FlywheelConstant._SJZ));
        } else {
            request.setShopIds(Lists.newArrayList(UserContext.getUser().getStore().getId()));
        }
        List<CustomerContacts> customerContactsList = new ArrayList<>();
        if (StringTools.isNotNull(request.getCustomerName()) || StringTools.isNotNull(request.getCustomerPhone())) {
            //个人客户信息查询条件
            customerContactsList = customerContactsService.searchByNameOrPhone(request.getCustomerName(), request.getCustomerPhone());
            if (customerContactsList.size() == 0) {
                return PageResult.<SaleReturnOrderListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(0)
                        .totalPage(0)
                        .build();
            }
            request.setCustomerContactsIdList(customerContactsList.stream()
                    .map(CustomerContacts::getId)
                    .collect(Collectors.toList()));
        }
        Page<BillSaleReturnOrder> page = returnOrderService.listByRequest(request);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<SaleReturnOrderListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        //个人列表
        Map<Integer, CustomerContacts> customerMap = Optional.ofNullable(customerContactsList.size() > 0 ? customerContactsList : null)
                .orElseGet(() -> customerContactsService.listByIds(page.getRecords()
                        .stream()
                        .map(BillSaleReturnOrder::getCustomerContactId)
                        .collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(CustomerContacts::getId, Function.identity()));
        Map<Integer, BillSaleOrder> saleOrderMap = billSaleOrderService.listByIds(page.getRecords()
                        .stream()
                        .map(BillSaleReturnOrder::getSaleId)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(BillSaleOrder::getId, Function.identity()));
        Map<Integer, CustomerContacts> finalCustomerMap = customerMap;
        return PageResult.<SaleReturnOrderListResult>builder()
                .result(Optional.ofNullable(page.getRecords())
                        .orElse(Lists.newArrayList())
                        .stream()
                        .map(t -> {
                            SaleReturnOrderListResult r = SaleReturnOrderConverter.INSTANCE.convertSaleReturnOrderListResult(t);
                            r.setCustomerName(Optional.ofNullable(finalCustomerMap.get(t.getCustomerContactId()))
                                    .map(CustomerContacts::getName).orElse(null));
                            r.setSaleSerialNo(saleOrderMap.containsKey(t.getSaleId()) ?
                                    saleOrderMap.get(t.getSaleId()).getSerialNo() : null);
                            r.setSaleMode(saleOrderMap.containsKey(t.getSaleId()) ?
                                    saleOrderMap.get(t.getSaleId()).getSaleMode().getValue() :
                                    SaleOrderModeEnum.NORMAL.getValue());
                            return r;
                        })
                        .collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public SaleReturnOrderDetailsResult details(SaleReturnOrderDetailsRequest request) {
        BillSaleReturnOrder returnOrder = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> returnOrderService.getOne(Wrappers.<BillSaleReturnOrder>lambdaQuery()
                        .eq(BillSaleReturnOrder::getId, t.getId())
                        .or().eq(BillSaleReturnOrder::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.SALE_RETURN_ORDER_BILL_NOT_EXIST));

        SaleReturnOrderDetailsResult result = SaleReturnOrderConverter.INSTANCE.convertSaleReturnOrderDetailsResult(returnOrder);

        //联系人信息
        CustomerContacts customerContacts = Optional.ofNullable(returnOrder.getCustomerContactId())
                .map(customerContactsService::getById)
                .orElse(null);
        if (Objects.nonNull(customerContacts)) {
            result.setContactName(customerContacts.getName());
            result.setContactPhone(customerContacts.getPhone());
            result.setContactAddress(customerContacts.getAddress());
        }
        if (SaleReturnOrderTypeEnum.TO_B_JS_TH.equals(returnOrder.getSaleReturnType())) {
            Customer customer = Optional.ofNullable(returnOrder.getCustomerId())
                    .map(customerService::getById)
                    .orElse(null);
            if (Objects.nonNull(customerContacts)) {
                result.setCustomerName(customer.getCustomerName());
            }
        } else if (SaleReturnOrderTypeEnum.TO_C_XS_TH.equals(returnOrder.getSaleReturnType())) {
            BillSaleOrder saleOrder = billSaleOrderService.getById(result.getSaleId());
            if (ObjectUtils.isNotNull(saleOrder)) {
                //填充第一第二第三销售人
                Map<Long, String> userMap = userService.list().stream().collect(Collectors.toMap(User::getId, User::getName));
                if (ObjectUtils.isNotNull(saleOrder.getFirstSalesman()))
                    result.setFirstSalesmanName(userMap.get(saleOrder.getFirstSalesman().longValue()));
                if (ObjectUtils.isNotNull(saleOrder.getSecondSalesman()))
                    result.setSecondSalesmanName(userMap.get(saleOrder.getSecondSalesman().longValue()));
                if (ObjectUtils.isNotNull(saleOrder.getThirdSalesman()))
                    result.setThirdSalesmanName(userMap.get(saleOrder.getThirdSalesman().longValue()));
                result.setSaleSerialNo(saleOrder.getSerialNo());
            }
        }

        //订单来源
        Tag shopTag = Optional.ofNullable(returnOrder.getShopId())
                .map(tagService::selectByStoreManagementId)
                .orElse(null);
        if (Objects.nonNull(shopTag)) {
            result.setShopName(shopTag.getTagName());
        }
        //发货位置
        Tag tag = Optional.ofNullable(returnOrder.getDeliveryLocationId())
                .map(tagService::selectByStoreManagementId)
                .orElse(null);
        Map<Integer, String> map = subjectService.list().stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));

        List<BillSaleReturnOrderLineDetailsVO> lineDetailsVOList =
                billSaleReturnOrderLineService.selectBySaleReturnId(returnOrder.getId());
        result.setLines(SaleReturnOrderConverter.INSTANCE.convertSaleOrderLineVO(lineDetailsVOList));

        result.getLines().stream().forEach(vo -> {
            if (Objects.nonNull(tag)) {
                vo.setLocationName(tag.getTagName());
            }
            BillSaleOrder saleOrder = billSaleOrderService.selectBySaleLineId(vo.getSaleLineId());
            if (Objects.nonNull(saleOrder)) {
                result.setSaleSerialNo(saleOrder.getSerialNo());
                vo.setSaleSerialNo(saleOrder.getSerialNo());
                vo.setSaleMode(saleOrder.getSaleMode().getValue());
            }
            vo.setRightOfManagementName(map.getOrDefault(vo.getRightOfManagement(), "未知经营权"));
        });

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleReturnOrderExpressNumberUploadResult uploadExpressNumber(SaleReturnOrderExpressNumberUploadRequest request) {
        SaleReturnOrderExpressNumberUploadResult result = returnOrderService.uploadExpressNumber(request);
        List<BillSaleReturnOrderLine> billSaleReturnOrderLines = billSaleReturnOrderLineService
                .list(new LambdaQueryWrapper<BillSaleReturnOrderLine>()
                        .eq(BillSaleReturnOrderLine::getSaleReturnId, request.getSaleReturnId()));
        List<StoreWorKCreateRequest> shopWorKList = billSaleReturnOrderLines.stream()
                .map(t -> StoreWorKCreateRequest.builder()
                        .stockId(t.getStockId())
                        .goodsId(t.getGoodsId())
                        .originSerialNo(result.getSerialNo())
                        .workSource(result.getSaleReturnSource())
                        .expressNumber(request.getExpressNumber())
                        //收货
                        .workType(StoreWorkTypeEnum.INT_STORE.getValue())
                        .customerId(result.getCustomerId())
                        .customerContactId(result.getCustomerContactId())
                        .belongingStoreId(result.getDeliveryLocationId())
                        .build())
                .collect(Collectors.toList());

        //创建出库作业
        //出库单结果
        List<StoreWorkCreateResult> storeWorkList = billStoreWorkPreService.create(shopWorKList);

        result.setStoreWorkList(storeWorkList);

        //个人销售退货 应收埋点 个人销售退货单 上传快递单号后
        if (BusinessBillTypeEnum.TO_C_XS_TH.getValue().equals(result.getSaleReturnSource()) || BusinessBillTypeEnum.TO_C_ON_LINE_TH.getValue().equals(result.getSaleReturnSource())) {
            accountingService.createSaleApaByReturn(ImmutableMap.of(result.getId(),
                            result.getStoreWorkList().stream().map(StoreWorkCreateResult::getStockId).collect(Collectors.toList())),
                    ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE,
                    FinancialStatusEnum.RETURN_PENDING_REVIEW);
        }

        try {
            List<BillSaleOrderLine> saleLineList = billSaleOrderLineService.listByIds(billSaleReturnOrderLines.stream()
                    .map(BillSaleReturnOrderLine::getSaleLineId)
                    .collect(Collectors.toList()));
            BillSaleOrder sale = billSaleOrderService.getById(saleLineList.get(0).getSaleId());

            saleReturnOrderDeliveryProducers.sendMsg(SaleReturnOrderDeliveryMessage.builder()
                    .bizOrderCode(sale.getBizOrderCode())
                    .serialNo(sale.getSerialNo())
                    .returnSerialNo(result.getSerialNo())
                    .stockIdList(saleLineList.stream().map(BillSaleOrderLine::getStockId).collect(Collectors.toList()))
                    .build());
        } catch (Exception e) {
            log.error("销售退货客户发货通知消息发送异常-{}", e.getMessage(), e);
        }

        return result;
    }

    @Override
    public ImportResult<SaleReturnStockQueryImportResult> stockQueryImport(SaleReturnStockQueryImportRequest request) {
        Map<String, SaleReturnStockQueryImportRequest.ImportDto> stockSnMap = request.getDataList().stream()
                .collect(Collectors.toMap(SaleReturnStockQueryImportRequest.ImportDto::getStockSn, Function.identity()));

        //查询的表身号条件
        List<String> stockSnList = new ArrayList<>(stockSnMap.keySet());
        //查询的行状态条件
        List<Integer> stateList = Lists.newArrayList(SaleOrderLineStateEnum.ON_CONSIGNMENT.getValue(),
                SaleOrderLineStateEnum.CONSIGNMENT_SETTLED.getValue(), SaleOrderLineStateEnum.DELIVERED.getValue());
        Map<String, BillSaleOrderLineSettlementVO> map = Lists.partition(stockSnList, 500)
                .stream()
                .map(snList -> billSaleOrderLineService.listStockBySnAndState(snList, stateList, request.getCustomerId()))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(BillSaleOrderLineSettlementVO::getStockSn, Function.identity()));

        //校验不可改的数据
        Collection<String> errorStock = CollectionUtils.subtract(stockSnList, new ArrayList<>(map.keySet()));

        List<BillSaleOrderLineSettlementVO> resultList = new ArrayList<>(map.values());

        if (CollectionUtils.isEmpty(resultList)) {
            return ImportResult.<SaleReturnStockQueryImportResult>builder()
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

        return ImportResult.<SaleReturnStockQueryImportResult>builder()
                .successList(
                        resultList.stream()
                                .map(t -> {
                                    SaleReturnStockQueryImportResult r = SaleReturnOrderConverter.INSTANCE.convertSaleReturnStockQueryImportResult(t);
                                    r.setLocationName(shopMap.get(t.getDeliveryLocationId()));
                                    r.setRightOfManagementName(rightOfManagementMap.get(t.getRightOfManagement()));
                                    return r;
                                })
                                .collect(Collectors.toList()))
                .errList(new ArrayList<>(errorStock))
                .build();
    }

    @Override
    public PageResult<B3SaleReturnOrderListResult> b3Page(B3SaleReturnOrderListRequest request) {
        Integer status = request.getStatus();
        Page<B3SaleReturnOrderListResult> page;
        if (status == 1 || status == 3) {
            page = billSaleReturnOrderLineService.b3Page(IP_SHOP_ID, b3ShopId, request);
        } else {
            page = billStoreWorkPreService.b3Page(b3ShopId, request);
        }
        return PageResult.<B3SaleReturnOrderListResult>builder()
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .result(page.getRecords())
                .build();
    }

    @Override
    public void b3AddRemark(B3SaleReturnOrderAddRemarkRequest request) {
        billSaleReturnOrderLineService.b3AddRemark(request);
    }

    @Override
    public void updateExpressNo(String trackingNo, String bizCode) {
        returnOrderService.updateExpressNo(trackingNo, bizCode);
    }

    @Transactional
    @Override
    public void refund(SaleReturnOrderRefundRequest request) {
        //创建申请打款单时 需校验正向订单是否已被核销 未核销时不可创建
        List<Integer> stockIdList = request.getLines().stream().map(SaleReturnOrderRefundRequest.LineVO::getStockId).collect(Collectors.toList());

        BillSaleReturnOrder returnOrder = billSaleReturnOrderService.getById(request.getId());

        Assert.notNull(returnOrder.getSaleId(), "销售id不能为空");

        BillSaleOrder saleOrder = billSaleOrderService.getById(returnOrder.getSaleId());

        Assert.notNull(saleOrder, "销售单不能为空");

        if (accountingService.list(new LambdaQueryWrapper<AccountsPayableAccounting>()
                        .eq(AccountsPayableAccounting::getType, OriginTypeEnum.XS)
                        .eq(AccountsPayableAccounting::getOriginType, ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE)
                        .gt(AccountsPayableAccounting::getTotalPrice, BigDecimal.ZERO)
                        .in(AccountsPayableAccounting::getStockId, stockIdList)
                        .eq(AccountsPayableAccounting::getOriginSerialNo, saleOrder.getSerialNo())
                        .eq(AccountsPayableAccounting::getDeleted, WhetherEnum.NO.getValue()))
                .stream()
                .anyMatch(a -> FinancialStatusEnum.PENDING_REVIEW.equals(a.getStatus()))) {
            throw new OperationRejectedException(OperationExceptionCode.FORWARD_ORDERS_HAVE_NOT_BEEN_WRITTEN_OFF);
        }
        List<BillSaleReturnOrderLineDetailsVO> detailsVOS = billSaleReturnOrderLineService.selectBySaleReturnId(request.getId());
        if (detailsVOS.stream().filter(a -> stockIdList.contains(a.getStockId()))
                .anyMatch(a -> a.getWhetherOperate() == 1)) {
            throw new OperationRejectedException(OperationExceptionCode.DO_NOT_OPERATE);
        }
//        List<PurchaseSubject> purchaseSubjectList = purchaseSubjectService.subjectCompanyQryBySubjectPayment(request.getSubjectPayment());
//        FinancialStatementCompany company = statementCompanyService.getOne(new LambdaQueryWrapper<FinancialStatementCompany>()
//                .eq(FinancialStatementCompany::getCompanyName, request.getSubjectPayment()));

        StoreRelationshipSubject subject = relationshipSubjectService.getByShopId(returnOrder.getShopId());
        Assert.isTrue(Objects.nonNull(subject), "退款打款主体不存在");
        Integer subjectPayment = subject.getSubjectId();

        ApplyFinancialPaymentCreateRequest createRequest = new ApplyFinancialPaymentCreateRequest();

        createRequest.setCustomerName(request.getCustomerName());
        createRequest.setTypePayment(ApplyFinancialPaymentTypeEnum.PERSONAL_SALES_RETURNS.getValue());
        createRequest.setPricePayment(request.getRefundAmount());
        createRequest.setSubjectPayment(subjectPayment);
        createRequest.setBankName(request.getBankName());
        createRequest.setBankAccount(request.getBankAccount());
        createRequest.setBankCard(request.getBankCard());
        createRequest.setBankCustomerName(request.getBankCustomerName());
        createRequest.setShopId(UserContext.getUser().getStore().getId());
        createRequest.setSalesMethod(FinancialSalesMethodEnum.REFUND.getValue());
        createRequest.setManualCreation(WhetherEnum.YES.getValue());
        createRequest.setWhetherUse(WhetherEnum.YES.getValue());
        createRequest.setOriginSerialNo(returnOrder.getSerialNo());
        ApplyFinancialPaymentCreateResult result = applyFinancialPaymentService.create(createRequest);

        returnOrderService.updateRefundFlag(request.getId(), SaleOrderReturnFlagEnum.TK);

        //申请打款单 和 stock 关联关系
        List<AccountStockRelation> relationList = request.getLines()
                .stream()
                .map(a -> {
                    AccountStockRelation relation = new AccountStockRelation();
                    relation.setStockId(a.getStockId());
                    relation.setAfpId(result.getId());
                    relation.setOriginPrice(a.getReturnPrice());
                    relation.setOriginSerialNo(request.getSerialNo());
                    return relation;
                }).collect(Collectors.toList());
        stockRelationService.saveBatch(relationList);

        detailsVOS.forEach(a -> {
            billSaleReturnOrderLineService.updateById(BillSaleReturnOrderLine.builder().id(a.getId()).whetherOperate(1).build());
        });

        accountingService.list(new LambdaQueryWrapper<AccountsPayableAccounting>()
                        .eq(AccountsPayableAccounting::getOriginSerialNo, request.getSerialNo())
                        .in(AccountsPayableAccounting::getStockId, request.getLines().stream().map(SaleReturnOrderRefundRequest.LineVO::getStockId).collect(Collectors.toList())))
                .forEach(a -> {
                    AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                    accounting.setId(a.getId());
                    accounting.setAfpSerialNo(result.getSerialNo());
                    accounting.setAfpId(result.getId());
                    accountingService.updateById(accounting);
                });
    }

    @Transactional
    @Override
    public void billErrRefund(SaleReturnOrderBillErrRefundRequest request) {

        //销售单的关联信息
        List<Integer> arcIds = stockRelationService.list(new LambdaQueryWrapper<AccountStockRelation>()
                        .in(AccountStockRelation::getOriginSerialNo, request.getLines().stream()
                                .map(a -> billSaleOrderService.selectBySaleLineId(a.getSaleLineId()))
                                .map(BillSaleOrder::getSerialNo).collect(Collectors.toSet())))
                .stream()
                .map(AccountStockRelation::getArcId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(arcIds)) {
            log.warn("关联关系为空=" + JSON.toJSONString(request));
            return;
        }

        List<AccountReceiptConfirm> accountReceiptConfirmList = accountReceiptConfirmService.listByIds(arcIds);

        if (CollectionUtils.isEmpty(accountReceiptConfirmList)) {
            log.warn("关联关系为空2=" + JSON.toJSONString(request));
            return;
        }

        if (accountReceiptConfirmList.stream().allMatch(a -> a.getStatus().equals(AccountReceiptConfirmStatusEnum.WAIT.getValue()))) {

            accountReceiptConfirmList.forEach(a -> accountReceiptConfirmService.updateById(AccountReceiptConfirm.builder().id(a.getId()).status(AccountReceiptConfirmStatusEnum.FINISH.getValue()).build()));

            Map<String, List<AccountReceiptConfirm>> collect = accountReceiptConfirmList.stream().collect(Collectors.groupingBy(AccountReceiptConfirm::getOriginSerialNo));

            if (MapUtils.isNotEmpty(collect)) {
                //销售单号
                Set<String> set = collect.keySet();

                List<BillSaleReturnOrder> saleReturnOrderList = new ArrayList<>();

                List<BillSaleOrder> saleOrderList = billSaleOrderService.list(Wrappers.<BillSaleOrder>lambdaQuery().in(BillSaleOrder::getSerialNo, new ArrayList<>(set)));

                if (CollectionUtils.isNotEmpty(saleOrderList)) {
                    saleReturnOrderList = billSaleReturnOrderService.list(Wrappers.<BillSaleReturnOrder>lambdaQuery().in(BillSaleReturnOrder::getSaleId, saleOrderList.stream().map(BillSaleOrder::getId).collect(Collectors.toList())));
                }

                List<AccountsPayableAccounting> saleAccounting = set.stream().map(s -> accountingService
                        .selectListByOriginSerialNoAndStatusAndType(s,
                                Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.RETURN_PENDING_REVIEW)
                                , Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE))).flatMap(Collection::stream).collect(Collectors.toList());

                List<AccountsPayableAccounting> saleReturnAccounting = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(saleReturnOrderList)) {
                    Map<String, List<BillSaleReturnOrder>> map = saleReturnOrderList.stream().collect(Collectors.groupingBy(BillSaleReturnOrder::getSerialNo));

                    saleReturnAccounting = map.keySet().stream().map(s -> accountingService
                            .selectListByOriginSerialNoAndStatusAndType(s,
                                    Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.RETURN_PENDING_REVIEW)
                                    , Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE))).flatMap(Collection::stream).collect(Collectors.toList());
                }

                List<Integer> stockIdList = request.getLines().stream().filter(Objects::nonNull).map(SaleReturnOrderBillErrRefundRequest.SaleReturnOrderLineVO::getStockId).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(saleAccounting)) {
                    accountingService.batchAudit(saleAccounting.stream().filter(a -> stockIdList.contains(a.getStockId()))
                            .map(AccountsPayableAccounting::getId)
                            .collect(Collectors.toList()), FlywheelConstant.WRONG_AIRWAY_BILL_AUDIT, UserContext.getUser().getUserName());
                }

                if (CollectionUtils.isNotEmpty(saleReturnAccounting)) {
                    accountingService.batchAudit(saleReturnAccounting.stream().filter(a -> stockIdList.contains(a.getStockId()))
                            .map(AccountsPayableAccounting::getId)
                            .collect(Collectors.toList()), FlywheelConstant.WRONG_AIRWAY_BILL_AUDIT, UserContext.getUser().getUserName());
                }
            }

            returnOrderService.updateRefundFlag(request.getId(), SaleOrderReturnFlagEnum.CDTH);

            billSaleReturnOrderLineService.updateBatchById(request.getLines().stream().map(a -> BillSaleReturnOrderLine.builder().id(a.getId()).whetherOperate(1).build()).collect(Collectors.toList()));
        } else {
            throw new OperationRejectedException(OperationExceptionCode.ACCOUNT_RECEIPT_CONFIRM_STATUS_NOT_WAIT);
        }
    }


    @Override
    public BillSaleReturnOrderResult singleByBizCode(String bizCode) {
        LambdaQueryWrapper<BillSaleReturnOrder> qw = Wrappers.<BillSaleReturnOrder>lambdaQuery()
                .eq(BillSaleReturnOrder::getBizOrderCode, bizCode)
                .last("limit 1");

        BillSaleReturnOrder one = billSaleReturnOrderService.getOne(qw);
        return SaleReturnOrderConverter.INSTANCE.to(one);
    }

    @Override
    public PageResult<SaleReturnOrderExportResult> exportOrderReturn(SaleReturnOrderExportRequest request) {
        if (CollectionUtils.isEmpty(UserContext.getUser().getRoles()) || request.getSaleReturnType() != SaleReturnOrderTypeEnum.TO_C_XS_TH.getValue()) {
            return PageResult.<SaleReturnOrderExportResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        //导出手选择项
        if (Objects.nonNull(request.getDocBatchIds()) && request.getDocBatchIds().size() == 0) {
            request.setDocBatchIds(null);
        }
        request.setSaleReturnType(Optional.ofNullable(request.getSaleReturnType())
                .filter(v -> v != -1)
                .orElse(null));

        if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(ROLE_NAMES::contains)) {
            request.setShopIds(ObjectUtils.isNotEmpty(request.getShopId()) ? Lists.newArrayList(request.getShopId()) : null);
        } else if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains)) {
            request.setShopIds(IP_SHOP_ID);
        } else if (UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(SJZ_ROLE_NAMES::contains)) {
            request.setShopIds(Lists.newArrayList(FlywheelConstant._SJZ));
        } else {
            request.setShopIds(Lists.newArrayList(UserContext.getUser().getStore().getId()));
        }
        List<CustomerContacts> customerContactsList = new ArrayList<>();
        if (StringTools.isNotNull(request.getCustomerName()) || StringTools.isNotNull(request.getCustomerPhone())) {
            //个人客户信息查询条件
            customerContactsList = customerContactsService.searchByNameOrPhone(request.getCustomerName(), request.getCustomerPhone());
            if (customerContactsList.size() == 0) {
                return PageResult.<SaleReturnOrderExportResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(0)
                        .totalPage(0)
                        .build();
            }
            request.setCustomerContactsIdList(customerContactsList.stream()
                    .map(CustomerContacts::getId)
                    .collect(Collectors.toList()));
        }
        Page<SaleReturnOrderExportResult> page = billSaleReturnOrderLineService.exportOrderReturn(request);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<SaleReturnOrderExportResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        //经营权 商品归属
        Map<Integer, String> purchaseSubjectMap = purchaseSubjectService.list()
                .stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
        Map<Integer, String> storeMap = storeManagementService.getStoreMap();
        //个人列表
        Map<Integer, CustomerContacts> customerMap = Optional.ofNullable(customerContactsList.size() > 0 ? customerContactsList : null)
                .orElseGet(() -> customerContactsService.listByIds(page.getRecords()
                        .stream()
                        .map(SaleReturnOrderExportResult::getCustomerContactId)
                        .collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(CustomerContacts::getId, Function.identity()));
        Map<Integer, BillSaleOrder> saleOrderMap = billSaleOrderService.listByIds(page.getRecords()
                        .stream()
                        .map(SaleReturnOrderExportResult::getSaleId)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(BillSaleOrder::getId, Function.identity()));
        Map<Integer, BillSaleOrderLine> orderLineMap = billSaleOrderLineService.listByIds(page.getRecords()
                        .stream()
                        .map(SaleReturnOrderExportResult::getSaleLineId)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(BillSaleOrderLine::getId, Function.identity()));
        //查商品
        Map<Integer, WatchDataFusion> goodsWatchMap = goodsWatchService.getWatchDataFusionListByStockIds(page.getRecords()
                        .stream()
                        .map(SaleReturnOrderExportResult::getStockId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity()));
        //填充第一第二第三销售人
        Map<Long, String> userMap = userService.list().stream().collect(Collectors.toMap(User::getId, User::getName));
        return PageResult.<SaleReturnOrderExportResult>builder()
                .result(Optional.ofNullable(page.getRecords())
                        .orElse(Lists.newArrayList())
                        .stream()
                        .map(t -> {
                            t.setCustomerName(Optional.ofNullable(customerMap.get(t.getCustomerContactId()))
                                    .map(CustomerContacts::getName).orElse(null));
                            BillSaleOrder saleOrder = saleOrderMap.getOrDefault(t.getSaleId(), new BillSaleOrder());
                            t.setSaleSerialNo(saleOrder.getSerialNo());
                            t.setSaleMode(saleOrder.getSaleMode().getValue());
                            t.setLocationName(storeMap.get(saleOrder.getDeliveryLocationId()));
                            if (ObjectUtils.isNotNull(saleOrder.getFirstSalesman()))
                                t.setFirstSalesmanName(userMap.get(saleOrder.getFirstSalesman().longValue()));

                            WatchDataFusion goods = goodsWatchMap.getOrDefault(t.getStockId(), new WatchDataFusion());
                            t.setFiness(goods.getFiness());
                            t.setBrandName(goods.getBrandName());
                            t.setSeriesName(goods.getSeriesName());
                            t.setModel(goods.getModel());
                            t.setStockSn(goods.getStockSn());
                            t.setAttachment(goods.getAttachment());
                            t.setPricePub(goods.getPricePub());

                            // 定位GMV绩效信息临时日志
//                            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//                            StackTraceElement caller = stackTrace[1];
//                            String className = caller.getClassName();
//                            String methodName = caller.getMethodName();
//                            log.info("调用{}类,{}方法,orderLineMap参数: {}, t参数: {}", className, methodName, JSON.toJSONString(orderLineMap), JSON.toJSONString(t));

                            BillSaleOrderLine orderLine = orderLineMap.getOrDefault(t.getSaleLineId(), new BillSaleOrderLine());
                            t.setGmvPerformance(orderLine.getGmvPerformance());
                            t.setClinchPrice(orderLine.getClinchPrice());
                            t.setTagPrice(orderLine.getTagPrice());
                            t.setTocPrice(orderLine.getTocPrice());
                            t.setTobPrice(orderLine.getTobPrice());
                            t.setConsignmentPrice(orderLine.getConsignmentPrice());
                            // 添加日志信息
                            log.info("BillSaleOrderLine: {}", JSON.toJSONString(orderLine));
                            log.info("GmvPerformance: {}", orderLine.getGmvPerformance());
                            log.info("ClinchPrice: {}", orderLine.getClinchPrice());
                            log.info("TagPrice: {}", orderLine.getTagPrice());
                            log.info("TocPrice: {}", orderLine.getTocPrice());
                            log.info("TobPrice: {}", orderLine.getTobPrice());
                            log.info("ConsignmentPrice: {}", orderLine.getConsignmentPrice());

                            t.setShopName(storeMap.get(t.getShopId()));
                            t.setRightOfManagementName(purchaseSubjectMap.get(t.getRightOfManagement()));
                            return t;
                        })
                        .collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Resource
    private StoreManagementMapper storeManagementMapper;

    @Resource
    private StockService stockService;

    @Override
    public List<SaleReturnToTimeoutResult> toTimeout(SaleReturnToTimeoutRequest request) {

        com.baomidou.mybatisplus.core.toolkit.Assert.isTrue(Objects.nonNull(request.getTimeoutDay()) || StringUtils.isNotBlank(request.getTimeoutDate()), "没有指定超时参数");

        //进行中 门店通知 的单据
        List<BillSaleReturnOrder> billSaleReturnOrderList = billSaleReturnOrderService.list(Wrappers.<BillSaleReturnOrder>lambdaQuery()
                .eq(BillSaleReturnOrder::getSaleReturnState, BusinessBillStateEnum.UNDER_WAY)
                .in(CollectionUtils.isNotEmpty(request.getStoreIdList()), BillSaleReturnOrder::getShopId, request.getStoreIdList())
        );

        /**
         * 门店名称
         */
        Map<Integer, StoreManagementInfo> storeManagementInfoMap = new HashMap<>();
        /**
         * 门店店长列表
         */
        Map<Integer, List<String>> shopManagerListMap = new HashMap<>();

        List<SaleReturnToTimeoutResult> result = new ArrayList<>();

        for (BillSaleReturnOrder billSaleReturnOrder : billSaleReturnOrderList) {

            List<BillSaleReturnOrderLine> billSaleReturnOrderLineList = billSaleReturnOrderLineService.list(Wrappers.<BillSaleReturnOrderLine>query()
                    .lambda()
                    .eq(BillSaleReturnOrderLine::getSaleReturnId, billSaleReturnOrder.getId())
                    .eq(BillSaleReturnOrderLine::getSaleReturnLineState, SaleReturnOrderLineStateEnum.UPLOAD_EXPRESS_NUMBER)
                    .and(StringUtils.isNotBlank(request.getTimeoutDate()) && Objects.nonNull(request.getTimeoutDay()),
                            i -> i.lt(BillSaleReturnOrderLine::getUpdatedTime, DateUtil.parse(request.getTimeoutDate()))
                                    .or().apply(" (DATEDIFF(now(),updated_time)) > " + request.getTimeoutDay())
                    )
                    .le(StringUtils.isNotBlank(request.getTimeoutDate()), BillSaleReturnOrderLine::getUpdatedTime, DateUtil.parse(request.getTimeoutDate()))
                    .and(Objects.nonNull(request.getTimeoutDay()), i -> i.apply(" (DATEDIFF(now(),updated_time)) > " + request.getTimeoutDay()))
            );

            StoreManagementInfo storeManagementInfo;

            List<String> shopManagerList;

            if (storeManagementInfoMap.containsKey(billSaleReturnOrder.getShopId())) {
                storeManagementInfo = storeManagementInfoMap.get(billSaleReturnOrder.getShopId());
            } else {
                storeManagementInfo = storeManagementService.selectInfoById(billSaleReturnOrder.getShopId());

                if (Objects.isNull(storeManagementInfo)) {
                    storeManagementInfo = new StoreManagementInfo();
                    storeManagementInfo.setName("查询不到此门店");
                }
                storeManagementInfoMap.put(billSaleReturnOrder.getShopId(), storeManagementInfo);
            }

            if (shopManagerListMap.containsKey(billSaleReturnOrder.getShopId())) {
                shopManagerList = shopManagerListMap.get(billSaleReturnOrder.getShopId());
            } else {
                shopManagerList = storeManagementMapper.listByShopManager(billSaleReturnOrder.getShopId(), request.getRoleId());

                if (CollectionUtils.isEmpty(shopManagerList)) {
                    shopManagerList = Lists.newArrayList("@all");
                }
                shopManagerListMap.put(billSaleReturnOrder.getShopId(), shopManagerList);
            }

            Map<Integer, StockExt> stockMap = Optional.ofNullable(billSaleReturnOrderLineList.stream()
                            .map(BillSaleReturnOrderLine::getStockId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()))
                    .map(stockService::selectByStockIdList)
                    .orElseGet(() -> new ArrayList<>())
                    .stream()
                    .collect(Collectors.toMap(StockExt::getStockId, Function.identity()));

            for (BillSaleReturnOrderLine billSaleReturnOrderLine : billSaleReturnOrderLineList) {

                StockExt stock = stockMap.get(billSaleReturnOrderLine.getStockId());
                result.add(SaleReturnToTimeoutResult.builder()
                        .serialNo(billSaleReturnOrder.getSerialNo())
                        .storeName(storeManagementInfo.getName())
                        .brandName(Objects.nonNull(stock) ? stock.getBrandName() : "未知")
                        .seriesName(Objects.nonNull(stock) ? stock.getSeriesName() : "未知")
                        .model(Objects.nonNull(stock) ? stock.getModel() : "未知")
                        .stockSn(Objects.nonNull(stock) ? stock.getStockSn() : "未知")
                        .timeoutMsg(String.format("您已超过%s天未接收该表",
                                StringUtils.isNotBlank(request.getTimeoutDate()) ? DateUtil.betweenDay(DateUtil.parse(request.getTimeoutDate()), DateUtil.date(), true) :
                                        DateUtil.betweenDay(billSaleReturnOrderLine.getUpdatedTime(), DateUtil.date(), true)))
                        .msgManList(shopManagerList)
                        .build());
            }
        }

        return result;
    }
}
