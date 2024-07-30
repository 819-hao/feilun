package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.maindata.entity.UserRole;
import com.seeease.flywheel.serve.maindata.mapper.UserRoleMapper;
import com.seeease.flywheel.serve.maindata.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
* @author dmmasxnmf
* @description 针对表【user_role(用户和角色关联表)】的数据库操作Service实现
* @createDate 2023-05-08 11:44:01
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService {

}




