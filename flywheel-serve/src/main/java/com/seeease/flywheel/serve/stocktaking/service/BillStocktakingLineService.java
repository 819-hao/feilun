package com.seeease.flywheel.serve.stocktaking.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.stocktaking.entity.BillStocktakingLine;
import com.seeease.flywheel.stocktaking.request.StocktakingDetailsRequest;
import com.seeease.flywheel.stocktaking.result.StocktakingDetailsResult;
import lombok.NonNull;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_stocktaking_line(盘点详情)】的数据库操作Service
 * @createDate 2023-06-17 10:26:51
 */
public interface BillStocktakingLineService extends IService<BillStocktakingLine> {

    /**
     * 详情页分页
     * @param request
     * @return
     */
    Page<StocktakingDetailsResult> pageOf(@NonNull StocktakingDetailsRequest request);

    /**
     * 根据参数获取对应数据的状态列表
     * @param request
     * @return
     */
    List<Integer> listStatus(@NonNull StocktakingDetailsRequest request);
}
