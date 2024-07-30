package com.seeease.flywheel.serve.sale.event;

import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.event.FixFinishEvent;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLineDto;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 维修完成事件
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Component
public class SaleReturnListenerForFixFinish implements BillHandlerEventListener<FixFinishEvent> {

    @Resource
    private BillFixService billFixService;
    @Resource
    protected BillSaleReturnOrderLineService billSaleReturnOrderLineService;

    private static List<BusinessBillTypeEnum> SALE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TO_C_XS_TH,
            BusinessBillTypeEnum.TO_B_XS_TH,
            BusinessBillTypeEnum.TO_C_ON_LINE_TH
    );
    @Override
    public void onApplicationEvent(FixFinishEvent event) {

        BillFix billFix = billFixService.getById(event.getFixId());

        if (ObjectUtils.isEmpty(billFix) || !SALE_RETURN_TYPE.contains(billFix.getFixSource())) {
            return;
        }

        billSaleReturnOrderLineService.updateLineState(BillSaleReturnOrderLineDto.builder()
                .serialNo(billFix.getOriginSerialNo())
                .stockIdList(Lists.newArrayList(billFix.getStockId()))
                .whetherChangeOrderState(Boolean.FALSE)
                .build(), SaleReturnOrderLineStateEnum.TransitionEnum.IN_FIX_INSPECTION_TO_QUALITY_TESTING);

    }
}
