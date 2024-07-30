package com.seeease.flywheel.serve.allocate.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateTypeEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.event.BillHandlerEventListener;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.enums.StoreWorkLogisticsRejectStateEnum;
import com.seeease.flywheel.serve.storework.event.LogisticsReceivingEvent;
import com.seeease.springframework.exception.e.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 调拨监听-收货事件
 *
 * @author Tiro
 * @date 2023/3/16
 */
@Component
public class AllocateListenerForReceiving extends BaseListenerForStoreWork<LogisticsReceivingEvent> implements BillHandlerEventListener<LogisticsReceivingEvent> {

    private List<AllocateTypeEnum> CHANGE_RIGHT_OF_MANAGEMENT_TYPE = Lists.newArrayList(AllocateTypeEnum.CONSIGN,
            AllocateTypeEnum.CONSIGN_RETURN,
            AllocateTypeEnum.FLAT);

    @Resource
    private StockService stockService;

    @Override
    public void onApplicationEvent(LogisticsReceivingEvent event) {
        super.onApplicationEvent(event.getWorkPreList(), event);
    }

    @Override
    void handler(BillAllocate allocate, List<BillStoreWorkPre> workPreList, LogisticsReceivingEvent event) {
        if (event.isShopReceived()) {
            Assert.isTrue(allocate.getAllocateSource().equals(BusinessBillTypeEnum.ZB_DB)
                    || allocate.getAllocateSource().equals(BusinessBillTypeEnum.MD_DB), "调拨类型异常");
        } else {
            Assert.isTrue(allocate.getAllocateSource().equals(BusinessBillTypeEnum.ZB_DB)
                    || allocate.getAllocateSource().equals(BusinessBillTypeEnum.MD_DB_ZB), "调拨类型异常");
        }
        List<Integer> stockIdList = workPreList.stream()
                .map(BillStoreWorkPre::getStockId)
                .sorted()
                .collect(Collectors.toList());

        List<BillAllocateLine> lineList = billAllocateLineService.list(Wrappers.<BillAllocateLine>lambdaQuery()
                .eq(BillAllocateLine::getAllocateId, allocate.getId())
                .in(BillAllocateLine::getStockId, stockIdList));

        Assert.isTrue(lineList.stream().map(BillAllocateLine::getAllocateLineState).distinct().count() == 1, "一个收货事件调拨行状态不一致");

        switch (lineList.get(0).getAllocateLineState()) {
            case RETURNING:
                this.returningReceiving(allocate, stockIdList, event.getLogisticsRejectState());
                break;
            case DELIVERED:
                this.deliveredReceiving(allocate, stockIdList, event.getLogisticsRejectState(), event.isShopReceived());
                break;
            default:
                throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
        }

        //门店正常收货流转商品状态
        if (StoreWorkLogisticsRejectStateEnum.NORMAL.equals(event.getLogisticsRejectState())
                && event.isShopReceived()) {
            stockService.updateStockStatus(stockIdList, StockStatusEnum.TransitionEnum.ALLOCATE_CANCEL_OR_IN_STORAGE);
        }

    }

    /**
     * 退货中收货
     *
     * @param allocate
     * @param returningStockIdList
     */
    private void returningReceiving(BillAllocate allocate, List<Integer> returningStockIdList, StoreWorkLogisticsRejectStateEnum stateEnum) {
        Assert.isTrue(StoreWorkLogisticsRejectStateEnum.NORMAL.equals(stateEnum), "退回收货无法拒收");
        billAllocateLineService.updateLineState(allocate.getId(), returningStockIdList, AllocateLineStateEnum.TransitionEnum.RECEIVING_RETURNED);
    }

    /**
     * 已发货的收货
     *
     * @param allocate
     * @param deliveredStockIdList
     * @param stateEnum
     * @param shopReceived
     */
    private void deliveredReceiving(BillAllocate allocate,
                                    List<Integer> deliveredStockIdList,
                                    StoreWorkLogisticsRejectStateEnum stateEnum,
                                    boolean shopReceived) {
        final AllocateLineStateEnum.TransitionEnum transitionEnum;
        switch (stateEnum) {
            case REJECT:
                if (shopReceived) {
                    transitionEnum = AllocateLineStateEnum.TransitionEnum.SHOP_RECEIVING_REJECT;
                } else {
                    //总部调拨收货无法拒绝
                    throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
                }
                break;
            case NORMAL:
                if (CHANGE_RIGHT_OF_MANAGEMENT_TYPE.contains(allocate.getAllocateType())) {
                    Map<Integer, BillAllocateLine> lineMap = billAllocateLineService.list(Wrappers.<BillAllocateLine>lambdaQuery()
                                    .eq(BillAllocateLine::getAllocateId, allocate.getId())
                                    .in(BillAllocateLine::getStockId, deliveredStockIdList))
                            .stream()
                            .collect(Collectors.toMap(BillAllocateLine::getStockId, Function.identity()));
                    Date time = new Date();
                    //修改经营权
                    deliveredStockIdList.forEach(id -> {
                        BillAllocateLine line = Objects.requireNonNull(lineMap.get(id));
                        //未改变经营权
                        if (Objects.isNull(line.getToRightOfManagement())) {
                            return;
                        }
                        Stock up = new Stock();
                        up.setId(id);
                        up.setStoreRkTime(time);
                        up.setRightOfManagement(line.getToRightOfManagement());
                        stockService.updateById(up);
                    });
                    //刷新门店库龄
                    stockService.refreshStorageAge(deliveredStockIdList);
                }
                //寄售归还清除需求门店
                if (AllocateTypeEnum.CONSIGN_RETURN.equals(allocate.getAllocateType())) {
                    stockService.cleanDemandIdByIds(deliveredStockIdList);
                }
                transitionEnum = shopReceived ? AllocateLineStateEnum.TransitionEnum.SHOP_RECEIVING
                        : AllocateLineStateEnum.TransitionEnum.RECEIVING;
                break;

            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
        billAllocateLineService.updateLineState(allocate.getId(), deliveredStockIdList, transitionEnum);
    }

}
