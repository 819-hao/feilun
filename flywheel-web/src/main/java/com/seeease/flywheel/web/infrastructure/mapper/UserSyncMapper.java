package com.seeease.flywheel.web.infrastructure.mapper;

import com.seeease.flywheel.web.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/29
 */
public interface UserSyncMapper {


    /**
     * @param userId
     * @return
     */
    List<User> selectUser(@Param("userId")String userId,@Param("storeId") Long storeId);

    /**
     * @param roleKey
     * @param shopId
     * @return
     */
    List<User> selectUserByRoleKey(@Param("roleKeys") List<String> roleKeys
            , @Param("shopId") Integer shopId
            , @Param("userIds") List<Integer> userIds);
}
