package com.seeease.flywheel.web.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.web.entity.KuaishouShopMapping;

/**
* @author dmmasxnmf
* @description 针对表【kuaishou_shop_mapping(快手门店映射表)】的数据库操作Service
* @createDate 2023-12-01 16:22:28
*/
public interface KuaishouShopMappingService extends IService<KuaishouShopMapping> {

    /**
     * 当前已经解密的数量
     *
     * @param douYinShopId
     * @return
     */
    int countDecryptNumberByToDays(Long douYinShopId);
}
