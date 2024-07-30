package com.seeease.flywheel.serve.purchase.event;

import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseLineNotice;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.event.FixReceiveEvent;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 维修收货事件
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Component
public class PurchaseListenerForFixReceive implements BillHandlerEventListener<FixReceiveEvent> {

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

    @Resource
    private BillFixService billFixService;

    @Override
    public void onApplicationEvent(FixReceiveEvent event) {

        BillFix billFix = billFixService.getById(event.getFixId());

        if (ObjectUtils.isEmpty(billFix) || !PURCHASE_TYPE.contains(billFix.getFixSource())) {
            return;
        }

        billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().
                stockId(billFix.getStockId()).lineState(PurchaseLineStateEnum.IN_FIX_INSPECTION).serialNo(billFix.getOriginSerialNo()).build());

    }
}
