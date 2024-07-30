package com.seeease.flywheel.serve.financial.convert;


import com.seeease.flywheel.financial.result.FinancialDetailsResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.goods.entity.StockPo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface FinancialDocumentsConvert extends EnumConvert {

    FinancialDocumentsConvert INSTANCE = Mappers.getMapper(FinancialDocumentsConvert.class);

    FinancialDocuments convert(FinancialSalesDto dto);

    FinancialDocuments convert(FinancialSalesReturnDto dto);

    @Mappings(value = {
            @Mapping(source = "consignmentPrice", target = "consignSalePrice"),
            @Mapping(source = "sn", target = "stockSn"),
            @Mapping(source = "id", target = "stockId"),
            @Mapping(source = "rightOfManagement", target = "outletStore"),
            @Mapping(target = "id", ignore = true),
    })
    FinancialDocumentsDetail convert(StockPo stock);

    FinancialDocuments convert(FinancialPurchaseDto dto);

    FinancialDocuments convert(FinancialPurchaseReturnDto dto);

    FinancialDetailsResult convert(FinancialDocumentsDetail t);
}
