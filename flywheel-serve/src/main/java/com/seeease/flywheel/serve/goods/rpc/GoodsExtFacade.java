package com.seeease.flywheel.serve.goods.rpc;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IGoodsExtFacade;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.goods.request.GoodsListRequest;
import com.seeease.flywheel.goods.request.GoodsWatchUpdateImportRequest;
import com.seeease.flywheel.goods.result.GoodsWatchUpdateImportResult;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.goods.convert.GoodsWatchConverter;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.Series;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.SeriesService;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2023/3/9
 */
@Slf4j
@DubboService(version = "1.0.0")
public class GoodsExtFacade implements IGoodsExtFacade {
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private SeriesService seriesService;
    @Resource
    private StoreRelationshipSubjectService storeRelationshipSubjectService;

    @Override
    public PageResult<GoodsBaseInfo> listGoods(GoodsListRequest request) {
        if (request.isNeedStockNumber()) {
            request.setRightOfManagement(storeRelationshipSubjectService.getByShopId(UserContext.getUser().getStore().getId()).getSubjectId());
        }
        if (CollectionUtils.isNotEmpty(request.getModelList())) {
            request.setLimit(request.getModelList().size());
        }
        if (request.isNeedStockNumberByLocation()) {
            request.setLocationId(storeRelationshipSubjectService.getByShopId(UserContext.getUser().getStore().getId()).getStoreManagementId());
        }

        Page<GoodsBaseInfo> goodsBaseInfoPage = goodsWatchService.listGoods(request);

        return PageResult.<GoodsBaseInfo>builder()
                .result(goodsBaseInfoPage.getRecords())
                .totalCount(goodsBaseInfoPage.getTotal())
                .totalPage(goodsBaseInfoPage.getPages())
                .build();
    }

    @Override
    public ImportResult<GoodsWatchUpdateImportResult> updateGoodsWatchImport(GoodsWatchUpdateImportRequest request) {
        //查型号
        Map<String, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByGoodsIds(request.getDataList()
                        .stream().map(GoodsWatchUpdateImportRequest.ImportDto::getModelCode)
                        .map(code -> Integer.valueOf(code.substring(NumberUtils.INTEGER_ONE)))
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getModelCode, Function.identity()));
        //查系列
        Map<String, List<Series>> seriesMap = seriesService.listByName(request.getDataList().stream().map(GoodsWatchUpdateImportRequest.ImportDto::getSeriesName)
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(Series::getName));

        List<String> errList = new ArrayList<>();
        List<GoodsWatchUpdateImportResult> successList = new ArrayList<>();

        request.getDataList().forEach(t -> {
            try {
                WatchDataFusion goods = goodsMap.get(t.getModelCode());
                if (Objects.isNull(goods)) {
                    throw new BusinessException(ExceptionCode.GOODS_NOT_SUPPORT);
                }
                GoodsWatch up = GoodsWatchConverter.INSTANCE.convert(t);
                //设置型号id
                up.setId(goods.getGoodsId());

                if (StringUtils.isNotBlank(t.getSeriesName())) {
                    Series series = Optional.ofNullable(seriesMap.get(t.getSeriesName()))
                            .orElse(Collections.emptyList())
                            .stream()
                            .filter(s -> s.getBrandId().intValue() == goods.getBrandId() // 品牌不能变
                                    && s.getSeriesType().getValue().intValue() == goods.getSeriesType()) // 系列类型不能变
                            .findFirst()
                            .orElse(null);

                    if (Objects.isNull(series)) {
                        throw new BusinessException(ExceptionCode.SERIES_NOT_SUPPORT);
                    }
                    //设置系列
                    up.setSeriesId(series.getId());
                }
                //缩略型号
                if (StringUtils.isNotBlank(up.getModel())) {
                    up.setSimplifyModel(up.getModel().replaceAll("\\.", ""));
                }
                //更新型号
                goodsWatchService.updateById(up);

                successList.add(GoodsWatchUpdateImportResult.builder()
                        .id(up.getId())
                        .modelCode(t.getModelCode())
                        .build());
            } catch (Exception e) {
                log.error("型号修改异常[{}]-{}", JSONObject.toJSONString(t), e.getMessage(), e);
                errList.add(t.getModelCode());
            }
        });

        return ImportResult.<GoodsWatchUpdateImportResult>builder()
                .successList(successList)
                .errList(errList)
                .build();
    }
}
