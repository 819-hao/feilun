package com.seeease.flywheel.serve.maindata.convert;


import com.seeease.flywheel.maindata.request.FirmShopSubmitRequest;
import com.seeease.flywheel.maindata.result.FirmShopQueryResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.maindata.entity.FirmShop;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/4/1
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface FirmShopConverter extends EnumConvert {

    FirmShopConverter INSTANCE = Mappers.getMapper(FirmShopConverter.class);


    FirmShop to(FirmShopSubmitRequest request);

    FirmShopQueryResult to(FirmShop firmShop);

}
