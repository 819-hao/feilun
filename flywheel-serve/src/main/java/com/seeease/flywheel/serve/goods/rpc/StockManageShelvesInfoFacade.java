package com.seeease.flywheel.serve.goods.rpc;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IStockManageShelvesInfoFacade;
import com.seeease.flywheel.goods.request.StockManageShelvesInfoImportRequest;
import com.seeease.flywheel.goods.request.StockManageShelvesInfoListRequest;
import com.seeease.flywheel.goods.result.StockManageShelvesInfoImportResult;
import com.seeease.flywheel.goods.result.StockManageShelvesInfoListResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.goods.convert.StockManageInfoConverter;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.StockManageShelvesInfo;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockManageShelvesInfoService;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/9/4
 */
@DubboService(version = "1.0.0")
public class StockManageShelvesInfoFacade implements IStockManageShelvesInfoFacade {

    @Resource
    private StockManageShelvesInfoService stockManageShelvesInfoService;
    @Resource
    private BrandService brandService;
    @Resource
    private GoodsWatchService goodsWatchService;

    @Override
    public PageResult<StockManageShelvesInfoListResult> list(StockManageShelvesInfoListRequest request) {

        LambdaUpdateWrapper<StockManageShelvesInfo> wrapper = Wrappers.<StockManageShelvesInfo>lambdaUpdate()
                .eq(Objects.nonNull(request.getBrandId()), StockManageShelvesInfo::getBrandId, request.getBrandId());

        if (StringUtils.isNotBlank(request.getModel())) {
            List<Integer> goodsIdList = goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                            .like(GoodsWatch::getModel, request.getModel()))
                    .stream()
                    .map(GoodsWatch::getId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(goodsIdList)) {
                return PageResult.<StockManageShelvesInfoListResult>builder()
                        .totalCount(NumberUtils.LONG_ZERO)
                        .totalPage(NumberUtils.LONG_ZERO)
                        .result(Collections.EMPTY_LIST)
                        .build();
            }
            wrapper.in(StockManageShelvesInfo::getGoodsId, goodsIdList);
        }

        Page<StockManageShelvesInfo> res = stockManageShelvesInfoService.page(Page.of(request.getPage(), request.getLimit()), wrapper);

        if (CollectionUtils.isEmpty(res.getRecords())) {
            return PageResult.<StockManageShelvesInfoListResult>builder()
                    .totalCount(res.getTotal())
                    .totalPage(res.getPages())
                    .result(Collections.EMPTY_LIST)
                    .build();
        }
        Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByGoodsIds(res.getRecords().stream().map(StockManageShelvesInfo::getGoodsId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));


        return PageResult.<StockManageShelvesInfoListResult>builder()
                .totalCount(res.getTotal())
                .totalPage(res.getPages())
                .result(res.getRecords().stream()
                        .map(t -> {

                            StockManageShelvesInfoListResult r = StockManageInfoConverter.INSTANCE.convertStockManageShelvesInfoListResult(t);
                            WatchDataFusion goods = goodsMap.get(t.getGoodsId());
                            if (Objects.nonNull(goods)) {
                                r.setImage(goods.getImage());
                                r.setBrandName(goods.getBrandName());
                                r.setSeriesName(goods.getSeriesName());
                                r.setModel(goods.getModel());
                            }
                            return r;
                        }).collect(Collectors.toList()))
                .build();
    }

    @Override
    public ImportResult<StockManageShelvesInfoImportResult> infoImport(StockManageShelvesInfoImportRequest request) {
        Map<String, Brand> brandMap = brandService.list(Wrappers.<Brand>lambdaQuery()
                        .in(Brand::getName, request.getDataList().stream()
                                .map(StockManageShelvesInfoImportRequest.ImportDto::getBrandName)
                                .distinct()
                                .collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(Brand::getName, Function.identity()));


        List<GoodsWatch> goodsList = goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                .in(GoodsWatch::getBrandId, brandMap.values().stream().map(Brand::getId).collect(Collectors.toList()))
                .in(GoodsWatch::getModel, request.getDataList().stream()
                        .map(StockManageShelvesInfoImportRequest.ImportDto::getModel)
                        .distinct()
                        .collect(Collectors.toList())));

        List<String> repetitiveModel = goodsList.stream()
                .collect(Collectors.groupingBy(GoodsWatch::getModel))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > NumberUtils.INTEGER_ONE)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(repetitiveModel)) {
            throw new OperationRejectedException(OperationExceptionCode.GOODS_MODEL_REPETITIVE_DATA_ERROR, JSONObject.toJSONString(repetitiveModel));
        }

        Map<String, GoodsWatch> goodsMap = goodsList.stream()
                .collect(Collectors.toMap(GoodsWatch::getModel, Function.identity()));

        List<String> errorList = new ArrayList<>();
        List<StockManageShelvesInfo> data = request.getDataList()
                .stream()
                .map(t -> {
                    GoodsWatch goods = goodsMap.get(t.getModel());
                    Brand brand = brandMap.get(t.getBrandName());
                    if (Objects.isNull(goods) || Objects.isNull(brand) || brand.getId() != goods.getBrandId().intValue()) {
                        errorList.add(t.getModel());
                        return null;
                    }
                    StockManageShelvesInfo info = new StockManageShelvesInfo();
                    info.setBrandId(brand.getId());
                    info.setGoodsId(goods.getId());
                    info.setShelvesSimplifiedCode(t.getShelvesSimplifiedCode());
                    return info;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        //新增或更新
        data.forEach(info -> stockManageShelvesInfoService.saveOrUpdate(info, Wrappers.<StockManageShelvesInfo>lambdaUpdate()
                .eq(StockManageShelvesInfo::getGoodsId, info.getGoodsId())));

        return ImportResult.<StockManageShelvesInfoImportResult>builder()
                .successList(data.stream()
                        .map(StockManageInfoConverter.INSTANCE::convertStockManageInfoImportResult)
                        .collect(Collectors.toList()))
                .errList(errorList)
                .build();
    }
}
