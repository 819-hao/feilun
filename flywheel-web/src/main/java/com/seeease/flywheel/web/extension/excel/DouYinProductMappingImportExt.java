package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.request.DouYinProductMappingImportRequest;
import com.seeease.flywheel.goods.result.DouYinProductMappingImportResult;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.entity.DouYinProductMapping;
import com.seeease.flywheel.web.entity.DouYinShopMapping;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.service.DouYinProductMappingService;
import com.seeease.flywheel.web.infrastructure.service.DouYinShopMappingService;
import com.seeease.springframework.context.UserContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/7/26
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.DOU_YIN_PRODUCT_MAPPING)
public class DouYinProductMappingImportExt implements ImportExtPtl<DouYinProductMappingImportRequest, DouYinProductMappingImportResult> {

    @Resource
    private DouYinProductMappingService douYinProductMappingService;
    @Resource
    private DouYinShopMappingService douYinShopMappingService;

    @Override
    public Class<DouYinProductMappingImportRequest> getRequestClass() {
        return DouYinProductMappingImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<DouYinProductMappingImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");
    }

    @Override
    public ImportResult<DouYinProductMappingImportResult> handle(ImportCmd<DouYinProductMappingImportRequest> cmd) {
        Integer shopId = UserContext.getUser().getStore().getId();
        List<DouYinShopMapping> douYinShopMapping = douYinShopMappingService.list(Wrappers.<DouYinShopMapping>lambdaQuery().eq(DouYinShopMapping::getShopId, shopId));

        List<Integer> douYinShopIdList = douYinShopMapping.stream().map(DouYinShopMapping::getDouYinShopId)
                .map(t -> t.intValue())
                .collect(Collectors.toList());

        List<DouYinProductMapping> mappingList = cmd.getRequest()
                .getDataList()
                .stream()
                .filter(t -> this.effective(t, douYinShopIdList))
                .map(t -> {
                    DouYinProductMapping mapping = new DouYinProductMapping();
                    mapping.setDouYinProductId(t.getDouYinProductId());
                    mapping.setDouYinSkuId(t.getDouYinSkuId());
                    mapping.setDouYinShopId(t.getDouYinShopId());
                    mapping.setModelCode(t.getModelCode());
                    mapping.setShopId(shopId);
                    mapping.setGoodsId(Integer.valueOf(t.getModelCode().replaceAll("M", "")));
                    return mapping;
                })
                .collect(Collectors.toList());

        douYinProductMappingService.saveOrUpdateMapping(mappingList);

        return ImportResult.<DouYinProductMappingImportResult>builder()
                .successList(mappingList.stream()
                        .map(t -> DouYinProductMappingImportResult.builder()
                                .id(t.getId())
                                .build())
                        .collect(Collectors.toList()))
                .errList(cmd.getRequest()
                        .getDataList()
                        .stream()
                        .filter(t -> !this.effective(t, douYinShopIdList))
                        .map(DouYinProductMappingImportRequest.ImportDto::getDouYinSkuId)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 有效数据
     *
     * @param t
     * @param douYinShopIdList
     * @return
     */
    private boolean effective(DouYinProductMappingImportRequest.ImportDto t, List<Integer> douYinShopIdList) {
        return Objects.nonNull(t.getDouYinShopId())
                && douYinShopIdList.contains(t.getDouYinShopId())
                && StringUtils.isNotBlank(t.getDouYinProductId())
                && StringUtils.isNotBlank(t.getDouYinSkuId())
                && StringUtils.isNotBlank(t.getModelCode())
                && t.getModelCode().startsWith("M");
    }
}
