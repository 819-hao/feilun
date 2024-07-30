package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeease.flywheel.serve.goods.entity.PhotoGallery;
import org.apache.ibatis.annotations.Param;

/**
 * @author Tiro
 * @description 针对表【photo_gallery】的数据库操作Mapper
 * @createDate 2023-04-13 14:22:27
 * @Entity com.seeease.flywheel.serve.goods.entity.PhotoGallery
 */
public interface PhotoGalleryMapper extends BaseMapper<PhotoGallery> {

    /**
     * @param goodsId
     * @param stockId
     * @return
     */
    String queryPhotoGalleryImgUrl(@Param("goodsId") Long goodsId, @Param("stockId") Long stockId);

    String queryPhotoGalleryImgUrlByGoodsId(@Param("goodsId") Integer goodsId, @Param("type") int i);

    String queryPhotoGalleryImgUrlByStockId(@Param("stockId") Integer stockId, @Param("type") int i);
}




