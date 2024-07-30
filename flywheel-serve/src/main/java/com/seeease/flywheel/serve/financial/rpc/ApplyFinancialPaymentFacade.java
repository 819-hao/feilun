package com.seeease.flywheel.serve.financial.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.IApplyFinancialPaymentFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.convert.ApplyFinancialPaymentConvert;
import com.seeease.flywheel.serve.financial.convert.ApplyFinancialPaymentObsoleteRecordConvert;
import com.seeease.flywheel.serve.financial.entity.AccountStockRelation;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPaymentObsoleteRecord;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentEnum;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentStateEnum;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialSalesMethodEnum;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentObsoleteRecordService;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.financial.template.payment.PaymentATemplate;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.StoreManagement;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.StoreQuotaService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.LoginStore;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@DubboService(version = "1.0.0")
public class ApplyFinancialPaymentFacade implements IApplyFinancialPaymentFacade {

    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;

    @Resource
    private CustomerContactsService contactsService;

    @Resource
    private CustomerService customerService;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private ApplyFinancialPaymentObsoleteRecordService obsoleteRecordService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private BillPurchaseService purchaseService;
    @Resource
    private AccountStockRelationService stockRelationService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private StoreQuotaService storeQuotaService;

    /**
     * 创建
     *
     * @param request
     * @return
     */
    @Override
    public ApplyFinancialPaymentCreateResult create(ApplyFinancialPaymentAppletCreateRequest request) {
        ApplyFinancialPaymentCreateRequest createRequest = ApplyFinancialPaymentConvert.INSTANCE.convertApplyFinancialPaymentCreateRequest(request);
        //插入
        createRequest.setShopId(UserContext.getUser().getStore().getId());

        createRequest.setManualCreation(WhetherEnum.YES.getValue());
        return applyFinancialPaymentService.create(createRequest);
    }

    /**
     * 作废 驳回
     * 取消 待确认
     *
     * @param request
     */
    @Override
    public ApplyFinancialPaymentUpdateResult update(ApplyFinancialPaymentUpdateRequest request) {
        return applyFinancialPaymentService.update(request);
    }

