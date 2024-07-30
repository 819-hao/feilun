package com.seeease.flywheel.maindata;

import com.seeease.flywheel.maindata.entity.UserInfo;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/4/13
 */
public interface IUserFacade {


    /**
     * 查用户，根据企微用户id
     *
     * @param qwUserIdList
     * @return
     */
    List<UserInfo> listUser(List<String> qwUserIdList);
}
