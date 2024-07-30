package com.seeease.flywheel.serve.sf.convert;

import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.sf.entity.ExpressOrderPrint;
import com.seeease.flywheel.sf.request.ExpressOrderPrintCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/29 10:52
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface ExpressOrderPrintConverter extends EnumConvert {

    ExpressOrderPrintConverter INSTANCE = Mappers.getMapper(ExpressOrderPrintConverter.class);

    /**
     * 入参转换
     *
     * @param request
     * @return
     */
    ExpressOrderPrint convertExpressOrderPrintCreateRequest(ExpressOrderPrintCreateRequest request);
}
