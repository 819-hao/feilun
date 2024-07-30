package com.seeease.flywheel.serve.financial.template.payment.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.convert.AuditLoggingConvert;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.mapper.*;
import com.seeease.flywheel.serve.financial.template.payment.PaymentFTemplate;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/1/11 14:55
 */
@Component
@Slf4j
public class PaymentFTemplateImpl implements PaymentFTemplate {

    @Resource
    private CustomerService customerService;

    @Resource
    private StockService stockService;

    @Resource
    private AccountsPayableAccountingMapper accountsPayableAccountingMapper;

    @Resource
    private AuditLoggingMapper auditLoggingMapper;

    @Resource
    private AuditLoggingDetailMapper auditLoggingDetailMapper;


    @Resource
    private AccountStockRelationMapper accountStockRelationMapper;

    @Resource
    private ApplyFinancialPaymentMapper applyFinancialPaymentMapper;

    @Resource
    private BillPurchaseMapper billPurchaseMapper;

    @Resource
    private BillPurchaseLineMapper billPurchaseLineMapper;

    @Resource
    private AccountReceiptConfirmMapper accountReceiptConfirmMapper;

    /**
     * 由业务自主创建
     * <p>
     * 申请打款后
     * 申请打款单 业务手动创建
     *
     * @param request
     */
    @Override
    public void generatePayable(JSONObject request) {

        //主动发起申请打款单
        ApplyFinancialPayment applyFinancialPayment = request.getObject("afp", ApplyFinancialPayment.class);

        AccountReceiptConfirm accountReceiptConfirm = request.getObject("arc", AccountReceiptConfirm.class);
        List<AccountStockRelation> accountStockRelationList;

        if (Objects.nonNull(applyFinancialPayment)) {
            accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery()
                    .eq(AccountStockRelation::getAfpId, applyFinancialPayment.getId()));

            Assert.isTrue(CollectionUtils.isNotEmpty(accountStockRelationList), "没有关联表数据");
            Assert.isTrue(accountStockRelationList.stream().allMatch(Objects::nonNull), "关联表数据错误");
        } else if (Objects.nonNull(accountReceiptConfirm)) {
            accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery()
                    .eq(AccountStockRelation::getArcId, accountReceiptConfirm.getId()));

