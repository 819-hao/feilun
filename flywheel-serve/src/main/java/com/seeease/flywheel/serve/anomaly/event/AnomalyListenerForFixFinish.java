package com.seeease.flywheel.serve.anomaly.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.anomaly.entity.BillAnomaly;
import com.seeease.flywheel.serve.anomaly.enums.AnomalyStateEnum;
import com.seeease.flywheel.serve.anomaly.service.BillAnomalyService;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.event.FixFinishEvent;
import com.seeease.flywheel.serve.fix.service.BillFixService;
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
public class AnomalyListenerForFixFinish implements BillHandlerEventListener<FixFinishEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.YC_CL
    );

    @Resource
    private BillAnomalyService billAnomalyService;

    @Resource
    private BillFixService billFixService;

    @Override
    public void onApplicationEvent(FixFinishEvent event) {

        BillFix billFix = billFixService.getById(event.getFixId());

        if (ObjectUtils.isEmpty(billFix) || !PURCHASE_RETURN_TYPE.contains(billFix.getFixSource())) {
            return;
        }

        BillAnomaly billAnomaly = billAnomalyService.getOne(Wrappers.<BillAnomaly>lambdaQuery().eq(BillAnomaly::getSerialNo, billFix.getSerialNo()));

        if (ObjectUtils.isNotEmpty(billAnomaly)) {
            BillAnomaly anomaly = new BillAnomaly();
            anomaly.setId(billAnomaly.getId());
            anomaly.setAnomalyState(AnomalyStateEnum.CANCEL_WHOLE);
            anomaly.setFixId(event.getFixId());
            billAnomalyService.updateById(anomaly);
        }
    }
}
