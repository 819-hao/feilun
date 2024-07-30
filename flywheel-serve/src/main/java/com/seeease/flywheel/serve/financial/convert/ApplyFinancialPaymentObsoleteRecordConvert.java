package com.seeease.flywheel.serve.financial.convert;

import com.seeease.flywheel.financial.result.ApplyFinancialPaymentObsoleteRecordPageResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPayment;
import com.seeease.flywheel.serve.financial.entity.ApplyFinancialPaymentObsoleteRecord;
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
public interface ApplyFinancialPaymentObsoleteRecordConvert extends EnumConvert {

    ApplyFinancialPaymentObsoleteRecordConvert INSTANCE = Mappers.getMapper(ApplyFinancialPaymentObsoleteRecordConvert.class);


    ApplyFinancialPaymentObsoleteRecord convertApplyFinancialPayment(ApplyFinancialPayment payment);

    ApplyFinancialPaymentObsoleteRecordPageResult convertApplyFinancialPaymentObsoleteRecord(ApplyFinancialPaymentObsoleteRecord applyFinancialPaymentObsoleteRecord);
}
