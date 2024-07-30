package com.seeease.flywheel.serve.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.sale.request.B3SaleReturnOrderAddRemarkRequest;
import com.seeease.flywheel.sale.request.B3SaleReturnOrderListRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderExportRequest;
import com.seeease.flywheel.sale.result.B3SaleReturnOrderListResult;
import com.seeease.flywheel.sale.result.SaleReturnOrderExportResult;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLineDetailsVO;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLineDto;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderMapper;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderLineService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【bill_sale_return_order_line】的数据库操作Service实现
 * @createDate 2023-03-09 20:01:50
 */
@Service
public class BillSaleReturnOrderLineServiceImpl extends ServiceImpl<BillSaleReturnOrderLineMapper, BillSaleReturnOrderLine>
        implements BillSaleReturnOrderLineService {

    @Resource
    private BillSaleReturnOrderMapper billSaleReturnOrderMapper;

    @Override
    public List<BillSaleReturnOrderLineDetailsVO> selectBySaleReturnId(Integer saleReturnId) {
        return this.baseMapper.selectBySaleReturnId(saleReturnId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLineState(BillSaleReturnOrderLineDto dto, SaleReturnOrderLineStateEnum.TransitionEnum transitionEnum) {
        if (StringUtils.isNotBlank(dto.getSerialNo())) {
            BillSaleReturnOrder returnOrder = billSaleReturnOrderMapper.selectOne(Wrappers.<BillSaleReturnOrder>lambdaQuery()
                    .eq(BillSaleReturnOrder::getSerialNo, dto.getSerialNo()));
            if (ObjectUtils.isNotEmpty(returnOrder) && ObjectUtils.isEmpty(dto.getSaleReturnId()))
                dto.setSaleReturnId(returnOrder.getId());
        }
        Map<Integer, BillSaleReturnOrderLine> lineMap = baseMapper.selectList(Wrappers.<BillSaleReturnOrderLine>lambdaQuery()
                        .eq(BillSaleReturnOrderLine::getSaleReturnId, dto.getSaleReturnId()))
                .stream()
                .collect(Collectors.toMap(BillSaleReturnOrderLine::getStockId, Function.identity()));

        dto.getStockIdList().forEach(t -> {
            BillSaleReturnOrderLine line = lineMap.get(t);
            if (Objects.isNull(line)) {
                throw new BusinessException(ExceptionCode.SALE_RETURN_ORDER_BILL_NOT_EXIST);
            }
            BillSaleReturnOrderLine up = new BillSaleReturnOrderLine();
            up.setId(line.getId());
            up.setTransitionStateEnum(transitionEnum);
            UpdateByIdCheckState.update(baseMapper, up);
        });
        if (dto.getWhetherChangeOrderState())
            //更新退货单状态
            this.updateSaleReturnState(dto.getSaleReturnId());
    }

    @Override
    public List<Integer> selectStateByReturnId(Integer saleReturnId) {
        return this.baseMapper.selectStateByReturnId(saleReturnId);
    }

    @Override
    public Integer selectStateByReturnIdAndStockId(Integer saleReturnId, Integer stockId) {
        return this.baseMapper.selectStateByReturnIdAndStockId(saleReturnId, stockId);
    }

    @Override
    public Page<B3SaleReturnOrderListResult> b3Page(List<Integer> shopIds, List<Integer> b3ShopId, B3SaleReturnOrderListRequest request) {
        return this.baseMapper.b3Page(Page.of(request.getPage(), request.getLimit()), shopIds, b3ShopId, request);
    }

    @Override
    public void b3AddRemark(B3SaleReturnOrderAddRemarkRequest request) {
        LambdaUpdateWrapper<BillSaleReturnOrderLine> qw = Wrappers.<BillSaleReturnOrderLine>lambdaUpdate()
                .in(BillSaleReturnOrderLine::getId, request.getIds())
                .set(BillSaleReturnOrderLine::getRemark, request.getRemark());
        update(qw);
    }

    @Override
    public List<BillSaleReturnOrderLine> saleReturnOrderLineQry(Integer saleReturnId, List<Integer> stockIdList, List<SaleOrderLineStateEnum> saleOrderLineStateEnums) {
        LambdaQueryWrapper<BillSaleReturnOrderLine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillSaleReturnOrderLine::getSaleReturnId, saleReturnId)
                .in(BillSaleReturnOrderLine::getStockId, stockIdList)
                .in(BillSaleReturnOrderLine::getSaleLineState, saleOrderLineStateEnums);

        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public void updateWhetherInvoiceBySerialNoListAndStockIdList(List<String> serialNoList, List<Integer> stockIdList, FinancialInvoiceStateEnum stateEnum) {
        baseMapper.updateWhetherInvoiceBySerialNoListAndStockIdList(serialNoList, stockIdList, stateEnum.getValue());
    }

    @Override
    public Page<SaleReturnOrderExportResult> exportOrderReturn(SaleReturnOrderExportRequest request) {
        return baseMapper.exportOrderReturn(Page.of(request.getPage(), request.getLimit()), request);
    }

    /**
     * 更新退货单状态
     *
     * @param id
     */
    private void updateSaleReturnState(Integer id) {
        /**
         * 统计状态
         */
        List<Integer> stateList = baseMapper.selectStateByReturnId(id);
        //转换状态枚举
        List<SaleReturnOrderLineStateEnum> stateEnumList = stateList.stream()
                .map(SaleReturnOrderLineStateEnum::fromValue).collect(Collectors.toList());


        BusinessBillStateEnum stateEnum;
        //当所有的状态都是取消的时候 将销售单状态改成取消
        if (stateEnumList.stream().allMatch(t -> t.getLineDesc().equals(SeeeaseConstant.CANCEL_WHOLE))) {
            stateEnum = BusinessBillStateEnum.CANCEL_WHOLE;
        } else if (stateEnumList.stream().allMatch(t -> t.getLineDesc().equals(SeeeaseConstant.COMPLETE))) {
            stateEnum = BusinessBillStateEnum.COMPLETE;
        } else if (stateEnumList.stream().allMatch(t -> t.getLineDesc().equals(SeeeaseConstant.UN_STARTED))) {
            stateEnum = BusinessBillStateEnum.UNCONFIRMED;
        } else {
            stateEnum = BusinessBillStateEnum.UNDER_WAY;
        }
        BillSaleReturnOrder returnOrder = new BillSaleReturnOrder();
        returnOrder.setId(id);
        returnOrder.setFinishTime(new Date());
        returnOrder.setSaleReturnState(stateEnum);
        billSaleReturnOrderMapper.updateById(returnOrder);
    }
}




