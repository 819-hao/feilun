package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.mapper.UserMapper;
import com.seeease.flywheel.serve.maindata.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【user(成员表)】的数据库操作Service实现
 * @createDate 2023-02-01 17:14:35
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public List<User> listByShopAndName(Integer shopId, String name) {
        return baseMapper.listByShopAndName(shopId, name);
    }
}




