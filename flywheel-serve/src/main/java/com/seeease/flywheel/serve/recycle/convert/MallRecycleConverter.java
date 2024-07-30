package com.seeease.flywheel.serve.recycle.convert;

import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.purchase.result.PurchaseDetailsResult;
import com.seeease.flywheel.recycle.request.ReplacementLineRequest;
import com.seeease.flywheel.recycle.request.ReplacementOrRecycleCreateRequest;
import com.seeease.flywheel.recycle.result.BuyBackForLineResult;
import com.seeease.flywheel.recycle.result.MallRecycleOrderDetailResult;
import com.seeease.flywheel.recycle.result.RecycleOrderDetailsResult;
import com.seeease.flywheel.recycle.result.SaleOrderDetailLineResult;
import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleOrderDetailsResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * 进行转换
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface MallRecycleConverter extends EnumConvert {

    MallRecycleConverter INSTANCE = Mappers.getMapper(MallRecycleConverter.class);


    PurchaseCreateRequest convert(ReplacementOrRecycleCreateRequest reqest);

    @Mappings(value = {
//            @Mapping(target = "createdTime", source = "createdTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
    })
    BuyBackForLineResult convertLine(PurchaseDetailsResult.PurchaseLineVO purchaseLineVO);

    SaleOrderCreateRequest.BillSaleOrderLineDto convertSaleOrderLine(ReplacementLineRequest replacementLineDtoList);

    /**
     * 查询列表销售行信息
     *
     * @param saleOrderLineVO
     * @return
     */
    SaleOrderDetailLineResult convertSaleLineResult(SaleOrderDetailsResult.SaleOrderLineVO saleOrderLineVO);

    /**
     * 转换详情返回
     *
     * @param request
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "serialNo", source = "serial"),
            @Mapping(target = "state", ignore = true),
            @Mapping(target = "identityCard", ignore = true),
            @Mapping(target = "mallState", source = "state"),
    })
    RecycleOrderDetailsResult convertSaleOrderDetailsResult(MallRecyclingOrder request);

    @Mappings(value = {
            @Mapping(target = "state", source = "mallState"),
    })
    MallRecycleOrderDetailResult mallDetails(RecycleOrderDetailsResult details);
}
