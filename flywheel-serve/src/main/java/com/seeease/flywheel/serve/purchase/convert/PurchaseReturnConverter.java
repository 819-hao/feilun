package com.seeease.flywheel.serve.purchase.convert;

import com.seeease.flywheel.purchase.request.PurchaseReturnCreateRequest;
import com.seeease.flywheel.purchase.result.PurchaseReturnCreateResult;
import com.seeease.flywheel.purchase.result.PurchaseReturnDetailsResult;
import com.seeease.flywheel.purchase.result.PurchaseReturnListResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface PurchaseReturnConverter extends EnumConvert {
    PurchaseReturnConverter INSTANCE = Mappers.getMapper(PurchaseReturnConverter.class);

    @Mappings(value = {
//            @Mapping(source = "purchaseSerialNo", target = "originSerialNo")
    })
    BillPurchaseReturn convert(PurchaseReturnCreateRequest request);

    BillPurchaseReturnLine convert(PurchaseReturnCreateRequest.BillPurchaseReturnLineDto dto);

    @Mappings({
            @Mapping(target = "purchaseReturnPrice", source = "purchasePrice")
    })
    BillPurchaseReturnLine convert(BillPurchaseLine dto);

    PurchaseReturnCreateResult convertPurchaseReturnCreateResult(BillPurchaseReturn billPurchaseReturn);
    
    PurchaseReturnListResult convertPurchaseReturnListResult(BillPurchaseReturn billPurchaseReturn);

    @Mappings({
            @Mapping(target = "id", source = "id")
    })
    PurchaseReturnDetailsResult convertPurchaseReturnDetailsResult(BillPurchaseReturn billPurchaseReturn);

    PurchaseReturnDetailsResult.PurchaseReturnLineVO convertPurchaseReturnLineVO(BillPurchaseReturnLine billPurchaseReturnLine);
}
