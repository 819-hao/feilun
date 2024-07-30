package com.seeease.flywheel.serve.purchase.event;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.PurchaseReturnLineNotice;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.financial.entity.FinancialGenerateDto;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseReturnLineStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseReturnLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseReturnService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.OutStorageEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 采购退货监听出库
 * @Date create in 2023/3/16 11:07
 */
@Component
public class PurchaseReturnListenerForOutStorage implements BillHandlerEventListener<OutStorageEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.CG_TH
    );

    @Resource
    private BillPurchaseReturnService billPurchaseReturnService;
    @Resource
    private BillPurchaseReturnLineService billPurchaseReturnLineService;
    @Resource
    private FinancialDocumentsService financialDocumentsService;

    @Override
    public void onApplicationEvent(OutStorageEvent event) {
        List<BillStoreWorkPre> workPreList = event.getWorkPreList();

        if (CollectionUtils.isEmpty(workPreList)) {
            return;
        }

        workPreList.forEach(t -> {

            if (!PURCHASE_RETURN_TYPE.contains(t.getWorkSource())) {
                return;
            }
            switch (t.getWorkSource()) {

                case CG_TH:
                    // 采购退货 后方出库
                    BillPurchaseReturn purchaseReturn = billPurchaseReturnService.getOne(new LambdaQueryWrapper<BillPurchaseReturn>()
                            .eq(BillPurchaseReturn::getSerialNo, t.getOriginSerialNo()));
                    BillPurchaseReturnLine line = billPurchaseReturnLineService.getOne(new LambdaQueryWrapper<BillPurchaseReturnLine>()
                            .eq(BillPurchaseReturnLine::getStockId, t.getStockId()).eq(BillPurchaseReturnLine::getPurchaseReturnId, purchaseReturn.getId()));
                    FinancialGenerateDto dto = new FinancialGenerateDto();
                    dto.setId(purchaseReturn.getId());
                    dto.setStockList(Lists.newArrayList(t.getStockId()));
                    dto.setType(line.getStockSrc());
                    financialDocumentsService.generatePurchaseReturn(dto);

                    billPurchaseReturnLineService.noticeListener(PurchaseReturnLineNotice.builder().
                            stockId(t.getStockId()).lineState(PurchaseReturnLineStateEnum.WAITING_WAREHOUSE_DELIVERY).serialNo(t.getOriginSerialNo())
                            .build());
                    break;
            }
        });
    }
}
