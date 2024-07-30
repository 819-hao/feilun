package com.seeease.flywheel.serve.maindata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.maindata.entity.User;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【user(成员表)】的数据库操作Service
 * @createDate 2023-02-01 17:14:35
 */
public interface UserService extends IService<User> {

    /**
     * 查关联门店标签用户
     *
     * @param shopId
     * @param name
     * @return
     */
    List<User> listByShopAndName(Integer shopId, String name);
}
