package com.seeease.flywheel.serve.goods.mapper;

import com.seeease.flywheel.serve.goods.entity.StockGuaranteeCardManage;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【stock_guarantee_card_manage(保卡管理)】的数据库操作Mapper
 * @createDate 2023-11-20 10:35:53
 * @Entity com.seeease.flywheel.serve.goods.entity.StockGuaranteeCardManage
 */
public interface StockGuaranteeCardManageMapper extends SeeeaseMapper<StockGuaranteeCardManage> {

    /**
     * 调拨出库
     *
     * @param stockIdList
     * @param allocateNo
     * @return
     */
    int allocateOutByStockId(@Param("stockIdList") List<Integer> stockIdList, @Param("allocateNo") String allocateNo);

    /**
     * 调拨入库
     *
     * @param stockIdList
     * @return
     */
    int allocateInByStockId(@Param("stockIdList") List<Integer> stockIdList);

    /**
     * 调拨取消
     *
     * @param allocateNo
     * @return
     */
    int allocateCancel(@Param("allocateNo") String allocateNo);
}




