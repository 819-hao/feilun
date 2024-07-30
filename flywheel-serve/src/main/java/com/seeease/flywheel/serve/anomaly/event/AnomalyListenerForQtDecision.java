package com.seeease.flywheel.serve.anomaly.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.anomaly.entity.BillAnomaly;
import com.seeease.flywheel.serve.anomaly.enums.AnomalyStateEnum;
import com.seeease.flywheel.serve.anomaly.service.BillAnomalyService;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.event.QtDecisionEvent;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 质检判定结果
 * @Date create in 2023/3/13 14:19
 */
@Component
public class AnomalyListenerForQtDecision implements BillHandlerEventListener<QtDecisionEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.YC_CL
    );

    @Resource
    private BillAnomalyService billAnomalyService;

    @Override
    public void onApplicationEvent(QtDecisionEvent event) {

        if (!PURCHASE_RETURN_TYPE.contains(event.getBusinessBillTypeEnum())) {
            return;
        }

        QualityTestingStateEnum qtState = event.getQtState();

        BillAnomaly billAnomaly = billAnomalyService.getOne(Wrappers.<BillAnomaly>lambdaQuery().eq(BillAnomaly::getSerialNo, event.getOriginSerialNo()));

        if (ObjectUtils.isEmpty(billAnomaly)) {
            return;
        }
        BillAnomaly anomaly = new BillAnomaly();
        anomaly.setId(billAnomaly.getId());
        switch (qtState) {
            case NORMAL:
            case ANOMALY:
                break;
            case FIX:
                anomaly.setAnomalyState(AnomalyStateEnum.COMPLETE);
                anomaly.setFixId(event.getFixId());
                billAnomalyService.updateById(anomaly);
                break;
        }
    }
}
