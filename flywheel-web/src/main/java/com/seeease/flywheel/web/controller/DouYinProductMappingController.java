package com.seeease.flywheel.web.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doudian.open.api.sku_syncStock.SkuSyncStockRequest;
import com.doudian.open.api.sku_syncStock.SkuSyncStockResponse;
import com.doudian.open.api.sku_syncStock.param.SkuSyncStockParam;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.web.common.context.DouYinConfig;
import com.seeease.flywheel.web.entity.DouYinProductMapping;
import com.seeease.flywheel.web.entity.DouYinProductMappingVO;
import com.seeease.flywheel.web.entity.request.DouYinProductMappingListRequest;
import com.seeease.flywheel.web.infrastructure.service.DouYinProductMappingService;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/7/20
 */
@Slf4j
@RestController
@RequestMapping("/douyinProduct")
public class DouYinProductMappingController {

    @Value(value = "${spring.profiles.active}")
    private String env;
    @Resource
    private DouYinProductMappingService douYinProductMappingService;

    /**
     * 列表
     *
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody DouYinProductMappingListRequest request) {
        Page<DouYinProductMapping> res = douYinProductMappingService.page(Page.of(request.getPage(), request.getLimit()), Wrappers.<DouYinProductMapping>lambdaQuery()
                .eq(DouYinProductMapping::getShopId, UserContext.getUser().getStore().getId())
                .like(StringUtils.isNotEmpty(request.getDouYinProductId()), DouYinProductMapping::getDouYinProductId, request.getDouYinProductId())
                .like(StringUtils.isNotEmpty(request.getDouYinSkuId()), DouYinProductMapping::getDouYinSkuId, request.getDouYinSkuId())
                .like(StringUtils.isNotEmpty(request.getModelCode()), DouYinProductMapping::getModelCode, request.getModelCode())
        );

        return SingleResponse.of(PageResult.<DouYinProductMappingVO>builder()
                .result(res.getRecords()
                        .stream()
                        .map(t -> DouYinProductMappingVO
                                .builder()
                                .id(t.getId())
                                .douYinShopId(t.getDouYinShopId())
                                .goodsId(t.getGoodsId())
                                .douYinProductId(t.getDouYinProductId())
                                .douYinSkuId(t.getDouYinSkuId())
                                .modelCode(t.getModelCode())
                                .number(t.getNumber())
                                .shopId(t.getShopId())
                                .syncTime(Optional.ofNullable(t.getSyncTime())
                                        .map(d -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d))
                                        .orElse(null)).build())
                        .collect(Collectors.toList()))
                .totalCount(res.getTotal())
                .totalPage(res.getPages())
                .build());
    }

    /**
     * 同步
     *
     * @return
     */
    @PostMapping("/sync")
    public SingleResponse sync() {
        if (!env.equals("prod")) {
            return SingleResponse.buildSuccess();
        }
        List<DouYinProductMappingVO> productMappingVOList = douYinProductMappingService.selectProductStock(UserContext.getUser().getStore().getId());
        if (CollectionUtils.isNotEmpty(productMappingVOList)) {
            productMappingVOList.forEach(product -> {
                try {
                    SkuSyncStockRequest request = new SkuSyncStockRequest();
                    SkuSyncStockParam param = request.getParam();
                    param.setSkuId(Long.valueOf(product.getDouYinSkuId()));
                    param.setProductId(Long.valueOf(product.getDouYinProductId()));
                    param.setIncremental(false);
                    param.setStockNum(product.getNumber().longValue());
                    SkuSyncStockResponse response = request.execute(DouYinConfig.getAccessToken(product.getDouYinShopId().longValue()));
                    boolean success = response.getCode().equals("10000");
                    if (success) {
                        DouYinProductMapping up = new DouYinProductMapping();
                        up.setId(product.getId());
                        up.setNumber(product.getNumber());
                        up.setSyncTime(new Date());
                        douYinProductMappingService.updateById(up);
                    }
                } catch (Exception e) {
                    log.error("抖音库存同步异常，{}", e.getMessage(), e);
                }
            });

        }
        return SingleResponse.buildSuccess();
    }
}
