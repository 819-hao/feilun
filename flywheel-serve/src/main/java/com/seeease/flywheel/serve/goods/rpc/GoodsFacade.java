package com.seeease.flywheel.serve.goods.rpc;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.goods.IGoodsFacade;
import com.seeease.flywheel.goods.request.GoodsMetaInfoOffsetBasedRequest;
import com.seeease.flywheel.goods.request.GoodsMetaInfoQueryRequest;
import com.seeease.flywheel.goods.result.GoodsMetaInfoOffsetBasedResult;
import com.seeease.flywheel.goods.result.GoodsMetaInfoQueryResult;
import com.seeease.flywheel.serve.goods.convert.StockConverter;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoDto;
import com.seeease.flywheel.serve.goods.entity.GoodsMetaInfoSync;
import com.seeease.flywheel.serve.goods.service.GoodsMetaInfoSyncService;
import com.seeease.flywheel.serve.goods.service.PhotoGalleryService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/2/18
 */
@Slf4j
@DubboService(version = "1.0.0")
public class GoodsFacade implements IGoodsFacade {
    private static final ReentrantLock lock = new ReentrantLock();
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private GoodsMetaInfoSyncService goodsMetaInfoSyncService;
    @Resource
    private PhotoGalleryService photoGalleryService;


    /**
     * 记录商品拉去最后的数据和时间
     *
     * @param goodsMetaInfoList
     */
    private void updateLatestPullData(List<GoodsMetaInfoDto> goodsMetaInfoList) {
        threadPoolTaskExecutor.submit(() -> {
            goodsMetaInfoList.forEach(t -> {
                try {
                    GoodsMetaInfoSync dto = new GoodsMetaInfoSync();
                    dto.setStockId(t.getStockId());
                    dto.setLatestPullData(JSONObject.toJSONString(t));
                    dto.setStockState(t.getStockStatus());
                    dto.setTocPrice(t.getTocPrice());
                    dto.setBrandNew(t.getBrandNew());
                    dto.setGoodsId(t.getGoodsId());
                    if (1 != goodsMetaInfoSyncService.updateLatestPullData(dto)) {
                        throw new RuntimeException("数据拉取记录保存失败");
                    }
                } catch (Exception e) {
                    log.error("数据拉取记录保存失败：{} {}", JSONObject.toJSONString(t), e.getMessage(), e);
                }
            });
        });
    }

    @Override
    @SneakyThrows
    public GoodsMetaInfoOffsetBasedResult query(GoodsMetaInfoOffsetBasedRequest request) {
        if (lock.tryLock(10, TimeUnit.MINUTES)) {
            try {
                //限制拉取数量
                request.setLimit(Optional.ofNullable(request.getLimit())
                        .filter(t -> t <= 1000)
                        .orElse(1000));

                List<GoodsMetaInfoDto> goodsMetaInfoList = goodsMetaInfoSyncService.selectGoodsMetaInfo(request.getCurrentOffset(), request.getLimit());

                if (CollectionUtils.isEmpty(goodsMetaInfoList)) {
                    return GoodsMetaInfoOffsetBasedResult.builder()
                            .goodsList(Collections.EMPTY_LIST)
                            .currentOffset(request.getCurrentOffset())
                            .isEnd(true)
                            .build();
                }

                int maxOffset = goodsMetaInfoSyncService.maxGoodsMetaInfo(request.getCurrentOffset());
                int currentOffset = goodsMetaInfoList.stream()
                        .mapToInt(GoodsMetaInfoDto::getStockId)
                        .max().getAsInt();

                //补充商品信息
                this.supplyGoods(goodsMetaInfoList);

                //记录商品拉去最后的数据和时间
                this.updateLatestPullData(goodsMetaInfoList);

                return GoodsMetaInfoOffsetBasedResult.builder()
                        .goodsList(goodsMetaInfoList.stream()
                                .map(StockConverter.INSTANCE::convert)
                                .collect(Collectors.toList()))
                        .currentOffset(currentOffset)
                        .isEnd(currentOffset >= maxOffset)
                        .build();
            } finally {
                lock.unlock();
            }
        } else {
            throw new RuntimeException("拉取频率过高，获取锁失败");
        }
    }

    @Override
    public GoodsMetaInfoQueryResult query(GoodsMetaInfoQueryRequest request) {
        if (CollectionUtils.isEmpty(request.getStockIdList())) {
            return GoodsMetaInfoQueryResult.builder()
                    .goodsList(Collections.emptyList())
                    .build();
        }
        List<GoodsMetaInfoDto> goodsMetaInfoList = goodsMetaInfoSyncService.selectGoodsMetaInfoByStockIdList(request.getStockIdList());
        //补充商品信息
        this.supplyGoods(goodsMetaInfoList);
        //记录商品拉去最后的数据和时间
        this.updateLatestPullData(goodsMetaInfoList);

        return GoodsMetaInfoQueryResult.builder()
                .goodsList(goodsMetaInfoList.stream()
                        .map(StockConverter.INSTANCE::convert)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * @param goodsMetaInfoList
     */
    private void supplyGoods(List<GoodsMetaInfoDto> goodsMetaInfoList) {
        if (CollectionUtils.isEmpty(goodsMetaInfoList)) {
            return;
        }
        goodsMetaInfoList.forEach(t -> {
            //设置图片库
            Long goodsId = null;
            Long stockId = null;
            if (NumberUtils.INTEGER_ONE.intValue() == t.getBrandNew()) {
                goodsId = t.getGoodsId().longValue();
            } else {
                stockId = t.getStockId().longValue();
            }
            t.setImages(photoGalleryService.queryPhotoGalleryImgUrl(goodsId, stockId));
        });

    }
}
