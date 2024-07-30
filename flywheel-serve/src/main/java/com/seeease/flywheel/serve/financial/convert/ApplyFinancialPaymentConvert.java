package com.seeease.flywheel.serve.financial.convert;

import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentDetailResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentPageAllResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentPageResult;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentRecordPageResult;
import com.seeease.flywheel.purchase.result.PurchaseApplySettlementResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPaymentRecord;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface ApplyFinancialPaymentConvert extends EnumConvert {

    ApplyFinancialPaymentConvert INSTANCE = Mappers.getMapper(ApplyFinancialPaymentConvert.class);

    @Mapping(target = "repurchaseCommitment", source = "buyBackTransfer")
    ApplyFinancialPayment convert(ApplyFinancialPaymentCreateRequest request);

    @Mapping(target = "repurchaseCommitment", source = "buyBackTransfer")
    ApplyFinancialPayment convert(ApplyFinancialPaymentUpdateRequest request);

    ApplyFinancialPayment convert(ApplyFinancialPaymentOperateRequest request);
    @Mapping(target = "buyBackTransfer", source = "repurchaseCommitment")
    ApplyFinancialPaymentDetailResult convert(ApplyFinancialPayment selectById);

    @Mappings(value = {
            @Mapping(source = "subjectPayment", target = "purchaseSubjectId")
    })
    ApplyFinancialPaymentPageAllResult convertApplyFinancialPaymentPageAllResult(ApplyFinancialPayment request);

    ApplyFinancialPaymentCreateRequest convertApplyFinancialPaymentCreateRequest(PurchaseApplySettlementResult result);

    ApplyFinancialPaymentCreateRequest convertApplyFinancialPaymentCreateRequest(AccountsPayableAccountingCreateAfpRequest request);

    ApplyFinancialPaymentCreateRequest convertApplyFinancialPaymentCreateRequest(ApplyFinancialPaymentAppletCreateRequest request);
//    @Mapping(target = "buyBackTransfer", source = "repurchaseCommitment")
    ApplyFinancialPaymentPageResult convertApplyFinancialPaymentPageResult(ApplyFinancialPayment request);
    ApplyFinancialPaymentRecordPageResult convertApplyFinancialPaymentRecordPageResult(ApplyFinancialPaymentRecord request);

    // str转list
    default List<String> str2List(String src) {
        if (StringUtils.isBlank(src))
            return null;
        String[] split = src.split(",");
        return Arrays.asList(split);
    }

    ApplyFinancialPayment convert(ApplyFinancialPaymentOrderCancelTaskRequest request);
}
