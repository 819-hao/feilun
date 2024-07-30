package com.seeease.flywheel.serve.financial.rpc;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.IAccountReceiptConfirmFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.convert.AccountReceiptConfirmConvert;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.AccountReceiptConfirmStatusEnum;
import com.seeease.flywheel.serve.financial.enums.CollectionTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialSalesMethodEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.service.AccountReceStateRelService;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.flywheel.serve.financial.service.AccountStockRelationService;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.FinancialStatementCompany;
import com.seeease.flywheel.serve.maindata.service.FinancialStatementCompanyService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@DubboService(version = "1.0.0")
public class AccountReceiptConfirmFacade implements IAccountReceiptConfirmFacade {

    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;
    @Resource
    private AccountStockRelationService accountStockRelationService;
    @Resource
    private AccountReceStateRelService receStateRelService;
    @Resource
    private StockService stockService;
    @Resource
    private CustomerBalanceService customerBalanceService;
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerContactsService contactsService;
    @Resource
    private BillPurchaseService billPurchaseService;
    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private FinancialStatementCompanyService statementCompanyService;

    @Override
    public PageResult<AccountReceiptConfirmMiniPageResult> accountReceiptConfirmPageQuery(AccountReceiptConfirmMiniPageRequest request) {

        Page<AccountReceiptConfirmMiniPageResult> page = accountReceiptConfirmService.accountReceiptConfirmMiniPageQuery(request);
        Map<Integer, String> map = statementCompanyService.list().stream().collect(Collectors.toMap(FinancialStatementCompany::getId, FinancialStatementCompany::getCompanyName));

        page.getRecords()
                .forEach(a -> {
                    a.setStatus(request.getStatus());
                    if (map.containsKey(a.getStatementCompanyId())) {
                        a.setFinancialStatementCompany(map.get(a.getStatementCompanyId()));
                    }
                });
        log.info("accountReceiptConfirmPageQuery function of AccountReceiptConfirmFacade = {}", JSON.toJSONString(page));
        return PageResult.<AccountReceiptConfirmMiniPageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public AccountReceiptConfirmAddResult accountReceiptConfirmCreate(AccountReceiptConfirmAddRequest request) {

        request.setMiniAppSource(Boolean.TRUE);
        request.setStatus(AccountReceiptConfirmStatusEnum.FINISH.getValue());
        request.setSalesMethod(FinancialSalesMethodEnum.CZ.getValue());
        request.setWaitAuditPrice(BigDecimal.ZERO);
        request.setMiniAppSource(Boolean.TRUE);
        request.setCollectionType(CollectionTypeEnum.KH_CZ.getValue());
        AccountReceiptConfirm confirm = accountReceiptConfirmService.accountReceiptConfirmAdd(request);
        return AccountReceiptConfirmAddResult.builder()
                .id(confirm.getId())
                .serialNo(confirm.getSerialNo())
                .createdBy(confirm.getCreatedBy())
                .createdTime(confirm.getCreatedTime())
                .build();
    }

    @Resource
    private BillSaleOrderService billSaleOrderService;

    @Override
    public PageResult<AccountReceiptConfirmPageResult> accountReceiptConfirmPCPageQuery(AccountReceiptConfirmPageRequest request) {
        Page<AccountReceiptConfirmPageResult> page = accountReceiptConfirmService.accountReceiptConfirmPageQuery(request);

        Map<Integer, String> map = statementCompanyService.list().stream().collect(Collectors.toMap(FinancialStatementCompany::getId, FinancialStatementCompany::getCompanyName));
        Map<Integer, String> storeMap = storeManagementService.getStoreMap();

        Map<String, BillSaleOrder> collect = page.getRecords().stream()
                .filter(Objects::nonNull)
                .filter(r -> Objects.nonNull(r.getCollectionType()) && r.getCollectionType().equals(CollectionTypeEnum.XF_TK.getValue()))
                .map(r -> billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery().eq(BillSaleOrder::getSerialNo, r.getOriginSerialNo())))
                .filter(Objects::nonNull).collect(Collectors.toMap(BillSaleOrder::getSerialNo, v -> v, (k1, k2) -> k1));

        page.getRecords()
                .forEach(a -> {
                    if (map.containsKey(a.getStatementCompanyId())) {
                        a.setFinancialStatementCompany(map.get(a.getStatementCompanyId()));
                    }

                    if (collect.containsKey(a.getOriginSerialNo())) {
                        BillSaleOrder billSaleOrder = collect.get(a.getOriginSerialNo());
                        a.setOriginSerialNoSaleType(billSaleOrder.getSaleType().getValue());
                        a.setOriginSerialNoSaleChannel(billSaleOrder.getSaleChannel().getValue());
                        a.setOriginSerialNoSaleBizOrderCode(billSaleOrder.getBizOrderCode());
                    }

                    //用户信息

                    Customer customer = customerService.getById(a.getCustomerId());
                    if (Objects.nonNull(customer)) {
                        a.setCustomerName(customer.getCustomerName());
                        a.setCustomerNameOrCustomerContactName(customer.getType() == CustomerTypeEnum.ENTERPRISE ? customer.getCustomerName() : "无");
                    }
                    CustomerContacts contacts = contactsService.getById(a.getCustomerContractId());
                    if (Objects.nonNull(contacts)) {
                        a.setCustomerContractName(contacts.getName());
                        a.setAddress(contacts.getAddress());
                        a.setPhone(contacts.getPhone());
                        a.setCustomerNameOrCustomerContactName(
                                Objects.nonNull(customer)? customer.getType() == CustomerTypeEnum.INDIVIDUAL? contacts.getName(): customer.getCustomerName() : "无");
                    } else {
                        contacts = contactsService.queryCustemerContactByCustomerId(a.getCustomerId());
                        if (Objects.nonNull(contacts)) {
                            a.setCustomerContractName(contacts.getName());
                            a.setAddress(contacts.getAddress());
                            a.setPhone(contacts.getPhone());
                            a.setCustomerNameOrCustomerContactName(Objects.nonNull(customer)? customer.getType() == CustomerTypeEnum.INDIVIDUAL? contacts.getName(): customer.getCustomerName() : "无");
                        }
                    }

                    if (CollectionTypeEnum.KH_CZ.getValue().equals(a.getCollectionType())) {

                        FinancialSalesMethodEnum salesMethodEnum = FinancialSalesMethodEnum.fromCode(a.getSalesMethod());

                        switch (salesMethodEnum) {
                            case SALE_NORMAL:
                                a.setAccountOpeningBalance(a.getAccountBalance());
                                a.setAccountClosingBalance(a.getAccountBalance().add(a.getReceivableAmount()));
                                a.setConsignmentOpeningBalance(a.getConsignmentMargin());
                                a.setConsignmentClosingBalance(a.getConsignmentMargin());
                                break;
                            case SALE_CONSIGN_FOR_SALE:
                                a.setConsignmentOpeningBalance(a.getConsignmentMargin());
                                a.setConsignmentClosingBalance(a.getConsignmentMargin().add(a.getReceivableAmount()));
                                a.setAccountOpeningBalance(a.getAccountBalance());
                                a.setAccountClosingBalance(a.getAccountBalance());
                                break;
                            default:
                                log.warn("未找到");
                        }
                    }
                    a.setShopName(storeMap.get(a.getShopId()));
                });

        return PageResult.<AccountReceiptConfirmPageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PageResult<AccountReceiptConfirmDetailResult> accountReceiptConfirmDetail(AccountReceiptConfirmDetailRequest request) {
        List<AccountReceiptConfirmDetailResult> list = accountReceiptConfirmService.accountReceiptConfirmDetail(request);

        return PageResult.<AccountReceiptConfirmDetailResult>builder()
                .result(list)
                .totalCount(CollectionUtil.isNotEmpty(list) ? list.size() : 1)
                .totalPage(1)
                .build();
    }

    @Override
    public AccountReceiptConfirmGoodsDetailResult accountReceiptConfirmGoodsDetail(AccountReceiptConfirmGoodsDetailRequest request) {
        AccountReceiptConfirmGoodsDetailResult result = new AccountReceiptConfirmGoodsDetailResult();
        List<AccountStockRelation> accountStockRelationList = accountStockRelationService.accountStockByArcIdList(request.getId());
        List<Integer> stockIdList = accountStockRelationList.stream().filter(Objects::nonNull)
                .map(e -> e.getStockId()).collect(Collectors.toList());
        AccountReceiptConfirm accountReceiptConfirm = accountReceiptConfirmService.accountReceiptConfirmQueryById(request.getId());
        //查看客户的寄售保证金 和 余额
        List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(accountReceiptConfirm.getCustomerId(), null);
        result.setConsignmentMargin(customerBalanceList.stream()
                .map(CustomerBalance::getConsignmentMargin)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        result.setAccountBalance(customerBalanceList.stream()
                .map(CustomerBalance::getAccountBalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        result.setCollectionType(accountReceiptConfirm.getCollectionType());
        if (CollectionUtil.isEmpty(stockIdList)) {
            Customer customer = customerService.getById(accountReceiptConfirm.getCustomerId());
            if (Objects.nonNull(customer)) {
                result.setCustomerName(customer.getCustomerName());
                CustomerContacts contacts = contactsService.queryCustemerContactByCustomerId(customer.getId());
                if (Objects.nonNull(contacts)) {
                    result.setPhone(contacts.getPhone());
                    result.setContactsName(contacts.getName());
                }
            } else {
                result.setCustomerName(accountReceiptConfirm.getCustomerName());
            }
            if (4 == accountReceiptConfirm.getSalesMethod() && Objects.nonNull(accountReceiptConfirm.getConsignmentMargin())) {
                result.setFinalConsignmentMargin(accountReceiptConfirm.getReceivableAmount().add(accountReceiptConfirm.getConsignmentMargin()));
            } else if (1 == accountReceiptConfirm.getSalesMethod() && Objects.nonNull(accountReceiptConfirm.getAccountBalance())) {
                result.setFinalAccountBalance(accountReceiptConfirm.getReceivableAmount().add(accountReceiptConfirm.getAccountBalance()));
            }
            return result;
        }
        Map<Integer, AccountStockRelation> accountStockRelationMap = accountStockRelationList.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(AccountStockRelation::getStockId, Function.identity(), (e1, e2) -> e1));

        List<StockExt> stockExtList = stockService.selectByStockIdList(stockIdList);
        Map<Integer, StockExt> stockExtMap = stockExtList.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(StockExt::getStockId, Function.identity(), (e1, e2) -> e1));


        //拼接数据
        result.setId(result.getId());
        result.setApplicant(accountReceiptConfirm.getCreatedBy());
        result.setApplicantTime(accountReceiptConfirm.getCreatedTime());
        result.setSerialNo(accountReceiptConfirm.getSerialNo());
        result.setRejectionCause(accountReceiptConfirm.getRejectionCause());

        List<AccountReceiptConfirmGoodsDetailResult.lineVO> lines = Lists.newArrayList();
        for (Map.Entry<Integer, StockExt> map : stockExtMap.entrySet()) {
            Integer stockId = map.getKey();
            AccountReceiptConfirmGoodsDetailResult.lineVO lineVO = new AccountReceiptConfirmGoodsDetailResult.lineVO();
            lineVO.setAttachment(map.getValue().getAttachmentDetails());
            String originSerialNo = FlywheelConstant.STRING_DAFULT_VALUE;
            if (!accountStockRelationMap.isEmpty() && null != accountStockRelationMap.get(stockId)
                    && StringUtils.isNotEmpty(accountStockRelationMap.get(stockId).getOriginSerialNo())) {
                originSerialNo = accountStockRelationMap.get(stockId).getOriginSerialNo();
                //成交价
                lineVO.setOriginPrice(accountStockRelationMap.get(stockId).getOriginPrice());
            }
            lineVO.setOriginSerialNo(originSerialNo);
            lineVO.setBrandName(map.getValue().getBrandName());
            lineVO.setSeriesName(map.getValue().getSeriesName());
            lineVO.setModel(map.getValue().getModel());
            lineVO.setStockSn(map.getValue().getStockSn());
            lines.add(lineVO);
        }
        result.setLines(lines);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void accountReceStateUpdate(AccountReceiptConfirmFlowUpdateRequest request) {

        if (null == request || null == request.getId()
                || CollectionUtil.isEmpty(request.getFinancialStatementList())) {
            return;
        }

        List<Integer> stockIdList = accountReceiptConfirmService.accountReceiptConfirmStateUpdate(request);

        //因为同行寄售的在采购入库就创建 这里可以删除
//        stockService.listByIds(stockIdList)
//                .stream()
//                .filter(stockPo -> Objects.equals(BusinessBillTypeEnum.TH_JS.getValue(), stockPo.getStockSrc()))
//                .forEach(s -> {
//                    BillPurchase purchase = billPurchaseService.selectOneByStockId(s.getId());
//                    accountingService.createApa(purchase.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE,
//                            FinancialStatusEnum.PENDING_REVIEW, com.google.common.collect.Lists.newArrayList(s.getId()), null, false);
//                });
    }

    @Override
    public AccountReceiptConfirmUpdateResult accountReceiptConfirmUpdate(AccountReceiptConfirmUpdateRequest request) {
        AccountReceiptConfirm receiptConfirm = accountReceiptConfirmService.getById(request.getId());
        if (!(AccountReceiptConfirmStatusEnum.WAIT.getValue() == receiptConfirm.getStatus() ||
                AccountReceiptConfirmStatusEnum.REJECTED.getValue() == receiptConfirm.getStatus())) {
            throw new OperationRejectedException(OperationExceptionCode.NO_MODIFICATION_ALLOWED);
        }
        AccountReceiptConfirm confirm = new AccountReceiptConfirm();
        confirm.setId(request.getId());
        confirm.setStatus(AccountReceiptConfirmStatusEnum.WAIT.getValue());
        confirm.setBatchPictureUrl(request.getBatchPictureUrl());
        accountReceiptConfirmService.updateById(confirm);
        return AccountReceiptConfirmUpdateResult.builder()
                .id(confirm.getId())
                .state("待确认")
                .serialNo(confirm.getSerialNo())
                .createdBy(confirm.getCreatedBy())
                .createdTime(confirm.getCreatedTime())
                .build();
    }

    @Override
    public AccountReceiptConfirmRejectedResult rejected(AccountReceiptConfirmRejectedRequest request) {
        AccountReceiptConfirm receiptConfirm = accountReceiptConfirmService.getById(request.getId());
        if (AccountReceiptConfirmStatusEnum.WAIT.getValue() != receiptConfirm.getStatus()) {
            throw new OperationRejectedException(OperationExceptionCode.NO_MODIFICATION_ALLOWED);
        }
        AccountReceiptConfirm confirm = new AccountReceiptConfirm();
        confirm.setId(request.getId());
        confirm.setStatus(AccountReceiptConfirmStatusEnum.REJECTED.getValue());
        confirm.setRejectionCause(request.getRejectionCause());
        accountReceiptConfirmService.updateById(confirm);
        return AccountReceiptConfirmRejectedResult.builder()
                .id(confirm.getId())
                .state("已驳回")
                .serialNo(confirm.getSerialNo())
                .createdId(confirm.getCreatedId())
                .createdBy(confirm.getCreatedBy())
                .shopId(confirm.getShopId())
                .createdTime(confirm.getCreatedTime())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountReceiptConfirmConfirmReceiptResult confirmReceipt(AccountReceiptConfirmConfirmReceiptRequest request) {
        if (null == request || null == request.getId()
                || CollectionUtil.isEmpty(request.getFinancialStatementList())) {
            throw new OperationRejectedException(OperationExceptionCode.NO_MODIFICATION_ALLOWED);
        }
        accountReceiptConfirmService.confirmReceipt(request);

        AccountReceiptConfirm confirm = accountReceiptConfirmService.getById(request.getId());
        return AccountReceiptConfirmConfirmReceiptResult.builder()
                .id(confirm.getId())
                .state(confirm.getStatus() == 1 ? "部分确认" : "已确认")
                .serialNo(confirm.getSerialNo())
                .createdId(confirm.getCreatedId())
                .createdBy(confirm.getCreatedBy())
                .shopId(confirm.getShopId())
                .createdTime(confirm.getCreatedTime())
                .build();
    }

    @Override
    public List<AccountReceiptConfirmConfirmReceiptRequest.CheckConfirmVO> checkConfirmReceipt(AccountReceiptConfirmConfirmReceiptRequest request) {
        List<AccountReceStateRel> relList = receStateRelService.list(new LambdaQueryWrapper<AccountReceStateRel>()
                .in(AccountReceStateRel::getFinancialStatementSerialNo, request.getFinancialStatementList()
                        .stream()
                        .map(AccountReceiptConfirmConfirmReceiptRequest.FinancialStatementUpdateRequest::getSerialNo)
                        .collect(Collectors.toList())));

        if (CollectionUtil.isEmpty(relList)) {
            return Collections.EMPTY_LIST;
        }

        List<AccountReceiptConfirmConfirmReceiptRequest.CheckConfirmVO> listMap = new ArrayList<>();
        relList.forEach(a -> {
            AccountReceiptConfirm confirm = accountReceiptConfirmService.accountReceiptConfirmQueryById(a.getAccountReceiptConfirmId());
            if (Objects.nonNull(confirm))
                listMap.add(AccountReceiptConfirmConfirmReceiptRequest.CheckConfirmVO
                        .builder().arcSerialNo(confirm.getSerialNo()).fsSerialNo(a.getFinancialStatementSerialNo()).build());
        });

        return listMap;
    }

    @Override
    public void cancel(AccountReceiptConfirmCancelRequest request) {
        AccountReceiptConfirm receiptConfirm = accountReceiptConfirmService.getById(request.getId());
        if (AccountReceiptConfirmStatusEnum.WAIT.getValue() == receiptConfirm.getStatus() ||
                AccountReceiptConfirmStatusEnum.REJECTED.getValue() == receiptConfirm.getStatus()) {
            AccountReceiptConfirm confirm = new AccountReceiptConfirm();
            confirm.setId(request.getId());
            confirm.setStatus(AccountReceiptConfirmStatusEnum.CANCEL.getValue());
            accountReceiptConfirmService.updateById(confirm);
        }
    }

    @Override
    public AccountReceiptConfirmAddResult accountReceiptConfirmCreate(AccountReceiptConfirmCreateRequest request) {
        AccountReceiptConfirm confirm = accountReceiptConfirmService.accountReceiptConfirmCreate(request);
        return AccountReceiptConfirmAddResult.builder()
                .id(confirm.getId())
                .serialNo(confirm.getSerialNo())
                .createdBy(confirm.getCreatedBy())
                .createdTime(confirm.getCreatedTime())
                .build();
    }

    @Override
    public AccountReceiptConfirmMiniDetailResult detail(AccountReceiptConfirmDetailRequest request) {
        AccountReceiptConfirm confirm = accountReceiptConfirmService.getById(request.getId());
        if (Objects.isNull(confirm))
            return AccountReceiptConfirmMiniDetailResult.builder().build();
        AccountReceiptConfirmMiniDetailResult result = AccountReceiptConfirmConvert.INSTANCE.convertAccountReceiptConfirm(confirm);
        if (CollectionTypeEnum.XF_TK.getValue().equals(confirm.getCollectionType())) {
            CustomerContacts contacts = contactsService.getById(result.getCustomerContractId());
            if (Objects.nonNull(contacts)) {
                result.setCustomerContractName(contacts.getName());
                result.setAddress(contacts.getAddress());
                result.setPhone(contacts.getPhone());
            }
        } else {
            Customer customer = customerService.getById(result.getCustomerId());
            if (Objects.nonNull(customer)) {
                result.setCustomerName(customer.getCustomerName());
            }
            CustomerContacts contacts = contactsService.getById(result.getCustomerContractId());
            if (Objects.nonNull(contacts)) {
                result.setCustomerContractName(contacts.getName());
                result.setAddress(contacts.getAddress());
                result.setPhone(contacts.getPhone());
            } else {
                contacts = contactsService.queryCustemerContactByCustomerId(result.getCustomerId());
                if (Objects.nonNull(contacts)) {
                    result.setCustomerContractName(contacts.getName());
                    result.setAddress(contacts.getAddress());
                    result.setPhone(contacts.getPhone());
                }
            }

            if (CollectionTypeEnum.KH_CZ.getValue().equals(confirm.getCollectionType())) {

                FinancialSalesMethodEnum salesMethodEnum = FinancialSalesMethodEnum.fromCode(result.getSalesMethod());

                switch (salesMethodEnum) {
                    case SALE_NORMAL:
                        result.setAccountOpeningBalance(result.getAccountBalance());
                        result.setAccountClosingBalance(result.getAccountBalance().add(result.getReceivableAmount()));
                        break;
                    case SALE_CONSIGN_FOR_SALE:
                        result.setConsignmentOpeningBalance(result.getConsignmentMargin());
                        result.setConsignmentClosingBalance(result.getConsignmentMargin().add(result.getReceivableAmount()));
                        break;
                    default:
                        log.warn("未找到");
                }
            }
        }
        if (Objects.nonNull(result.getStatementCompanyId()))
            result.setStatementCompanyName(statementCompanyService.getById(result.getStatementCompanyId()).getCompanyName());
        Map<Integer, String> storeMap = storeManagementService.getStoreMap();

        result.setShopName(storeMap.get(result.getShopId()));
//        //查看客户的寄售保证金 和 余额
//        List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(result.getCustomerId(), null);
//        result.setConsignmentMargin(customerBalanceList.stream()
//                .map(CustomerBalance::getConsignmentMargin)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add));
//        result.setAccountBalance(customerBalanceList.stream()
//                .map(CustomerBalance::getAccountBalance)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return result;
    }

    @Override
    public List<AccountReceiptConfirmCollectionDetailsResult> collectionDetails(AccountReceiptConfirmCollectionDetailsRequest request) {
        List<AccountReceStateRel> rels = receStateRelService.list(new LambdaQueryWrapper<AccountReceStateRel>()
                .eq(AccountReceStateRel::getAccountReceiptConfirmId, request.getId()));
        if (CollectionUtil.isEmpty(rels)) {
            return Collections.EMPTY_LIST;
        }
        return rels.stream().map(AccountReceiptConfirmConvert.INSTANCE::convertReceStateRelList).collect(Collectors.toList());
    }

    @Resource
    private AccountsPayableAccountingService accountsPayableAccountingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAudit(AccountReceiptConfirmBatchAuditRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()))
            return;

        List<AccountReceiptConfirm> list = accountReceiptConfirmService.listByIds(request.getIds());
        if (!list.stream().allMatch(a -> AccountReceiptConfirmStatusEnum.WAIT.getValue() == a.getStatus()))
            throw new BusinessException(ExceptionCode.BATCH_AUDIT_FAIL);
        List<AccountReceiptConfirm> confirmList = list.stream().map(a -> AccountReceiptConfirm.builder()
                .waitBindingAmount(BigDecimal.ZERO)
                .auditDescription(request.getAuditDescription())
                .auditor(UserContext.getUser().getUserName())
                .auditTime(new Date())
                .id(a.getId())
                .status(AccountReceiptConfirmStatusEnum.FINISH.getValue())
                .build()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(confirmList))
            accountReceiptConfirmService.updateBatchById(confirmList);

        if (CollectionUtils.isNotEmpty(list)) {

            List<AccountsPayableAccounting> payableAccountingList = accountsPayableAccountingService.list(Wrappers.<AccountsPayableAccounting>lambdaQuery()
                    .in(AccountsPayableAccounting::getArcSerialNo, list.stream().map(AccountReceiptConfirm::getSerialNo).collect(Collectors.toList()))
                    .in(AccountsPayableAccounting::getStatus, Arrays.asList(FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.PORTION_WAIT_AUDIT))
            );

            if (CollectionUtils.isNotEmpty(payableAccountingList)) {
                accountsPayableAccountingService.batchAudit(payableAccountingList.stream().map(AccountsPayableAccounting::getId).collect(Collectors.toList()), FlywheelConstant.SETTLEMENT_AUDIT, UserContext.getUser().getUserName());
            }
        }
    }


}
