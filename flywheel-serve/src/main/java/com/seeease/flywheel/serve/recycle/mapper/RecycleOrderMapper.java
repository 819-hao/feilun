package com.seeease.flywheel.serve.recycle.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.recycle.request.RecycleOrderListRequest;
import com.seeease.flywheel.recycle.result.RecyclingListResult;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther Gilbert
 * @Date 2023/9/4 11:03
 */
public interface RecycleOrderMapper extends SeeeaseMapper<MallRecyclingOrder> {
    Page<RecyclingListResult> listByRequest(IPage<RecyclingListResult> page, @Param("request") RecycleOrderListRequest request);

    /**
     * 查询之回收
     *
     * @param page
     * @param request
     * @return
     */
    Page<RecyclingListResult> listByRequestByRecycle(IPage<RecyclingListResult> page, @Param("request") RecycleOrderListRequest request);
}
