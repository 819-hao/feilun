package com.seeease.flywheel.serve.goods.service;

import com.seeease.flywheel.serve.goods.entity.PhotoGallery;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Tiro
* @description 针对表【photo_gallery】的数据库操作Service
* @createDate 2023-04-13 14:22:27
*/
public interface PhotoGalleryService extends IService<PhotoGallery> {

    String queryPhotoGalleryImgUrl(Long goodsId, Long stockId);

    String queryPhotoGalleryImgUrlByGoodsId(Integer goodsId, int i);

    String queryPhotoGalleryImgUrlByStockId(Integer stockId, int i);
}
