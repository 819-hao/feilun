package com.seeease.flywheel.serve.stocktaking.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.stocktaking.entity.BillStocktakingLine;
import com.seeease.flywheel.serve.stocktaking.mapper.BillStocktakingLineMapper;
import com.seeease.flywheel.serve.stocktaking.service.BillStocktakingLineService;
import com.seeease.flywheel.stocktaking.request.StocktakingDetailsRequest;
import com.seeease.flywheel.stocktaking.result.StocktakingDetailsResult;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_stocktaking_line(盘点详情)】的数据库操作Service实现
 * @createDate 2023-06-17 10:26:51
 */
@Service
public class BillStocktakingLineServiceImpl extends ServiceImpl<BillStocktakingLineMapper, BillStocktakingLine>
        implements BillStocktakingLineService {



    @Override
    public Page<StocktakingDetailsResult> pageOf(@NonNull StocktakingDetailsRequest request) {
        return this.getBaseMapper().pageOf(Page.of(request.getPage(),request.getLimit()),request);
    }

    @Override
    public List<Integer> listStatus(@NonNull StocktakingDetailsRequest request) {
        return this.getBaseMapper().listStatus(request);
    }
}




