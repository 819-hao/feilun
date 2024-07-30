package com.seeease.flywheel.serve.goods.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IGoodsExtFacade;
import com.seeease.flywheel.goods.ISeriesFacade;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.goods.request.*;
import com.seeease.flywheel.goods.result.SeriesPageResult;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.goods.convert.SeriesConverter;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.Series;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.SeriesService;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@DubboService(version = "1.0.0")
public class SeriesFacade implements ISeriesFacade {
    @Resource
    private SeriesService service;
    @Resource
    private GoodsWatchService watchService;

    @Override
    public PageResult<SeriesPageResult> queryPage(SeriesPageRequest request) {
        Page<SeriesPageResult> page = service.queryPage(request);
        return PageResult.<SeriesPageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(SeriesDeleteRequest request) {
        long count = watchService.count(new LambdaQueryWrapper<GoodsWatch>()
                .in(GoodsWatch::getSeriesId, request.getIdList()));
        if (count != 0) {
            throw new BusinessException(ExceptionCode.SERIES_BATCH_DELETE_NOT_SUPPORT);
        }
        service.removeBatchByIds(request.getIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SeriesUpdateRequest request) {
        Series series = SeriesConverter.INSTANCE.convertUpdateRequest(request);
        service.updateById(series);

        if (Objects.isNull(request.getBrandId()))
            watchService.updateBrandBySeries(request.getId(), request.getBrandId());
    }

    @Override
    public void create(SeriesCreateRequest request) {
        Series series = SeriesConverter.INSTANCE.convertCreateRequest(request);
        service.save(series);
    }
}
