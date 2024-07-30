package com.seeease.flywheel.serve.purchase.strategy;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialSalesMethodEnum;
import com.seeease.flywheel.serve.purchase.enums.RecycleModeEnum;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Objects;

/**
 * 个人回收-仅回收
 *
 * @author Tiro
 * @date 2023/3/2
 */
@Component
public class RecyclePersonOnlyStrategy extends PurchaseStrategy {

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.GR_HS_JHS;
    }

    @Override
    void preRequestProcessing(PurchaseCreateRequest request) {

        request.setRecycleModel(request.getStoreId().intValue() == FlywheelConstant._ZB_ID ? RecycleModeEnum.DEPOSIT.getValue() : RecycleModeEnum.PREPARE.getValue());

        request.setFrontIdentityCard(Arrays.asList(request.getApplyFinancialPaymentDetailResult().getFrontIdentityCard()));
        request.setReverseIdentityCard(Arrays.asList(request.getApplyFinancialPaymentDetailResult().getReverseIdentityCard()));
        request.setRecoveryPricingRecord(Arrays.asList(request.getApplyFinancialPaymentDetailResult().getRecoveryPricingRecord()));
        request.setAgreementTransfer(Arrays.asList(request.getApplyFinancialPaymentDetailResult().getAgreementTransfer()));
        //迭代采购价
        request.getDetails().forEach(billPurchaseLineDto -> billPurchaseLineDto.setPurchasePrice(billPurchaseLineDto.getRecyclePrice()));
    }

    @Override
    void checkRequest(PurchaseCreateRequest request) throws BusinessException {

        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getRecyclePrice())), "回收价不能为空");

        Assert.notNull(request.getApplyPaymentSerialNo(), "申请打款单不能为空");


        if (request.getMallUser()) {

            ApplyFinancialPaymentTypeEnum paymentTypeEnum = ApplyFinancialPaymentTypeEnum.fromCode(request.getApplyFinancialPaymentDetailResult().getTypePayment());
            FinancialSalesMethodEnum financialSalesMethodEnum = FinancialSalesMethodEnum.fromCode(request.getApplyFinancialPaymentDetailResult().getSalesMethod());

            if ((paymentTypeEnum != ApplyFinancialPaymentTypeEnum.PERSONAL_RECYCLING || financialSalesMethodEnum != FinancialSalesMethodEnum.PURCHASE_RECYCLE)) {
                throw new OperationRejectedException(OperationExceptionCode.PAYMENT_MATERIAL_NON_NULL);
            }

        }

        super.bizCheckApplyPayment(request);
    }
}
