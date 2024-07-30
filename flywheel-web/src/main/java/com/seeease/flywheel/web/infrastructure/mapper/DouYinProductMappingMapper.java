package com.seeease.flywheel.web.infrastructure.mapper;

import com.seeease.flywheel.web.entity.DouYinProductMapping;
import com.seeease.flywheel.web.entity.DouYinProductMappingVO;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【douyin_product_mapping(抖音商品关系)】的数据库操作Mapper
 * @createDate 2023-07-20 14:50:56
 * @Entity com.seeease.flywheel.web.entity.DouYinProductMapping
 */
public interface DouYinProductMappingMapper extends SeeeaseMapper<DouYinProductMapping> {

    /**
     * 抖音库存
     *
     * @param shopId
     * @return
     */
    List<DouYinProductMappingVO> selectProductStock(@Param("shopId") Integer shopId
            , @Param("rightOfManagementList") List<Integer> rightOfManagementList);

    /**
     * @param shopId
     * @param shuIdList
     * @return
     */
    int deleteBySku(@Param("shopId") Integer shopId, @Param("shuIdList") List<String> shuIdList);


    List<Integer> selectSubject(@Param("shopIdList") List<Integer> shopIdList);
}




