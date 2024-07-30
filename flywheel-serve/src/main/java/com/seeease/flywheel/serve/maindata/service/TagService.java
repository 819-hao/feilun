package com.seeease.flywheel.serve.maindata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.entity.TagPo;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【tag(标签)】的数据库操作Service
 * @createDate 2023-01-31 16:30:28
 */
public interface TagService extends IService<Tag> {
    /**
     * 根据门店id(storeManagementId) 查标签
     *
     * @param storeManagementId
     * @return
     */
    Tag selectByStoreManagementId(Integer storeManagementId);
    Tag selectByShopId(Integer shopId);
    List<TagPo> selectListByStoreManagement();

    /**
     * 某个门店的用户id
     * @param storeManagementId
     * @param roleName
     * @return
     */
    List<Integer> selectUserIds(Integer storeManagementId, String roleName);
}
