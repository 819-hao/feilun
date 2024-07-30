package com.seeease.flywheel.serve.purchase.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseLineNotice;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.LogisticsReceivingEvent;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 物流
 * 收货或者拒绝
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Component
public class PurchaseListenerForLogisticsReceiving implements BillHandlerEventListener<LogisticsReceivingEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_TYPE = Lists.newArrayList(

            BusinessBillTypeEnum.TO_C_XS_TH,
            BusinessBillTypeEnum.TO_C_XS_TH,

            BusinessBillTypeEnum.TH_CG_DJ,
            BusinessBillTypeEnum.TH_CG_BH,
            BusinessBillTypeEnum.TH_CG_PL,
            BusinessBillTypeEnum.TH_CG_QK,
            BusinessBillTypeEnum.TH_CG_DJTP,
            BusinessBillTypeEnum.TH_JS,
            BusinessBillTypeEnum.GR_JS,
            BusinessBillTypeEnum.GR_HS_JHS,
            BusinessBillTypeEnum.GR_HS_ZH,
            BusinessBillTypeEnum.GR_HG_ZH,
            BusinessBillTypeEnum.GR_HG_JHS

    );

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private StockService stockService;

    @Override
    public void onApplicationEvent(LogisticsReceivingEvent event) {

        List<BillStoreWorkPre> workPreList = event.getWorkPreList();

        if (CollectionUtils.isEmpty(workPreList)) {
            return;
        }
        //加入总部&门店
        workPreList.forEach(t -> {
            if (!PURCHASE_TYPE.contains(t.getWorkSource())) {
                return;
            }
            switch (t.getWorkSource()) {
                case TH_CG_DJ:
                case TH_CG_BH:
                case TH_CG_PL:
                case TH_JS:
                case GR_JS:
                case GR_HS_JHS:
                case GR_HS_ZH:
                case GR_HG_JHS:
                case GR_HG_ZH:
                case TH_CG_QK:
                case TH_CG_DJTP:
                    //总部
                    switch (t.getWorkState()) {
                        case WAIT_FOR_IN_STORAGE:
                            billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().
                                    stockId(t.getStockId()).lineState(PurchaseLineStateEnum.IN_QUALITY_INSPECTION).serialNo(t.getOriginSerialNo()).build());

                            break;
                        case DELIVERY:
                            billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().
                                    stockId(t.getStockId()).lineState(PurchaseLineStateEnum.IN_RETURN).serialNo(t.getOriginSerialNo()).build());
                            break;
                    }
                    Stock stock = stockService.getById(t.getStockId());
                    switch (event.getLogisticsRejectState()) {

                        case NORMAL:


                            if (stock.getStockStatus() == StockStatusEnum.WAIT_RECEIVED) {
                                stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.WAIT_RECEIVED_PURCHASE_IN_TRANSIT);
                            }

                            break;
                        case REJECT:
                            // 拒收
                            // 定价拒收
                            // 未定价拒收
//                            stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_IN_TRANSIT_PURCHASE_RETURNED);
//                            Stock stock = stockService.getById(t.getStockId());

                            if (stock.getStockStatus() == StockStatusEnum.WAIT_PRICING) {
                                stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.WAIT_PRICING_PURCHASE_RETURNED);
//                            } else if (stock.getStockStatus() == StockStatusEnum.PURCHASE_IN_TRANSIT) {
                            } else if (stock.getStockStatus() == StockStatusEnum.WAIT_RECEIVED) {
                                stockService.updateStockStatus(Arrays.asList(t.getStockId()), StockStatusEnum.TransitionEnum.WAIT_RECEIVED_PURCHASE_RETURNED);
                            }

                            break;
                    }

                    /**
                     * ？？？？？
                     */
                case TO_C_XS_TH:
                case TO_B_XS_TH:

                    //只有销售发货 会通知采购这边 寄售中才能
                    BillPurchaseLine billPurchaseLineTh = billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery()
                            .eq(BillPurchaseLine::getStockId, t.getStockId())
                            .eq(BillPurchaseLine::getPurchaseLineState, PurchaseLineStateEnum.TO_BE_SETTLED));
                    if (ObjectUtils.isEmpty(billPurchaseLineTh)) {
                        return;
                    }
                    billPurchaseLineService.noticeListener(PurchaseLineNotice.builder()
                            .stockId(t.getStockId()).lineState(PurchaseLineStateEnum.ON_CONSIGNMENT)
                            .purchaseId(billPurchaseLineTh.getPurchaseId())
                            .isSettlement(WhetherEnum.NO)
                            .build());
                    break;
            }

        });

    }
}