            Assert.isTrue(CollectionUtils.isNotEmpty(accountStockRelationList), "没有关联表数据");
            Assert.isTrue(accountStockRelationList.stream().allMatch(Objects::nonNull), "关联表数据错误");
        } else {
            //todo 平帐
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }

        AccountStockRelation accountStockRelation = accountStockRelationList.get(FlywheelConstant.INDEX);

        //查询采购单
        BillPurchase purchase = billPurchaseMapper.selectOne(Wrappers.<BillPurchase>lambdaQuery()
                .eq(BillPurchase::getSerialNo, accountStockRelation.getOriginSerialNo()));

        Assert.notNull(purchase, "采购单不能为空");
        //查询采购行
        List<BillPurchaseLine> purchaseLineList = billPurchaseLineMapper.selectList(Wrappers.<BillPurchaseLine>lambdaQuery()
                .eq(BillPurchaseLine::getPurchaseId, purchase.getId())
                .eq(BillPurchaseLine::getStockId, accountStockRelation.getStockId())
        );

        Assert.notEmpty(purchaseLineList, "采购行不能为空");
        Assert.isTrue(purchaseLineList.stream().allMatch(Objects::nonNull), "采购行数据不能为空");
        Map<Integer, StockExt> stockMap = Optional.ofNullable(purchaseLineList.stream().map(BillPurchaseLine::getStockId).filter(Objects::nonNull).collect(Collectors.toList())).filter(CollectionUtils::isNotEmpty).map(ids -> stockService.selectByStockIdList(ids).stream().collect(Collectors.toMap(StockExt::getStockId, Function.identity()))).orElse(Collections.EMPTY_MAP);

        Customer customer = customerService.getById(purchase.getCustomerId());
        Assert.notNull(customer, "客户不能为空");

        AccountsPayableAccounting payableAccounting = new AccountsPayableAccounting();
        payableAccounting.setSerialNo(SerialNoGenerator.generateAmountPayableSerialNo());
        payableAccounting.setType(ReceiptPaymentTypeEnum.AMOUNT_PAYABLE);

        FinancialSalesMethodEnum salesMethod;

        switch (purchase.getPurchaseSource()) {
            case GR_HG_ZH:
                salesMethod = FinancialSalesMethodEnum.PURCHASE_DISPLACE;
                break;
            case GR_HG_JHS:
                salesMethod = FinancialSalesMethodEnum.PURCHASE_RECYCLE;
                break;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }

        //单数据
        payableAccounting.setSalesMethod(salesMethod);
        payableAccounting.setClassification(FinancialClassificationEnum.GR_HG);
        payableAccounting.setOriginSerialNo(purchase.getSerialNo());
        if (Objects.nonNull(applyFinancialPayment)) {
            payableAccounting.setAfpSerialNo(applyFinancialPayment.getSerialNo());
            payableAccounting.setAfpId(applyFinancialPayment.getId());
        }

        if (Objects.nonNull(accountReceiptConfirm)) {
            payableAccounting.setArcSerialNo(accountReceiptConfirm.getSerialNo());
        }
        payableAccounting.setShopId(purchase.getStoreId());
        payableAccounting.setBelongId(purchase.getPurchaseSubjectId());
        payableAccounting.setStatus(FinancialStatusEnum.PENDING_REVIEW);
        payableAccounting.setCustomerId(purchase.getCustomerId());
        payableAccounting.setCustomerContactId(purchase.getCustomerContactId());
        payableAccounting.setCustomerType(Objects.nonNull(customer) ? customer.getType().getValue() : null);
        payableAccounting.setApplicant(purchase.getCreatedBy());
        payableAccounting.setWhetherUse(WhetherEnum.YES.getValue());
        payableAccounting.setPurchaseId(purchase.getPurchaseId());
        payableAccounting.setDemanderStoreId(purchase.getDemanderStoreId());
        payableAccounting.setOriginType(OriginTypeEnum.CG);

        BillPurchaseLine billPurchaseLine = purchaseLineList.get(FlywheelConstant.INDEX);

        StockExt ext = stockMap.get(billPurchaseLine.getStockId());

        //行数据
        payableAccounting.setTotalPrice(billPurchaseLine.getPurchasePrice());
        payableAccounting.setStockId(billPurchaseLine.getStockId());
        payableAccounting.setStockSn(billPurchaseLine.getStockSn());
        payableAccounting.setWaitAuditPrice(billPurchaseLine.getPurchasePrice());
        payableAccounting.setBrandName(ext.getBrandName());
        payableAccounting.setSeriesName(ext.getSeriesName());
        payableAccounting.setModel(ext.getModel());

        accountsPayableAccountingMapper.insert(payableAccounting);

    }

    @Override
    public void listenerVerification(JSONObject request) {
        Integer node = request.getInteger("node");

        //核销->打款成功&&商品入库
        Integer stockId = request.getInteger("stockId");
        String originSerialNo = request.getString("originSerialNo");
        BillPurchase billPurchase = request.getObject("purchase", BillPurchase.class);

        ApplyFinancialPayment applyFinancialPayment = null;

        AccountReceiptConfirm accountReceiptConfirm = null;

        List<AccountStockRelation> accountStockRelationList = null;
        AccountStockRelation accountStockRelation = null;

        Boolean check = null;

        if (Objects.nonNull(billPurchase)) {
            switch (billPurchase.getPaymentMethod()) {
                case FK_QK:
                    //申请打款
                    accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery()
                            .eq(AccountStockRelation::getOriginSerialNo, originSerialNo)
                            .eq(AccountStockRelation::getStockId, stockId));

                    check = true;
                    break;
                case FK_CE:
                    //打款
                    if (billPurchase.getSalePrice().compareTo(billPurchase.getTotalPurchasePrice()) < 0) {
                        //申请打款
                        accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery()
                                .eq(AccountStockRelation::getOriginSerialNo, originSerialNo)
                                .eq(AccountStockRelation::getStockId, stockId));

                        check = true;
                    } else if (billPurchase.getSalePrice().compareTo(billPurchase.getTotalPurchasePrice()) > 0) {
                        //确认收款
                        accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery()
                                .eq(AccountStockRelation::getOriginSerialNo, originSerialNo)
                                .eq(AccountStockRelation::getStockId, stockId));

                        check = false;
                    } else {
                        log.warn("平帐,purchase={}", billPurchase.getSerialNo());
                    }

                    break;
            }
        } else {
            //申请打款
            accountStockRelationList = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery()
                    .eq(AccountStockRelation::getOriginSerialNo, originSerialNo)
                    .eq(AccountStockRelation::getStockId, stockId));

            check = true;
        }

        Stock stock = stockService.getById(stockId);
        switch (node) {
            //入库成功 && 申请打款状态
            case 1:

                if (Objects.nonNull(check) && CollectionUtils.isNotEmpty(accountStockRelationList)) {

                    accountStockRelation = accountStockRelationList.stream().findFirst().get();

                    if (Objects.nonNull(accountStockRelation.getAfpId())) {
                        applyFinancialPayment = applyFinancialPaymentMapper.selectById(accountStockRelation.getAfpId());

                        Assert.notNull(applyFinancialPayment, "申请打款单不能为空");
                    } else if (Objects.nonNull(accountStockRelation.getArcId())) {
                        accountReceiptConfirm = accountReceiptConfirmMapper.selectById(accountStockRelation.getArcId());

                        Assert.notNull(accountReceiptConfirm, "确认收款单不能为空");
                    }

                    if (check && (ObjectUtils.isEmpty(applyFinancialPayment) || !applyFinancialPayment.getState().equals(ApplyFinancialPaymentStateEnum.PAID))) {
                        log.warn("入库成功&未打款，表的id={}, 申请打款单号={}", stockId, applyFinancialPayment.getSerialNo());
                        return;
                    }
                    if (!check && (ObjectUtils.isEmpty(accountReceiptConfirm) || !accountReceiptConfirm.getStatus().equals(AccountReceiptConfirmStatusEnum.FINISH.getValue()))) {
                        log.warn("入库成功&未收款，表的id={}, 确认收款单号={}", stockId, accountReceiptConfirm.getSerialNo());
                        return;
                    }
                } else if (Objects.nonNull(check) && CollectionUtils.isEmpty(accountStockRelationList)) {
                    //未进行申请结算
                    return;
                }

                break;
            //打款成功 || 收款成功
            case 2:
                if (!Arrays.asList(StockStatusEnum.WAIT_PRICING, StockStatusEnum.MARKETABLE).contains(stock.getStockStatus())) {
                    return;
                }

                if (check){
                    accountStockRelation = accountStockRelationList.stream().findFirst().get();

                    if (Objects.nonNull(accountStockRelation.getAfpId())) {
                        applyFinancialPayment = applyFinancialPaymentMapper.selectById(accountStockRelation.getAfpId());

                        Assert.notNull(applyFinancialPayment, "申请打款单不能为空");
                    }
                }
                break;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }

        AccountsPayableAccounting payableAccounting = accountsPayableAccountingMapper
                .selectOne(Wrappers.<AccountsPayableAccounting>lambdaQuery().eq(AccountsPayableAccounting::getStockId, stockId)
                        .eq(AccountsPayableAccounting::getType, ReceiptPaymentTypeEnum.AMOUNT_PAYABLE)
                        .eq(AccountsPayableAccounting::getOriginSerialNo, originSerialNo));

        Assert.notNull(payableAccounting, "应收应付单不能为空");

        AccountsPayableAccounting accountsPayableAccounting = new AccountsPayableAccounting();
        accountsPayableAccounting.setId(payableAccounting.getId());
        if (Objects.nonNull(check) && check) {
            accountsPayableAccounting.setAfpSerialNo(applyFinancialPayment.getSerialNo());
            accountsPayableAccounting.setAfpId(applyFinancialPayment.getId());
        } else if (Objects.nonNull(check) && !check) {
            accountsPayableAccounting.setArcSerialNo(accountReceiptConfirm.getSerialNo());
        }
        accountsPayableAccounting.setStatus(FinancialStatusEnum.AUDITED);
        accountsPayableAccounting.setAuditDescription(node == 1 ? FlywheelConstant.IN_STORE_AUDIT : FlywheelConstant.PAYMENT_AUDIT);
        accountsPayableAccounting.setWaitAuditPrice(BigDecimal.ZERO);
        accountsPayableAccounting.setAuditTime(new Date());
        accountsPayableAccounting.setAuditor(UserContext.getUser().getUserName());

        accountsPayableAccountingMapper.updateById(accountsPayableAccounting);

        //查询内容
        AccountsPayableAccounting accounting = accountsPayableAccountingMapper.selectById(payableAccounting.getId());

        AuditLogging auditLogging = AuditLoggingConvert.INSTANCE.convertAccountsPayableAccounting(accounting);
        auditLogging.setId(null);
        auditLogging.setAuditTime(new Date());
        auditLogging.setAuditDescription(FlywheelConstant.IN_STORE_AUDIT);
        auditLogging.setAuditName(UserContext.getUser().getUserName());
        auditLogging.setStatus(FinancialStatusEnum.AUDITED);
        auditLogging.setNumber(1);

        auditLoggingMapper.insert(auditLogging);

        AuditLoggingDetail detail = AuditLoggingConvert.INSTANCE.convertAuditLoggingDetail(accounting);

        detail.setId(null);
        detail.setAuditLoggingId(auditLogging.getId());
        detail.setApaId(accounting.getId());
        detail.setSerialNo(accounting.getSerialNo());
        switch (auditLogging.getType()) {
            case PRE_PAID_AMOUNT:
                detail.setPrePaidAmount(accounting.getTotalPrice());
                break;
            case AMOUNT_PAYABLE:
                detail.setAmountPayable(accounting.getTotalPrice());
                break;
            case AMOUNT_RECEIVABLE:
                detail.setAmountReceivable(accounting.getTotalPrice());
                break;
            case PRE_RECEIVE_AMOUNT:
                detail.setPreReceiveAmount(accounting.getTotalPrice());
                break;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
        auditLoggingDetailMapper.insert(detail);
    }
}
