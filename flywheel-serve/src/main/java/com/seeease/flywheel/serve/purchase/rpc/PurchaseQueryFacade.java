package com.seeease.flywheel.serve.purchase.rpc;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.goods.request.SelectInsertPurchaseLineRequest;
import com.seeease.flywheel.goods.request.SelectInsertPurchaseRequest;
import com.seeease.flywheel.purchase.IPurchaseQueryFacade;
import com.seeease.flywheel.purchase.request.PurchaseBuyBackRequest;
import com.seeease.flywheel.purchase.result.PurchaseBuyBackResult;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;
import com.seeease.flywheel.purchase.result.PurchaseDetailsResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.base.WNOUtil;
import com.seeease.flywheel.serve.dict.entity.DictData;
import com.seeease.flywheel.serve.dict.service.DictDataService;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.enums.StoreWorkReturnTypeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/4/18
 */
@DubboService(version = "1.0.0")
public class PurchaseQueryFacade implements IPurchaseQueryFacade {

    @Resource
    private BillSaleOrderService billSaleOrderService;

    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;

    @Resource
    private BillPurchaseService billPurchaseService;

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private TagService tagService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private AccountsPayableAccountingService accountingService;

    @Override
    public PurchaseBuyBackResult queryBuyBack(PurchaseBuyBackRequest request) {

        /**
         * 为空
         */
        if (extracted(request)) {
            return PurchaseBuyBackResult.builder().details(Arrays.asList()).build();
        }

        /**
         * 采购单
         */
        List<BillPurchase> billPurchaseList = billPurchaseService.list(Wrappers.<BillPurchase>lambdaQuery()
                .in(BillPurchase::getOriginSaleSerialNo, request.getSaleSerialNoList())
                .in(BillPurchase::getPurchaseSource, Arrays.asList(BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_ZH))
        );

        if (CollectionUtils.isEmpty(billPurchaseList)) {
            return PurchaseBuyBackResult.builder().details(Arrays.asList()).build();
        }

        /**
         * 采购行
         */
        List<BillPurchaseLine> billPurchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery()
                .in(BillPurchaseLine::getPurchaseId, billPurchaseList.stream().map(BillPurchase::getId).collect(Collectors.toList())));

        if (CollectionUtils.isEmpty(billPurchaseLineList)) {
            return PurchaseBuyBackResult.builder().details(Arrays.asList()).build();
        }

        Map<Integer, String> storeNameMap = new HashMap<>();

        /**
         * 型号
         */
        List<WatchDataFusion> watchDataFusionList = goodsWatchService.getWatchDataFusionListByGoodsIds(billPurchaseLineList.stream().map(BillPurchaseLine::getGoodsId).collect(Collectors.toList()));

