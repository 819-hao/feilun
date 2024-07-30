package com.seeease.flywheel.serve.maindata.convert;

import com.seeease.flywheel.fix.request.FixSiteCreateRequest;
import com.seeease.flywheel.fix.request.FixSiteEditRequest;
import com.seeease.flywheel.fix.result.FixSiteCreateResult;
import com.seeease.flywheel.fix.result.FixSiteDetailsResult;
import com.seeease.flywheel.fix.result.FixSiteEditResult;
import com.seeease.flywheel.fix.result.FixSiteListResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.maindata.entity.FixSite;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/11/18 10:43
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface FixSiteConverter extends EnumConvert {

    FixSiteConverter INSTANCE = Mappers.getMapper(FixSiteConverter.class);

    FixSite convert(FixSiteCreateRequest request);

    FixSiteCreateResult convertFixSiteCreateResult(FixSite request);

    FixSiteDetailsResult convertFixSiteDetailsResult(FixSite request);

    FixSiteEditResult convertFixSiteEditResult(FixSite request);
    FixSiteListResult convertFixSiteListResult(FixSite request);

    FixSite convert(FixSiteEditRequest request);
}
