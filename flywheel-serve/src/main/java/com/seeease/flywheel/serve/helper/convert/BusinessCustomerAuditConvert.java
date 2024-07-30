package com.seeease.flywheel.serve.helper.convert;


import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.helper.request.BusinessCustomerAuditCreateRequest;
import com.seeease.flywheel.helper.result.BusinessCustomerPageResult;
import com.seeease.flywheel.serve.helper.entity.BusinessCustomerAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
imports = {JSONObject.class,Integer.class})
public interface BusinessCustomerAuditConvert {

    BusinessCustomerAuditConvert INSTANCE = Mappers.getMapper(BusinessCustomerAuditConvert.class);

    @Mapping(target = "areaIds",expression = "java(request.getAreaIds() != null ? JSONObject.toJSONString(request.getAreaIds()) : null)")
    BusinessCustomerAudit to(BusinessCustomerAuditCreateRequest request);
    @Mapping(target = "areaIds",expression = "java(entity.getAreaIds() != null ?  JSONObject.parseArray(entity.getAreaIds(),Integer.class) : null)")
    @Mapping(target = "status", expression = "java(entity.getStatus().getValue())")
    BusinessCustomerPageResult to(BusinessCustomerAudit entity);

    List<BusinessCustomerPageResult> toList(List<BusinessCustomerAudit> entityList);

}
