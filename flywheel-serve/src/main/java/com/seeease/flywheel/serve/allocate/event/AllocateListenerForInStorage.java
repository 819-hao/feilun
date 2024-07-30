package com.seeease.flywheel.serve.allocate.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockGuaranteeCardManageService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.event.InStorageEvent;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 调拨监听-入库事件
 *
 * @author Tiro
 * @date 2023/3/16
 */
@Component
public class AllocateListenerForInStorage extends BaseListenerForStoreWork<InStorageEvent> implements BillHandlerEventListener<InStorageEvent> {

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    private StockService stockService;
    @Resource
    private StockGuaranteeCardManageService cardManageService;


    @Override
    public void onApplicationEvent(InStorageEvent event) {
        super.onApplicationEvent(event.getWorkPreList(), event);
    }

    @Override
    void handler(BillAllocate allocate, List<BillStoreWorkPre> workPreList, InStorageEvent inStorageEvent) {
        /**
         * 只有寄售归还和寄售异常入库
         */
        Assert.isTrue(allocate.getAllocateSource().equals(BusinessBillTypeEnum.MD_DB_ZB)
                || allocate.getAllocateSource().equals(BusinessBillTypeEnum.ZB_DB), "调拨监听事件非正常监听");
        //调拨已退回入库
        List<Integer> returning = billAllocateLineService.list(Wrappers.<BillAllocateLine>lambdaQuery()
                        .eq(BillAllocateLine::getAllocateLineState, AllocateLineStateEnum.RETURNED)
                        .eq(BillAllocateLine::getAllocateId, allocate.getId()))
                .stream()
                .map(BillAllocateLine::getStockId)
                .collect(Collectors.toList());

        //处理入库事件，过滤已退回入库作业
        this.handlerInStorage(allocate, workPreList.stream()
                .filter(t -> !returning.contains(t.getStockId().intValue()))
                .collect(Collectors.toList()));

        //入库流转商品状态
        workPreList.stream()
                .collect(Collectors.groupingBy(BillStoreWorkPre::getExceptionMark))
                .forEach((k, v) -> {
                    StockStatusEnum.TransitionEnum transitionEnum;
                    switch (k) {
                        case YES:
                            transitionEnum = StockStatusEnum.TransitionEnum.ALLOCATE_IN_EXCEPTION_STORAGE;
                            break;
                        default:
                            transitionEnum = StockStatusEnum.TransitionEnum.ALLOCATE_CANCEL_OR_IN_STORAGE;
                    }
                    stockService.updateStockStatus(v.stream().map(BillStoreWorkPre::getStockId)
                                    .sorted()
                                    .collect(Collectors.toList())
                            , transitionEnum);
                });

        //调拨保卡管理
        cardManageService.allocateInByStockId(workPreList.stream().map(BillStoreWorkPre::getStockId).collect(Collectors.toList()));
    }

    /**
     * 处理调拨单
     *
     * @param allocate
     * @param workPreList
     */
    private void handlerInStorage(BillAllocate allocate, List<BillStoreWorkPre> workPreList) {
        if (CollectionUtils.isEmpty(workPreList)) {
            return;
        }
        //库存id
        List<Integer> stockIdList = workPreList
                .stream()
                .map(BillStoreWorkPre::getStockId)
                .sorted()
                .collect(Collectors.toList());

        switch (allocate.getAllocateType()) {
            //寄售归还入库
            case CONSIGN_RETURN:
                Assert.isTrue(allocate.getToId().intValue() == FlywheelConstant._ZB_ID, "寄售归还入库方异常");
                //更新行状态
                billAllocateLineService.updateLineState(allocate.getId(),
                        stockIdList,
                        AllocateLineStateEnum.TransitionEnum.IN_STOCK);



                break;
            //寄售异常入库
            case CONSIGN:
                Assert.isTrue(workPreList.stream().allMatch(t -> WhetherEnum.YES.equals(t.getExceptionMark())), "寄售归还入库方异常");
                //更新行状态
                billAllocateLineService.updateLineState(allocate.getId(),
                        stockIdList,
                        AllocateLineStateEnum.TransitionEnum.EXCEPTION_IN_STOCK);
                //取消下游作业单
                billStoreWorkPreService.upstreamDeliveryOfCancel(allocate.getSerialNo(), stockIdList);
                break;
            default:
                throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
        }
    }

}
