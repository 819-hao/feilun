package com.seeease.flywheel.serve.allocate.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.service.StockGuaranteeCardManageService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.OutStorageEvent;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 调拨监听-出货事件
 *
 * @author Tiro
 * @date 2023/3/16
 */
@Component
public class AllocateListenerForOutStorage extends BaseListenerForStoreWork<OutStorageEvent> implements BillHandlerEventListener<OutStorageEvent> {
    @Resource
    private StockGuaranteeCardManageService cardManageService;

    @Override
    public void onApplicationEvent(OutStorageEvent event) {
        super.onApplicationEvent(event.getWorkPreList(), event);
    }

    @Override
    void handler(BillAllocate allocate, List<BillStoreWorkPre> workPreList, OutStorageEvent outStorageEvent) {
        /**
         * 总部调拨才有出库
         */
        Assert.isTrue(allocate.getAllocateSource().equals(BusinessBillTypeEnum.ZB_DB), "调拨监听事件非正常监听");

        List<Integer> stockIdList = workPreList.stream()
                .map(BillStoreWorkPre::getStockId)
                .sorted()
                .collect(Collectors.toList());
        //更新调拨单状态
        billAllocateLineService.updateLineState(allocate.getId(), stockIdList, AllocateLineStateEnum.TransitionEnum.OUT_STOCK);

        //调拨保卡管理
        List<BillAllocateLine> lineList = billAllocateLineService.list(Wrappers.<BillAllocateLine>lambdaQuery()
                .eq(BillAllocateLine::getAllocateId, allocate.getId()));
        List<Integer> cardManageStockIdList = lineList.stream()
                .filter(l -> l.getGuaranteeCardManage().equals(WhetherEnum.YES.getValue()))
                .map(BillAllocateLine::getStockId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(cardManageStockIdList)) {
            cardManageService.allocateOutByStockId(cardManageStockIdList, allocate.getSerialNo());
        }

    }
}
