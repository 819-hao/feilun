package com.seeease.flywheel.web.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.web.entity.DouYinShopMapping;
import com.seeease.flywheel.web.entity.DouYinShopMappingVO;

/**
 * @author Tiro
 * @description 针对表【douyin_shop_mapping(抖音门店映射表)】的数据库操作Service
 * @createDate 2023-05-05 18:25:55
 */
public interface DouYinShopMappingService extends IService<DouYinShopMapping> {

    /**
     * @param douYinShopId 抖音门店id
     * @param authorId     达人id
     * @return
     */
    DouYinShopMapping getByDouYinShopId(Long douYinShopId, Long authorId);

    DouYinShopMappingVO getByDouYinOrderId(String orderId);

    /**
     * 当前已经解密的数量
     *
     * @param douYinShopId
     * @return
     */
    int countDecryptNumberByToDays(Long douYinShopId);
}
