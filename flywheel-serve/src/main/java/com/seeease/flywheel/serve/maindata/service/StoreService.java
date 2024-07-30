package com.seeease.flywheel.serve.maindata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.maindata.entity.Store;

/**
 * @author Tiro
 * @description 针对表【store(仓库表)】的数据库操作Service
 * @createDate 2023-03-07 19:29:21
 */
public interface StoreService extends IService<Store> {
    /**
     * 根据门店id查仓库
     *
     * @param shopId
     * @return
     */
    Store selectByShopId(Integer shopId);


}