        /**
         * 附件
         */
        List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));

        return PurchaseBuyBackResult.builder().details(billPurchaseLineList.stream().map(billPurchaseLine -> {

            Optional<WatchDataFusion> watchDataFusionOptional = watchDataFusionList.stream().filter(watchDataFusion -> watchDataFusion.getGoodsId().equals(billPurchaseLine.getGoodsId())).findAny();

            Optional<BillPurchase> billPurchaseOptional = billPurchaseList.stream().filter(billPurchase -> billPurchase.getId().equals(billPurchaseLine.getPurchaseId())).findAny();

            PurchaseBuyBackResult.PurchaseBuyBackDto.PurchaseBuyBackDtoBuilder builder = PurchaseBuyBackResult.PurchaseBuyBackDto.builder();
            builder

                    .referenceBuyBackPrice(billPurchaseLine.getReferenceBuyBackPrice())
                    .planFixPrice(billPurchaseLine.getPlanFixPrice())
                    .watchbandReplacePrice(billPurchaseLine.getWatchbandReplacePrice())
                    .purchasePrice(billPurchaseLine.getPurchasePrice())
                    .stockSn(billPurchaseLine.getStockSn())
                    .finess(billPurchaseLine.getFiness())
                    .isCard(billPurchaseLine.getIsCard())
                    .warrantyDate(billPurchaseLine.getWarrantyDate())
                    .strapMaterial(billPurchaseLine.getStrapMaterial())
                    .watchSection(billPurchaseLine.getWatchSection());

            if (watchDataFusionOptional.isPresent()) {
                WatchDataFusion watchDataFusion = watchDataFusionOptional.get();
                builder.brandName(watchDataFusion.getBrandName())
                        .attachment(convert(dataList, billPurchaseLine.getAttachmentList(), billPurchaseLine.getIsCard(), billPurchaseLine.getWarrantyDate()))
                        .seriesName(watchDataFusion.getSeriesName())
                        .model(watchDataFusion.getModel());
            }

            if (billPurchaseOptional.isPresent()) {
                BillPurchase billPurchase = billPurchaseOptional.get();
                if (!storeNameMap.containsKey(billPurchase.getStoreId())) {
                    Tag tag = tagService.selectByStoreManagementId(billPurchase.getStoreId());
                    storeNameMap.put(billPurchase.getStoreId(), ObjectUtils.isNotEmpty(tag) ? tag.getTagName() : StringUtils.EMPTY);
                }
                builder.serialNo(billPurchase.getSerialNo())
                        .storeName(storeNameMap.get(billPurchase.getStoreId()))
                        .createTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, billPurchase.getCreatedTime()))
                        .createBy(billPurchase.getCreatedBy())
                        .saleSerialNo(billPurchase.getOriginSaleSerialNo())
                        .purchaseState(billPurchase.getPurchaseState().getValue());

            }
            return builder.build();
        }).collect(Collectors.toList())).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseCreateListResult autoPurchaseCreate(Integer workId) {

        BillStoreWorkPre billStoreWorkPre = billStoreWorkPreService.getById(workId);

        if (Objects.nonNull(billStoreWorkPre) && Arrays.asList(BusinessBillTypeEnum.TH_CG_BH, BusinessBillTypeEnum.TH_CG_DJ, BusinessBillTypeEnum.TH_CG_QK, BusinessBillTypeEnum.TH_CG_DJTP).contains(billStoreWorkPre.getWorkSource())

                && Arrays.asList(StoreWorkReturnTypeEnum.INT_STORE, StoreWorkReturnTypeEnum.OUT_STORE).contains(billStoreWorkPre.getReturnType())) {

            List<BillPurchase> billPurchaseList = billPurchaseService.list(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getSerialNo, billStoreWorkPre.getOriginSerialNo()));

            if (CollectionUtils.isNotEmpty(billPurchaseList)) {

                BillPurchase billPurchase = billPurchaseList.get(0);

                List<BillPurchaseLine> billPurchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery()
                        .eq(BillPurchaseLine::getPurchaseId, billPurchase.getId())
                        .eq(BillPurchaseLine::getStockId, billStoreWorkPre.getStockId())
                );

                if (CollectionUtils.isNotEmpty(billPurchaseLineList)) {

                    BillPurchaseLine billPurchaseLine = billPurchaseLineList.get(0);
                    billPurchaseLine.setStockId(null);
                    String serialNo = SerialNoGenerator.generatePurchaseSerialNo();
                    SelectInsertPurchaseRequest selectInsertPurchaseRequest = SelectInsertPurchaseRequest.builder()
                            .id(billPurchase.getId())
                            .serialNo(serialNo)
                            .totalPurchasePrice(billPurchaseLine.getPurchasePrice())
                            .build();

                    String wno = WNOUtil.generateWNO();
                    if (Objects.nonNull(billPurchaseLine.getGoodsId())) {
                        Optional<WatchDataFusion> optional = goodsWatchService.getWatchDataFusionListByGoodsIds(Lists.newArrayList(billPurchaseLine.getGoodsId())).stream().findFirst();
                        if (optional.isPresent()) {
                            WatchDataFusion fusion = optional.get();
                            if (SeriesTypeEnum.BAGS.getValue().equals(fusion.getSeriesType())) {
                                wno = WNOUtil.generateWNOB();
                            } else if (SeriesTypeEnum.ORNAMENT.getValue().equals(fusion.getSeriesType())) {
                                wno = WNOUtil.generateWNOJ();
                            }
                        }
                    }

                    SelectInsertPurchaseLineRequest selectInsertPurchaseLineRequest = SelectInsertPurchaseLineRequest.builder()
                            .id(billPurchaseLine.getId())
                            .wno(wno)
                            .build();

                    billPurchaseService.autoPurchaseCreate(selectInsertPurchaseRequest, selectInsertPurchaseLineRequest);

                    PurchaseDetailsResult.PurchaseLineVO purchaseLineVO = new PurchaseDetailsResult.PurchaseLineVO();
                    purchaseLineVO.setWno(wno);

//                    accountingService.createApa(serialNo, ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT,
//                            FinancialStatusEnum.PENDING_REVIEW, Arrays.asList(billPurchaseLine.getStockId()), null, false);

                    return PurchaseCreateListResult.builder()
                            //门店简码
                            .shortcodes(tagService.selectByStoreManagementId(billPurchase.getStoreId()).getShortcodes())
                            .serialNo(serialNo)
                            .createdBy(billPurchase.getCreatedBy())
                            .createdId(billPurchase.getCreatedId())
                            .businessKey(billPurchase.getPurchaseSource().getValue())
                            .line(Arrays.asList(
                                    purchaseLineVO
                            ))
                            .build();
                }
            }
        }

        return null;
    }

    /**
     * 销售单校验
     *
     * @param request
     * @return
     */
    private boolean extracted(PurchaseBuyBackRequest request) {
        if (ObjectUtils.isEmpty(request.getSaleSerialNoList()) || CollectionUtils.isEmpty(request.getSaleSerialNoList())) {
            return true;
        }
        /**
         * 销售单
         */
        List<BillSaleOrder> billSaleOrderList = billSaleOrderService.list(Wrappers.<BillSaleOrder>lambdaQuery()
                .in(BillSaleOrder::getSerialNo, request.getSaleSerialNoList()));

        if (CollectionUtils.isEmpty(billSaleOrderList)) {
            return true;
        }

        /**
         * 销售行
         */
        List<BillSaleOrderLine> billSaleOrderLineList = billSaleOrderLineService.list(Wrappers.<BillSaleOrderLine>lambdaQuery()
                .in(BillSaleOrderLine::getSaleId, billSaleOrderList.stream().map(BillSaleOrder::getId).collect(Collectors.toList())));

        if (CollectionUtils.isEmpty(billSaleOrderLineList)) {
            return true;
        }
        return false;
    }

    /**
     * 附件校验
     *
     * @param dataList
     * @param itemList
     * @param isCard
     * @param warrantyDate
     * @return
     */
    private String convert(List<DictData> dataList, List<Integer> itemList, Integer isCard, String warrantyDate) {

        String join = org.apache.commons.lang3.ObjectUtils.isEmpty(isCard) ? StringUtils.EMPTY : (isCard.equals(1) ? StringUtils.replace("保卡(date)", "date", warrantyDate) : isCard.equals(0) ? StringUtils.EMPTY : "空白保卡");

        String attachment = StringUtils.EMPTY;

        if (CollectionUtils.isNotEmpty(dataList)) {

            List<String> collect = null;
            if (CollectionUtils.isNotEmpty(itemList)) {
                collect = itemList.stream().flatMap(item -> dataList.stream().filter(dictData -> item.intValue() == (dictData.getDictCode().intValue()))).map(dictData -> dictData.getDictLabel()).collect(Collectors.toList());
            }
            attachment = CollectionUtils.isEmpty(itemList) ? StringUtils.EMPTY : StringUtils.join(collect, "/") + (org.apache.commons.lang3.ObjectUtils.isEmpty(join) ? StringUtils.EMPTY : "/" + join);

        } else {
            return join;
        }

        return attachment;
    }
}
