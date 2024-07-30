package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.customer.ICustomerFacade;
import com.seeease.flywheel.customer.entity.CustomerContactsInfo;
import com.seeease.flywheel.customer.entity.CustomerInfo;
import com.seeease.flywheel.goods.IGoodsExtFacade;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.goods.request.GoodsListRequest;
import com.seeease.flywheel.purchase.IAttachmentStockRecordFacade;
import com.seeease.flywheel.purchase.request.AttachmentStockImportRequest;
import com.seeease.flywheel.purchase.result.AttachmentStockImportResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.ATTACHMENT_STOCK_CREATE)
public class AttachmentStockImportExt implements ImportExtPtl<AttachmentStockImportRequest, AttachmentStockImportResult> {
    //允许导入的品牌
    private List<String> PERMITTED_BRAND = Arrays.asList("配件", "礼品");

    @DubboReference(check = false, version = "1.0.0")
    private ICustomerFacade customerFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IGoodsExtFacade goodsExtFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IAttachmentStockRecordFacade attachmentStockRecordFacade;

    @Override
    public Class<AttachmentStockImportRequest> getRequestClass() {
        return AttachmentStockImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<AttachmentStockImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        //详细校验
        for (AttachmentStockImportRequest.ImportDto importDto : cmd.getRequest().getDataList()) {
            if (StringUtils.isBlank(importDto.getCustomerName())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.CUSTOMER_REQUIRE_NON_NULL);
            }
            if (!PERMITTED_BRAND.contains(importDto.getBrandName())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.BRAND_NOT_ALLOW, importDto.getBrandName());
            }
            if (StringUtils.isBlank(importDto.getBrandName())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.BRAND_REQUIRE_NON_NULL);
            }
            if (StringUtils.isBlank(importDto.getSeriesName())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.SERIES_REQUIRE_NON_NULL);
            }
            if (StringUtils.isBlank(importDto.getModel())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.MODEL_REQUIRE_NON_NULL);
            }
            if (Objects.isNull(importDto.getNumber()) || importDto.getNumber() <= NumberUtils.INTEGER_ZERO) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.NUMBER_REQUIRE_NON_NULL);
            }
            if (BigDecimalUtil.leZero(importDto.getPurchasePrice())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.PURCHASE_PRICE_REQUIRE_NON_NULL);
            }
            if (BigDecimalUtil.leZero(importDto.getConsignmentPrice())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.CONSIGNMENT_PRICE_REQUIRE_NON_NULL);
            }
            if (BigDecimalUtil.lt(importDto.getConsignmentPrice(), importDto.getPurchasePrice())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.CONSIGNMENT_PRICE_ILLEGALITY);
            }
            long sizeGroup1 = Lists.newArrayList(importDto.getLengthSize(), importDto.getWidthSize())
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .count();
            if (sizeGroup1 == NumberUtils.INTEGER_ONE) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.SIZE_ILLEGAL);
            }
            long sizeGroup2 = Lists.newArrayList(importDto.getDiameterSize()
                            , importDto.getThicknessSize()
                            , importDto.getRadianSize())
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .count();
            if (sizeGroup2 == NumberUtils.INTEGER_ONE
                    || sizeGroup2 == NumberUtils.INTEGER_TWO) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.SHAPE_ILLEGAL);
            }
            long sizeGroup3 = Lists.newArrayList(importDto.getMovementNo())
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .count();
            long sizeGroup4 = Lists.newArrayList(importDto.getBatteryModel())
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .count();
            long sizeGroupCount = Lists.newArrayList(sizeGroup1, sizeGroup2, sizeGroup3, sizeGroup4)
                    .stream()
                    .filter(t -> t > 0)
                    .count();
            if (sizeGroupCount > NumberUtils.INTEGER_ONE) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.SIZE_GROUP_ILLEGAL);
            }
        }
    }

    @Override
    public ImportResult<AttachmentStockImportResult> handle(ImportCmd<AttachmentStockImportRequest> cmd) {
        //供应商校验
        Map<String, CustomerInfo> customerMap = customerFacade.findByCustomerName(cmd.getRequest().getDataList()
                        .stream()
                        .map(AttachmentStockImportRequest.ImportDto::getCustomerName)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(CustomerInfo::getCustomerName, Function.identity(), (k1, k2) -> k1));

        //补充客户id
        cmd.getRequest().getDataList()
                .forEach(t -> {
                    CustomerInfo customerInfo = customerMap.get(t.getCustomerName());
                    if (Objects.isNull(customerInfo) || CollectionUtils.isEmpty(customerInfo.getContactsInfoList())) {
                        throw new OperationRejectedException(OperationExceptionCodeEnum.CUSTOMER_NON_EXISTENT, t.getCustomerName());
                    }
                    t.setCustomerId(customerInfo.getId());
                    t.setCustomerContactsId(customerInfo.getContactsInfoList()
                            .stream()
                            .mapToInt(CustomerContactsInfo::getId)
                            .max()
                            .getAsInt()
                    );
                });

        //品牌系列型号
        Map<String/*model*/, List<GoodsBaseInfo>> goodsMap = goodsExtFacade.listGoods(GoodsListRequest.builder()
                        .modelList(cmd.getRequest().getDataList()
                                .stream()
                                .map(AttachmentStockImportRequest.ImportDto::getModel)
                                .distinct()
                                .collect(Collectors.toList()))
                        .build())
                .getResult()
                .stream()
                .collect(Collectors.groupingBy(GoodsBaseInfo::getModel));


        //补充型号商品id
        cmd.getRequest().getDataList()
                .forEach(t -> {
                    GoodsBaseInfo goods = Optional.ofNullable(goodsMap.get(t.getModel()))
                            .map(gl -> gl.stream()
                                    .filter(g -> g.getBrandName().equals(t.getBrandName()) && g.getSeriesName().equals(t.getSeriesName()))
                                    .findFirst()
                                    .orElse(null))
                            .orElse(null);
                    if (Objects.isNull(goods)) {
                        throw new OperationRejectedException(OperationExceptionCodeEnum.MODEL_NON_EXISTENT, StringUtils.join(Arrays.asList(t.getBrandName(), t.getSeriesName(), t.getModel()), "/"));
                    }
                    t.setGoodsId(goods.getGoodsId());
                });

        //导入创建
        AttachmentStockImportResult result = attachmentStockRecordFacade.importHandle(cmd.getRequest());

        return ImportResult.<AttachmentStockImportResult>builder()
                .successList(cmd.getRequest()
                        .getDataList()
                        .stream()
                        .map(t -> result)
                        .collect(Collectors.toList()))
                .errList(Collections.emptyList())
                .build();
    }
}
