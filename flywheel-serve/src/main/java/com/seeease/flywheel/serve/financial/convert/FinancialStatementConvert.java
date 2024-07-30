package com.seeease.flywheel.serve.financial.convert;


import com.seeease.flywheel.financial.request.FinancialStatementCreateRequest;
import com.seeease.flywheel.financial.result.FinancialStatementImportResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.financial.entity.FinancialStatement;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface FinancialStatementConvert extends EnumConvert {

    FinancialStatementConvert INSTANCE = Mappers.getMapper(FinancialStatementConvert.class);

    FinancialStatementImportResult convertImportResult(FinancialStatement financialStatement);

    @Mapping(target = "collectionTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    FinancialStatement convertCreateRequest(FinancialStatementCreateRequest request);
}
