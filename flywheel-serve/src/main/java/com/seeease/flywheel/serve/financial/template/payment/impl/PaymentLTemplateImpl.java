package com.seeease.flywheel.serve.financial.template.payment.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.financial.convert.AuditLoggingConvert;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.entity.AuditLogging;
import com.seeease.flywheel.serve.financial.entity.AuditLoggingDetail;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.mapper.AuditLoggingDetailMapper;
import com.seeease.flywheel.serve.financial.mapper.AuditLoggingMapper;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.template.payment.PaymentLTemplate;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/1/11 14:55
 */
@Component
public class PaymentLTemplateImpl implements PaymentLTemplate {

    @Resource
    private AccountsPayableAccountingService accountsPayableAccountingService;

    @Resource
    private AuditLoggingMapper auditLoggingMapper;

    @Resource
    private AuditLoggingDetailMapper auditLoggingDetailMapper;

    @Override
    public void listenerVerification(JSONObject request) {

        Integer stockId = request.getInteger("stockId");
        String originSerialNo = request.getString("originSerialNo");

        List<AccountsPayableAccounting> payableAccountingList = accountsPayableAccountingService.list(Wrappers
                        .<AccountsPayableAccounting>lambdaQuery()
                        .eq(AccountsPayableAccounting::getStockId, stockId)
//                .eq(AccountsPayableAccounting::getClassification, FinancialClassificationEnum.GR_HS.getValue())
//                .eq(AccountsPayableAccounting::getSalesMethod, FinancialSalesMethodEnum.PURCHASE_C.getValue())
                        .eq(AccountsPayableAccounting::getOriginSerialNo, originSerialNo)
        );

        if (CollectionUtils.isEmpty(payableAccountingList)){
            return;
        }

        Assert.notEmpty(payableAccountingList, "应收应付不能为空");
        Assert.isTrue(payableAccountingList.stream().allMatch(Objects::nonNull), "应收应付不能为空");

        AuditLogging auditLogging = AuditLoggingConvert.INSTANCE.convertAccountsPayableAccounting(payableAccountingList.get(FlywheelConstant.INDEX));
        auditLogging.setId(null);
        auditLogging.setAuditTime(new Date());
        auditLogging.setAuditName(UserContext.getUser().getUserName());
        auditLogging.setAuditDescription(FlywheelConstant.AUTOMATIC_SYSTEM);
        auditLogging.setNumber(1);
        auditLogging.setStatus(FinancialStatusEnum.AUDITED);

        auditLoggingMapper.insert(auditLogging);

        ArrayList<@Nullable AuditLoggingDetail> arrayList = Lists.newArrayList();

        for (AccountsPayableAccounting payableAccounting : payableAccountingList) {

            AuditLoggingDetail detail = AuditLoggingConvert.INSTANCE.convertAuditLoggingDetail(payableAccounting);

            detail.setId(null);
            detail.setAuditLoggingId(auditLogging.getId());

            switch (auditLogging.getType()) {
                case PRE_PAID_AMOUNT:
                    detail.setPrePaidAmount(payableAccounting.getTotalPrice());
                    break;
                case AMOUNT_PAYABLE:
                    detail.setAmountPayable(payableAccounting.getTotalPrice());
                    break;
                case AMOUNT_RECEIVABLE:
                    detail.setAmountReceivable(payableAccounting.getTotalPrice());
                    break;
                case PRE_RECEIVE_AMOUNT:
                    detail.setPreReceiveAmount(payableAccounting.getTotalPrice());
                    break;
                default:
                    throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
            }

            AccountsPayableAccounting accountsPayableAccounting = new AccountsPayableAccounting();
            accountsPayableAccounting.setStatus(FinancialStatusEnum.AUDITED);
            accountsPayableAccounting.setAuditDescription(FlywheelConstant.C_AUDIT);
            accountsPayableAccounting.setId(payableAccounting.getId());
            accountsPayableAccounting.setAuditor(UserContext.getUser().getUserName());
            accountsPayableAccounting.setAuditTime(new Date());

            accountsPayableAccountingService.updateById(accountsPayableAccounting);

            arrayList.add(detail);
        }
        auditLoggingDetailMapper.insertBatchSomeColumn(arrayList);
    }
}
