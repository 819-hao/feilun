package com.seeease.flywheel.serve.anomaly.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.anomaly.entity.BillAnomaly;
import com.seeease.flywheel.serve.anomaly.enums.AnomalyStateEnum;
import com.seeease.flywheel.serve.anomaly.service.BillAnomalyService;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.InStorageEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 异常监听入库
 * @Date create in 2023/3/16 11:07
 */
@Component
public class AnomalyListenerForInStorage implements BillHandlerEventListener<InStorageEvent> {

    private static List<BusinessBillTypeEnum> PURCHASE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.YC_CL
    );

    @Resource
    private BillAnomalyService billAnomalyService;

    @Resource
    private StockService stockService;

    @Resource
    private BillQualityTestingService billQualityTestingService;

    @Override
    public void onApplicationEvent(InStorageEvent event) {
        List<BillStoreWorkPre> workPreList = event.getWorkPreList();

        if (CollectionUtils.isEmpty(workPreList)) {
            return;
        }

        workPreList.stream().sorted(Comparator.comparing(BillStoreWorkPre::getId)).forEach(t -> {

            if (!PURCHASE_RETURN_TYPE.contains(t.getWorkSource())) {
                return;
            }

            BillAnomaly billAnomaly = billAnomalyService.getOne(Wrappers.<BillAnomaly>lambdaQuery().eq(BillAnomaly::getSerialNo, t.getOriginSerialNo()));

            if (ObjectUtils.isNotEmpty(billAnomaly)) {

                BillAnomaly anomaly = new BillAnomaly();
                anomaly.setId(billAnomaly.getId());
                anomaly.setAnomalyState(AnomalyStateEnum.IN);
                anomaly.setFinishTime(new Date());
                billAnomalyService.updateById(anomaly);

                Stock stock = stockService.getById(billAnomaly.getStockId());

                BillQualityTesting billQualityTesting = billQualityTestingService.getOne(Wrappers.<BillQualityTesting>lambdaQuery()
                        .eq(BillQualityTesting::getOriginSerialNo, t.getOriginSerialNo())
                        .eq(BillQualityTesting::getStockId, billAnomaly.getStockId())
                        .eq(BillQualityTesting::getQtSource, BusinessBillTypeEnum.YC_CL)
                );

                //当前的质检结果
                if (ObjectUtils.isNotEmpty(billQualityTesting)) {

                    if (billQualityTesting.getQtConclusion().equals(QualityTestingConclusionEnum.ANOMALY)) {
                        stockService.updateStockStatus(Arrays.asList(stock.getId()), StockStatusEnum.TransitionEnum.EXCEPTION_IN_EXCEPTION);
                        return;
                    }
                }

                if (ObjectUtils.isNotEmpty(stock.getTobPrice()) && ObjectUtils.isNotEmpty(stock.getTocPrice())) {
                    stockService.updateStockStatus(Arrays.asList(stock.getId()), StockStatusEnum.TransitionEnum.EXCEPTION_IN_MARKETABLE);
                } else {
                    stockService.updateStockStatus(Arrays.asList(stock.getId()), StockStatusEnum.TransitionEnum.EXCEPTION_IN_WAIT_PRICING);
                }

            }
        });

        //重新计算寄售价
        stockService.recalculateConsignmentPrice(workPreList.stream().map(BillStoreWorkPre::getStockId).sorted().collect(Collectors.toList()));
    }
}
