package com.seeease.flywheel.serve.purchase.strategy;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialSalesMethodEnum;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * 同行采购-备货
 *
 * @author Tiro
 * @date 2023/3/2
 */
@Component
public class PurchasePeerStockStrategy extends PurchaseStrategy {

    @NacosValue(value = "${toB.purchaseSubject}", autoRefreshed = true)
    private List<Integer> purchaseSubject;

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.TH_CG_BH;
    }

    @Override
    void preRequestProcessing(PurchaseCreateRequest request) {

    }

    @Override
    void checkRequest(PurchaseCreateRequest request) throws BusinessException {
        Assert.notNull(request.getCustomerId(), "供应商不能为空");
//        if (StringUtils.isBlank(request.getBank()) || StringUtils.isBlank(request.getAccountName()) || StringUtils.isBlank(request.getBankAccount()) || StringUtils.isBlank(request.getBankCustomerName())) {
//            throw new OperationRejectedException(OperationExceptionCode.STRAP_PAYMENT_NON_NULL);
//        }
        Assert.notNull(request.getPurchaseId(), "实际采购人不能为空");

        if (!purchaseSubject.contains(request.getPurchaseSubjectId())) {
            throw new OperationRejectedException(OperationExceptionCode.PURCHASE_SUBJECT_NON_NULL);
        }

        for (PurchaseCreateRequest.BillPurchaseLineDto detail : request.getDetails()) {
            if (SeriesTypeEnum.WRISTWATCH.getValue().equals(detail.getSeriesType()))
            if (StringUtils.isBlank(detail.getStrapMaterial()) || !Arrays.asList("金属", "皮", "针织", "绢丝", "其他").contains(detail.getStrapMaterial())) {
                throw new OperationRejectedException(OperationExceptionCode.STRAP_MATERIAL_NON_NULL);
            }
        }

//        if (ObjectUtils.isNotEmpty(request.getPrePayment()) && request.getPrePayment().intValue() == 1 && ObjectUtils.isEmpty(request.getApplyPaymentSerialNo())) {
//            throw new OperationRejectedException(OperationExceptionCode.PAYMENT_MATERIAL_NON_NULL);
//        }

        if (ObjectUtils.isNotEmpty(request.getApplyFinancialPaymentDetailResult())) {

            ApplyFinancialPaymentTypeEnum paymentTypeEnum = ApplyFinancialPaymentTypeEnum.fromCode(request.getApplyFinancialPaymentDetailResult().getTypePayment());
            FinancialSalesMethodEnum financialSalesMethodEnum = FinancialSalesMethodEnum.fromCode(request.getApplyFinancialPaymentDetailResult().getSalesMethod());

            if (paymentTypeEnum != ApplyFinancialPaymentTypeEnum.PEER_PROCUREMENT || financialSalesMethodEnum != FinancialSalesMethodEnum.PURCHASE_PREPARE) {
                throw new OperationRejectedException(OperationExceptionCode.PAYMENT_MATERIAL_NON_NULL);
            }
            //查询申请打款单
            super.bizCheckApplyPayment(request);
        }
//        Assert.notNull(request.getApplyPaymentSerialNo(), "申请打款单不能为空");

//        super.bizCheckApplyPayment(request);
    }
}
