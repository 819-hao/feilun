package com.seeease.flywheel.serve.financial.template.payment.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmAddRequest;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.service.AccountReceiptConfirmService;
import com.seeease.flywheel.serve.financial.template.payment.PaymentATemplate;
import com.seeease.flywheel.serve.maindata.entity.FinancialStatementCompany;
import com.seeease.flywheel.serve.maindata.service.FinancialStatementCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/1/11 14:55
 */
@Component
@Slf4j
public class PaymentATemplateImpl implements PaymentATemplate {

    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;

    @Resource
    private FinancialStatementCompanyService financialStatementCompanyService;


    @Override
    public void createReceipt(JSONObject request) {

        log.warn("创建确认收款单->{}", JSON.toJSONString(request.getObject("afp", ApplyFinancialPayment.class)));

        ApplyFinancialPayment applyFinancialPayment = request.getObject("afp", ApplyFinancialPayment.class);

        accountReceiptConfirmService.accountReceiptConfirmAdd(AccountReceiptConfirmAddRequest.builder()
                .customerName(applyFinancialPayment.getCustomerName())
                .contactPhone(applyFinancialPayment.getCustomerPhone())
                .shopId(applyFinancialPayment.getShopId())
                .miniAppSource(Boolean.FALSE)
                .waitAuditPrice(applyFinancialPayment.getPricePayment())
                .receivableAmount(applyFinancialPayment.getPricePayment())
                .collectionType(CollectionTypeEnum.CG_TK.getValue())
                .classification(applyFinancialPayment.getTypePayment() == ApplyFinancialPaymentTypeEnum.PERSONAL_RECYCLING ? FinancialClassificationEnum.GR_HS.getValue() : FinancialClassificationEnum.TH_CG.getValue())
                .salesMethod(FinancialSalesMethodEnum.PURCHASE_CANCEL.getValue())
                .payer("-")
                .statementCompanyId(financialStatementCompanyService.list(Wrappers.<FinancialStatementCompany>lambdaQuery().like(FinancialStatementCompany::getSubjectId, applyFinancialPayment.getSubjectPayment())).stream().findFirst().get().getId())
                .status(AccountReceiptConfirmStatusEnum.WAIT.getValue())
                .payer(applyFinancialPayment.getCustomerName())
                .build());
    }
}
