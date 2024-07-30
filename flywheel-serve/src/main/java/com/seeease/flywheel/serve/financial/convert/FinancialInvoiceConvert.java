package com.seeease.flywheel.serve.financial.convert;


import com.seeease.flywheel.express.entity.FeilunInvoiceMaycurCreateMessage;
import com.seeease.flywheel.financial.request.FinancialInvoiceCreateRequest;
import com.seeease.flywheel.financial.request.FinancialInvoiceUpdateRequest;
import com.seeease.flywheel.financial.result.FinancialInvoiceDetailResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface FinancialInvoiceConvert extends EnumConvert {

    FinancialInvoiceConvert INSTANCE = Mappers.getMapper(FinancialInvoiceConvert.class);

    @Mapping(target = "customerContactsId",source = "contactId")
    FinancialInvoice convertCreate(FinancialInvoiceCreateRequest request);

    FinancialInvoice convertUpdate(FinancialInvoiceUpdateRequest request);

    FinancialInvoiceDetailResult convertDetail(FinancialInvoice invoice);

    @Mapping(target = "invoiceAmount", source = "clinchPrice")
    FeilunInvoiceMaycurCreateMessage.LineDto convertLineDto(FinancialInvoiceCreateRequest.LineDto a);
}
