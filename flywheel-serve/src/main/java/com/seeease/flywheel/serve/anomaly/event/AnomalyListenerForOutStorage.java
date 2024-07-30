package com.seeease.flywheel.serve.anomaly.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.anomaly.entity.BillAnomaly;
import com.seeease.flywheel.serve.anomaly.enums.AnomalyStateEnum;
import com.seeease.flywheel.serve.anomaly.service.BillAnomalyService;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.OutStorageEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 异常监听出库
 * @Date create in 2023/3/16 11:07
 */
@Component
public class AnomalyListenerForOutStorage implements BillHandlerEventListener<OutStorageEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.YC_CL
    );

    @Resource
    private BillAnomalyService billAnomalyService;

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

            BillAnomaly billAnomaly = billAnomalyService.getOne(Wrappers.<BillAnomaly>lambdaQuery().eq(BillAnomaly::getStoreWorkSerialNo, t.getSerialNo()));

            if (ObjectUtils.isNotEmpty(billAnomaly)) {
                BillAnomaly anomaly = new BillAnomaly();
                anomaly.setId(billAnomaly.getId());
                anomaly.setAnomalyState(AnomalyStateEnum.CANCEL_WHOLE);
                billAnomalyService.updateById(anomaly);
            }
        });
    }
}
