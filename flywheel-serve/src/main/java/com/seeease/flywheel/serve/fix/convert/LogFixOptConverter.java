package com.seeease.flywheel.serve.fix.convert;

import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.entity.LogFixOpt;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/6 14:44
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface LogFixOptConverter extends EnumConvert {

    LogFixOptConverter INSTANCE = Mappers.getMapper(LogFixOptConverter.class);

    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdId", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedId", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
//            @Mapping(target = "imgList", ignore = true),
//            @Mapping(target = "newImgList", ignore = true),
//            @Mapping(target = "resultContent", ignore = true),
//            @Mapping(target = "resultImgList", ignore = true)
    })
    LogFixOpt convert(BillFix dto);


}
