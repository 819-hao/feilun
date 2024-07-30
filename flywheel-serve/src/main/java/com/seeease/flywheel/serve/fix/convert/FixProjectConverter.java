package com.seeease.flywheel.serve.fix.convert;

import com.seeease.flywheel.fix.result.FixProjectResult;
import com.seeease.flywheel.serve.fix.entity.FixProject;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/2 17:09
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface FixProjectConverter {

    FixProjectConverter INSTANCE = Mappers.getMapper(FixProjectConverter.class);

    FixProjectResult convertFixProjectResult(FixProject fixProject);
}
