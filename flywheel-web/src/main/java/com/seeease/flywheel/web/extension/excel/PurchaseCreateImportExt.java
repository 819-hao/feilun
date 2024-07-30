package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseStockQueryImportRequest;
import com.seeease.flywheel.purchase.result.PurchaseStockQueryImportResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 采购导入
 * @Date create in 2023/3/31 10:51
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.PURCHASE_CREATE)
public class PurchaseCreateImportExt implements ImportExtPtl<PurchaseStockQueryImportRequest, PurchaseStockQueryImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseFacade purchaseFacade;

    @Override
    public Class<PurchaseStockQueryImportRequest> getRequestClass() {
        return PurchaseStockQueryImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<PurchaseStockQueryImportRequest> cmd) {

        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        for (PurchaseStockQueryImportRequest.ImportDto importDto : cmd.getRequest().getDataList()) {
            /**
             * 型号
             */
            if (StringUtils.isBlank(importDto.getModel())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.MODEL_REQUIRE_NON_NULL);
            }
            /**
             * 成色
             */
            if (StringUtils.isBlank(importDto.getFiness())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.FINESS_REQUIRE_NON_NULL);
            }

            /**
             * 表身号
             */
            if (StringUtils.isBlank(importDto.getStockSn())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
            }
            //去除前后空格
            importDto.setStockSn(importDto.getStockSn().trim());

            /**
             * 采购价
             */
            if (ObjectUtils.isEmpty(importDto.getPurchasePrice()) || importDto.getPurchasePrice().equals(BigDecimal.ZERO)) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.PURCHASE_PRICE_NON_NULL);
            }

//            /**
//             * 销售优先等级
//             */
//            if (StringUtils.isBlank(importDto.getSalesPriorityName())) {
//                throw new OperationRejectedException(OperationExceptionCodeEnum.SALES_PRIORITY_NON_NULL);
//            }

            /**
             * 表带类型
             */
            if (StringUtils.isBlank(importDto.getStrapMaterial()) || !Arrays.asList("金属", "皮", "针织", "绢丝", "其他").contains(importDto.getStrapMaterial())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.STRAP_MATERIAL_NON_NULL);
            }

//            /**
//             * 商品等级
//             */
//            if (StringUtils.isBlank(importDto.getGoodsLevel())) {
//                throw new OperationRejectedException(OperationExceptionCodeEnum.GOODS_LEVEL_NON_NULL);
//            }

            List<Integer> allDict = Arrays.asList(
                    Integer.parseInt(importDto.getBox()),
                    Integer.parseInt(importDto.getBook()),
                    Integer.parseInt(importDto.getWarranty()),
                    Integer.parseInt(importDto.getInvoice()),
                    Integer.parseInt(importDto.getZCheck()),
                    Integer.parseInt(importDto.getGCheck()),
                    Integer.parseInt(importDto.getXCheck()),
                    Integer.parseInt(importDto.getOCheck()),
                    Integer.parseInt(importDto.getDrillCard()),
                    Integer.parseInt(importDto.getShoulderStrap()),
                    Integer.parseInt(importDto.getDustCoverBag()),
                    Integer.parseInt(importDto.getPurchaseVoucher()),
                    Integer.parseInt(importDto.getJewelCertificate()),
                    Integer.parseInt(importDto.getAttachment()),
                    Integer.parseInt(importDto.getNotacoria()),
                    Integer.parseInt(importDto.getHolomembrane()),
                    Integer.parseInt(importDto.getCard())
            );

            if (allDict.stream().allMatch(item -> item.equals(0))) {
                importDto.setSingleStock("1");
            }

            /**
             * 附件信息
             */
            if (importDto.getCard().equals(1) && StringUtils.isBlank(DateFormatUtils.format(importDto.getWarrantyDate(), "yyyy-MM"))) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.ATTACHMENT_NON_NULL);
            }

        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(PurchaseStockQueryImportRequest.ImportDto::getStockSn))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> e.getKey())
                .collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(repeatStockSn)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REPEAT, repeatStockSn);
        }
    }

    @Override
    public ImportResult<PurchaseStockQueryImportResult> handle(ImportCmd<PurchaseStockQueryImportRequest> cmd) {

        return purchaseFacade.stockQueryImport(cmd.getRequest());
    }
}
