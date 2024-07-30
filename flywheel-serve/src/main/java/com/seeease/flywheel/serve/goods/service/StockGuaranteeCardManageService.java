package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.goods.entity.StockGuaranteeCardManage;

import java.util.List;

/**
 * 保卡管理，和商品保卡信息无关，给仓库调拨用
 *
 * @author Tiro
 * @description 针对表【stock_guarantee_card_manage(保卡管理)】的数据库操作Service
 * @createDate 2023-11-20 10:35:53
 */
public interface StockGuaranteeCardManageService extends IService<StockGuaranteeCardManage> {

    /**
     * 调拨出库
     *
     * @param stockIdList
     * @param allocateNo
     * @return
     */
    int allocateOutByStockId(List<Integer> stockIdList, String allocateNo);

    /**
     * 调拨入库
     *
     * @param stockIdList
     * @return
     */
    int allocateInByStockId(List<Integer> stockIdList);

    /**
     * 调拨取消
     *
     * @param allocateNo
     * @return
     */
    int allocateCancel(String allocateNo);

    /**
     * @param manageList
     * @return
     */
    int insertBatchSomeColumn(List<StockGuaranteeCardManage> manageList);
}
