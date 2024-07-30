package com.seeease.flywheel.serve.maindata.convert;

import com.seeease.flywheel.maindata.entity.UserInfo;
import com.seeease.flywheel.serve.base.EnumConvert;
import com.seeease.flywheel.serve.maindata.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author Tiro
 * @date 2023/4/1
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface UserConverter extends EnumConvert {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);


    UserInfo convertUserInfo(User user);

}
