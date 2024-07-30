package com.seeease.flywheel.serve.purchase.convert;

import com.seeease.flywheel.purchase.request.PurchaseTaskCreateRequest;
import com.seeease.flywheel.purchase.request.PurchaseTaskEditRequest;
import com.seeease.flywheel.purchase.result.PurchaseTaskCancelResult;
import com.seeease.flywheel.purchase.result.PurchaseTaskDetailsResult;
import com.seeease.flywheel.purchase.result.PurchaseTaskPageResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/10/25 15:43
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface PurchaseTaskConverter extends EnumConvert {

    PurchaseTaskConverter INSTANCE = Mappers.getMapper(PurchaseTaskConverter.class);

    /**
     * 采购需求转化
     *
     * @param request
     * @return
     */
    @Mappings(
            value = {
//                    @Mapping(source = "salePrice", target = "salePrice", defaultValue = "0"),
            }
    )
    BillPurchaseTask convert(PurchaseTaskCreateRequest request);

    BillPurchaseTask convert(PurchaseTaskEditRequest request);

    PurchaseTaskPageResult convert(BillPurchaseTask request);

    PurchaseTaskDetailsResult convertDetail(BillPurchaseTask request);
    PurchaseTaskCancelResult convertCancel(BillPurchaseTask request);
}
