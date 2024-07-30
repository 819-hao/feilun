package com.seeease.flywheel.serve.sf.convert;

import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.sf.entity.ExpressOrder;
import com.seeease.flywheel.sf.request.ExpressOrderCreateRequest;
import com.seeease.flywheel.sf.request.ExpressOrderEditRequest;
import com.seeease.flywheel.sf.result.ExpressOrderQueryResult;
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
public interface ExpressOrderConverter extends EnumConvert {

    ExpressOrderConverter INSTANCE = Mappers.getMapper(ExpressOrderConverter.class);

    /**
     * 入参转换
     *
     * @param request
     * @return
     */
    ExpressOrder convertExpressOrderCreateRequest(ExpressOrderCreateRequest request);

    /**
     * 入参转换
     *
     * @param request
     * @return
     */
    ExpressOrder convertExpressOrderEditRequest(ExpressOrderEditRequest request);

    /**
     * 转换
     *
     * @param request
     * @return
     */
    ExpressOrderQueryResult.ExpressOrderQueryDTO convertExpressOrder(ExpressOrder request);
}
