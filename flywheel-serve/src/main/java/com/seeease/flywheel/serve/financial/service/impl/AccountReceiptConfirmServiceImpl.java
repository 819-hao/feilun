package com.seeease.flywheel.serve.financial.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmDetailResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmMiniPageResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmPageResult;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.financial.convert.AccountReceiptConfirmConvert;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.AccountReceiptConfirmStatusEnum;
import com.seeease.flywheel.serve.financial.enums.CollectionNatureEnum;
import com.seeease.flywheel.serve.financial.enums.CollectionTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.event.AccountReceiptConfirmPassEvent;
import com.seeease.flywheel.serve.financial.mapper.*;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountReceiptConfirmServiceImpl extends ServiceImpl<AccountReceiptConfirmMapper, AccountReceiptConfirm>
        implements AccountReceiptConfirmService {

    @Resource
    private AccountReceStateRelMapper accountReceStateRelMapper;
    @Resource
    private CustomerBalanceMapper customerBalanceMapper;
    @Resource
    private FinancialStatementMapper financialStatementMapper;
    @Resource
    private AccountStockRelationMapper accountStockRelationMapper;
    @Resource
    private AccountsPayableAccountingMapper accountsPayableAccountingMapper;
    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;

    @Transactional
    @Override
    public AccountReceiptConfirm accountReceiptConfirmAdd(AccountReceiptConfirmAddRequest request) {
        AccountReceiptConfirm accountReceiptConfirm = new AccountReceiptConfirm();
        accountReceiptConfirm.setSerialNo(SerialNoGenerator.generateAccountReceiptConfirmSerialNo());

        accountReceiptConfirm.setStatus(request.getStatus());
        accountReceiptConfirm.setShopId(request.getShopId());
        accountReceiptConfirm.setTotalNumber(null != request.getTotalNumber() ? request.getTotalNumber() : FlywheelConstant.ONE);

        //收款类型:CollectionTypeEnum---0客户充值，1消费收款
        accountReceiptConfirm.setCollectionType(request.getCollectionType());
        //收款性质:CollectionNatureEnum---0寄售保证金,1客户余额,2正常销售
        accountReceiptConfirm.setCollectionNature(request.getCollectionNature());
        //订单分类：OriginTypeEnum---2采购退货，3销售
        accountReceiptConfirm.setOriginType(request.getOriginType());
        //业务方式:FinancialSalesMethodEnum---同行采购-7备货、6订金、8批量
        //个人销售---1正常、2订金、3赠送
        //同行销售---1正常、4寄售
        //个人回收---9仅回收
        accountReceiptConfirm.setSalesMethod(request.getSalesMethod());
        //订单类型:FinancialClassificationEnum--- 1-同行采购、7-个人销售、8-同行销售、3-个人回收
        accountReceiptConfirm.setClassification(request.getClassification());

        accountReceiptConfirm.setType(null != request.getType() ? request.getType() : FlywheelConstant.INTEGER_DAFULT_VALUE);
        accountReceiptConfirm.setReceivableAmount(null != request.getReceivableAmount() ? request.getReceivableAmount() : BigDecimal.ZERO);
        accountReceiptConfirm.setWaitBindingAmount(null != request.getWaitAuditPrice() ? request.getWaitAuditPrice() : BigDecimal.ZERO);
        accountReceiptConfirm.setCustomerId(request.getCustomerId());
        accountReceiptConfirm.setCustomerName(request.getCustomerName());
        accountReceiptConfirm.setCustomerContractId(accountReceiptConfirm.getCustomerContractId());
        accountReceiptConfirm.setCustomerContractName(accountReceiptConfirm.getCustomerContractName());
        accountReceiptConfirm.setOriginSerialNo(StringUtils.isNoneBlank(request.getOriginSerialNo()) ? request.getOriginSerialNo() : FlywheelConstant.STRING_DAFULT_VALUE);
        accountReceiptConfirm.setStatementCompanyId(request.getStatementCompanyId());
        accountReceiptConfirm.setPayer(request.getPayer());

        accountReceiptConfirm.setCreatedId(request.getCreatedId());
        accountReceiptConfirm.setCreatedBy(request.getCreatedBy());
        //
        this.baseMapper.insert(accountReceiptConfirm);

        //收款流水关系
        if (null != request.getFinancialStatementId()) {
            AccountReceStateRel accountReceStateRel = new AccountReceStateRel();
            accountReceStateRel.setFinancialStatementId(request.getFinancialStatementId());
            accountReceStateRel.setAccountReceiptConfirmId(accountReceiptConfirm.getId());
            accountReceStateRel.setFundsReceived(request.getWaitAuditPrice());
            accountReceStateRel.setFundsUsed(request.getWaitAuditPrice());
            accountReceStateRel.setWhetherMatching(WhetherEnum.NO);
            accountReceStateRelMapper.insert(accountReceStateRel);

            accountReceiptConfirmStatusWriteOff(accountReceiptConfirm.getId(), Lists.newArrayList(request.getFinancialStatementId()));
        }

        //客户余额数据新增或者更新
        if (request.getMiniAppSource()) {
            customerBalanceCmd(request);
        }

        return accountReceiptConfirm;

    }

    private void customerBalanceCmd(AccountReceiptConfirmAddRequest request) {
        CustomerBalance customerBalance = new CustomerBalance();
        customerBalance.setCustomerId(request.getCustomerId());
        customerBalance.setShopId(request.getShopId());
        customerBalance.setCustomerContactId(request.getContactId());
        BigDecimal consignmentMargin = BigDecimal.ZERO;
        BigDecimal accountBalance = BigDecimal.ZERO;
        if (CollectionNatureEnum.ACCOUNT_BALANCE.getValue() == request.getCollectionNature()
                && null != request.getReceivableAmount()) {
            accountBalance = request.getReceivableAmount();
        } else if (CollectionNatureEnum.CONSIGNMENT_MARGIN.getValue() == request.getCollectionNature()
                && null != request.getReceivableAmount()) {
            consignmentMargin = request.getReceivableAmount();
        }
        customerBalance.setAccountBalance(accountBalance);
        customerBalance.setConsignmentMargin(consignmentMargin);
        customerBalance.setType(request.getCollectionNature());
        customerBalance.setUserId(FlywheelConstant.INTEGER_DAFULT_VALUE);
        customerBalanceMapper.insert(customerBalance);
    }


    @Override
    public Page<AccountReceiptConfirmMiniPageResult> accountReceiptConfirmMiniPageQuery(AccountReceiptConfirmMiniPageRequest request) {
        return this.baseMapper.getMiniPage(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<AccountReceiptConfirmPageResult> accountReceiptConfirmPageQuery(AccountReceiptConfirmPageRequest request) {
        return this.baseMapper.getPCPage(new Page(request.getPage(), request.getLimit()), request);
    }

    @Transactional
    @Override
    public List<Integer> accountReceiptConfirmStateUpdate(AccountReceiptConfirmFlowUpdateRequest request) {

        BigDecimal totalAmount = request.getFinancialStatementList().stream()
                .map(e -> e.getReceivableAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        AccountReceiptConfirm accountReceiptConfirm = this.baseMapper.selectById(request.getId());
        Assert.isTrue(accountReceiptConfirm.getWaitBindingAmount().compareTo(totalAmount) >= 0, "绑定流水使用总金额大于待核销金额");

        for (AccountReceiptConfirmFlowUpdateRequest.FinancialStatementUpdateRequest financialStatement : request.getFinancialStatementList()) {

            AccountReceStateRel receStateRel = new AccountReceStateRel();
            receStateRel.setAccountReceiptConfirmId(request.getId());
            receStateRel.setFinancialStatementId(financialStatement.getId());
            //本次使用金额前，剩余金额
            receStateRel.setFundsReceived(financialStatement.getWaitAuditPrice());
            //本次使用金额
            receStateRel.setFundsUsed(financialStatement.getReceivableAmount());
            receStateRel.setWhetherMatching(WhetherEnum.NO);
            accountReceStateRelMapper.insert(receStateRel);
        }

        return accountReceiptConfirmPartWriteOff(request.getId(), request.getFinancialStatementList());

    }

    /**
     * 小程序新建确认收款单核销
     *
     * @param accountReceiptConfirmId
     * @param financialStatementIds
     * @return
     */
    private List<Integer> accountReceiptConfirmStatusWriteOff(Integer accountReceiptConfirmId, List<Integer> financialStatementIds) {
        AccountReceiptConfirm accountReceiptConfirm = this.baseMapper.selectById(accountReceiptConfirmId);
        LambdaQueryWrapper<FinancialStatement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FinancialStatement::getId, financialStatementIds);
        List<FinancialStatement> financialStatementList = financialStatementMapper.selectList(queryWrapper);
        BigDecimal waitAuditPriceSum = financialStatementList.stream().filter(Objects::nonNull)
                .map(e -> e.getWaitAuditPrice()).reduce(BigDecimal.ZERO, (b1, b2) -> b1.add(b2));

        //流水金额等于确认收款单金额，确认收款单已核销
        if (accountReceiptConfirm.getWaitBindingAmount().compareTo(BigDecimal.ZERO) != 0 &&
                accountReceiptConfirm.getWaitBindingAmount().compareTo(waitAuditPriceSum) != 0) {
            throw new BusinessException(ExceptionCode.ACCOUNT_RECE_CONFIRM_AMOUNT_ERR);
        }


        LambdaUpdateWrapper<FinancialStatement> financialStatementUpdateWrapper = new LambdaUpdateWrapper<>();
        financialStatementUpdateWrapper.in(FinancialStatement::getId, financialStatementIds)
                .set(FinancialStatement::getStatus, FinancialStatusEnum.AUDITED)
                .set(FinancialStatement::getWaitAuditPrice, BigDecimal.ZERO);
        financialStatementMapper.update(null, financialStatementUpdateWrapper);

        LambdaUpdateWrapper<AccountReceiptConfirm> accountReceiptConfirmUpdateWrapper = new LambdaUpdateWrapper<>();
        accountReceiptConfirmUpdateWrapper.eq(AccountReceiptConfirm::getId, accountReceiptConfirmId)
                .set(AccountReceiptConfirm::getWaitBindingAmount, BigDecimal.ZERO)
                .set(AccountReceiptConfirm::getStatus, AccountReceiptConfirmStatusEnum.FINISH.getValue());
        this.baseMapper.update(null, accountReceiptConfirmUpdateWrapper);

        LambdaQueryWrapper<AccountStockRelation> stockRelationQueryWrapper = new LambdaQueryWrapper<>();
        stockRelationQueryWrapper.eq(AccountStockRelation::getArcId, accountReceiptConfirmId);
        List<AccountStockRelation> accountStockRelationList = accountStockRelationMapper.selectList(stockRelationQueryWrapper);
        List<Integer> stockIdList = accountStockRelationList.stream().filter(Objects::nonNull).map(e -> e.getStockId()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(stockIdList)) {
            return Collections.EMPTY_LIST;
        }

        LambdaUpdateWrapper<AccountsPayableAccounting> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(AccountsPayableAccounting::getStockId, stockIdList)
                .set(AccountsPayableAccounting::getStatus, FinancialStatusEnum.AUDITED);
        accountsPayableAccountingMapper.update(null, updateWrapper);

        return stockIdList;
    }

    /**
     * 确认收款单---核销
     * 可能部分核销
     */
    private List<Integer> accountReceiptConfirmPartWriteOff(Integer accountReceiptConfirmId,
                                                            List<AccountReceiptConfirmFlowUpdateRequest.FinancialStatementUpdateRequest> financialStatementList) {
        AccountReceiptConfirm accountReceiptConfirm = this.baseMapper.selectById(accountReceiptConfirmId);

        //本次核销金额
        BigDecimal receiveAmountSum = financialStatementList.stream().filter(Objects::nonNull)
                .map(e -> e.getReceivableAmount()).reduce(BigDecimal.ZERO, (b1, b2) -> b1.add(b2));

        //流水金额等于确认收款单金额，确认收款单已核销
        if (accountReceiptConfirm.getWaitBindingAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException(ExceptionCode.ACCOUNT_RECE_CONFIRM_AMOUNT_ERR);
        }

        //存在多笔流水部分核销
        for (AccountReceiptConfirmFlowUpdateRequest.FinancialStatementUpdateRequest financialStatementRequest : financialStatementList) {
            LambdaUpdateWrapper<FinancialStatement> financialStatementUpdateWrapper = new LambdaUpdateWrapper<>();
            financialStatementUpdateWrapper.eq(FinancialStatement::getId, financialStatementRequest.getId());
            if (financialStatementRequest.getReceivableAmount().compareTo(financialStatementRequest.getWaitAuditPrice()) == 0) {
                financialStatementUpdateWrapper.set(FinancialStatement::getStatus, FinancialStatusEnum.AUDITED)
                        .set(FinancialStatement::getWaitAuditPrice, BigDecimal.ZERO);
            } else {
                BigDecimal waitAuditPriceLeft = financialStatementRequest.getWaitAuditPrice().subtract(financialStatementRequest.getReceivableAmount());
                Assert.isTrue(waitAuditPriceLeft.compareTo(BigDecimal.ZERO) > 0, "流水最终使用金额不能扣成负数");

                financialStatementUpdateWrapper.set(FinancialStatement::getStatus, FinancialStatusEnum.PORTION_WAIT_AUDIT)
                        .set(FinancialStatement::getWaitAuditPrice, waitAuditPriceLeft);
            }

            financialStatementMapper.update(null, financialStatementUpdateWrapper);
        }

        BigDecimal waitBindAmountLeft = accountReceiptConfirm.getWaitBindingAmount().subtract(receiveAmountSum);
        LambdaUpdateWrapper<AccountReceiptConfirm> accountReceiptConfirmUpdateWrapper = new LambdaUpdateWrapper<>();
        accountReceiptConfirmUpdateWrapper.eq(AccountReceiptConfirm::getId, accountReceiptConfirmId);
        if (waitBindAmountLeft.compareTo(BigDecimal.ZERO) <= 0) {
            accountReceiptConfirmUpdateWrapper.set(AccountReceiptConfirm::getWaitBindingAmount, BigDecimal.ZERO)
                    .set(AccountReceiptConfirm::getStatus, AccountReceiptConfirmStatusEnum.FINISH.getValue());
        } else {
            Assert.isTrue(waitBindAmountLeft.compareTo(BigDecimal.ZERO) > 0, "确认收款单待金额不能扣成负数");
            accountReceiptConfirmUpdateWrapper.set(AccountReceiptConfirm::getWaitBindingAmount, waitBindAmountLeft)
                    .set(AccountReceiptConfirm::getStatus, AccountReceiptConfirmStatusEnum.PART.getValue());
        }

        this.baseMapper.update(null, accountReceiptConfirmUpdateWrapper);

        LambdaQueryWrapper<AccountStockRelation> stockRelationQueryWrapper = new LambdaQueryWrapper<>();
        stockRelationQueryWrapper.eq(AccountStockRelation::getArcId, accountReceiptConfirmId);
        List<AccountStockRelation> accountStockRelationList = accountStockRelationMapper.selectList(stockRelationQueryWrapper);
        List<Integer> stockIdList = accountStockRelationList.stream().filter(Objects::nonNull).map(e -> e.getStockId()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(stockIdList)) {
            return Collections.EMPTY_LIST;
        }

        LambdaUpdateWrapper<AccountsPayableAccounting> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(AccountsPayableAccounting::getStockId, stockIdList)
                .set(AccountsPayableAccounting::getStatus, FinancialStatusEnum.AUDITED);
        accountsPayableAccountingMapper.update(null, updateWrapper);

        return stockIdList;
    }

    @Override
    public List<AccountReceiptConfirmDetailResult> accountReceiptConfirmDetail(AccountReceiptConfirmDetailRequest request) {
        LambdaQueryWrapper<AccountReceStateRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountReceStateRel::getAccountReceiptConfirmId, request.getId())
                .orderByDesc(AccountReceStateRel::getCreatedTime);
        List<AccountReceStateRel> stateRels = accountReceStateRelMapper.selectList(queryWrapper);

        List<Integer> financialIdList = stateRels.stream().filter(Objects::nonNull).map(AccountReceStateRel::getFinancialStatementId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<FinancialStatement> financialStatementQueryWrapper = new LambdaQueryWrapper<>();
        financialStatementQueryWrapper.in(FinancialStatement::getId, financialIdList);
        List<FinancialStatement> financialStatementList = financialStatementMapper.selectList(financialStatementQueryWrapper);
        Map<Integer, FinancialStatement> financialStatementMap = financialStatementList.stream()
                .collect(Collectors.toMap(FinancialStatement::getId, Function.identity()));

        List<AccountReceiptConfirmDetailResult> resultList = Lists.newArrayList();
        for (AccountReceStateRel rel : stateRels) {
            Integer financialStateId = rel.getFinancialStatementId();
            AccountReceiptConfirmDetailResult result = new AccountReceiptConfirmDetailResult();
            result.setFinancialStatementId(financialStateId);
            if (financialStatementMap.containsKey(financialStateId)) {
                result.setSerialNo(financialStatementMap.get(financialStateId).getSerialNo());
                result.setCollectionTime(financialStatementMap.get(financialStateId).getCollectionTime());
                result.setPayer(financialStatementMap.get(financialStateId).getPayer());
            }
            result.setFundsReceived(rel.getFundsReceived());
            result.setReceivableAmount(rel.getFundsUsed());

            resultList.add(result);
        }

        return resultList;
    }

    @Override
    public AccountReceiptConfirm accountReceiptConfirmQueryById(Integer id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public void confirmReceipt(AccountReceiptConfirmConfirmReceiptRequest request) {
        //本次核销金额
        BigDecimal totalAmount = request.getFinancialStatementList().stream()
                .map(AccountReceiptConfirmConfirmReceiptRequest.FinancialStatementUpdateRequest::getReceivableAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        AccountReceiptConfirm accountReceiptConfirm = this.baseMapper.selectById(request.getId());
        BigDecimal waitBindAmountLeft = accountReceiptConfirm.getWaitBindingAmount().subtract(totalAmount);
        if (waitBindAmountLeft.compareTo(BigDecimal.ZERO) < 0)
            throw new OperationRejectedException(OperationExceptionCode.WAIT_BINDING_AMOUNT_CANNOT_BE_GREATER_THAN_RECEIVABLE_AMOUNT, accountReceiptConfirm.getWaitBindingAmount());
        accountReceStateRelMapper.insertBatchSomeColumn(request.getFinancialStatementList()
                .stream()
                .map(a -> {
                    AccountReceStateRel receStateRel = new AccountReceStateRel();
                    receStateRel.setAccountReceiptConfirmId(request.getId());
                    receStateRel.setFinancialStatementSerialNo(a.getSerialNo().trim());
                    //本次使用金额
                    receStateRel.setFundsUsed(a.getReceivableAmount());
                    receStateRel.setWhetherMatching(WhetherEnum.NO);
                    return receStateRel;
                })
                .collect(Collectors.toList()));
        LambdaUpdateWrapper<AccountReceiptConfirm> accountReceiptConfirmUpdateWrapper = new LambdaUpdateWrapper<>();
        accountReceiptConfirmUpdateWrapper.eq(AccountReceiptConfirm::getId, request.getId());

        List<AccountStockRelation> accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery()
                .eq(AccountStockRelation::getArcId, request.getId()));

        if (waitBindAmountLeft.compareTo(BigDecimal.ZERO) == 0) {
            accountReceiptConfirmUpdateWrapper.set(AccountReceiptConfirm::getWaitBindingAmount, BigDecimal.ZERO)
                    .set(AccountReceiptConfirm::getStatus, AccountReceiptConfirmStatusEnum.FINISH.getValue());
            billHandlerEventPublisher.publishEvent(new AccountReceiptConfirmPassEvent(FinancialStatusEnum.AUDITED,
                    accountReceiptConfirm.getSerialNo(), accountReceiptConfirm.getOriginSerialNo(), accountReceiptConfirm.getCollectionType(), waitBindAmountLeft,
                    CollectionUtils.isNotEmpty(accountStockRelationList) ? accountStockRelationList.stream().map(AccountStockRelation::getStockId).collect(Collectors.toList()) : null));
//            accountsPayableAccountingMapper.updateStatusByArcSerialNo(accountReceiptConfirm.getSerialNo(), FinancialStatusEnum.AUDITED.getValue());

            if (CollectionTypeEnum.KH_CZ.getValue().equals(accountReceiptConfirm.getCollectionType())) {
                CustomerBalance customerBalance = new CustomerBalance();
                customerBalance.setCustomerId(accountReceiptConfirm.getCustomerId());
                customerBalance.setShopId(accountReceiptConfirm.getShopId());
                customerBalance.setCustomerContactId(accountReceiptConfirm.getCustomerContractId());
                BigDecimal consignmentMargin = BigDecimal.ZERO;
                BigDecimal accountBalance = BigDecimal.ZERO;
                if (1 == accountReceiptConfirm.getSalesMethod() && null != accountReceiptConfirm.getReceivableAmount()) {
                    accountBalance = accountReceiptConfirm.getReceivableAmount();
                } else if (4 == accountReceiptConfirm.getSalesMethod() && null != accountReceiptConfirm.getReceivableAmount()) {
                    consignmentMargin = accountReceiptConfirm.getReceivableAmount();
                }
                customerBalance.setAccountBalance(accountBalance);
                customerBalance.setConsignmentMargin(consignmentMargin);
                customerBalance.setType(1 == accountReceiptConfirm.getSalesMethod() ? 1 : 0);
                customerBalance.setUserId(accountReceiptConfirm.getCreatedId());
                customerBalanceMapper.insert(customerBalance);
            }

        } else if (waitBindAmountLeft.compareTo(BigDecimal.ZERO) > 0) {
            accountReceiptConfirmUpdateWrapper.set(AccountReceiptConfirm::getWaitBindingAmount, waitBindAmountLeft)
                    .set(AccountReceiptConfirm::getStatus, AccountReceiptConfirmStatusEnum.PART.getValue());
            billHandlerEventPublisher.publishEvent(new AccountReceiptConfirmPassEvent(FinancialStatusEnum.PORTION_WAIT_AUDIT,
                    accountReceiptConfirm.getSerialNo(), accountReceiptConfirm.getOriginSerialNo(), accountReceiptConfirm.getCollectionType(), waitBindAmountLeft
                    , CollectionUtils.isNotEmpty(accountStockRelationList) ? accountStockRelationList.stream().map(AccountStockRelation::getStockId).collect(Collectors.toList()) : null));
//            accountsPayableAccountingMapper.updateStatusByArcSerialNo(accountReceiptConfirm.getSerialNo(), FinancialStatusEnum.PORTION_WAIT_AUDIT.getValue());
        }
        this.baseMapper.update(null, accountReceiptConfirmUpdateWrapper);

    }

    @Override
    public AccountReceiptConfirm accountReceiptConfirmCreate(AccountReceiptConfirmCreateRequest request) {
        AccountReceiptConfirm accountReceiptConfirm = AccountReceiptConfirmConvert.INSTANCE.convertCreateRequest(request);
        accountReceiptConfirm.setSerialNo(SerialNoGenerator.generateAccountReceiptConfirmSerialNo());
        accountReceiptConfirm.setCollectionType(CollectionTypeEnum.KH_CZ.getValue());
        accountReceiptConfirm.setStatus(AccountReceiptConfirmStatusEnum.WAIT.getValue());
        accountReceiptConfirm.setWaitBindingAmount(request.getReceivableAmount());
        accountReceiptConfirm.setShopId(UserContext.getUser().getStore().getId());
        accountReceiptConfirm.setTotalNumber(null != request.getTotalNumber() ? request.getTotalNumber() : FlywheelConstant.ONE);
        accountReceiptConfirm.setCollectionNature(request.getSalesMethod() == 1 ? 1 : 0);
//        accountReceiptConfirm.setSalesMethod(request.getSalesMethod());
//        accountReceiptConfirm.setStatementCompanyId(request.getStatementCompanyId());
//        accountReceiptConfirm.setReceivableAmount(request.getReceivableAmount());
//        accountReceiptConfirm.setPayer(request.getPayer());
//        accountReceiptConfirm.setCustomerId(request.getCustomerId());
//        accountReceiptConfirm.setCustomerName(request.getCustomerName());
//        accountReceiptConfirm.setCustomerContractId(request.getCustomerContractId());
//        accountReceiptConfirm.setCustomerContractName(request.getCustomerContractName());
        this.baseMapper.insert(accountReceiptConfirm);

        return accountReceiptConfirm;

    }
}


