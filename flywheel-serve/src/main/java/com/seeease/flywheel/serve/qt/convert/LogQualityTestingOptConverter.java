package com.seeease.flywheel.serve.qt.convert;

import com.seeease.flywheel.qt.result.QualityTestingDetailsResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.entity.LogQualityTestingOpt;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/6 11:13
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface LogQualityTestingOptConverter extends EnumConvert {

    LogQualityTestingOptConverter INSTANCE = Mappers.getMapper(LogQualityTestingOptConverter.class);

    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdId", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
    })
    LogQualityTestingOpt convert(BillQualityTesting dto);


    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdId", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
    })
    LogQualityTestingOpt convert(QualityTestingDetailsResult dto);
}
