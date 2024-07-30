package com.seeease.flywheel.serve.menu.convert;

import com.seeease.flywheel.menu.request.MenuCreateRequest;
import com.seeease.flywheel.menu.request.MenuUpdateRequest;
import com.seeease.flywheel.menu.result.MenuListResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.menu.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @Auther Gilbert
 * @Date 2023/10/24 15:13
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface MenuConverter extends EnumConvert {
    MenuConverter INSTANCE =Mappers.getMapper(MenuConverter.class);



    MenuListResult convertQueryResult(Menu menu);


    Menu convertDO(MenuCreateRequest request);

    Menu convertUpdateDO(MenuUpdateRequest request);



}
