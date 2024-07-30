package com.seeease.flywheel.serve.goods.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.allocate.request.ModelLiveScriptImportRequest;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.goods.IGoodsWatchFacade;
import com.seeease.flywheel.goods.request.GoodsWatchInfoRequest;
import com.seeease.flywheel.goods.request.StockListRequest;
import com.seeease.flywheel.goods.result.GoodsWatchInfo;
import com.seeease.flywheel.sale.request.SaleStockQueryImportRequest;
import com.seeease.flywheel.serve.goods.convert.GoodsWatchConverter;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.ModelLiveScript;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.mapper.ModelLiveScriptMapper;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.ModelLiveScriptService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@DubboService(version = "1.0.0")
public class GoodsWatchFacade implements IGoodsWatchFacade {
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private BrandService brandService;
    @Resource
    private ModelLiveScriptService modelLiveScriptService;
    @Resource
    private StockService stockService;

    @Override
    public List<GoodsWatchInfo> getAllList(GoodsWatchInfoRequest request) {
        if (StringUtils.isNotBlank(request.getBrandName())) {
            List<Integer> list = brandService.list(new LambdaQueryWrapper<Brand>()
                            .likeRight(Brand::getName, request.getBrandName()))
                    .stream()
                    .map(Brand::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)) {
                return Collections.EMPTY_LIST;
            }
            request.setBrandIdList(list);
        }
        List<GoodsWatch> list = goodsWatchService.getAllList(request);
        if (CollectionUtils.isEmpty(list))
            return Collections.EMPTY_LIST;
        return list.stream().map(GoodsWatchConverter.INSTANCE::convertGoodsWatch).collect(Collectors.toList());
    }

    @Override
    public String getModelLiveScript(Integer goodsWatchId) {
        LambdaQueryWrapper<ModelLiveScript> qw = Wrappers.<ModelLiveScript>lambdaQuery()
                .eq(ModelLiveScript::getGoodsWatchId, goodsWatchId);
        ModelLiveScript one = modelLiveScriptService.getOne(qw);
        if (one == null) {
            return null;
        } else {
            return one.getLiveScript();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void excelImport(List<ModelLiveScriptImportRequest.ImportDto> dataList) {
        Map<String, String> collect = dataList.stream()
                .collect(Collectors.groupingBy(ModelLiveScriptImportRequest.ImportDto::getStockSn))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0).getLiveScript()));

        LambdaQueryWrapper<Stock> qw = Wrappers.<Stock>lambdaQuery().in(Stock::getSn, collect.keySet()).eq(Stock::getDeleted, WhetherEnum.NO.getValue());


        Map<Integer, ModelLiveScript> goodsIdMap = stockService.list(qw).stream()
                .collect(Collectors.toMap(Stock::getSn, Stock::getGoodsId))
                .entrySet()
                .stream()
                .map(v -> {
                    if (collect.get(v.getKey()) != null) {
                        ModelLiveScript modelLiveScript = new ModelLiveScript();
                        modelLiveScript.setGoodsWatchId(v.getValue());
                        modelLiveScript.setLiveScript(collect.get(v.getKey()));
                        return modelLiveScript;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(ModelLiveScript::getGoodsWatchId))
                .values()
                .stream()
                .map(liveScripts -> liveScripts.get(0)) //去重
                .collect(Collectors.toMap(ModelLiveScript::getGoodsWatchId, Function.identity()));



        LambdaQueryWrapper<ModelLiveScript> qw1 = Wrappers.<ModelLiveScript>lambdaQuery().in(ModelLiveScript::getGoodsWatchId, goodsIdMap.keySet());
        modelLiveScriptService.list(qw1)
                .forEach(e-> goodsIdMap.get(e.getGoodsWatchId()).setId(e.getId()));

        modelLiveScriptService.saveOrUpdateBatch(goodsIdMap.values());


    }

    @Override
    public Map<String, String> listByModeCode(List<String> request) {

        return goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery().in(GoodsWatch::getModelCode, request))
                .stream().collect(Collectors.toMap(GoodsWatch::getModelCode, GoodsWatch::getModel));
    }
}
