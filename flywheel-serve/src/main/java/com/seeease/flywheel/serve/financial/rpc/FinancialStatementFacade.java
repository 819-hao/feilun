package com.seeease.flywheel.serve.financial.rpc;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.IFinancialStatementFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.financial.convert.FinancialStatementConvert;
import com.seeease.flywheel.serve.financial.entity.AccountReceStateRel;
import com.seeease.flywheel.serve.financial.entity.AccountReceiptConfirm;
import com.seeease.flywheel.serve.financial.entity.FinancialStatement;
import com.seeease.flywheel.serve.financial.entity.SaleReturnSubjectMapping;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.service.AccountReceStateRelService;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.flywheel.serve.financial.service.FinancialStatementService;
import com.seeease.flywheel.serve.financial.service.SaleReturnSubjectMappingService;
import com.seeease.flywheel.serve.maindata.entity.*;
import com.seeease.flywheel.serve.maindata.service.*;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import com.seeease.springframework.utils.DateUtils;
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
 * @date 2023/9/1
 */
@Slf4j
@DubboService(version = "1.0.0")
public class FinancialStatementFacade implements IFinancialStatementFacade {
    @Resource
    private AccountReceStateRelService accountReceStateRelService;
    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;
    @Resource
    private FinancialStatementService statementService;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private FinancialStatementCompanyService statementCompanyService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private AccountReceStateRelService stateRelService;
    @Resource
    private FirmShopService firmShopService;
    @Resource
    private StoreRelationshipSubjectService storeRelationshipSubjectService;

    private static final Set<FinancialStatusEnum> STATUS_ENUM = ImmutableSet.of(FinancialStatusEnum.RETURN_PENDING_REVIEW,
            FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.PORTION_WAIT_AUDIT);

    @Override
    public PageResult<FinancialStatementQueryAllResult> queryAll(FinancialStatementQueryAllRequest request) {
        return getStatementQueryAllResultPageResult(request);
    }

