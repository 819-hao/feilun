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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

/**
 * 个人回收-去置换
 *
 * @author Tiro
 * @date 2023/3/2
 */
@Component
public class RecyclePersonReplaceStrategy extends PurchaseStrategy {

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.GR_HS_ZH;
    }

    @Override
    void preRequestProcessing(PurchaseCreateRequest request) {

        //本次销售单子的销售价
        request.setSalePrice(request.getSaleOrderDetailsResult().getTotalSalePrice());

        if (ObjectUtils.isNotEmpty(request.getApplyPaymentSerialNo())) {
            request.setFrontIdentityCard(Arrays.asList(request.getApplyFinancialPaymentDetailResult().getFrontIdentityCard()));
            request.setReverseIdentityCard(Arrays.asList(request.getApplyFinancialPaymentDetailResult().getReverseIdentityCard()));
            request.setRecoveryPricingRecord(Arrays.asList(request.getApplyFinancialPaymentDetailResult().getRecoveryPricingRecord()));
            request.setAgreementTransfer(Arrays.asList(request.getApplyFinancialPaymentDetailResult().getAgreementTransfer()));
        }

        request.setRecycleModel(request.getStoreId().intValue() == FlywheelConstant._ZB_ID ? RecycleModeEnum.DEPOSIT.getValue() : RecycleModeEnum.PREPARE.getValue());

        //总采购价
        request.getDetails().forEach(billPurchaseLineDto -> billPurchaseLineDto.setPurchasePrice(billPurchaseLineDto.getRecyclePrice()));
    }

    @Override
    void checkRequest(PurchaseCreateRequest request) throws BusinessException {

        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getRecyclePrice())), "回收价不能为空");
        Assert.notNull(request.getSaleSerialNo(), "本次销售单号不能为空");
        Assert.notNull(request.getSalePrice(), "本次销售价格不能为空");

        //本次销售价
        //回收价累加
        BigDecimal reduce = request.getDetails().stream().map(PurchaseCreateRequest.BillPurchaseLineDto::getRecyclePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("累计回收价" + reduce);
        if (request.getSalePrice().compareTo(reduce) < 0) {
            if (StringUtils.isBlank(request.getBank()) || StringUtils.isBlank(request.getAccountName()) || StringUtils.isBlank(request.getBankAccount()) || StringUtils.isBlank(request.getBankCustomerName())) {
                throw new OperationRejectedException(OperationExceptionCode.STRAP_PAYMENT_NON_NULL);
            }
        }

        if (request.getMallUser() && ObjectUtils.isNotEmpty(request.getApplyPaymentSerialNo())) {
            ApplyFinancialPaymentTypeEnum paymentTypeEnum = ApplyFinancialPaymentTypeEnum.fromCode(request.getApplyFinancialPaymentDetailResult().getTypePayment());
            FinancialSalesMethodEnum financialSalesMethodEnum = FinancialSalesMethodEnum.fromCode(request.getApplyFinancialPaymentDetailResult().getSalesMethod());

            if (paymentTypeEnum != ApplyFinancialPaymentTypeEnum.PERSONAL_RECYCLING || financialSalesMethodEnum != FinancialSalesMethodEnum.PURCHASE_DISPLACE) {
                throw new OperationRejectedException(OperationExceptionCode.PAYMENT_MATERIAL_NON_NULL);
            }
            super.bizCheckApplyPayment(request);
        }
    }
}