    @Override
    public PageResult query(ApplyFinancialPaymentQueryRequest request) {

        LoginUser user = UserContext.getUser();

        LoginStore store = user.getStore();

        List<LoginRole> roleList = user.getRoles();

        Integer uId = null;

        if (store.getId().equals(FlywheelConstant._ZB_ID) && roleList.stream().anyMatch(r -> Arrays.asList("admin", "总部财务").contains(r.getRoleName()))) {
        } else {
            //门店
            request.setShopId(store.getId());
            uId = user.getId();
        }

        LambdaQueryWrapper<ApplyFinancialPayment> wrapper = Wrappers.<ApplyFinancialPayment>query()

                .lambda().between(StringUtils.isNotBlank(request.getCreatedTimeStart()) && StringUtils.isNotBlank(request.getCreatedTimeEnd()), ApplyFinancialPayment::getCreatedTime, request.getCreatedTimeStart(), request.getCreatedTimeEnd()).between(StringUtils.isNotBlank(request.getPaymentTimeStart()) && StringUtils.isNotBlank(request.getPaymentTimeEnd()), ApplyFinancialPayment::getOperateTime, request.getPaymentTimeStart(), request.getPaymentTimeEnd()).likeRight(StringUtils.isNotBlank(request.getCreatedBy()), ApplyFinancialPayment::getCreatedBy, request.getCreatedBy()).likeRight(StringUtils.isNotBlank(request.getOperator()), ApplyFinancialPayment::getOperator, request.getOperator())
                .like(StringUtils.isNotBlank(request.getSerialNo()), ApplyFinancialPayment::getSerialNo, request.getSerialNo())
                .eq(Objects.nonNull(request.getShopId()), ApplyFinancialPayment::getShopId, request.getShopId()).eq(Objects.nonNull(uId), ApplyFinancialPayment::getCreatedId, uId)

                .like(StringUtils.isNotBlank(request.getOriginSerialNo()), ApplyFinancialPayment::getOriginSerialNo, request.getOriginSerialNo()).eq(Objects.nonNull(request.getSubjectPayment()) && !request.getSubjectPayment().equals(-1), ApplyFinancialPayment::getSubjectPayment, request.getSubjectPayment()).and(StringUtils.isNotBlank(request.getSearchCriteria()), i -> i.apply("created_by = \"" + request.getSearchCriteria() + "\" OR customer_name = \"" + request.getSearchCriteria() + "\"")).orderByDesc(ApplyFinancialPayment::getId);

        if (Objects.nonNull(request.getWhetherUse()) && !request.getWhetherUse().equals(-1)) {
            wrapper.eq(ApplyFinancialPayment::getWhetherUse, WhetherEnum.fromValue(request.getWhetherUse()));
        }
        if (Objects.nonNull(request.getState()) && !request.getState().equals(-1)) {
            wrapper.eq(ApplyFinancialPayment::getState, ApplyFinancialPaymentStateEnum.fromCode(request.getState()));
        }
        if (Objects.nonNull(request.getTypePayment()) && !request.getTypePayment().equals(-1)) {
            wrapper.eq(ApplyFinancialPayment::getTypePayment, ApplyFinancialPaymentTypeEnum.fromCode(request.getTypePayment()));
        }
        if (Objects.nonNull(request.getSalesMethod()) && !request.getSalesMethod().equals(-1)) {
            wrapper.eq(ApplyFinancialPayment::getSalesMethod, FinancialSalesMethodEnum.fromCode(request.getSalesMethod()));
        }
        if (Objects.nonNull(request.getPayment()) && !request.getPayment().equals(-1)) {
            wrapper.eq(ApplyFinancialPayment::getPayment, ApplyFinancialPaymentEnum.fromCode(request.getPayment()));
        }

        Page<ApplyFinancialPayment> page = applyFinancialPaymentService.page(new Page<>(request.getPage(), request.getLimit()), wrapper);

        Map<Integer, String> storeMap = storeManagementService.getStoreMap();

        Map<Integer, String> subjectMap = purchaseSubjectService.list(null).stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));

        List<ApplyFinancialPayment> pageRecords = page.getRecords();

        Map<Integer, Long> relationMap;

        if (!pageRecords.isEmpty()) {
            relationMap = stockRelationService.list(Wrappers.<AccountStockRelation>lambdaQuery().in(AccountStockRelation::getAfpId, pageRecords.stream().map(ApplyFinancialPayment::getId).collect(Collectors.toList()))).stream().collect(Collectors.groupingBy(AccountStockRelation::getAfpId, Collectors.counting()));
        } else {
            relationMap = new HashMap<>();
        }

        List<CustomerContacts> customerContactsList = Optional.ofNullable(pageRecords.stream()
                        .map(ApplyFinancialPayment::getCustomerContactsId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(contactsService::listByIds).orElse(new ArrayList<>());



        List<Customer> customerList = Optional.ofNullable(customerContactsList)
                .filter(CollectionUtils::isNotEmpty)
                        .map(t->customerService.list(Wrappers.<Customer>lambdaQuery().in(Customer::getId, t.stream().map(CustomerContacts::getCustomerId).collect(Collectors.toList()))))
                .orElse(new ArrayList<>());

        return PageResult.<ApplyFinancialPaymentPageResult>builder().result(pageRecords.stream().map(r -> {
            ApplyFinancialPaymentPageResult result = ApplyFinancialPaymentConvert.INSTANCE.convertApplyFinancialPaymentPageResult(r);
            result.setShopName(Objects.nonNull(result.getShopId()) && storeMap.containsKey(result.getShopId()) ? storeMap.get(result.getShopId()) : "-");
            result.setDemanderStoreName(Objects.nonNull(result.getDemanderStoreId()) && storeMap.containsKey(result.getDemanderStoreId()) ? storeMap.get(result.getDemanderStoreId()) : "-");
            result.setSubjectPaymentName(Objects.nonNull(result.getSubjectPayment()) && subjectMap.containsKey(result.getSubjectPayment()) ? subjectMap.get(result.getSubjectPayment()) : "-");
            result.setNumber(Objects.nonNull(relationMap) && relationMap.containsKey(result.getId()) ? relationMap.get(result.getId()).intValue() : 0);

            //应收应付一样
            CustomerContacts contacts = customerContactsList.stream().filter(t -> t.getId().equals(r.getCustomerContactsId())).findFirst().orElse(null);

            if (Objects.nonNull(contacts)){
                Customer customer = customerList.stream().filter(t -> t.getId().equals(contacts.getCustomerId())).findFirst().orElse(null);
                result.setCustomerType(customer.getType().getValue());
                if (Objects.nonNull(customer) && customer.getType() == CustomerTypeEnum.ENTERPRISE){
                    result.setCustomerNameOrCustomerContactName(customer.getCustomerName());
                }else if (Objects.nonNull(customer) && customer.getType() == CustomerTypeEnum.INDIVIDUAL){
                    result.setCustomerNameOrCustomerContactName(contacts.getName());
                }
            }


            return result;
        }).collect(Collectors.toList())).totalCount(page.getTotal()).totalPage(page.getPages()).build();
    }

    @Override
    public PageResult<ApplyFinancialPaymentPageAllResult> queryAll(ApplyFinancialPaymentQueryAllRequest request) {

        request.setTypePayment(Optional.ofNullable(request.getTypePayment())
                .filter(v -> v != -1)
                .orElse(null));

        LambdaQueryWrapper<ApplyFinancialPayment> queryWrapper = Wrappers.<ApplyFinancialPayment>lambdaQuery()
                .eq(ApplyFinancialPayment::getState, ApplyFinancialPaymentStateEnum.PAID)
                .eq(ApplyFinancialPayment::getWhetherUse, WhetherEnum.NO)
                .orderByDesc(ApplyFinancialPayment::getCreatedTime);
        if (ObjectUtils.isNotEmpty(request.getTypePayment())) {
            queryWrapper.eq(ApplyFinancialPayment::getTypePayment, request.getTypePayment());
        }

        if (request.getSalesMethod() != null && !request.getSalesMethod().isEmpty()) {
            queryWrapper.in(ApplyFinancialPayment::getSalesMethod, request.getSalesMethod());
        }

        if (ObjectUtils.isNotEmpty(request.getSerialNo())) {
            queryWrapper.like(ApplyFinancialPayment::getSerialNo, request.getSerialNo());
        }

        Page<ApplyFinancialPayment> page = applyFinancialPaymentService.page(new Page<>(request.getPage(), request.getLimit()), queryWrapper);

        List<ApplyFinancialPayment> records = page.getRecords();

        if (CollectionUtils.isNotEmpty(records)) {
            return PageResult.<ApplyFinancialPaymentPageAllResult>builder().result(records.stream().map(ApplyFinancialPaymentConvert.INSTANCE::convertApplyFinancialPaymentPageAllResult).collect(Collectors.toList())).totalCount(page.getTotal()).totalPage(page.getPages()).build();
        } else {
            return PageResult.<ApplyFinancialPaymentPageAllResult>builder().result(Arrays.asList()).totalCount(page.getTotal()).totalPage(page.getPages()).build();
        }

    }

    @Override
    public ApplyFinancialPaymentOperateResult operate(ApplyFinancialPaymentOperateRequest request) {
        request.setOperator(UserContext.getUser().getUserName());
        return applyFinancialPaymentService.operate(request);
    }

    @Override
    public ApplyFinancialPaymentDetailResult detail(ApplyFinancialPaymentDetailRequest request) {
        log.info("detail function of ApplyFinancialPaymentFacade start and request = {}", JSON.toJSONString(request));
        ApplyFinancialPaymentDetailResult detail = applyFinancialPaymentService.detail(request);
        if (Objects.nonNull(detail) && detail.getDemanderStoreId() != null && detail.getDemanderStoreId() != 0) {
            //蜥蜴助手可用额度
//            String availableCredit = limitPrice(detail.getDemanderStoreId());
//            detail.setAvailableCredit(availableCredit);
        }

        if (CollectionUtils.isEmpty(obsoleteRecordService.list(Wrappers.<ApplyFinancialPaymentObsoleteRecord>lambdaQuery().eq(ApplyFinancialPaymentObsoleteRecord::getAfpId, request.getId()))) && (
                (detail.getTypePayment().equals(ApplyFinancialPaymentTypeEnum.PEER_PROCUREMENT.getValue()) && Arrays.asList(FinancialSalesMethodEnum.PURCHASE_PREPARE.getValue(), FinancialSalesMethodEnum.PURCHASE_FUll_AMOUNT.getValue(), FinancialSalesMethodEnum.PURCHASE_DEPOSIT_SA, FinancialSalesMethodEnum.PURCHASE_FUll_AMOUNT).contains(detail.getSalesMethod()))
                        || (detail.getTypePayment().equals(ApplyFinancialPaymentTypeEnum.PERSONAL_RECYCLING.getValue()) && Arrays.asList(FinancialSalesMethodEnum.PURCHASE_RECYCLE.getValue()).contains(detail.getSalesMethod())))) {
            detail.setPreButton(Boolean.TRUE);
        } else {
            detail.setPreButton(Boolean.FALSE);
        }
        return detail;
    }

    @Override
    public PageResult<ApplyFinancialPaymentRecordPageResult> approvedMemo(ApplyFinancialPaymentRecordRequest request) {
        Page<ApplyFinancialPaymentRecordPageResult> page = applyFinancialPaymentService.approvedMemo(request);
        return PageResult.<ApplyFinancialPaymentRecordPageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PageResult<ApplyFinancialPaymentPageQueryByConditionResult> queryByCondition(ApplyFinancialPaymentQueryByConditionRequest request) {
        request.setWhetherUse(Optional.ofNullable(request.getWhetherUse())
                .filter(v -> v != -1)
                .orElse(null));

        Page<ApplyFinancialPaymentPageQueryByConditionResult> page = applyFinancialPaymentService.queryByCondition(request);
        List<ApplyFinancialPaymentPageQueryByConditionResult> list = page.getRecords();
        if (CollectionUtils.isEmpty(list))
            return PageResult.<ApplyFinancialPaymentPageQueryByConditionResult>builder()
                    .result(Lists.newArrayList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        Map<Integer, String> shopMap = Optional.ofNullable(list.stream()
                        .map(ApplyFinancialPaymentPageQueryByConditionResult::getShopId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(storeManagementService::selectInfoByIds)
                .map(t -> t.stream()
                        .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);
        Map<Integer, String> storeMap = Optional.ofNullable(list.stream()
                        .map(ApplyFinancialPaymentPageQueryByConditionResult::getDemanderStoreId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(storeManagementService::selectInfoByIds)
                .map(t -> t.stream()
                        .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);

        Map<Integer, String> map = stockRelationService.selectByAfpIds(
                        list.stream().map(ApplyFinancialPaymentPageQueryByConditionResult::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(AccountStockRelation::getAfpId, AccountStockRelation::getOriginSerialNo));
        list.forEach(r -> {
            r.setShopName(shopMap.getOrDefault(r.getShopId(), "-"));
            r.setDemanderStoreName(storeMap.getOrDefault(r.getDemanderStoreId(), "-"));
            r.setObsoleteRecordCount(obsoleteRecordService.count(new LambdaQueryWrapper<ApplyFinancialPaymentObsoleteRecord>()
                    .eq(ApplyFinancialPaymentObsoleteRecord::getAfpId, r.getId())));
            if (map.containsKey(r.getId()))
                r.setPurchaseSerialNo(map.get(r.getId()));
        });
        return PageResult.<ApplyFinancialPaymentPageQueryByConditionResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PageResult<ApplyFinancialPaymentPageQueryByConditionResult> export(ApplyFinancialPaymentQueryByConditionRequest request) {
        //导出手选择项
        if (Objects.nonNull(request.getDocBatchIds()) && request.getDocBatchIds().size() == 0) {
            request.setDocBatchIds(null);
        }
        request.setWhetherUse(Optional.ofNullable(request.getWhetherUse())
                .filter(v -> v != -1)
                .orElse(null));

        Page<ApplyFinancialPaymentPageQueryByConditionResult> page = applyFinancialPaymentService.queryByCondition(request);

        return PageResult.<ApplyFinancialPaymentPageQueryByConditionResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public void cancel(ApplyFinancialPaymentAppletCancelRequest request) {
        log.info("cancel function of ApplyFinancialPaymentFacade and request = {}", JSON.toJSONString(request));
        if (ObjectUtils.isEmpty(request))
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        ApplyFinancialPayment payment = applyFinancialPaymentService.getById(request.getId());
        if (ObjectUtils.isEmpty(payment))
            throw new OperationRejectedException(OperationExceptionCode.APPLY_FINANCIAL_PAYMENT);
        //仅能取消手动创建的申请打款单 仅创建人取消
        if (WhetherEnum.NO.getValue().equals(payment.getManualCreation()) ||
                !UserContext.getUser().getUserName().equals(payment.getCreatedBy()))
            throw new OperationRejectedException(OperationExceptionCode.PAYMENT_MATERIAL_CREATE_NOT_ALLOWED);

        ApplyFinancialPaymentCancelRequest.UseScenario useScenario = getUseScenario(payment);
        if (ObjectUtils.isEmpty(useScenario))
            throw new OperationRejectedException(OperationExceptionCode.PAYMENT_MATERIAL_SCENARIO_NO_EXIST);
        applyFinancialPaymentService.cancel(ApplyFinancialPaymentCancelRequest.builder()
                .serialNo(payment.getSerialNo())
                .useScenario(useScenario)
                .build());
    }

    @Override
    public String limitPrice(Integer shopId) {
        StoreManagement storeManagement = storeManagementService.getById(shopId);
        Assert.notNull(storeManagement, "用户所在门店不存在");
        BigDecimal usedPrice = applyFinancialPaymentService.usedPrice(storeManagement.getTagId());
        BigDecimal quota = Optional.ofNullable(storeQuotaService.selectQuota(storeManagement.getId(), new Date())).orElse(BigDecimal.ZERO);
        return quota.subtract(usedPrice)
                .setScale(2, RoundingMode.DOWN).toPlainString();
    }

    @Override
    public ApplyFinancialPaymentObsoleteRecordResult obsolete(ApplyFinancialPaymentObsoleteRequest request) {
        return applyFinancialPaymentService.obsolete(request);
    }

    @Override
    public List<ApplyFinancialPaymentObsoleteRecordPageResult> obsoleteRecordPage(ApplyFinancialPaymentObsoleteRecordPageRequest request) {
        List<ApplyFinancialPaymentObsoleteRecord> list = obsoleteRecordService.list(new LambdaQueryWrapper<ApplyFinancialPaymentObsoleteRecord>()
                .eq(ApplyFinancialPaymentObsoleteRecord::getAfpId, request.getAfpId()));

        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        Map<Integer, String> subjectMap = purchaseSubjectService.list()
                .stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
        Map<Integer, String> storeMap = storeManagementService.getStoreMap();

        return list.stream()
                .map(a -> {
                    ApplyFinancialPaymentObsoleteRecordPageResult b = ApplyFinancialPaymentObsoleteRecordConvert.INSTANCE.convertApplyFinancialPaymentObsoleteRecord(a);
                    b.setSubjectPaymentName(subjectMap.get(a.getSubjectPayment()));
                    b.setDemanderStoreName(storeMap.get(a.getDemanderStoreId()));
                    return b;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> checkoutStockSn(ApplyFinancialPaymentCheckoutStockSnRequest request) {
        return purchaseService.checkoutStockSn(request);
    }

    @Resource
    private PaymentATemplate paymentATemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(ApplyFinancialPaymentOrderCancelTaskRequest request) {
        ApplyFinancialPayment applyFinancialPayment = applyFinancialPaymentService.cancelTask(request);

        //生成确认收款单
        paymentATemplate.createReceipt(new JSONObject().fluentPut("afp", applyFinancialPayment));
    }

    @Override
    public ApplyFinancialPaymentOrderDetailsResult orderDetails(ApplyFinancialPaymentOrderDetailRequest request) {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getId())) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }
        ApplyFinancialPayment payment = applyFinancialPaymentService.getById(request.getId());
        if (ObjectUtils.isEmpty(payment)) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }
        ApplyFinancialPaymentOrderDetailsResult result = ApplyFinancialPaymentOrderDetailsResult.builder().build();
        Page<AccountStockRelation> page = stockRelationService.page(Page.of(request.getPage(), request.getLimit()),
                new LambdaQueryWrapper<AccountStockRelation>()
                        .eq(AccountStockRelation::getAfpId, payment.getId())
                        .eq(AccountStockRelation::getDeleted, WhetherEnum.NO.getValue()));
        result.setApplicant(payment.getCreatedBy());
        result.setSerialNo(payment.getSerialNo());
        result.setApplicant(payment.getCreatedBy());
        result.setApplicantTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, payment.getCreatedTime()));
        if (CollectionUtils.isEmpty(page.getRecords())) {
            result.setLines(Collections.EMPTY_LIST);
            return result;
        }
        Map<Integer, WatchDataFusion> watchDataFusionMap = goodsWatchService.getWatchDataFusionListByStockIds(page.getRecords()
                        .stream()
                        .map(AccountStockRelation::getStockId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getStockId, watchDataFusion -> watchDataFusion));
        result.setLines(page.getRecords()
                .stream()
                .map(a -> {
                    ApplyFinancialPaymentOrderDetailsResult.lineVO t = new ApplyFinancialPaymentOrderDetailsResult.lineVO();
                    t.setOriginSerialNo(a.getOriginSerialNo());
                    t.setOriginPrice(a.getOriginPrice());
                    WatchDataFusion watchDataFusion = watchDataFusionMap.get(a.getStockId());
                    if (Objects.nonNull(watchDataFusion)) {
                        t.setAttachment(watchDataFusion.getAttachment());
                        t.setBrandName(watchDataFusion.getBrandName());
                        t.setSeriesName(watchDataFusion.getSeriesName());
                        t.setModel(watchDataFusion.getModel());
                        t.setStockSn(watchDataFusion.getStockSn());
                    }
                    return t;
                }).collect(Collectors.toList()));
        return result;
    }

    private ApplyFinancialPaymentCancelRequest.UseScenario getUseScenario(ApplyFinancialPayment payment) {
        switch (payment.getTypePayment()) {
            case PEER_PROCUREMENT:
                return ApplyFinancialPaymentCancelRequest.UseScenario.PURCHASE_PEER_CANCEL;
            case PERSONAL_RECYCLING:
                return ApplyFinancialPaymentCancelRequest.UseScenario.RECYCLE_PERSON_CANCEL;
            case BALANCE_REFUND:
                return ApplyFinancialPaymentCancelRequest.UseScenario.BALANCE_REFUND;
            default:
                return null;
        }
    }

}
