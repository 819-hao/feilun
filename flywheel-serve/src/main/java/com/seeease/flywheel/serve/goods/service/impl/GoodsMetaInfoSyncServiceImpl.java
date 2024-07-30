package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoDto;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoSync;
import com.seeease.flywheel.serve.goods.mapper.GoodsMetaInfoSyncMapper;
import com.seeease.flywheel.serve.goods.service.GoodsMetaInfoSyncService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @description 针对表【goods_meta_info(商品元数据)】的数据库操作Service实现
 * @createDate 2023-04-13 14:04:43
 */
@Service
public class GoodsMetaInfoSyncServiceImpl extends ServiceImpl<GoodsMetaInfoSyncMapper, GoodsMetaInfoSync>
        implements GoodsMetaInfoSyncService {


    @Override
    public Integer maxGoodsMetaInfo(Integer currentOffset) {
        return baseMapper.maxGoodsMetaInfo(currentOffset);
    }

    @Override
    public List<GoodsMetaInfoDto> selectGoodsMetaInfo(Integer currentOffset, Integer limit) {
        return baseMapper.selectGoodsMetaInfo(currentOffset, limit);
    }

    @Override
    public List<GoodsMetaInfoDto> selectGoodsMetaInfoByStockIdList(List<Integer> stockIdList) {
        return baseMapper.selectGoodsMetaInfoByStockIdList(stockIdList);
    }

    @Override
    public List<GoodsMetaInfoSync> findPropertyChangeGoods() {
        return baseMapper.findPropertyChangeGoods();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLatestPullData(GoodsMetaInfoSync goodsMetaInfoSync) {
        return baseMapper.updateLatestPullData(goodsMetaInfoSync);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePropertyChange(Integer stockId, Date noticeTime) {
        return baseMapper.updatePropertyChange(stockId, noticeTime);
    }
}




