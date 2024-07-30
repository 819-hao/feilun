package com.seeease.flywheel.serve.storework.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPreExt;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCapacityDTO;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCollect;
import com.seeease.flywheel.serve.storework.enums.WmsWorkCollectWorkStateEnum;
import com.seeease.flywheel.storework.request.WmsWorkListRequest;
import com.seeease.flywheel.storework.result.WmsWorkCollectCountResult;

import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @description 针对表【wms_work_collect(发货作业集单表)】的数据库操作Service
 * @createDate 2023-08-31 17:58:28
 */
public interface WmsWorkCollectService extends IService<WmsWorkCollect> {

    /**
     * 待集单拣货列表
     *
     * @param request
     * @return
     */
    Page<BillStoreWorkPreExt> waitWorkList(WmsWorkListRequest request);

    /**
     * 集单作业列表
     *
     * @param request
     * @return
     */
    Page<BillStoreWorkPreExt> listWorkCollect(WmsWorkListRequest request);

    /**
     * 集单作业分页列表
     *
     * @param request
     * @return
     */
    Page<BillStoreWorkPreExt> pageWorkCollect(WmsWorkListRequest request);

    /**
     * 集单
     *
     * @param shopId
     * @param billStoreWorkPreList
     */
    void collectWork(Integer shopId, List<BillStoreWorkPre> billStoreWorkPreList);

    /**
     * 聚合统计数量
     *
     * @param request
     * @return
     */
    List<WmsWorkCollectCountResult> countByGroupModelAndSn(WmsWorkListRequest request);

    /**
     * 更新集单工作状态
     *
     * @param collectList
     * @param transitionEnum
     */
    void updateCollectWorkState(List<WmsWorkCollect> collectList, WmsWorkCollectWorkStateEnum.TransitionEnum transitionEnum);

    /**
     * 工作中库存统计
     *
     * @param goodsIdList
     * @param belongingStoreId
     * @return
     */
    Map<Integer, WmsWorkCapacityDTO> inWorkStockCount(List<Integer> goodsIdList, Integer belongingStoreId);
}
