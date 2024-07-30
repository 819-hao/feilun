package com.seeease.flywheel.serve.sale.convert;

import com.seeease.flywheel.sale.request.SaleDeliveryVideoRequest;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.sale.entity.RcSaleDeliveryVideo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/9/15
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface RcSaleDeliveryVideoConverter extends EnumConvert {
    RcSaleDeliveryVideoConverter INSTANCE = Mappers.getMapper(RcSaleDeliveryVideoConverter.class);

    @Mappings(value = {
            @Mapping(source = "data", target = "rcData"),
    })
    RcSaleDeliveryVideo convert(SaleDeliveryVideoRequest request);
}
