package com.seeease.flywheel.web.infrastructure.service.impl;

import com.seeease.flywheel.web.entity.User;
import com.seeease.flywheel.web.infrastructure.mapper.UserSyncMapper;
import com.seeease.flywheel.web.infrastructure.service.UserSyncService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/29
 */
@Service
public class UserSyncServiceImpl implements UserSyncService {
    @Resource
    private UserSyncMapper userSyncMapper;

    @Override
    public List<User> selectUser(String userId, Long storeId) {
        return userSyncMapper.selectUser(userId, storeId);
    }

}
