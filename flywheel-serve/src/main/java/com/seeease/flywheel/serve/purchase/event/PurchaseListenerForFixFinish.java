package com.seeease.flywheel.serve.purchase.event;

import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseLineNotice;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.entity.FixProjectMapper;
import com.seeease.flywheel.serve.fix.event.FixFinishEvent;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 维修完成事件
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Component
public class PurchaseListenerForFixFinish implements BillHandlerEventListener<FixFinishEvent> {

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
    public void onApplicationEvent(FixFinishEvent event) {

        BillFix billFix = billFixService.getById(event.getFixId());

        if (ObjectUtils.isEmpty(billFix) || !PURCHASE_TYPE.contains(billFix.getFixSource())) {
            return;
        }

        List<FixProjectMapper> fixProjectMapperList = billFix.getContent();
        //实际维修费
        BigDecimal reduce = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(fixProjectMapperList)) {
            reduce = fixProjectMapperList.stream().map(FixProjectMapper::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        billPurchaseLineService.noticeListener(PurchaseLineNotice.builder()
                .stockId(billFix.getStockId())
                .lineState(PurchaseLineStateEnum.IN_QUALITY_INSPECTION).serialNo(billFix.getOriginSerialNo())
                .fixPrice(reduce)
                .build()

        );

    }
}
