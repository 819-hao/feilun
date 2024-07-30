package com.seeease.flywheel.serve.helper.convert;


import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.helper.request.BreakPriceAuditSubmitRequest;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.helper.entity.BreakPriceAudit;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
imports = {JSONObject.class,Integer.class})
public interface BreakPriceAuditConvert extends EnumConvert {

    BreakPriceAuditConvert INSTANCE = Mappers.getMapper(BreakPriceAuditConvert.class);


    BreakPriceAudit to (BreakPriceAuditSubmitRequest request);
}