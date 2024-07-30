package com.seeease.flywheel.serve.goods.rpc;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IAttachmentStockFacade;
import com.seeease.flywheel.goods.request.AttachmentStockInfoListRequest;
import com.seeease.flywheel.goods.result.AttachmentStockInfo;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.ExtAttachmentStockService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.service.StoreService;
import com.seeease.springframework.context.UserContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2024/1/19
 */
@DubboService(version = "1.0.0")
public class AttachmentStockFacade implements IAttachmentStockFacade {
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private StoreService storeService;
    @Resource
    private ExtAttachmentStockService extAttachmentStockService;

    @Override
    public PageResult<AttachmentStockInfo> pageList(AttachmentStockInfoListRequest request) {
        //尺寸参数精确化处理
        if (StringUtils.isNotBlank(request.getSize())) {
            switch (request.getSizeType()) {
                case 1:
                    request.setSize("S:%" + request.getSize().replaceAll(" ", "")
                            .replaceAll("X", "x")
                            .replaceAll("x", " x ")
                            + "%");
                    break;
                case 2:
                    request.setSize("G:%" + request.getSize().replaceAll(" ", "")
                            .replaceAll("X", "x")
                            .replaceAll("x", " x ")
                            + "%");
                    break;
                case 3:
                    request.setSize("M:%" + request.getSize().trim() + "%");
                    break;
                case 4:
                    request.setSize("B:%" + request.getSize().trim() + "%");
                    break;
            }
        }

        switch (request.getUseScenario()) {
            case ALLOCATE:
                //调拨只能选择位置在当前门店
                request.setStoreId(storeService.selectByShopId(UserContext.getUser().getStore().getId()).getId());
                break;
        }

        PageResult<AttachmentStockInfo> result = extAttachmentStockService.listStock(request);
        if (CollectionUtils.isNotEmpty(result.getResult())) {
            //型号信息
            Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByGoodsIds(result.getResult().stream()
                            .map(AttachmentStockInfo::getGoodsId)
                            .distinct()
                            .collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));
            //仓库信息
            Map<Integer, Store> storeMap = storeService.listByIds(result.getResult().stream()
                            .map(AttachmentStockInfo::getStoreId)
                            .distinct()
                            .collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(Store::getId, Function.identity()));

            result.getResult().forEach(t -> {
                t.setUuid(UUID.randomUUID().toString().replace("-", ""));
                WatchDataFusion goods = goodsMap.get(t.getGoodsId());
                if (Objects.nonNull(goods)) {
                    t.setBrandName(goods.getBrandName());
                    t.setSeriesName(goods.getSeriesName());
                    t.setModel(goods.getModel());
                    t.setImage(goods.getImage());
                }
                Store store = storeMap.get(t.getStoreId());
                if (Objects.nonNull(store)) {
                    t.setStoreName(store.getStoreName());
                }
            });
        }
        return result;
    }

    private int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }
}
