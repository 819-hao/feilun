package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.goods.entity.PhotoGallery;
import com.seeease.flywheel.serve.goods.mapper.PhotoGalleryMapper;
import com.seeease.flywheel.serve.goods.service.PhotoGalleryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Tiro
 * @description 针对表【photo_gallery】的数据库操作Service实现
 * @createDate 2023-04-13 14:22:27
 */
@Service
public class PhotoGalleryServiceImpl extends ServiceImpl<PhotoGalleryMapper, PhotoGallery>
        implements PhotoGalleryService {

    /**
     * @param goodsId
     * @param stockId
     * @return
     */
    @Override
    public String queryPhotoGalleryImgUrl(Long goodsId, Long stockId) {
        if (Objects.isNull(goodsId) && Objects.isNull(stockId)) {
            return StringUtils.EMPTY;
        }
        return baseMapper.queryPhotoGalleryImgUrl(goodsId, stockId);
    }

    @Override
    public String queryPhotoGalleryImgUrlByGoodsId(Integer goodsId, int i) {
        if (Objects.isNull(goodsId)) {
            return StringUtils.EMPTY;
        }
        return baseMapper.queryPhotoGalleryImgUrlByGoodsId(goodsId,i);
    }

    @Override
    public String queryPhotoGalleryImgUrlByStockId(Integer stockId, int i) {
        if (Objects.isNull(stockId)) {
            return StringUtils.EMPTY;
        }
        return baseMapper.queryPhotoGalleryImgUrlByStockId(stockId,i);
    }

}




