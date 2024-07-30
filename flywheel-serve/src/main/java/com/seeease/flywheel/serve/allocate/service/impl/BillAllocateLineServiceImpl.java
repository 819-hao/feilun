package com.seeease.flywheel.serve.allocate.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.allocate.request.AllocateCancelRequest;
import com.seeease.flywheel.allocate.result.AllocateCancelResult;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateTask;
import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateStateEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateTaskStateEnum;
import com.seeease.flywheel.serve.allocate.mapper.BillAllocateLineMapper;
import com.seeease.flywheel.serve.allocate.mapper.BillAllocateMapper;
import com.seeease.flywheel.serve.allocate.mapper.BillAllocateTaskMapper;
import com.seeease.flywheel.serve.allocate.service.BillAllocateLineService;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum.TransitionEnum.IN_STOCK;
import static com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum.TransitionEnum.SHOP_RECEIVING;

/**
 * @author Tiro
 * @description 针对表【bill_allocate_line(调拨单行)】的数据库操作Service实现
 * @createDate 2023-03-07 10:40:02
 */
@Service
public class BillAllocateLineServiceImpl extends ServiceImpl<BillAllocateLineMapper, BillAllocateLine>
        implements BillAllocateLineService {
    @Resource
    private BillAllocateMapper billAllocateMapper;
    @Resource
    private BillAllocateTaskMapper billAllocateTaskMapper;
    @Resource
    private StockMapper stockMapper;


    @Override
    public AllocateCancelResult cancel(AllocateCancelRequest request) {
        BillAllocate allocate = billAllocateMapper.selectById(request.getAllocateId());
        if (Objects.isNull(allocate)) {
            throw new BusinessException(ExceptionCode.ALLOCATE_BILL_NOT_EXIST);
        }
        List<BillAllocateLine> lines = baseMapper.selectList(Wrappers.<BillAllocateLine>lambdaQuery()
                .eq(BillAllocateLine::getAllocateId, allocate.getId()));

        if (lines.stream().anyMatch(t -> !AllocateLineStateEnum.INIT.equals(t.getAllocateLineState()))) {
            throw new OperationRejectedException(OperationExceptionCode.ALLOCATE_BILL_NOT_CANCEL);
        }
        //修改行状态
        lines.forEach(t -> {
            BillAllocateLine up = new BillAllocateLine();
            up.setId(t.getId());
            up.setTransitionStateEnum(AllocateLineStateEnum.TransitionEnum.CANCEL_ALLOCATE);
            UpdateByIdCheckState.update(baseMapper, up);
        });

        //更新调拨单状态
        this.updateAllocateState(allocate.getId());

        return AllocateCancelResult.builder()
                .id(allocate.getId())
                .serialNo(allocate.getSerialNo())
                .stockIdList(lines.stream()
                        .map(BillAllocateLine::getStockId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 更新行状态
     *
     * @param allocateId
     * @param stockIdList
     * @param transitionEnum
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLineState(Integer allocateId, List<Integer> stockIdList, AllocateLineStateEnum.TransitionEnum transitionEnum) {
        Map<Integer, BillAllocateLine> lineMap = baseMapper.selectList(Wrappers.<BillAllocateLine>lambdaQuery()
                        .in(BillAllocateLine::getStockId, stockIdList)
                        .eq(BillAllocateLine::getAllocateId, allocateId))
                .stream()
                .collect(Collectors.toMap(BillAllocateLine::getStockId, Function.identity()));
        if (MapUtils.isEmpty(lineMap)) {
            throw new BusinessException(ExceptionCode.ALLOCATE_BILL_NOT_EXIST);
        }
        stockIdList.forEach(t -> {
            BillAllocateLine line = lineMap.get(t);
            if (Objects.isNull(line)) {
                throw new BusinessException(ExceptionCode.ALLOCATE_BILL_NOT_EXIST);
            }
            BillAllocateLine up = new BillAllocateLine();
            up.setId(line.getId());
            up.setTransitionStateEnum(transitionEnum);
            UpdateByIdCheckState.update(baseMapper, up);

            if (transitionEnum == IN_STOCK || transitionEnum == SHOP_RECEIVING){
                System.out.println("1111111111111111111111111111111111111111111");
                Stock stock = new Stock();
                stock.setId(line.getStockId());
                stock.setNewSettlePrice(line.getTransferPrice());
                stockMapper.updateById(stock);
            }

        });

        //更新调拨单状态
        this.updateAllocateState(allocateId);
    }

    /**
     * 更新调拨单状态
     *
     * @param allocateId
     */
    private void updateAllocateState(Integer allocateId) {
        /**
         * 统计状态
         */
        List<Integer> stateList = baseMapper.selectStateByAllocateId(allocateId);

        AllocateStateEnum allocateState = AllocateStateEnum.OUT_STOCK;
        //全部已退回
        if (stateList.stream().allMatch(t -> Lists.newArrayList(AllocateLineStateEnum.RETURNED.getValue(),
                AllocateLineStateEnum.CANCEL.getValue()).contains(t))) {
            allocateState = AllocateStateEnum.CANCEL_WHOLE;
        } else if (stateList.stream().allMatch(t -> Lists.newArrayList(AllocateLineStateEnum.IN_STOCK.getValue(),
                AllocateLineStateEnum.RETURNED.getValue()).contains(t))) {
            allocateState = AllocateStateEnum.COMPLETE;
        }
        //更新整单状态
        BillAllocate upAllocate = new BillAllocate();
        upAllocate.setId(allocateId);
        upAllocate.setAllocateState(allocateState);
        billAllocateMapper.updateById(upAllocate);

        //更新调拨任务状态
        Optional.ofNullable(allocateState)
                .map(state -> {
                    switch (state) {
                        case COMPLETE:
                            return AllocateTaskStateEnum.COMPLETE;
                        case CANCEL_WHOLE:
                            return AllocateTaskStateEnum.CANCEL;
                        default:
                            return null;
                    }
                })
                .ifPresent(s -> {
                    BillAllocateTask task = new BillAllocateTask();
                    task.setTaskState(s);
                    billAllocateTaskMapper.update(task, Wrappers.<BillAllocateTask>lambdaUpdate().eq(BillAllocateTask::getAllocateId, allocateId));
                });
    }

}




