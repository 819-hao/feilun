package com.seeease.flywheel.serve.allocate.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.LogisticsDeliveryEvent;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.springframework.exception.e.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 调拨监听-发货事件
 *
 * @author Tiro
 * @date 2023/3/16
 */
@Component
public class AllocateListenerForDelivery extends BaseListenerForStoreWork<LogisticsDeliveryEvent> implements BillHandlerEventListener<LogisticsDeliveryEvent> {
    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    private StockService stockService;

    @Override
    public void onApplicationEvent(LogisticsDeliveryEvent event) {
        super.onApplicationEvent(event.getWorkPreList(), event);
    }


    @Override
    void handler(BillAllocate allocate, List<BillStoreWorkPre> workPreList, LogisticsDeliveryEvent event) {
        if (!event.isShopDelivery()) {
            Assert.isTrue(allocate.getAllocateSource().equals(BusinessBillTypeEnum.ZB_DB), "调拨类型异常");
        }

        List<Integer> stockIdList = workPreList.stream()
                .map(BillStoreWorkPre::getStockId)
                .sorted()
                .collect(Collectors.toList());

        List<BillAllocateLine> lineList = billAllocateLineService.list(Wrappers.<BillAllocateLine>lambdaQuery()
                .eq(BillAllocateLine::getAllocateId, allocate.getId())
                .in(BillAllocateLine::getStockId, stockIdList));

        Assert.isTrue(lineList.stream().map(BillAllocateLine::getAllocateLineState).distinct().count() == 1, "调拨行状态不一致");

        if (AllocateLineStateEnum.RETURNING.equals(lineList.get(0).getAllocateLineState())) {
            //调拨下游退回发货,通知上游收货
            billStoreWorkPreService.downstreamDeliveryOfReturned(allocate.getSerialNo(),
                    event.getDeliveryExpressNumber(),
                    stockIdList);
            return;
        }

        final AllocateLineStateEnum.TransitionEnum transitionEnum;
        //门店发货
        switch (allocate.getAllocateType()) {
            case CONSIGN_RETURN:
            case FLAT:
            case BORROW:
                Assert.isTrue(event.isShopDelivery(), "当前调拨应该是门店发货");
                transitionEnum = AllocateLineStateEnum.TransitionEnum.SHOP_DELIVERY;
                break;

            case CONSIGN:
                Assert.isTrue(!event.isShopDelivery(), "当前调拨应该是总部发货");
                transitionEnum = AllocateLineStateEnum.TransitionEnum.DELIVERY;

                //总部调拨后 商品 需求门店 清空
                stockService.cleanDemandIdByIds(stockIdList);
                break;

            default:
                throw new BusinessException(ExceptionCode.BILL_EVENT_HANDLER_FAIL);
        }
        
        //修改发货快递单号
        lineList.stream()
                .sorted(Comparator.comparing(BillAllocateLine::getId))
                .forEach(t -> {
                    BillAllocateLine up = new BillAllocateLine();
                    up.setId(t.getId());
                    up.setExpressNumber(event.getDeliveryExpressNumber());
                    billAllocateLineService.updateById(up);
                });

        //更新调拨单状态
        billAllocateLineService.updateLineState(allocate.getId(), stockIdList, transitionEnum);

        //通知收货
        billStoreWorkPreService.upstreamDelivery(allocate.getSerialNo(),
                event.getDeliveryExpressNumber(),
                stockIdList);
    }
}
