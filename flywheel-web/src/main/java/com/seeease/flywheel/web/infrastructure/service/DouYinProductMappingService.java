package com.seeease.flywheel.web.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.web.entity.DouYinProductMapping;
import com.seeease.flywheel.web.entity.DouYinProductMappingVO;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【douyin_product_mapping(抖音商品关系)】的数据库操作Service
 * @createDate 2023-07-20 14:50:56
 */
public interface DouYinProductMappingService extends IService<DouYinProductMapping> {

    /**
     * @param shopId
     * @return
     */
    List<DouYinProductMappingVO> selectProductStock(Integer shopId);

    /**
     * @param mappingList
     */
    void saveOrUpdateMapping(List<DouYinProductMapping> mappingList);
}
