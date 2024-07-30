package com.seeease.flywheel.serve.maindata.mapper;

import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;

/**
 * @author Tiro
 * @description 针对表【store(仓库表)】的数据库操作Mapper
 * @createDate 2023-03-07 19:29:21
 * @Entity com.seeease.flywheel.serve.maindata.entity.Store
 */
public interface StoreMapper extends SeeeaseMapper<Store> {

    /**
     * 根据门店id查仓库
     *
     * @param shopId
     * @return
     */
    Store selectByShopId(Integer shopId);

}




