package com.seeease.flywheel.serve.sale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.sale.request.SaleReturnOrderCancelRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderExpressNumberUploadRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderListRequest;
import com.seeease.flywheel.sale.result.SaleReturnOrderCancelResult;
import com.seeease.flywheel.sale.result.SaleReturnOrderCreateResult;
import com.seeease.flywheel.sale.result.SaleReturnOrderDetailsResult;
import com.seeease.flywheel.sale.result.SaleReturnOrderExpressNumberUploadResult;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.StringTools;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.maindata.mapper.TagMapper;
import com.seeease.flywheel.serve.sale.convert.SaleReturnOrderConverter;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderReturnFlagEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderMapper;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【bill_sale_return_order】的数据库操作Service实现
 * @createDate 2023-03-09 20:01:50
 */
@Service
public class BillSaleReturnOrderServiceImpl extends ServiceImpl<BillSaleReturnOrderMapper, BillSaleReturnOrder>
        implements BillSaleReturnOrderService {

    @Resource
    private StockMapper stockMapper;
    @Resource
    private BillSaleReturnOrderLineMapper billSaleReturnOrderLineMapper;
    @Resource
    private BillSaleOrderLineMapper billSaleOrderLineMapper;
    @Resource
    private BillSaleOrderMapper billSaleOrderMapper;
    @Resource
    private TagMapper tagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleReturnOrderCreateResult create(SaleReturnOrderCreateRequest request) {
        List<SaleReturnOrderCreateResult.SaleReturnOrderDto> saleReturnOrderDtoList = new ArrayList<>();

        AtomicInteger num = new AtomicInteger(1);

        Map<Integer, FinancialInvoiceStateEnum> invoiceStateEnumMap = billSaleOrderLineMapper.selectBatchIds(request.getDetails()
                        .stream()
                        .map(SaleReturnOrderCreateRequest.BillSaleReturnOrderLineDto::getSaleLineId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(BillSaleOrderLine::getId, BillSaleOrderLine::getWhetherInvoice));

        request.getDetails()
                .stream()
                .collect(Collectors.groupingBy(SaleReturnOrderCreateRequest.BillSaleReturnOrderLineDto::getSaleId))
                .forEach((saleId, returnLineList) -> {
                    BillSaleOrder order = billSaleOrderMapper.selectById(saleId);
                    if (Objects.isNull(order)) {
                        throw new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE);
                    }
                    Assert.isTrue(request.getShopId() == order.getShopId().intValue(), "退货门店和销售门店不一致");
                    Assert.isTrue(request.getSaleReturnType().intValue() == order.getSaleType().getValue(), "退货类型和销售类型不一致");

                    BillSaleReturnOrder returnOrder = SaleReturnOrderConverter.INSTANCE.convertBillSaleReturnOrder(request);
                    if (Objects.nonNull(request.getCreator())) {
                        returnOrder.setCreatedId(request.getCreator().getCreatedId());
                        returnOrder.setCreatedBy(request.getCreator().getCreatedBy());
                    }
                    returnOrder.setSaleId(order.getId());
                    returnOrder.setCustomerId(order.getCustomerId());
                    returnOrder.setCustomerContactId(order.getCustomerContactId());
                    returnOrder.setTotalSaleReturnPrice(returnLineList.stream()
                            .map(SaleReturnOrderCreateRequest.BillSaleReturnOrderLineDto::getReturnPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    returnOrder.setSaleReturnNumber(returnLineList.size());
                    returnOrder.setSaleReturnState(BusinessBillStateEnum.UNCONFIRMED);
                    returnOrder.setSerialNo(StringTools.dataSplicing(request.getParentSerialNo(), num.getAndIncrement()));
                    //原路返退回发货地，否则退回销售地
                    returnOrder.setDeliveryLocationId(request.isReturnByOriginalRoute() ?
                            order.getDeliveryLocationId() : order.getShopId());
                    baseMapper.insert(returnOrder);

                    List<BillSaleReturnOrderLine> returnOrderLineList = returnLineList.stream()
                            .map(t -> {
                                BillSaleOrderLine billSaleOrderLine = billSaleOrderLineMapper.selectById(t.getSaleLineId());

                                BillSaleReturnOrderLine line = SaleReturnOrderConverter.INSTANCE.convertBillSaleReturnOrderLine(t);
                                line.setNewSettlePrice(billSaleOrderLine.getNewSettlePrice());
                                line.setSaleReturnId(returnOrder.getId());
                                line.setSaleReturnLineState(SaleReturnOrderLineStateEnum.UNCONFIRMED);
                                if (FinancialInvoiceStateEnum.INVOICE_COMPLETE.equals(invoiceStateEnumMap.get(line.getSaleLineId()))) {
                                    line.setWhetherInvoice(FinancialInvoiceStateEnum.INVOICE_COMPLETE);
                                }else if (FinancialInvoiceStateEnum.NO_INVOICED.equals(invoiceStateEnumMap.get(line.getSaleLineId()))) {
                                    line.setWhetherInvoice(FinancialInvoiceStateEnum.NO_INVOICED);
                                } else {
                                    throw new BusinessException(ExceptionCode.FINANCIAL_INVOICE_STATE_IN_INVOICED_NOT_SUPPORT);
                                }
                                line.setWhetherOperate(0);
                                line.setSaleSerialNo(order.getSerialNo());
                                return line;
                            }).collect(Collectors.toList());

                    billSaleReturnOrderLineMapper.insertBatchSomeColumn(returnOrderLineList);

                    //变更状态
                    returnOrderLineList.forEach(t -> changeSaleLineStatus(t, Boolean.FALSE));

                    saleReturnOrderDtoList.add(SaleReturnOrderCreateResult.SaleReturnOrderDto.builder()
                            .deliveryLocationId(returnOrder.getDeliveryLocationId())
                            .shortcodes(tagMapper.selectByStoreManagementId(returnOrder.getDeliveryLocationId()).getShortcodes())
                            .returnId(returnOrder.getId())
                            .serialNo(returnOrder.getSerialNo())
                            .stockIdList(returnOrderLineList.stream()
                                    .map(BillSaleReturnOrderLine::getStockId)
                                    .collect(Collectors.toList()))
                            .build());

                });
        SaleReturnOrderCreateResult build = SaleReturnOrderCreateResult.builder()
                .list(saleReturnOrderDtoList)
                .build();
        return build;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleReturnOrderCancelResult cancel(SaleReturnOrderCancelRequest request) {
        if (ObjectUtils.isNotEmpty(request.getSerialNo())) {
            BillSaleReturnOrder returnOrder = baseMapper.selectOne(new LambdaQueryWrapper<BillSaleReturnOrder>()
                    .eq(BillSaleReturnOrder::getSerialNo, request.getSerialNo()));
            if (ObjectUtils.isEmpty(returnOrder))
                throw new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE);
            request.setId(returnOrder.getId());
            request.setSaleId(returnOrder.getSaleId());
        }
        BillSaleReturnOrder returnOrder = new BillSaleReturnOrder();
        returnOrder.setId(request.getId());
        returnOrder.setSaleReturnState(BusinessBillStateEnum.CANCEL_WHOLE);
        baseMapper.updateById(returnOrder);
        //returnOrder.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNCONFIRMED_TO_CANCEL_WHOLE);
//        UpdateByIdCheckState.update(baseMapper, returnOrder);

        List<BillSaleReturnOrderLine> lines = billSaleReturnOrderLineMapper.selectList(Wrappers.<BillSaleReturnOrderLine>lambdaQuery()
                .eq(BillSaleReturnOrderLine::getSaleReturnId, request.getId()));

        lines.forEach(t -> {
            BillSaleReturnOrderLine billSaleOrderLine = new BillSaleReturnOrderLine();
            billSaleOrderLine.setId(t.getId());
            billSaleOrderLine.setTransitionStateEnum(SaleReturnOrderLineStateEnum.TransitionEnum.UNCONFIRMED_TO_CANCEL_WHOLE);
            UpdateByIdCheckState.update(billSaleReturnOrderLineMapper, billSaleOrderLine);
            changeSaleLineStatus(t, Boolean.TRUE);
        });

        SaleReturnOrderCancelResult build = SaleReturnOrderCancelResult.builder()
                .serialNo(baseMapper.selectById(request.getId()).getSerialNo())
                .line(lines.stream().map(billSaleReturnOrderLine -> {
                    SaleReturnOrderDetailsResult.SaleReturnOrderLineVO saleReturnOrderLineVO = new SaleReturnOrderDetailsResult.SaleReturnOrderLineVO();
                    saleReturnOrderLineVO.setStockId(billSaleReturnOrderLine.getStockId());
                    saleReturnOrderLineVO.setWhetherInvoice(billSaleReturnOrderLine.getWhetherInvoice().getValue());
                    return saleReturnOrderLineVO;
                }).collect(Collectors.toList()))
                .build();

        return build;
    }

    @Override
    public Page<BillSaleReturnOrder> listByRequest(SaleReturnOrderListRequest request) {
        return baseMapper.listByRequest(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleReturnOrderExpressNumberUploadResult uploadExpressNumber(SaleReturnOrderExpressNumberUploadRequest request) {
        //修改单状态
        BillSaleReturnOrder returnOrder = new BillSaleReturnOrder();
        returnOrder.setId(request.getSaleReturnId());
//        returnOrder.setTransitionStateEnum(BusinessBillStateEnum.TransitionEnum.UNCONFIRMED_TO_UNDER_WAY);
        returnOrder.setSaleReturnState(BusinessBillStateEnum.UNDER_WAY);
        returnOrder.setExpressNumber(request.getExpressNumber());
        baseMapper.updateById(returnOrder);
//        UpdateByIdCheckState.update(baseMapper, returnOrder);

        List<BillSaleReturnOrderLine> lines = billSaleReturnOrderLineMapper.selectList(Wrappers.<BillSaleReturnOrderLine>lambdaQuery()
                .eq(BillSaleReturnOrderLine::getSaleReturnId, request.getSaleReturnId()));

        lines.forEach(t -> {
            BillSaleReturnOrderLine returnOrderLine = new BillSaleReturnOrderLine();
            returnOrderLine.setId(t.getId());
//            returnOrderLine.setSaleReturnLineState(SaleReturnOrderLineStateEnum.UPLOAD_EXPRESS_NUMBER);
            returnOrderLine.setTransitionStateEnum(SaleReturnOrderLineStateEnum.TransitionEnum.UNCONFIRMED_TO_UPLOAD_EXPRESS_NUMBER);
            UpdateByIdCheckState.update(billSaleReturnOrderLineMapper, returnOrderLine);

        });
        BillSaleReturnOrder select = baseMapper.selectById(request.getSaleReturnId());
        return SaleReturnOrderConverter.INSTANCE.convertSaleReturnOrderExpressNumberUploadResult(select);
    }

    @Override
    public BillSaleReturnOrder selectBySriginSerialNo(String serialNo) {
        return baseMapper.selectOne(new LambdaQueryWrapper<BillSaleReturnOrder>().eq(BillSaleReturnOrder::getSerialNo, serialNo));
    }

    @Override
    public void updateExpressNo(String trackingNo, String bizCode) {
        LambdaUpdateWrapper<BillSaleReturnOrder> qw = Wrappers.<BillSaleReturnOrder>lambdaUpdate()
                .eq(BillSaleReturnOrder::getBizOrderCode, bizCode)
                .set(BillSaleReturnOrder::getExpressNumber, trackingNo);
        update(qw);
    }

    @Override
    public void updateRefundFlag(Integer id, SaleOrderReturnFlagEnum refundFlag) {
        LambdaUpdateWrapper<BillSaleReturnOrder> qw = Wrappers.<BillSaleReturnOrder>lambdaUpdate()
                .eq(BillSaleReturnOrder::getId, id)
                .set(BillSaleReturnOrder::getRefundFlag, refundFlag);
        update(qw);
    }

    @Override
    public Integer selectDouYinOrderBySerialNo(String assocSerialNumber) {
        return baseMapper.selectDouYinOrderBySerialNo(assocSerialNumber);
    }

    /**
     * @param t
     * @param whetherCancel
     */
    private void changeSaleLineStatus(BillSaleReturnOrderLine t, Boolean whetherCancel) {
        //变更销售详情状态
        BillSaleOrderLine saleOrderLine = new BillSaleOrderLine();
        saleOrderLine.setId(t.getSaleLineId());
        if (whetherCancel && Objects.equals(t.getSaleLineState(), SaleOrderLineStateEnum.ON_CONSIGNMENT.getValue()))
            saleOrderLine.setTransitionStateEnum(SaleOrderLineStateEnum.TransitionEnum.IN_RETURN_TO_ON_CONSIGNMENT);
        else if (whetherCancel && Objects.equals(t.getSaleLineState(), SaleOrderLineStateEnum.DELIVERED.getValue()))
            saleOrderLine.setTransitionStateEnum(SaleOrderLineStateEnum.TransitionEnum.IN_RETURN_TO_DELIVERED);
        else if (whetherCancel && Objects.equals(t.getSaleLineState(), SaleOrderLineStateEnum.CONSIGNMENT_SETTLED.getValue()))
            saleOrderLine.setTransitionStateEnum(SaleOrderLineStateEnum.TransitionEnum.IN_RETURN_TO_CONSIGNMENT_SETTLED);
        else if (!whetherCancel && Objects.equals(t.getSaleLineState(), SaleOrderLineStateEnum.ON_CONSIGNMENT.getValue()))
            saleOrderLine.setTransitionStateEnum(SaleOrderLineStateEnum.TransitionEnum.ON_CONSIGNMENT_TO_IN_RETURN);
        else if (!whetherCancel && Objects.equals(t.getSaleLineState(), SaleOrderLineStateEnum.DELIVERED.getValue()))
            saleOrderLine.setTransitionStateEnum(SaleOrderLineStateEnum.TransitionEnum.DELIVERED_TO_IN_RETURN);
        else if (!whetherCancel && Objects.equals(t.getSaleLineState(), SaleOrderLineStateEnum.CONSIGNMENT_SETTLED.getValue()))
            saleOrderLine.setTransitionStateEnum(SaleOrderLineStateEnum.TransitionEnum.CONSIGNMENT_SETTLED_TO_IN_RETURN);
        UpdateByIdCheckState.update(billSaleOrderLineMapper, saleOrderLine);
    }
}




