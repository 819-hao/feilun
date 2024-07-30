package com.seeease.flywheel.serve.purchase.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.PurchaseReturnLineNotice;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseReturnLineStateEnum;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseReturnLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseReturnMapper;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseReturnLineService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Service
public class BillPurchaseReturnLineServiceImpl extends ServiceImpl<BillPurchaseReturnLineMapper, BillPurchaseReturnLine> implements BillPurchaseReturnLineService {

    @Resource
    private BillPurchaseReturnLineMapper billPurchaseReturnLineMapper;

    @Resource
    private BillPurchaseReturnMapper billPurchaseReturnMapper;

    @Resource
    private BillPurchaseLineMapper billPurchaseLineMapper;

    @Override
    public void changeState(Integer primarySourceId, List<Integer> list) {
        List<BillPurchaseReturnLine> allList = billPurchaseReturnLineMapper.selectList(
                Wrappers.<BillPurchaseReturnLine>lambdaQuery()
                        .eq(BillPurchaseReturnLine::getPurchaseReturnId, primarySourceId));

        //通过作业单 里的stockId 得到所有的当前状态枚举是 待出库状态 需要 改变为待物流发货状态的采购退货详情数据
        List<BillPurchaseReturnLine> lines = allList.stream()
                .filter(billPurchaseReturnLine -> list.contains(billPurchaseReturnLine.getStockId())
                        && billPurchaseReturnLine.getPurchaseReturnLineState().equals(PurchaseReturnLineStateEnum.WAITING_WAREHOUSE_DELIVERY))
                .collect(Collectors.toList());
        //根据采购退货单Id 和 作业单里的stockId 和待出库状态的数据 查出所有符合
//        List<BillPurchaseReturnLine> lines = billPurchaseReturnLineMapper.selectList(
//                Wrappers.<BillPurchaseReturnLine>lambdaQuery()
//                        .eq(BillPurchaseReturnLine::getPurchaseReturnId, request.getPurchaseReturnId())
//                        .eq(BillPurchaseReturnLine::getPurchaseReturnLineState, PurchaseReturnLineStateEnum.WAITING_FOR_DELIVERY)
//                        .in(BillPurchaseReturnLine::getStockId, list));
        if (list.size() != lines.size())
            throw new BusinessException(ExceptionCode.OPERATION_DATA_DOES_NOT_MATCH_DATABASE_DATA);
        //更新采购退货行
        lines.forEach(t -> {
            BillPurchaseReturnLine billPurchaseReturnLine = new BillPurchaseReturnLine();
            billPurchaseReturnLine.setId(t.getId());
            billPurchaseReturnLine.setPurchaseReturnLineState(PurchaseReturnLineStateEnum.WAITING_LOGISTICS_DELIVERY);
            if (1 != billPurchaseReturnLineMapper.updateById(billPurchaseReturnLine)) {
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
            }
        });
        //如果当前待出库状态数据为0 则代表可以改变 采购退货的状态
        if (0 == allList.stream().filter(billPurchaseReturnLine ->
                        billPurchaseReturnLine.getPurchaseReturnLineState()
                                .equals(PurchaseReturnLineStateEnum.WAITING_WAREHOUSE_DELIVERY)
                                && !list.contains(billPurchaseReturnLine.getStockId()))
                .collect(Collectors.toList()).size()) {
            BillPurchaseReturn billPurchaseReturn = new BillPurchaseReturn();
            billPurchaseReturn.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNCONFIRMED_TO_UNDER_WAY);
            billPurchaseReturn.setId(primarySourceId);
            billPurchaseReturnMapper.updateByIdCheckState(billPurchaseReturn);
        }
    }


    @Override
    public void noticeListener(PurchaseReturnLineNotice lineNotice) {

        BillPurchaseReturn billPurchaseReturn;

        if (ObjectUtils.isEmpty(lineNotice.getSerialNo()) && ObjectUtils.isNotEmpty(lineNotice.getPurchaseReturnId())) {
            billPurchaseReturn = billPurchaseReturnMapper.selectById(lineNotice.getPurchaseReturnId());
        } else {
            billPurchaseReturn = billPurchaseReturnMapper.selectOne(Wrappers.<BillPurchaseReturn>lambdaQuery().eq(BillPurchaseReturn::getSerialNo, lineNotice.getSerialNo()));
        }

        if (Objects.nonNull(billPurchaseReturn)) {

            //1。更新
            BillPurchaseReturnLine billPurchaseReturnLine = new BillPurchaseReturnLine();
            billPurchaseReturnLine.setPurchaseReturnLineState(Optional.ofNullable(lineNotice.getLineState()).orElse(null));
            billPurchaseReturnLine.setExpressNumber(lineNotice.getExpressNumber());

            //2。查询
            List<BillPurchaseReturnLine> billPurchaseReturnLineList = this.baseMapper.selectList(new LambdaQueryWrapper<BillPurchaseReturnLine>()
                    .eq(BillPurchaseReturnLine::getPurchaseReturnId, billPurchaseReturn.getId())
            );

            if (ObjectUtils.isNotEmpty(lineNotice.getPurchaseReturnId()) && Objects.isNull(lineNotice.getStockId())) {
                lineNotice.setStockId(billPurchaseReturnLineList.get(0).getStockId());
            }

            this.baseMapper.update(billPurchaseReturnLine, new LambdaUpdateWrapper<BillPurchaseReturnLine>()
                    .eq(BillPurchaseReturnLine::getPurchaseReturnId, billPurchaseReturn.getId())
                    .eq(BillPurchaseReturnLine::getStockId, lineNotice.getStockId()));

            BillPurchaseReturn purchaseReturn = new BillPurchaseReturn();
            purchaseReturn.setId(billPurchaseReturn.getId());
            purchaseReturn.setExpressNumber(lineNotice.getExpressNumber());

            //2。更改数据
            Map<Integer, PurchaseReturnLineStateEnum> map = billPurchaseReturnLineList.stream().collect(Collectors.toMap(BillPurchaseReturnLine::getStockId, BillPurchaseReturnLine::getPurchaseReturnLineState));

            map.put(lineNotice.getStockId(), lineNotice.getLineState());

            if (map.values().stream().allMatch(purchaseReturnLineStateEnum -> Lists.newArrayList(
                    PurchaseReturnLineStateEnum.TO_BE_CONFIRMED
            ).contains(purchaseReturnLineStateEnum))) {
                //待开始
            } else if (map.values().stream().allMatch(purchaseReturnLineStateEnum -> Lists.newArrayList(
                    PurchaseReturnLineStateEnum.CANCEL_WHOLE).contains(purchaseReturnLineStateEnum))) {
                //已取消
                purchaseReturn.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNCONFIRMED_TO_CANCEL_WHOLE);
            } else if (map.values().stream().allMatch(purchaseReturnLineStateEnum -> Lists.newArrayList(
                    PurchaseReturnLineStateEnum.WAITING_LOGISTICS_DELIVERY
            ).contains(purchaseReturnLineStateEnum))) {
                //1。待确认-已发货 2。进行中到-已发货
                //已完成
                if (billPurchaseReturn.getPurchaseReturnState() == BusinessBillStateEnum.UNCONFIRMED) {
                    purchaseReturn.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNCONFIRMED_TO_COMPLETE);
                } else {
                    purchaseReturn.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNDER_WAY_TO_COMPLETE);
                }

                //更新 更新采购单-采购退货单
                BillPurchaseLine billPurchaseLine = new BillPurchaseLine();
                billPurchaseLine.setOriginPurchaseReturnSerialNo(lineNotice.getSerialNo());
                billPurchaseLineMapper.update(billPurchaseLine,
                        Wrappers.<BillPurchaseLine>lambdaQuery()
                                .in(BillPurchaseLine::getStockId, billPurchaseReturnLineList.stream().map(BillPurchaseReturnLine::getStockId).collect(Collectors.toList()))
                );
            } else {
                //进行中
                if (billPurchaseReturn.getPurchaseReturnState() == BusinessBillStateEnum.UNCONFIRMED) {
                    purchaseReturn.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNCONFIRMED_TO_UNDER_WAY);
                } else {
                    return;
                }
            }

            UpdateByIdCheckState.update(billPurchaseReturnMapper, purchaseReturn);
        }
    }
}
