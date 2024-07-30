package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoDto;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoSync;
import com.seeease.flywheel.serve.goods.entity.GoodsStockCPrice;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @description 针对表【goods_meta_info(商品元数据)】的数据库操作Mapper
 * @createDate 2023-04-13 14:04:43
 * @Entity com.seeease.flywheel.serve.goods.entity.GoodsMetaInfo
 */
public interface GoodsMetaInfoSyncMapper extends BaseMapper<GoodsMetaInfoSync> {

    Integer maxGoodsMetaInfo(@Param("currentOffset") Integer currentOffset);

    List<GoodsMetaInfoDto> selectGoodsMetaInfo(@Param("currentOffset") Integer currentOffset, @Param("limit") Integer limit);

    List<GoodsMetaInfoDto> selectGoodsMetaInfoByStockIdList(@Param("stockIdList") List<Integer> stockIdList);

    /**
     * 更新拉取状态等信息
     *
     * @param goodsMetaInfoSync
     * @return
     */
    int updateLatestPullData(GoodsMetaInfoSync goodsMetaInfoSync);

    /**
     * @return
     */
    List<GoodsMetaInfoSync> findPropertyChangeGoods();

    /**
     * @param stockId
     * @return
     */
    int updatePropertyChange(@Param("stockId") Integer stockId, @Param("noticeTime") Date noticeTime);

    /**
     * 查询吊牌价
     *
     * @param goodsId
     * @return
     */
    List<GoodsStockCPrice> checkCPrice(@Param("goodsId") Integer goodsId, @Param("extra") Boolean extra);

    /**
     * 查询新表
     *
     * @param stockId
     * @return
     */
    GoodsStockCPrice checkNewStock(@Param("stockId") Integer stockId);
}




