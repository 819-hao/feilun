package com.seeease.flywheel.web.infrastructure.service;

import com.seeease.flywheel.web.entity.User;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/29
 */
public interface UserSyncService {
    List<User> selectUser(String userId, Long storeId);
}
