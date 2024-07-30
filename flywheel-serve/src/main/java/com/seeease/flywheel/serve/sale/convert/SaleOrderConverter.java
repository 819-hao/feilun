package com.seeease.flywheel.serve.sale.convert;

import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.sale.entity.SaleOrder;
import com.seeease.flywheel.sale.entity.SaleOrderLine;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.request.SaleOrderEditRequest;
import com.seeease.flywheel.sale.request.SaleOrderUpdateRequest;
import com.seeease.flywheel.sale.result.*;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.sale.entity.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 *
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface SaleOrderConverter extends EnumConvert {

    SaleOrderConverter INSTANCE = Mappers.getMapper(SaleOrderConverter.class);

    @Mappings(value = {
            @Mapping(source = "inspectionType", target = "inspectionType", defaultValue = "NO_INSPECTION"),
    })
    BillSaleOrder convertBillSaleOrder(SaleOrderCreateRequest request);

    @Mappings(value = {
            @Mapping(source = "isCounterPurchase", target = "isCounterPurchase", defaultValue = "0"),
            @Mapping(source = "isRepurchasePolicy", target = "isRepurchasePolicy", defaultValue = "0"),
            @Mapping(source = "strapReplacementPrice", target = "strapReplacementPrice", defaultValue = "0"),
    })
    BillSaleOrderLine convertBillSaleOrderLine(SaleOrderCreateRequest.BillSaleOrderLineDto billSaleOrderLineDto);

    BillSaleOrder convertBillSaleOrder(SaleOrderEditRequest request);

    BillSaleOrderLine convertBillSaleOrderLine(SaleOrderEditRequest request);

    SaleOrderListResult convertSaleOrderListResult(BillSaleOrder saleOrder);

    SaleOrderDetailsResult convertSaleOrderDetailsResult(BillSaleOrder saleOrder);

    List<SaleOrderDetailsResult.SaleOrderLineVO> convertSaleOrderLineVO(List<BillSaleOrderLineDetailsVO> lineDetailsVOList);

    List<BuyBackPolicyInfo> convertBuyBackPolicyList(List<BuyBackPolicyMapper> list);

    List<BuyBackPolicyMapper> convertBuyBackPolicyMapper(List<BuyBackPolicyInfo> list);


    SaleOrderSettlementListResult convertSaleOrderSettlementListResult(BillSaleOrderLineSettlementVO t);

    SaleOrderCreateResult.SaleOrderDto convertSaleOrderDto(BillSaleOrder order);

    SaleStockQueryImportResult convertSaleStockQueryImportResult(StockBaseInfo stockBaseInfo);

    @Mappings(value = {
            @Mapping(target = "orderId", source = "id"),
            @Mapping(target = "orderNo", source = "serialNo"),
            @Mapping(target = "thirdOrderNo", source = "bizOrderCode"),
            @Mapping(target = "payModel", source = "paymentMethod.desc"),
    })
    SaleOrder convertSaleOrder(BillSaleOrder order);

    SaleOrderLine convertSaleOrderLine(BillSaleOrderLine line);

    SaleOrderCreateRequest.BillSaleOrderLineDto convertBillSaleOrderLineDto(BillSaleOrderLine t);

    SingleSaleOrderResult to(BillSaleOrder selectBySerialNo);


    List<BuyBackPolicyMapper> convert(List<SaleOrderUpdateRequest.BillSaleOrderLineDto.BuyBackPolicyMapper> buyBackPolicy);
}
