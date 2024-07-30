package com.seeease.flywheel.serve.purchase.event;

import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseLineNotice;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseTypeEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 监听完成事件
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Component
public class PurchaseListenerForUploadFinish implements BillHandlerEventListener<PurchaseUploadEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TH_CG_DJ,
            BusinessBillTypeEnum.TH_CG_QK,
            BusinessBillTypeEnum.TH_CG_DJTP,
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

    @Override
    public void onApplicationEvent(PurchaseUploadEvent request) {
//        BillQualityTesting one = billQualityTestingService.getOne(Wrappers.<BillQualityTesting>lambdaQuery().
//                eq(BillQualityTesting::getOriginSerialNo, event.getOriginSerialNo()).eq(BillQualityTesting::getStockId, event.getStockId()));
//
//
//        if (ObjectUtils.isEmpty(one) || !PURCHASE_TYPE.contains(one.getQtSource())) {
//            return;
//        }

        if (Arrays.asList(PurchaseTypeEnum.GR_HG).contains(request.getPurchaseType())) {
            //作废申请打款
            billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().purchaseId(request.getPurchaseId()).lineState(PurchaseLineStateEnum.CUSTOMER_HAS_SHIPPED).isSettlement(WhetherEnum.YES).build());
        }
    }
}
