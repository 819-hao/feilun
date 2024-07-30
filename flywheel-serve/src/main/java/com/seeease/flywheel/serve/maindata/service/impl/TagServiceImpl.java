package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.entity.TagPo;
import com.seeease.flywheel.serve.maindata.mapper.TagMapper;
import com.seeease.flywheel.serve.maindata.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【tag(标签)】的数据库操作Service实现
 * @createDate 2023-01-31 16:30:28
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {

    @Override
    public Tag selectByStoreManagementId(Integer storeManagementId) {
        return baseMapper.selectByStoreManagementId(storeManagementId);
    }

    @Override
    public Tag selectByShopId(Integer shopId) {
        return baseMapper.selectByShopId(shopId);
    }


    @Override
    public List<TagPo> selectListByStoreManagement() {
        return this.baseMapper.selectListByStoreManagement();
    }

    @Override
    public List<Integer> selectUserIds(Integer storeManagementId, String roleName) {
        return this.baseMapper.selectUserIds(storeManagementId, roleName);
    }
}




