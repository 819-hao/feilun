package com.seeease.flywheel.serve.purchase.convert;

import com.seeease.flywheel.purchase.request.PurchaseDemandConfirmRequest;
import com.seeease.flywheel.purchase.request.PurchaseDemandCreateRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanCreateRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanUpdateRequest;
import com.seeease.flywheel.purchase.result.PurchaseDemandPageResult;
import com.seeease.flywheel.purchase.result.PurchasePlanDetailsResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseDemand;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlan;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlanLine;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;



@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface PurchaseDemandConverter extends EnumConvert {

    PurchaseDemandConverter INSTANCE = Mappers.getMapper(PurchaseDemandConverter.class);


    BillPurchaseDemand to (PurchaseDemandCreateRequest request);

    BillPurchaseDemand to (PurchaseDemandConfirmRequest request);

}
