package com.seeease.flywheel.serve.stocktaking.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.serve.stocktaking.entity.BillStocktakingLine;
import com.seeease.flywheel.stocktaking.request.StocktakingDetailsRequest;
import com.seeease.flywheel.stocktaking.result.StocktakingDetailsResult;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_stocktaking_line(盘点详情)】的数据库操作Mapper
 * @createDate 2023-06-17 10:26:51
 * @Entity com.seeease.flywheel.serve.stocktaking.entity.BillStocktakingLine
 */
public interface BillStocktakingLineMapper extends SeeeaseMapper<BillStocktakingLine> {

    /**
     * 分页查询
     * @param page
     * @param req
     * @return
     */
    Page<StocktakingDetailsResult> pageOf(IPage<StocktakingDetailsResult> page,
                                          @Param("req") StocktakingDetailsRequest req);


    /**
     * 根据参数获取对应数据的状态列表
     * @param req
     * @return
     */
    List<Integer> listStatus(@Param("req") StocktakingDetailsRequest req);

    /**
     * 更新stockId
     * @param stId
     */
    void updateStockId(@Param("stId") Integer stId);
}




