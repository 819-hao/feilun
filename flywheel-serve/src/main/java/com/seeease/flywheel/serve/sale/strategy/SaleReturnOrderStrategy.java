package com.seeease.flywheel.serve.sale.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleReturnOrderCreateResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.template.Bill;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderTypeEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderService;
import com.seeease.seeeaseframework.mybatis.type.TransactionalUtil;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.exception.e.SeeeaseBaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 销售退货
 */
@Slf4j
public abstract class SaleReturnOrderStrategy implements Bill<SaleReturnOrderCreateRequest, SaleReturnOrderCreateResult> {

    @Resource
    private BillSaleReturnOrderService saleReturnOrderService;
    @Resource
    private BillSaleOrderLineService saleOrderLineService;
    @Resource
    private AccountsPayableAccountingService accountingService;

    @Resource
    protected TransactionalUtil transactionalUtil;
    private static final Set<Integer> SALE_LINE_STATE_LIST = ImmutableSet.of(SaleOrderLineStateEnum.DELIVERED.getValue(),
            SaleOrderLineStateEnum.ON_CONSIGNMENT.getValue(), SaleOrderLineStateEnum.CONSIGNMENT_SETTLED.getValue());

    /**
     * 前置处理
     * 1、参数转换
     * 2、参数填充
     *
     * @param request
     */
    abstract void preRequestProcessing(SaleReturnOrderCreateRequest request);

    /**
     * 业务校验
     * 1、必要参数校验
     * 2、金额校验
     * 3、业务可行性校验
     *
     * @param request
     * @throws BusinessException
     */
    abstract void checkRequest(SaleReturnOrderCreateRequest request) throws BusinessException;

    @Override
    public void preProcessing(SaleReturnOrderCreateRequest request) {
        request.setSaleReturnSource(this.getType().getValue());
        Map<Integer, BillSaleOrderLine> lineMap = saleOrderLineService.listByIds(request.getDetails().stream()
                        .map(SaleReturnOrderCreateRequest.BillSaleReturnOrderLineDto::getSaleLineId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(BillSaleOrderLine::getId, Function.identity()));
        request.getDetails().forEach(dto -> {
            BillSaleOrderLine orderLine = Objects.requireNonNull(lineMap.get(dto.getSaleLineId()));
            dto.setSaleIdCheck(orderLine.getSaleId());
            dto.setStockId(orderLine.getStockId());
            dto.setGoodsId(orderLine.getGoodsId());
            dto.setSaleLineState(orderLine.getSaleLineState().getValue());
            dto.setRightOfManagement(orderLine.getRightOfManagement());
            dto.setWhetherInvoice(orderLine.getWhetherInvoice().getValue());
        });
        this.preRequestProcessing(request);
    }

    @Override
    public void bizCheck(SaleReturnOrderCreateRequest request) throws SeeeaseBaseException {
        Assert.notNull(request.getShopId(), "门店id不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getDetails()), "销售退货详情异常");
        request.getDetails()
                .forEach(t -> {
                    Assert.notNull(t.getSaleId(), "销售单id不能为空");
                    Assert.notNull(t.getSaleLineId(), "销售行id不能为空");
                    Assert.notNull(t.getReturnPrice(), "退货金额不能为空");
                    if (FinancialInvoiceStateEnum.IN_INVOICE.getValue().equals(t.getWhetherInvoice())) {
                        throw new OperationRejectedException(OperationExceptionCode.SALE_ORDER_EXISTS_IN_STATE);
                    }
                    if (!SALE_LINE_STATE_LIST.contains(t.getSaleLineState()))
                        throw new OperationRejectedException(OperationExceptionCode.SALE_ORDER_NOT_ALLOWED_TO_CREATE);
                });
        this.checkRequest(request);
    }

    private static final List<Integer> list = ImmutableList.of(SaleOrderLineStateEnum.CONSIGNMENT_SETTLED.getValue(),SaleOrderLineStateEnum.DELIVERED.getValue());
    @Override
    public SaleReturnOrderCreateResult save(SaleReturnOrderCreateRequest request) {
        return transactionalUtil.transactional(() -> {
            SaleReturnOrderCreateResult result = saleReturnOrderService.create(request);

            // 销售员新建同行销售退货单 自动生成预收单  1、正常销售 2、寄售结算商品退回
            if (SaleReturnOrderTypeEnum.TO_B_JS_TH.getValue().equals(request.getSaleReturnType())) {
                Set<Integer> stockIds = request.getDetails().stream().filter(a -> list.contains(a.getSaleLineState()))
                        .map(SaleReturnOrderCreateRequest.BillSaleReturnOrderLineDto::getStockId)
                        .collect(Collectors.toSet());
                Map<Integer, List<Integer>> map = new HashMap<>();
                for (SaleReturnOrderCreateResult.SaleReturnOrderDto dto : result.getList()) {
                    map.put(dto.getReturnId(), dto.getStockIdList().stream().filter(stockIds::contains).collect(Collectors.toList()));
                }
                accountingService.createSaleApaByReturn(map, ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT, FinancialStatusEnum.PENDING_REVIEW);
            }

            return result;
        });
    }

}
