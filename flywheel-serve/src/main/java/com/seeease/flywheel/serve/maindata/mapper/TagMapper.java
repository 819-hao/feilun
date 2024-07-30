package com.seeease.flywheel.serve.maindata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.entity.TagPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【tag(标签)】的数据库操作Mapper
 * @createDate 2023-01-31 16:30:28
 * @Entity com.seeease.flywheel.Tag
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据门店id(storeManagementId) 查标签
     *
     * @param storeManagementId
     * @return
     */
    Tag selectByStoreManagementId(Integer storeManagementId);

    List<TagPo> selectListByStoreManagement();

    Tag selectByShopId(Integer shopId);

    /**
     * 查询门店下某个角色的id
     * @param storeManagementId
     * @param roleName
     * @return
     */
    List<Integer> selectUserIds(@Param("storeManagementId") Integer storeManagementId, @Param("roleName") String roleName);
}




