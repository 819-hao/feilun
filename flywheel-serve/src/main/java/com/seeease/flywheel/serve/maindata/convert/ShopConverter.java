package com.seeease.flywheel.serve.maindata.convert;

import com.seeease.flywheel.maindata.entity.Shop;
import com.seeease.flywheel.maindata.entity.ShopMember;
import com.seeease.flywheel.maindata.result.ShopStaffListResult;
import com.seeease.flywheel.maindata.result.ShopStoreQueryResult;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.maindata.entity.ShopDto;
import com.seeease.flywheel.serve.maindata.entity.ShopMemberDto;
import com.seeease.flywheel.serve.maindata.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/4/1
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface ShopConverter extends EnumConvert {

    ShopConverter INSTANCE = Mappers.getMapper(ShopConverter.class);

    @Mappings(value = {
            @Mapping(target = "shopName", source = "name"),
            @Mapping(target = "mallStoreName", source = "showName"),
            @Mapping(target = "shopId", source = "id"),
    })
    Shop convert(ShopDto dto);

    @Mappings(value = {
            @Mapping(target = "roleKey", source = "roleKeys"),
            @Mapping(target = "roleName", source = "roleNames"),
    })
    ShopMember convert(ShopMemberDto dto);

    @Mappings(value = {
            @Mapping(target = "staffId", source = "id"),
            @Mapping(target = "staffName", source = "name"),
    })
    ShopStaffListResult convert(User user);

    ShopStoreQueryResult convertStore(ShopDto shopDto);
}
