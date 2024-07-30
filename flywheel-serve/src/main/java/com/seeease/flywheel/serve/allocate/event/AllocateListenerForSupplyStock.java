package com.seeease.flywheel.serve.allocate.event;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.entity.BillLifeCycle;
import com.seeease.flywheel.serve.goods.service.BillLifeCycleService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.OutStorageSupplyStockEvent;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 调拨监听-发货事件
 *
 * @author Tiro
 * @date 2023/3/16
 */
@Slf4j
@Component
public class AllocateListenerForSupplyStock extends BaseListenerForStoreWork<OutStorageSupplyStockEvent> implements BillHandlerEventListener<OutStorageSupplyStockEvent> {

    @Resource
    private BillLifeCycleService billLifeCycleService;

    @Override
    public void onApplicationEvent(OutStorageSupplyStockEvent event) {
        super.onApplicationEvent(event.getOutWorkList(), event);
    }


    @Override
    void handler(BillAllocate allocate, List<BillStoreWorkPre> workPreList, OutStorageSupplyStockEvent event) {
        //查调拨行
        Map<Integer, BillAllocateLine> lineMap = billAllocateLineService.list(Wrappers.<BillAllocateLine>lambdaQuery()
                        .eq(BillAllocateLine::getAllocateId, allocate.getId()))
                .stream()
                .collect(Collectors.toMap(BillAllocateLine::getId, Function.identity()));

        workPreList.forEach(t -> {
            BillAllocateLine line = lineMap.get(Objects.requireNonNull(this.getLineId(t.getMateMark())));
            if (Objects.isNull(line)) {
                throw new BusinessException(ExceptionCode.ALLOCATE_BILL_NOT_EXIST);
            }
            //更新商品
            BillAllocateLine up = new BillAllocateLine();
            up.setId(line.getId());
            up.setStockId(t.getStockId());
            billAllocateLineService.updateById(up);
            //补充生命周期
            try {
                BillLifeCycle billLifeCycle = new BillLifeCycle();
                billLifeCycle.setStockId(t.getStockId());
                billLifeCycle.setOriginSerialNo(allocate.getSerialNo());
                billLifeCycle.setOperationDesc("新建调拨单");
                billLifeCycle.setOperationTime(allocate.getCreatedTime().getTime());
                billLifeCycleService.save(billLifeCycle);
            } catch (Exception e) {
                log.error("保存表身号补充生命周期异常，allocate={}", JSONObject.toJSONString(allocate));
            }
        });

    }

    /**
     * @param mateMark
     * @return
     */
    private Integer getLineId(String mateMark) {
        try {
            return Integer.valueOf(mateMark.split("-")[2]);
        } catch (Exception e) {
            log.error("解析配对标记异常:{}", e.getMessage(), e);
            return null;
        }
    }
}
