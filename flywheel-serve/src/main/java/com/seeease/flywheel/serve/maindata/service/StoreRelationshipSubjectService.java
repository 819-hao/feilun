package com.seeease.flywheel.serve.maindata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.maindata.entity.StoreRelationshipSubject;

/**
 * @author edy
 * @description 针对表【store_relationship_subject】的数据库操作Service
 * @createDate 2023-03-07 16:49:00
 */
public interface StoreRelationshipSubjectService extends IService<StoreRelationshipSubject> {


    /**
     * 根据门店id查采购主体
     *
     * @param storeManagementId
     * @return
     */
    StoreRelationshipSubject getByShopId(Integer storeManagementId);

    /**
     * @param subjectId
     * @return
     */
    StoreRelationshipSubject getBySubjectId(Integer subjectId);
}
