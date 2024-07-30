package com.seeease.flywheel.serve.sale.convert;

import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.sale.result.*;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLineSettlementVO;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLineDetailsVO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface SaleReturnOrderConverter extends EnumConvert {

    SaleReturnOrderConverter INSTANCE = Mappers.getMapper(SaleReturnOrderConverter.class);


    BillSaleReturnOrder convertBillSaleReturnOrder(SaleReturnOrderCreateRequest request);

    BillSaleReturnOrderLine convertBillSaleReturnOrderLine(SaleReturnOrderCreateRequest.BillSaleReturnOrderLineDto dto);

    SaleReturnOrderDetailsResult convertSaleReturnOrderDetailsResult(BillSaleReturnOrder returnOrder);

    SaleReturnOrderListResult convertSaleReturnOrderListResult(BillSaleReturnOrder t);

    List<SaleReturnOrderDetailsResult.SaleReturnOrderLineVO> convertSaleOrderLineVO(List<BillSaleReturnOrderLineDetailsVO> lineDetailsVOList);

    SaleReturnOrderExpressNumberUploadResult convertSaleReturnOrderExpressNumberUploadResult(BillSaleReturnOrder returnOrder);

    SaleReturnStockQueryImportResult convertSaleReturnStockQueryImportResult(BillSaleOrderLineSettlementVO t);

    SaleSellteStockQueryImportResult convertSaleSettleStockQueryImportResult(BillSaleOrderLineSettlementVO t);

    BillSaleReturnOrderResult to(BillSaleReturnOrder one);

}
