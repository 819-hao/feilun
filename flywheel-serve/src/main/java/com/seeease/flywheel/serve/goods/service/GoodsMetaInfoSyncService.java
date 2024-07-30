package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoDto;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoSync;

import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @description 针对表【goods_meta_info(商品元数据)】的数据库操作Service
 * @createDate 2023-04-13 14:04:43
 */
public interface GoodsMetaInfoSyncService extends IService<GoodsMetaInfoSync> {

    /**
     * @param currentOffset
     * @return
     */
    Integer maxGoodsMetaInfo(Integer currentOffset);

    /**
     * @param currentOffset
     * @param limit
     * @return
     */
    List<GoodsMetaInfoDto> selectGoodsMetaInfo(Integer currentOffset, Integer limit);

    /**
     * @param stockIdList
     * @return
     */
    List<GoodsMetaInfoDto> selectGoodsMetaInfoByStockIdList(List<Integer> stockIdList);

    /**
     * 查属性变动数据
     *
     * @return
     */
    List<GoodsMetaInfoSync> findPropertyChangeGoods();

    /**
     * 更新拉取状态和时间
     *
     * @param goodsMetaInfoSync
     * @return
     */
    int updateLatestPullData(GoodsMetaInfoSync goodsMetaInfoSync);

    /**
     * 更新属性变动状态
     *
     * @param stockId
     * @param noticeTime
     * @return
     */
    int updatePropertyChange(Integer stockId, Date noticeTime);
}
