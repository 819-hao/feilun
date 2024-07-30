package com.seeease.flywheel.serve.purchase.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseLineNotice;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.event.QtReceiveEvent;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 质检已转交
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Component
public class PurchaseListenerForQtReceive implements BillHandlerEventListener<QtReceiveEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TH_CG_DJ,
            BusinessBillTypeEnum.TH_CG_BH,
            BusinessBillTypeEnum.TH_CG_PL,
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
    private BillQualityTestingService billQualityTestingService;

    @Override
    public void onApplicationEvent(QtReceiveEvent event) {

        BillQualityTesting one = billQualityTestingService.getOne(Wrappers.<BillQualityTesting>lambdaQuery().
                eq(BillQualityTesting::getOriginSerialNo, event.getOriginSerialNo()).eq(BillQualityTesting::getStockId, event.getStockId()));

        if (ObjectUtils.isEmpty(one) || !PURCHASE_TYPE.contains(one.getQtSource())) {
            return;
        }

        if (one.getDeliverTo().intValue() == 1) {
            billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().
                    stockId(event.getStockId()).lineState(PurchaseLineStateEnum.IN_RETURN).serialNo(event.getOriginSerialNo()).build());
        } else if (one.getDeliverTo().intValue() == 0) {
            billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().
                    stockId(event.getStockId()).lineState(PurchaseLineStateEnum.IN_FIX_INSPECTION).serialNo(event.getOriginSerialNo()).build());
        }

    }
}
