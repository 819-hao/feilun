package com.seeease.flywheel.serve.purchase.convert;

import com.seeease.flywheel.purchase.request.PurchasePlanCreateRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanUpdateRequest;
import com.seeease.flywheel.purchase.result.PurchasePlanDetailsResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.purchase.entity.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * @author Tiro
 * @date 2023/1/7
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface PurchasePlanConverter extends EnumConvert {

    PurchasePlanConverter INSTANCE = Mappers.getMapper(PurchasePlanConverter.class);

    BillPurchasePlanLine convertPlanLine(PurchasePlanCreateRequest.BillPurchasePlanLineDto p);

    PurchasePlanDetailsResult convertPurchaseDetailsResult(BillPurchasePlan purchasePlan);

    List<PurchasePlanDetailsResult.PurchasePlanLineVO> convertPurchasePlanLineVO(List<BillPurchasePlanLine> lineDetailsVOList);

    BillPurchasePlan convertPurchasePlanUpdate(PurchasePlanUpdateRequest request);

    BillPurchasePlanLine convertPurchaseDetailPlanUpdate(PurchasePlanUpdateRequest.BillPurchasePlanLineDto billPurchasePlanLineDto) ;

    @Mappings(value = {
            @Mapping(target = "businessType", source = "businessType", defaultValue = "0"),
    })
    BillPurchasePlan convertPlan(PurchasePlanCreateRequest request);
}
