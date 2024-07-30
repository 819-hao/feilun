package com.seeease.flywheel.web.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeease.flywheel.web.entity.DouYinShopMapping;
import com.seeease.flywheel.web.entity.DouYinShopMappingVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author Tiro
 * @description 针对表【douyin_shop_mapping(抖音门店映射表)】的数据库操作Mapper
 * @createDate 2023-05-05 18:25:55
 * @Entity com.seeease.flywheel.web.entity.DouYinShopMapping
 */
public interface DouYinShopMappingMapper extends BaseMapper<DouYinShopMapping> {

    /**
     * @param orderId
     * @return
     */
    DouYinShopMappingVO getByOrder(@Param("orderId") String orderId);

    /**
     * 当前已经解密的数量
     *
     * @param douYinShopId
     * @return
     */
    int countDecryptNumberByToDays(@Param("douYinShopId") Long douYinShopId);
}