    private PageResult<FinancialStatementQueryAllResult> getStatementQueryAllResultPageResult(FinancialStatementQueryAllRequest request) {
        Page<FinancialStatementQueryAllResult> page = statementService.queryAll(request);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<FinancialStatementQueryAllResult>builder()
                    .result(Lists.newArrayList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        Map<Integer, String> subjectMap = Optional.ofNullable(page.getRecords().stream()
                        .map(FinancialStatementQueryAllResult::getSubjectId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(statementCompanyService::listByIds)
                .map(t -> t.stream().collect(Collectors.toMap(FinancialStatementCompany::getId, FinancialStatementCompany::getCompanyName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);

        Map<Integer, String> shopMap = Optional.ofNullable(page.getRecords().stream()
                        .map(FinancialStatementQueryAllResult::getShopId)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(storeManagementService::selectInfoByIds)
                .map(t -> t.stream()
                        .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName, (k1, k2) -> k2)))
                .orElse(Collections.EMPTY_MAP);
        page.getRecords().forEach(r -> {
            r.setShopName(shopMap.get(r.getShopId()));
            r.setSubjectName(subjectMap.get(r.getSubjectId()));
        });
        return PageResult.<FinancialStatementQueryAllResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public FinancialStatementDetailsResult detail(FinancialStatementDetailsRequest request) {
        FinancialStatementDetailsResult result = statementService.detail(request);
        FinancialStatementCompany subject = Optional.ofNullable(statementCompanyService.getById(result.getSubjectId())).orElse(new FinancialStatementCompany());
        Map<Integer, String> storeMap = storeManagementService.getStoreMap();
        result.setSubjectName(subject.getCompanyName());
        result.setShopName(storeMap.get(result.getShopId()));

        result.setDetails(accountReceStateRelService.list(new LambdaQueryWrapper<AccountReceStateRel>()
                        .eq(AccountReceStateRel::getFinancialStatementId, result.getId()))
                .stream()
                .map(a -> {
                    FinancialStatementDetailsResult.LineVo line = new FinancialStatementDetailsResult.LineVo();
                    AccountReceiptConfirm confirm = accountReceiptConfirmService.getById(a.getAccountReceiptConfirmId());
                    line.setArcSerialNo(confirm.getSerialNo());
                    line.setAvailableAmount(a.getFundsReceived().subtract(a.getFundsUsed()));
                    line.setUsedAmount(a.getFundsUsed());
                    line.setAuditName(confirm.getCreatedBy());
                    return line;
                })
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public PageResult<FinancialStatementQueryAllResult> export(FinancialStatementQueryAllRequest request) {
        //导出手选择项
        if (Objects.nonNull(request.getDocBatchIds()) && request.getDocBatchIds().size() == 0) {
            request.setDocBatchIds(null);
        }
        return getStatementQueryAllResultPageResult(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult<FinancialStatementImportResult> financialStatementImport(FinancialStatementImportRequest request) {

        Map<String, FinancialStatement> map = statementService.list(new LambdaQueryWrapper<FinancialStatement>()
                        .in(FinancialStatement::getSerialNo, request.getDataList()
                                .stream().map(FinancialStatementImportRequest.ImportDto::getSerialNo)
                                .collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(FinancialStatement::getSerialNo, Function.identity()));

        Map<String, Integer> storeMap = storeManagementService.getStoreIdMap();

        Map<String, Integer> subjectMap = statementCompanyService.list().stream().collect(Collectors.toMap(FinancialStatementCompany::getCompanyName, FinancialStatementCompany::getId, (e1, e2) -> e1));

        List<String> errList = new ArrayList<>();

        List<FinancialStatement> statementList = request.getDataList()
                .stream()
                .map(t -> {
                    if (!map.containsKey(t.getSerialNo().trim())) {
                        //流水号不在系统中的 新增
                        FinancialStatement statement = new FinancialStatement();
                        statement.setSerialNo(t.getSerialNo().trim());
                        statement.setCollectionTime(t.getCollectionTime());
                        statement.setShopId(storeMap.get(t.getShopName()));
                        statement.setSubjectId(subjectMap.get(t.getSubjectName().trim()));
                        statement.setPayer(t.getPayer());
                        statement.setRemarks(t.getRemarks());
                        statement.setFundsReceived(t.getFundsReceived());
                        statement.setProcedureFee(t.getProcedureFee());
                        statement.setReceivableAmount(t.getReceivableAmount());
                        statement.setWaitAuditPrice(t.getWaitAuditPrice());
                        statement.setStatus(FinancialStatusEnum.PENDING_REVIEW);
                        return statement;
                    } else if (map.containsKey(t.getSerialNo().trim()) &&
                            FinancialStatusEnum.PENDING_REVIEW.equals(map.get(t.getSerialNo()).getStatus())) {
                        //流水号在系统中的并且状态是待审核 修改
                        FinancialStatement statement = new FinancialStatement();
                        statement.setId(map.get(t.getSerialNo()).getId());
                        statement.setSerialNo(t.getSerialNo());
                        statement.setCollectionTime(t.getCollectionTime());
                        statement.setShopId(storeMap.get(t.getShopName()));
                        statement.setSubjectId(subjectMap.get(t.getSubjectName().trim()));
                        statement.setPayer(t.getPayer());
                        statement.setRemarks(t.getRemarks());
                        statement.setFundsReceived(t.getFundsReceived());
                        statement.setProcedureFee(t.getProcedureFee());
                        statement.setReceivableAmount(t.getReceivableAmount());
                        statement.setWaitAuditPrice(t.getWaitAuditPrice());
                        statement.setStatus(FinancialStatusEnum.PENDING_REVIEW);
                        return statement;
                    } else {
                        errList.add(t.getSerialNo());
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
        statementService.saveOrUpdateBatch(statementList);

        return ImportResult.<FinancialStatementImportResult>builder()
                .successList(statementList.stream()
                        .map(FinancialStatementConvert.INSTANCE::convertImportResult)
                        .collect(Collectors.toList()))
                .errList(errList)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAudit(FinancialStatementBatchAuditRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()))
            return;
        List<FinancialStatement> list = statementService.listByIds(request.getIds());
        list.forEach(a -> {
            if (!STATUS_ENUM.contains(a.getStatus())) {
                throw new OperationRejectedException(OperationExceptionCode.NO_MODIFICATION_ALLOWED);
            }
        });
        statementService.batchAudit(request.getIds(), request.getAuditDescription(), UserContext.getUser().getUserName());
    }

    @Override
    public PageResult<FinancialStatementMiniPageQueryResult> miniPageQuery(FinancialStatementMiniPageQueryRequest request) {
        Page<FinancialStatementMiniPageQueryResult> page = statementService.miniPageQuery(request);
        return PageResult.<FinancialStatementMiniPageQueryResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PageResult<FinancialStatementQueryAllResult> allNotAudit(FinancialStatementQueryAllRequest request) {
        PageResult<FinancialStatementQueryAllResult> pageResult = getStatementQueryAllResultPageResult(request);

        List<FinancialStatementQueryAllResult> resultList = Lists.newArrayList();
        resultList = pageResult.getResult().stream().filter(Objects::nonNull)
                .filter(e -> !FinancialStatusEnum.AUDITED.getValue().equals(e.getStatus()))
                .collect(Collectors.toList());
        pageResult.setResult(resultList);
        pageResult.setTotalCount(CollectionUtil.isEmpty(resultList) ? resultList.size() : 0);

        return pageResult;
    }

    @Override
    public PageResult<PurchaseSubjectNameResult> subjectCompanyQry(FinancialStatementSubjectNameQueryRequest request) {
//        List<PurchaseSubject> purchaseSubjectList = purchaseSubjectService.subjectCompanyQry(request.getShopName());
//
//        List<PurchaseSubjectNameResult> resultList = Lists.newArrayList();
//        for (PurchaseSubject purchaseSubject : purchaseSubjectList) {
//            List<String> subjectCompanyArr = Splitter.on(",").splitToList(purchaseSubject.getSubjectCompany());
//            for (String subjectCompanyStr : subjectCompanyArr) {
//                PurchaseSubjectNameResult result = new PurchaseSubjectNameResult();
//                result.setId(purchaseSubject.getId());
//                result.setShopName(purchaseSubject.getName());
//                result.setSubjectCompany(subjectCompanyStr);
//                resultList.add(result);
//            }
//        }

        List<PurchaseSubjectNameResult> collect = statementCompanyService.queryCompanyName(FlywheelConstant._SJZ_SUBJECT)
                .stream().map(a -> {
                    PurchaseSubjectNameResult result = new PurchaseSubjectNameResult();
                    result.setId(a.getId());
                    result.setSubjectCompany(a.getCompanyName());
                    return result;
                }).collect(Collectors.toList());

        if (StringUtils.isNotBlank(request.getOriginSerialNo())) {
            BillSaleReturnOrder billSaleReturnOrder = Optional.ofNullable(request)
                    .filter(t -> Objects.nonNull(t.getOriginId()) || StringUtils.isNotBlank(t.getOriginSerialNo()))
                    .map(t -> billSaleReturnOrderService.getOne(Wrappers.<BillSaleReturnOrder>lambdaQuery()
                            .eq(BillSaleReturnOrder::getId, t.getOriginId())
                            .or().eq(BillSaleReturnOrder::getSerialNo, t.getOriginSerialNo())))
                    .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

            if (Objects.nonNull(billSaleReturnOrder) && Objects.nonNull(billSaleReturnOrder.getShopId())) {
                List<SaleReturnSubjectMapping> saleReturnSubjectMappingList = saleReturnSubjectMappingService.list(Wrappers.<SaleReturnSubjectMapping>lambdaQuery().eq(SaleReturnSubjectMapping::getShopId, billSaleReturnOrder.getShopId()));
                StoreManagementInfo storeManagementInfo = storeManagementService.selectInfoById(billSaleReturnOrder.getShopId());
                if (CollectionUtils.isNotEmpty(saleReturnSubjectMappingList)) {
                    SaleReturnSubjectMapping saleReturnSubjectMapping = saleReturnSubjectMappingList.get(0);

                    PurchaseSubject purchaseSubject = purchaseSubjectService.getById(saleReturnSubjectMapping.getSubjectId());

                    collect.add(0, PurchaseSubjectNameResult.builder().shopName(Optional.ofNullable(storeManagementInfo).get().getName()).subjectCompany(purchaseSubject.getName()).build());
                }
            }
        }

        return PageResult.<PurchaseSubjectNameResult>builder()
                .result(collect)
                .totalCount(1)
                .totalPage(1)
                .build();
    }

    @Resource
    private SaleReturnSubjectMappingService saleReturnSubjectMappingService;

    @Resource
    private BillSaleReturnOrderService billSaleReturnOrderService;

    @Override
    public List<FinancialStatementCompanyQueryResult> queryAllSubjectName() {
        return statementCompanyService.list()
                .stream()
                .map(a -> FinancialStatementCompanyQueryResult.builder()
                        .id(a.getId())
                        .name(a.getCompanyName())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public void create(FinancialStatementCreateRequest request) {
        FinancialStatement statement = statementService.getOne(new LambdaQueryWrapper<FinancialStatement>()
                .eq(FinancialStatement::getSerialNo, request.getSerialNo()));

        FinancialStatement fs = FinancialStatementConvert.INSTANCE.convertCreateRequest(request);

        if (Objects.isNull(statement)) {
            fs.setStatus(FinancialStatusEnum.PENDING_REVIEW);
            statementService.save(fs);
            return;
        }
        if (!FinancialStatusEnum.PENDING_REVIEW.equals(statement.getStatus())) {
            return;
        }
        fs.setId(statement.getId());
        statementService.updateById(fs);
    }

    @Override
    @Transactional
    public void matchingWriteOff(FinancialStatementMatchingWriteOffRequest request) {
        List<FinancialStatement> statementList = statementService.listByIds(request.getIds());
        Map<String, List<AccountReceStateRel>> map = stateRelService.list(new LambdaQueryWrapper<AccountReceStateRel>()
                        .eq(AccountReceStateRel::getWhetherMatching, WhetherEnum.NO)
                        .in(AccountReceStateRel::getFinancialStatementSerialNo, statementList.stream().map(FinancialStatement::getSerialNo).collect(Collectors.toList())))
                .stream().collect(Collectors.groupingBy(AccountReceStateRel::getFinancialStatementSerialNo));
        Date date = new Date();

        /**
         * 实收金额没有变导致
         */
        statementList.forEach(statement -> {
            //点击匹配核销后，自动将财务上传的流水与确认收款单内的流水进行匹配核销
            //1、流水金额-已使用金额=未使用金额=0时自动核销该比流水，状态该位【已核销】
            //2、流水金额-已使用金额=未使用金额＞0时状态改为【部分核销】
            //3、待核销金额=流水金额时状态为【待核销】
            if (map.containsKey(statement.getSerialNo())) {
                List<AccountReceStateRel> relList = map.get(statement.getSerialNo());
                BigDecimal totalFundsUsed = relList.stream().map(AccountReceStateRel::getFundsUsed).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal subtract = statement.getWaitAuditPrice().subtract(totalFundsUsed);

                if (subtract.compareTo(BigDecimal.ZERO) >= 0) {

                    //总的流程
                    FinancialStatement fs = new FinancialStatement();
                    fs.setId(statement.getId());
                    fs.setStatus(FinancialStatusEnum.AUDITED);
                    fs.setWaitAuditPrice(subtract);
                    fs.setAuditDescription(subtract.compareTo(BigDecimal.ZERO) == 0 ? "匹配核销(手动)" : "部分匹配核销(手动)");
                    fs.setAuditTime(date);
                    fs.setAuditName(UserContext.getUser().getUserName());
                    statementService.updateById(fs);

                    BigDecimal waitAuditPrice = statement.getWaitAuditPrice();

                    for (AccountReceStateRel r : relList) {
                        AccountReceStateRel rel = new AccountReceStateRel();
                        rel.setId(r.getId());
                        rel.setFinancialStatementId(statement.getId());
                        rel.setFundsReceived(waitAuditPrice);
                        rel.setWhetherMatching(WhetherEnum.YES);
                        stateRelService.updateById(rel);

                        waitAuditPrice = waitAuditPrice.subtract(r.getFundsUsed());
                    }
                }
//                else if (subtract.compareTo(BigDecimal.ZERO) > 0) {
//                    FinancialStatement fs = new FinancialStatement();
//                    fs.setId(statement.getId());
//                    fs.setStatus(FinancialStatusEnum.PORTION_WAIT_AUDIT);
//                    fs.setWaitAuditPrice(subtract);
//                    fs.setAuditDescription("部分匹配核销(手动)");
//                    fs.setAuditTime(date);
//                    fs.setAuditName(UserContext.getUser().getUserName());
//                    statementService.updateById(fs);
//
//                    relList.forEach(r -> {
//                        AccountReceStateRel rel = new AccountReceStateRel();
//                        rel.setId(r.getId());
//                        rel.setFinancialStatementId(statement.getId());
//                        rel.setFundsReceived(statement.getWaitAuditPrice());
//                        rel.setWhetherMatching(WhetherEnum.YES);
//                        stateRelService.updateById(rel);
//                    });
//                }
                else {
                    throw new OperationRejectedException(OperationExceptionCode.CANNOT_BE_GREATER_THAN_RECEIVABLE_AMOUNT, totalFundsUsed);
                }
            }
        });
    }

    @Override
    public Boolean saveHfTradeFlow(HfTradeFlowSaveRequest request) {
        FirmShop firmShop = firmShopService.list(Wrappers.<FirmShop>lambdaQuery()
                        .eq(FirmShop::getHfMemberId, request.getMemberId()))
                .stream()
                .findFirst()
                .orElse(null);

        if (Objects.isNull(firmShop)) {
            return false;
        }
        StoreRelationshipSubject relationshipSubject = storeRelationshipSubjectService.getByShopId(firmShop.getDeptId());

        if (Objects.isNull(relationshipSubject)) {
            return false;
        }

        BigDecimal ordAmt = BigDecimalUtil.centToYuan(request.getOrdAmt());

        //流水号不在系统中的 新增
        FinancialStatement statement = new FinancialStatement();
        statement.setSerialNo(request.getOrdId());
        statement.setCollectionTime(DateUtils.parseDate(DateUtils.YMD_HMS2, request.getTransDateTime()));
        statement.setShopId(firmShop.getDeptId());
        statement.setSubjectId(statementCompanyService.queryCompanyName(relationshipSubject.getSubjectId() + "").stream().map(FinancialStatementCompany::getId).findFirst().orElse(null));
        statement.setRemarks(request.getRemarks());
        statement.setFundsReceived(ordAmt);
        statement.setReceivableAmount(ordAmt);
        statement.setWaitAuditPrice(ordAmt);
        statement.setStatus(FinancialStatusEnum.PENDING_REVIEW);
        return statementService.save(statement);
    }
}
