package com.seeease.flywheel.serve.account.convert;

import com.seeease.flywheel.account.result.CostJdFlMappingResult;
import com.seeease.flywheel.serve.account.entity.CostJdFlMapping;
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
public interface CostJdFlMappingConverter extends EnumConvert {

    CostJdFlMappingConverter INSTANCE = Mappers.getMapper(CostJdFlMappingConverter.class);

    /**
     * 转换
     *
     * @param request
     * @return
     */
    CostJdFlMappingResult convertCostJdFlMappingResult(CostJdFlMapping request);
}
