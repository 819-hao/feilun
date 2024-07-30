package com.seeease.flywheel.serve.financial.rpc;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.IAccountsPayableAccountingFacade;
import com.seeease.flywheel.financial.request.AccountsPayableAccountingBatchAuditRequest;
import com.seeease.flywheel.financial.request.AccountsPayableAccountingCreateAfpRequest;
import com.seeease.flywheel.financial.request.AccountsPayableAccountingQueryRequest;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCreateRequest;
import com.seeease.flywheel.financial.result.AccountsPayableAccountingPageResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentCreateResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.convert.ApplyFinancialPaymentConvert;
import com.seeease.flywheel.serve.financial.entity.AccountReceiptConfirm;
import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.service.*;
import com.seeease.flywheel.serve.financial.template.payment.PaymentDTemplate;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
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
 * @date 2023/2/27
 */
@DubboService(version = "1.0.0")
@Slf4j
public class AccountsPayableAccountingFacade implements IAccountsPayableAccountingFacade {

    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    private AccountStockRelationService stockRelationService;
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService contactsService;
    @Resource
    private PurchaseSubjectService subjectService;
    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;
    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;
    @Resource
    private AccountReceStateRelService accountReceStateRelService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private BillPurchaseLineService purchaseLineService;
    @Resource
    private UserService userService;
    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private PaymentDTemplate paymentDTemplate;

    private static final Set<FinancialStatusEnum> STATUS_ENUM = ImmutableSet.of(FinancialStatusEnum.RETURN_PENDING_REVIEW,
            FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW);

