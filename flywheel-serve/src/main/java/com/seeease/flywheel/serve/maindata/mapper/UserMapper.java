package com.seeease.flywheel.serve.maindata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeease.flywheel.serve.maindata.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【user(成员表)】的数据库操作Mapper
 * @createDate 2023-02-01 17:14:35
 * @Entity com.seeease.flywheel.User
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查关联门店标签用户
     *
     * @param shopId
     * @return
     */
    List<User> listByShop(@Param("shopId") Integer shopId);


    /**
     * 查关联门店标签用户
     *
     * @param shopId
     * @return
     */
    List<User> listByShopAndName(@Param("shopId") Integer shopId, @Param("name") String name);
}




