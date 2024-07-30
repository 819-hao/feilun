package com.seeease.flywheel.serve.account.convert;

import com.seeease.flywheel.account.result.ShopCompanyMappingResult;
import com.seeease.flywheel.serve.account.entity.ShopCompanyMapping;
import com.seeease.flywheel.serve.base.EnumConvert;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 16:33
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface ShopCompanyMappingConverter extends EnumConvert {

    ShopCompanyMappingConverter INSTANCE = Mappers.getMapper(ShopCompanyMappingConverter.class);

    /**
     * 转换
     *
     * @param request
     * @return
     */
    ShopCompanyMappingResult convertShopCompanyMappingResult(ShopCompanyMapping request);
}