    @Override
    public PageResult<AccountsPayableAccountingPageResult> query(AccountsPayableAccountingQueryRequest request) {
        return getAccountsPayableAccountingPageResultPageResult(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAudit(AccountsPayableAccountingBatchAuditRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()))
            return;

        List<AccountsPayableAccounting> list = accountingService.listByIds(request.getIds());
        list.forEach(a -> {
            if (!STATUS_ENUM.contains(a.getStatus()))
                throw new BusinessException(ExceptionCode.BATCH_AUDIT_FAIL);
        });
        accountingService.batchAudit(request.getIds(), request.getAuditDescription(), UserContext.getUser().getUserName());

        Map<Integer, AccountsPayableAccounting> map = list.stream()
                .collect(Collectors.toMap(AccountsPayableAccounting::getStockId, Function.identity()));
        Map<Integer, WatchDataFusion> watchDataFusionMap = goodsWatchService.getWatchDataFusionListByStockIds(
                        list.stream().map(AccountsPayableAccounting::getStockId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getStockId, watchDataFusion -> watchDataFusion));
        //查询同行寄售采购单
        List<AccountsPayableAccounting> accountingList = purchaseLineService
                .listByStockIds(list.stream().map(AccountsPayableAccounting::getStockId).collect(Collectors.toList()))
                .stream().map(t ->
                        {
                            //同行寄售 应收单 核销后 自动生成 应付单 状态：待核销
                            Customer customer = customerService.getById(t.getCustomerId());
                            AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                            accounting.setSerialNo(SerialNoGenerator.generateAmountPayableSerialNo());
                            accounting.setAfpId(map.get(t.getStockId()).getAfpId());
                            accounting.setAfpSerialNo(map.get(t.getStockId()).getAfpSerialNo());
                            accounting.setPurchaseId(t.getId());
                            accounting.setDemanderStoreId(t.getDemanderStoreId());
                            accounting.setOriginSerialNo(t.getSerialNo());
                            accounting.setStockId(t.getStockId());
                            accounting.setStockSn(t.getStockSn());
                            accounting.setBrandName(watchDataFusionMap.get(t.getStockId()).getBrandName());
                            accounting.setSeriesName(watchDataFusionMap.get(t.getStockId()).getSeriesName());
                            accounting.setModel(watchDataFusionMap.get(t.getStockId()).getModel());
                            accounting.setTotalPrice(t.getPurchasePrice());
                            accounting.setCustomerId(t.getCustomerId());
                            accounting.setApplicant(t.getCreatedBy());
                            accounting.setShopId(t.getStoreId());
                            accounting.setBelongId(t.getPurchaseSubjectId());
                            accounting.setCustomerContactId(t.getCustomerContactId());
                            accounting.setSalesMethod(PurchaseModeEnum.convert(PurchaseModeEnum.fromCode(t.getPurchaseMode())));
                            accounting.setCustomerType(Objects.nonNull(customer) ? customer.getType().getValue() : null);
                            accounting.setType(ReceiptPaymentTypeEnum.AMOUNT_PAYABLE);
                            accounting.setClassification(BusinessBillTypeEnum.convertClassification(BusinessBillTypeEnum.fromValue(t.getPurchaseSource())));
                            accounting.setStatus(FinancialStatusEnum.PENDING_REVIEW);
                            accounting.setOriginType(OriginTypeEnum.CG);
                            return accounting;
                        }
                ).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(accountingList)) {
            accountingService.saveBatch(accountingList);
        }
    }

    @Override
    public PageResult<AccountsPayableAccountingPageResult> export(AccountsPayableAccountingQueryRequest request) {
        //导出手选择项
        if (Objects.nonNull(request.getDocBatchIds()) && request.getDocBatchIds().size() == 0) {
            request.setDocBatchIds(null);
        }
        return getAccountsPayableAccountingPageResultPageResult(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAfp(AccountsPayableAccountingCreateAfpRequest request) {

        /**
         * 不存在的
         */
        List<String> notExit = new ArrayList<>();
        /**
         * 重复
         */
        List<String> repeat = new ArrayList<>();

        /**
         * 符合的条件数据
         */
        List<AccountsPayableAccounting> list = new ArrayList<>();

        List<AccountsPayableAccountingCreateAfpRequest.AccountsPayableAccountingCreateAfpRequestDto> mapperList = new ArrayList<>();

        for (AccountsPayableAccountingCreateAfpRequest.AccountsPayableAccountingCreateAfpRequestDto mapper : request.getList()) {

            List<AccountsPayableAccounting> payableAccountingList = accountingService.list(Wrappers.<AccountsPayableAccounting>lambdaQuery()
                    .eq(AccountsPayableAccounting::getStockId, mapper.getStockId())
                    .eq(AccountsPayableAccounting::getStockSn, mapper.getStockSn())
                    .eq(AccountsPayableAccounting::getStatus, FinancialStatusEnum.PENDING_REVIEW)
                    .eq(AccountsPayableAccounting::getType, ReceiptPaymentTypeEnum.AMOUNT_PAYABLE)
                    .eq(AccountsPayableAccounting::getOriginSerialNo, mapper.getOriginSerialNo())
            );

            if (CollectionUtils.isEmpty(payableAccountingList)) {
                notExit.add(mapper.getStockSn());
            }

            if (payableAccountingList.size() > 1) {
                repeat.add(mapper.getStockSn());
            }

            list.add(payableAccountingList.get(FlywheelConstant.INDEX));
            mapperList.add(mapper);
        }

        log.warn("不存在的表身号，{}", JSON.toJSONString(notExit));
        log.warn("重复的表身号，{}", JSON.toJSONString(repeat));

        ApplyFinancialPaymentCreateRequest createRequest = ApplyFinancialPaymentConvert.INSTANCE.convertApplyFinancialPaymentCreateRequest(request);
        createRequest.setPricePayment(mapperList.stream().map(AccountsPayableAccountingCreateAfpRequest.AccountsPayableAccountingCreateAfpRequestDto::getSettlePrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        createRequest.setTypePayment(ApplyFinancialPaymentTypeEnum.PEER_PROCUREMENT.getValue());
        createRequest.setSalesMethod(FinancialSalesMethodEnum.PURCHASE_BATCH.getValue());
        createRequest.setPayment(ApplyFinancialPaymentEnum.PAID.getValue());
        createRequest.setShopId(UserContext.getUser().getStore().getId());
        createRequest.setWhetherUse(WhetherEnum.YES.getValue());
        createRequest.setSubjectPayment(request.getSubjectPayment());
        createRequest.setOriginSerialNo(mapperList.stream().map(AccountsPayableAccountingCreateAfpRequest.AccountsPayableAccountingCreateAfpRequestDto::getOriginSerialNo).collect(Collectors.joining(",")));
        ApplyFinancialPaymentCreateResult result = applyFinancialPaymentService.create(createRequest);

        //申请打款单 和 商品关联关系
        List<AccountStockRelation> relationList = list.stream()
                .map(a -> AccountStockRelation.builder()
                        .afpId(result.getId())
                        .stockId(a.getStockId())
                        .originPrice(a.getTotalPrice())
                        .originSerialNo(a.getOriginSerialNo())
                        .build())
                .collect(Collectors.toList());
        stockRelationService.saveBatch(relationList);
        paymentDTemplate.updatePayable(new JSONObject().fluentPut("afp", applyFinancialPaymentService.getById(result.getId())));
    }

    private PageResult<AccountsPayableAccountingPageResult> getAccountsPayableAccountingPageResultPageResult(AccountsPayableAccountingQueryRequest request) {
        switch (request.getUseScenario()) {
            case AMOUNT_PAYABLE:
                if (Objects.isNull(request.getType())) {
                    request.setTypeList(Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_PAYABLE.getValue(), ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT.getValue()));
                } else if (ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE.getValue().equals(request.getType()) || ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT.getValue().equals(request.getType())) {
                    request.setType(null);
                }
                break;
            case AMOUNT_RECEIVABLE:
                if (Objects.isNull(request.getType())) {
                    request.setTypeList(Lists.newArrayList(ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE.getValue(), ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT.getValue()));
                } else if (ReceiptPaymentTypeEnum.AMOUNT_PAYABLE.getValue().equals(request.getType()) || ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT.getValue().equals(request.getType())) {
                    request.setType(null);
                }
                break;
        }
        if (StringUtils.isNotEmpty(request.getSearchCustomerCriteria())) {
            List<Integer> customerIds = customerService.searchByNameOrPhone(request.getSearchCustomerCriteria(), null)
                    .stream().map(Customer::getId).collect(Collectors.toList());
            List<Integer> contactsIds = contactsService.searchByName(request.getSearchCustomerCriteria())
                    .stream().map(CustomerContacts::getId).collect(Collectors.toList());
            request.setContactsIds(contactsIds.size() > 0 ? contactsIds : Lists.newArrayList(-1));
            request.setCustomerIds(customerIds.size() > 0 ? customerIds : Lists.newArrayList(-1));
        }
        if (StringUtils.isNotEmpty(request.getPurchaseBy())) {
            List<User> list = userService.list(new LambdaQueryWrapper<User>().eq(User::getName, request.getPurchaseBy()));
            if (CollectionUtils.isNotEmpty(list))
                request.setPurchaseIds(list.stream().map(User::getId).collect(Collectors.toList()));
        }
        Page<AccountsPayableAccountingPageResult> page = accountingService.page(request);
        List<AccountsPayableAccountingPageResult> list = page.getRecords();
        if (CollectionUtils.isEmpty(list))
            return PageResult.<AccountsPayableAccountingPageResult>builder()
                    .result(Lists.newArrayList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();


        List<Customer> customers = Optional.ofNullable(list.stream()
                        .map(AccountsPayableAccountingPageResult::getCustomerId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(customerService::listByIds).orElse(new ArrayList<>());

        Map<Integer, String> customerMap = customers.stream().collect(Collectors.toMap(Customer::getId, Customer::getCustomerName, (k1, k2) -> k2))
//                .map(t -> t.stream().collect())
//                .orElse(Collections.EMPTY_MAP)
                ;


        Map<Integer, String> contactsMap = Optional.ofNullable(list.stream()
                        .map(AccountsPayableAccountingPageResult::getCustomerContactId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(contactsService::listByIds)
                .map(t -> t.stream().collect(Collectors.toMap(CustomerContacts::getId, CustomerContacts::getName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);
        Map<Integer, String> subjectMap = Optional.ofNullable(list.stream()
                        .map(AccountsPayableAccountingPageResult::getBelongId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(subjectService::listByIds)
                .map(t -> t.stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);
        Map<Integer, String> shopMap = storeManagementService.getStoreMap();
        Map<Long, String> userMap = userService.list().stream().collect(Collectors.toMap(User::getId, User::getName, (k1, k2) -> k2));

        Map<String, List<AccountStockRelation>> map = stockRelationService.list(new LambdaQueryWrapper<AccountStockRelation>()
                        .in(AccountStockRelation::getOriginSerialNo, list.stream().map(AccountsPayableAccountingPageResult::getOriginSerialNo).collect(Collectors.toList())))
                .stream()
                .collect(Collectors.groupingBy(AccountStockRelation::getOriginSerialNo));

        list.forEach(r -> {
            r.setCustomerName(customerMap.getOrDefault(r.getCustomerId(), "-"));
            r.setCustomerContactName(contactsMap.getOrDefault(r.getCustomerContactId(), "-"));
            r.setBelongName(subjectMap.getOrDefault(r.getBelongId(), "-"));
            r.setShopName(shopMap.getOrDefault(r.getShopId(), "-"));
            r.setDemanderStoreName(shopMap.getOrDefault(r.getDemanderStoreId(), "-"));

            Customer customer = customers.stream().filter(t -> t.getId().equals(r.getCustomerId())).findFirst().orElse(null);

            r.setCustomerNameOrCustomerContactName(Objects.nonNull(customer) && customer.getType() == CustomerTypeEnum.ENTERPRISE ? r.getCustomerName() : r.getCustomerContactName());

            if (Objects.nonNull(r.getPurchaseId()))
                r.setPurchaseBy(userMap.getOrDefault(r.getPurchaseId().longValue(), "-"));
            if (map.containsKey(r.getOriginSerialNo())) {
                List<AccountStockRelation> stockRelationList = map.get(r.getOriginSerialNo());
                Map<Integer, Integer> arcMap = stockRelationList.stream()
                        .filter(a -> Objects.nonNull(a.getArcId()))
                        .collect(Collectors.toMap(AccountStockRelation::getStockId, AccountStockRelation::getArcId, (e1, e2) -> e1));
                if (arcMap.containsKey(r.getStockId())) {
                    AccountReceiptConfirm confirm = accountReceiptConfirmService.getById(arcMap.get(r.getStockId()));
                    if (Objects.nonNull(confirm) && r.getTotalPrice().compareTo(BigDecimal.ZERO) < 0)
                        r.setArcSerialNo(confirm.getSerialNo());
                }
                Map<Integer, Integer> afpMap = stockRelationList.stream()
                        .filter(a -> Objects.nonNull(a.getAfpId()))
                        .collect(Collectors.toMap(AccountStockRelation::getStockId, AccountStockRelation::getAfpId, (e1, e2) -> e1));
                if (afpMap.containsKey(r.getStockId())) {
                    ApplyFinancialPayment payment = applyFinancialPaymentService.getById(afpMap.get(r.getStockId()));
                    if (Objects.nonNull(payment) && r.getTotalPrice().compareTo(BigDecimal.ZERO) > 0)
                        r.setAfpSerialNo(payment.getSerialNo());
                }
            }
        });
        return PageResult.<AccountsPayableAccountingPageResult>builder()
                .result(list)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }
}
