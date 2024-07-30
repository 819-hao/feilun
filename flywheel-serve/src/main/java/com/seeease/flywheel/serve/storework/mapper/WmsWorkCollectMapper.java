package com.seeease.flywheel.serve.storework.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPreExt;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCapacityDTO;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCollect;
import com.seeease.flywheel.storework.request.WmsWorkListRequest;
import com.seeease.flywheel.storework.result.WmsWorkCollectCountResult;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【wms_work_collect(发货作业集单表)】的数据库操作Mapper
 * @createDate 2023-08-31 17:58:28
 * @Entity com.seeease.flywheel.serve.storework.entity.WmsWorkCollect
 */
public interface WmsWorkCollectMapper extends SeeeaseMapper<WmsWorkCollect> {


    /**
     * 待集单拣货列表
     *
     * @param page
     * @param request
     * @return
     */
    Page<BillStoreWorkPreExt> waitWorkList(Page page, @Param("request") WmsWorkListRequest request);

    /**
     * 集单作业列表
     *
     * @param request
     * @return
     */
    List<BillStoreWorkPreExt> listWorkCollect(@Param("request") WmsWorkListRequest request);

    /**
     * 集单作业分页列表
     *
     * @param page
     * @param request
     * @return
     */
    Page<BillStoreWorkPreExt> pageWorkCollect(Page page, @Param("request") WmsWorkListRequest request);

    /**
     * 聚合统计数量
     *
     * @param request
     * @return
     */
    List<WmsWorkCollectCountResult> countByGroupModelAndSn(@Param("request") WmsWorkListRequest request);

    /**
     * 工作中库存统计
     *
     * @param belongingStoreId
     * @return
     */
    List<WmsWorkCapacityDTO> inWorkStockCount(@Param("belongingStoreId") Integer belongingStoreId);
}




