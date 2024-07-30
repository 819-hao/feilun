package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.maindata.entity.StoreRelationshipSubject;
import com.seeease.flywheel.serve.maindata.mapper.StoreRelationshipSubjectMapper;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import org.springframework.stereotype.Service;

/**
 * @author edy
 * @description 针对表【store_relationship_subject】的数据库操作Service实现
 * @createDate 2023-03-07 16:49:00
 */
@Service
public class StoreRelationshipSubjectServiceImpl extends ServiceImpl<StoreRelationshipSubjectMapper, StoreRelationshipSubject>
        implements StoreRelationshipSubjectService {

    @Override
    public StoreRelationshipSubject getByShopId(Integer storeManagementId) {
        return baseMapper.selectOne(Wrappers.<StoreRelationshipSubject>lambdaQuery()
                .eq(StoreRelationshipSubject::getStoreManagementId, storeManagementId)
        );
    }

    @Override
    public StoreRelationshipSubject getBySubjectId(Integer subjectId) {
        return baseMapper.selectOne(Wrappers.<StoreRelationshipSubject>lambdaQuery()
                .eq(StoreRelationshipSubject::getSubjectId, subjectId)
        );
    }
}




