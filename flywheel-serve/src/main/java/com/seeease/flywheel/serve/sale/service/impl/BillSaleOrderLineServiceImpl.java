package com.seeease.flywheel.serve.sale.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.sale.request.SaleOrderBatchSettlementRequest;
import com.seeease.flywheel.sale.request.SaleOrderSettlementListRequest;
import com.seeease.flywheel.sale.result.SaleOrderBatchSettlementResult;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceCmdTypeEnum;
import com.seeease.flywheel.serve.customer.enums.CustomerBalanceTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerBalanceService;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.entity.CustomerBalance;
import com.seeease.flywheel.serve.financial.entity.FinancialGenerateDto;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.GpmConfig;
import com.seeease.flywheel.serve.maindata.mapper.GpmConfigMapper;
import com.seeease.flywheel.serve.sale.entity.*;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderStateEnum;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderMapper;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.utils.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【bill_sale_line】的数据库操作Service实现
 * @createDate 2023-03-06 10:38:19
 */
@Slf4j
@Service
public class BillSaleOrderLineServiceImpl extends ServiceImpl<BillSaleOrderLineMapper, BillSaleOrderLine>
        implements BillSaleOrderLineService {

    @Resource
    private BillSaleOrderMapper billSaleOrderMapper;

    @Resource
    private GpmConfigMapper gpmConfigMapper;
    @Resource
    private StockService stockService;
    @Resource
    private StockMapper stockMapper;
    @Resource
    private FinancialDocumentsService financialDocumentsService;
    @Resource
    protected CustomerBalanceService customerBalanceService;
    @Resource
    private AccountsPayableAccountingService accountingService;

    /**
     * bsol.gmv_performance wei null
     *
     * SELECT bso.serial_no,s.location_id,bsol.gmv_performance FROM bill_sale_order_line bsol
     * LEFT JOIN bill_sale_order bso on bso.id = bsol.sale_id
     * LEFT JOIN stock s on s.id = bsol.stock_id
     * WHERE gmv_performance is NULL
     * AND bsol.created_time > '2024-07-1 00:00:00'
     * and sale_line_state = 5
     *
     * https://pre.seeease.com/feilun/flywheel/workSubmit/shop/logisticsDelivery
     *
     *
     * @param id
     * @return
     */


    @Override
    public List<BillSaleOrderLineDetailsVO> selectBySaleId(Integer id) {
        return this.baseMapper.selectBySaleId(id);
    }

    @Override
    public Page<BillSaleOrderLineSettlementVO> querySettlementList(SaleOrderSettlementListRequest request) {
        return this.baseMapper.querySettlementList(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaleOrderBatchSettlementResult batchSettlement(SaleOrderBatchSettlementRequest request) {
        log.info("batchSettlement function of BillSaleOrderLineServiceImpl start and request = {}", JSON.toJSONString(request));
        //统一查询tob绩效gmv
        Date date = DateUtils.getNowDate();
        GpmConfig toB = gpmConfigMapper.selectOne(new LambdaQueryWrapper<GpmConfig>()
                .eq(GpmConfig::getToTarget, "ToB")
                .ge(GpmConfig::getEndDateTime, date)
                .le(GpmConfig::getStartDateTime, date));

        Set<Integer> saleIds = request.getList().stream()
                .map(SaleOrderBatchSettlementRequest.SaleOrderBatchSettlementLine::getSaleId)
                .collect(Collectors.toSet());
        List<BillSaleOrder> saleOrderList = billSaleOrderMapper.selectBatchIds(saleIds);
        Map<Integer, BillSaleOrder> orderMap = saleOrderList.stream().collect(Collectors.toMap(BillSaleOrder::getId, Function.identity()));

        request.getList().forEach(settlementLine -> {
            // 多做一层校验 企业客户id 和 saleId的 校验
            if (billSaleOrderMapper.queryByIdAndCustomerId(settlementLine.getSaleId(), request.getCustomerId()) == 0)
                throw new BusinessException(ExceptionCode.SALE_ORDER_BILL_NOT_EXIST);

            BillSaleOrderLine line = baseMapper.selectOne(Wrappers.<BillSaleOrderLine>lambdaQuery()
                    .eq(BillSaleOrderLine::getSaleId, settlementLine.getSaleId())
                    .eq(BillSaleOrderLine::getStockId, settlementLine.getStockId()));
            BillSaleOrderLine up = new BillSaleOrderLine();

            // 定位GMV绩效信息临时日志
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement caller = stackTrace[1];
            String className = caller.getClassName();
            String methodName = caller.getMethodName();
            log.info("调用{}类,{}方法,settlementLine参数: {}, line参数: {}, toB参数: {}", className, methodName, JSON.toJSONString(settlementLine), JSON.toJSONString(line), JSON.toJSONString(toB));

            log.info("Calculating GMV Performance for SaleOrderLine [saleId={}, stockId={}] with parameters: settlementLine={}, line={}, toB={}",
                    settlementLine.getSaleId(), settlementLine.getStockId(), JSON.toJSONString(settlementLine), JSON.toJSONString(line), JSON.toJSONString(toB));

            up.setGmvPerformance(Optional.ofNullable(settlementLine.getClinchPrice())
                    .filter(BigDecimalUtil::gtZero)
                    .map(p -> p.subtract(line.getConsignmentPrice()).multiply(new BigDecimal(100)).divide(toB.getGpmTarget(), 2, RoundingMode.HALF_UP))
                    .orElse(BigDecimal.ZERO));

            log.info("Calculated GMV Performance for SaleOrderLine [saleId={}, stockId={}]: {}",
                    settlementLine.getSaleId(), settlementLine.getStockId(), up.getGmvPerformance());

            up.setId(line.getId());
            up.setClinchPrice(settlementLine.getClinchPrice());
            up.setTransitionStateEnum(SaleOrderLineStateEnum.TransitionEnum.ON_CONSIGNMENT_TO_CONSIGNMENT_SETTLED);
            up.setMarginPrice(settlementLine.getClinchPrice().subtract(line.getPreClinchPrice()));
            up.setBalanceDirection(up.getMarginPrice().compareTo(BigDecimal.ZERO) < 0 ? 0 : 1);
            up.setConsignmentSaleFinishTime(new Date());
            up.setConsignmentSettlementOperator(UserContext.getUser().getUserName());

            log.info("Updating SaleOrderLine [id={}] with fields: gmvPerformance={}, clinchPrice={}, transitionStateEnum={}, marginPrice={}, balanceDirection={}, consignmentSaleFinishTime={}, consignmentSettlementOperator={}",
                    up.getId(), up.getGmvPerformance(), up.getClinchPrice(), up.getTransitionStateEnum(), up.getMarginPrice(), up.getBalanceDirection(), up.getConsignmentSaleFinishTime(), up.getConsignmentSettlementOperator());

            UpdateByIdCheckState.update(baseMapper, up);

            log.info("Updated SaleOrderLine [id={}] successfully", up.getId());

            //生成财务单据
            FinancialGenerateDto dto = new FinancialGenerateDto();
            dto.setId(settlementLine.getSaleId());
            dto.setStockList(Lists.newArrayList(settlementLine.getStockId()));
            financialDocumentsService.generateSale(dto);

            //将商品状态从寄售 变为售出
            stockService.updateStockStatus(Lists.newArrayList(settlementLine.getStockId()), StockStatusEnum.TransitionEnum.SALE_CONSIGNMENT_SETTLEMENT);

            //寄售确认售出 寄售保证金扣款
            if (orderMap.containsKey(settlementLine.getSaleId())) {
                log.info("customerBalanceCmd of batchSettlement billSaleOrderLineServiceImpl start and saleId = {}", settlementLine.getSaleId());
                BillSaleOrder saleOrder = orderMap.get(settlementLine.getSaleId());
                List<CustomerBalance> customerBalanceList = customerBalanceService.customerBalanceList(request.getCustomerId(), null);
                BigDecimal consignmentMarginSumLeft = customerBalanceList.stream().filter(Objects::nonNull)
                        .map(CustomerBalance::getConsignmentMargin)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (BigDecimal.ZERO.compareTo(consignmentMarginSumLeft) >= 0) {
                    throw new BusinessException(ExceptionCode.CUSTOMER_ACCOUNT_CONSIGNMENTMARGIN_LEFT_ERR);
                }
                BigDecimal amount = settlementLine.getClinchPrice();
                customerBalanceService.customerBalanceByCreateIdCmd(saleOrder.getCustomerId(), saleOrder.getCustomerContactId(),
                        amount, CustomerBalanceTypeEnum.JS_AMOUNT.getValue(), saleOrder.getShopId(), CustomerBalanceCmdTypeEnum.MINUS.getValue(), saleOrder.getFirstSalesman());

                //同行销售  预收单自动核销
                List<AccountsPayableAccounting> list = accountingService
                        .selectListByOriginSerialNoAndStatusAndType(saleOrder.getSerialNo(),
                                Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW)
                                , Lists.newArrayList(ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT));
                accountingService.batchAudit(list.stream().filter(a -> settlementLine.getStockId().equals(a.getStockId()))
                        .map(AccountsPayableAccounting::getId)
                        .collect(Collectors.toList()), FlywheelConstant.SETTLEMENT_AUDIT, UserContext.getUser().getUserName());
                //商品是同行寄售的，生成应付单
//                accountingService.createApa(saleOrder.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE,
//                        FinancialStatusEnum.PENDING_REVIEW, Lists.newArrayList(settlementLine.getStockId()), saleOrder.getTotalSalePrice(), false);
            }

            //当同行寄售结算时，寄售金额与元签订的【同行寄售价】不符合
            //寄售保证金100000          寄售货值8000（3000+5000）          保证金余额92000
            //1、情况一：例-同行寄售价5000，结算成交价10000（最终成交价＞同行寄售价）
            //结果：寄售保证金90000          寄售货值3000          保证金余额87000
            //原预售单自动核销逻辑不动
            //新增单据（直接到已核销）
            //金额：5000（10000-5000）   订单种类：应收        订单分类：销售        订单类型：同行销售         业务类型：寄售
            //2、情况二：例-同行寄售价5000，结算成交价3000（最终成交价＜同行寄售价）
            //结果：寄售保证金97000          寄售货值3000          保证金余额94000
            //原预售单自动核销逻辑不动
            //新增单据（直接到已核销）
            //金额：2000（5000-3000）   订单种类：预收        订单分类：销售退货        订单类型：同行销售         业务类型：寄售
            if (BigDecimalUtil.gt(settlementLine.getClinchPrice(), line.getPreClinchPrice())) {
                accountingService.createSpecialApa(orderMap.get(settlementLine.getSaleId()), ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE,
                        FinancialStatusEnum.AUDITED, Lists.newArrayList(settlementLine.getStockId()), settlementLine.getClinchPrice().subtract(line.getPreClinchPrice()), true);
            } else if (BigDecimalUtil.lt(settlementLine.getClinchPrice(), line.getPreClinchPrice())) {
                accountingService.createSpecialApa(orderMap.get(settlementLine.getSaleId()), ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT,
                        FinancialStatusEnum.AUDITED, Lists.newArrayList(settlementLine.getStockId()), settlementLine.getClinchPrice().subtract(line.getPreClinchPrice()), false);
            }
        });

        saleOrderList.forEach(billSaleOrder -> {
            BillSaleOrder saleOrder = new BillSaleOrder();
            saleOrder.setId(billSaleOrder.getId());
            saleOrder.setFinishTime(new Date());
            billSaleOrderMapper.updateById(saleOrder);

            Set<Integer> set = new HashSet<>(baseMapper.selectStateBySaleId(billSaleOrder.getId()));
            if (set.size() == 1)
                updateSaleState(billSaleOrder.getId());
        });
        //填充生命周期需要的参数
        List<SaleOrderBatchSettlementResult.SaleOrderBatchSettlementLine> list = new ArrayList<>();
        Map<Integer, Integer> map = request.getList().stream()
                .collect(Collectors.toMap(SaleOrderBatchSettlementRequest.SaleOrderBatchSettlementLine::getStockId,
                        SaleOrderBatchSettlementRequest.SaleOrderBatchSettlementLine::getSaleId));
        Map<Integer, String> saleMap = saleOrderList.stream()
                .collect(Collectors.toMap(BillSaleOrder::getId, BillSaleOrder::getSerialNo));
        map.forEach((stockId, saleId) -> {
            SaleOrderBatchSettlementResult.SaleOrderBatchSettlementLine line = new SaleOrderBatchSettlementResult.SaleOrderBatchSettlementLine();
            line.setStockId(stockId);
            line.setSerialNo(saleMap.get(saleId));
            list.add(line);
        });

        return SaleOrderBatchSettlementResult.builder()
                .list(list)
                .build();
    }

    /**
     * 计算toc gmv 2C实际绩效GMV = 商品实际售价55000 * 实际毛利率 15% / 对应销售时间的2C目标毛利率20%
     * 例如： 55000 * 0.15 / 0.2 = 41520。
     * 实际毛利率 = （实际售价55000-总成本46920）/实际售价55000 * 100%
     * 例如：（55000-46920）/ 55000  *100% = 0.1469 * 100%  ≈ 0.15 * 100%  ≈15%
     * 结果需要四舍五入保留2位计算。
     * 销售订单，每个表都有一个实际绩效gmv
     * “实际绩效GMV”发货后，开始计算
     * 总成本取值时，商家组to b销售卖总部商品取“总成本”计算，其余总成本取值“门店采购价”计算；
     *
     * @param lineDto
     * @param transitionEnum
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLineState(BillSaleOrderLineDto lineDto, SaleOrderLineStateEnum.TransitionEnum transitionEnum) {
        //销售行
        List<Integer> saleLineIdList = Optional.ofNullable(lineDto.getSaleLineIdList())
                .filter(CollectionUtils::isNotEmpty)
                .orElseGet(() -> {
                    Map<Integer, BillSaleOrderLine> lineMap = baseMapper.selectList(Wrappers.<BillSaleOrderLine>lambdaQuery()
                                    .eq(BillSaleOrderLine::getSaleId, lineDto.getSaleId()))
                            .stream()
                            .collect(Collectors.toMap(BillSaleOrderLine::getStockId, Function.identity()));

                    return lineDto.getStockIdList()
                            .stream()
                            .map(t -> {
                                BillSaleOrderLine line = lineMap.get(t);
                                if (Objects.isNull(line)) {
                                    throw new BusinessException(ExceptionCode.SALE_ORDER_BILL_NOT_EXIST);
                                }
                                return line.getId();
                            })
                            .collect(Collectors.toList());
                });

        saleLineIdList.forEach(id -> {
            BillSaleOrderLine up = new BillSaleOrderLine();
            up.setId(id);
            up.setTransitionStateEnum(transitionEnum);
            UpdateByIdCheckState.update(baseMapper, up);
        });

        //更新销售单状态
        this.updateSaleState(lineDto.getSaleId());
    }

    @Override
    public List<BillSaleOrderLineSettlementVO> listStockBySnAndState(List<String> snList, List<Integer> stateList, Integer customerId) {
        return this.baseMapper.listStockBySnAndState(snList, stateList, customerId);
    }

    @Override
    public int countStateBySaleId(Integer id) {
        return baseMapper.countStateBySaleId(id);
    }

    @Override
    public void updateWarrantyPeriod(Integer id) {
        baseMapper.updateWarrantyPeriod(id);
    }

    @Override
    public List<BillSaleOrderLine> selectBySaleIds(List<Integer> saleIds) {
        if (CollectionUtils.isEmpty(saleIds)) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.selectList(new LambdaQueryWrapper<BillSaleOrderLine>()
                .in(BillSaleOrderLine::getSaleId, saleIds));
    }

    @Override
    public void updateWhetherInvoiceBySerialNoListAndStockIdList(List<String> serialNoList, List<Integer> stockIdList, FinancialInvoiceStateEnum stateEnum) {
        baseMapper.updateWhetherInvoiceBySerialNoListAndStockIdList(serialNoList, stockIdList, stateEnum.getValue());
    }

    @Override
    public void updateWhetherInvoiceById(Integer lineId, FinancialInvoiceStateEnum inInvoice) {
        baseMapper.updateWhetherInvoiceById(lineId, inInvoice.getValue());
    }

    @Override
    public void updateState(BillSaleOrderLine billSaleOrderLine) {
        UpdateByIdCheckState.update(baseMapper, billSaleOrderLine);
    }

    /**
     * 更新销售单状态
     *
     * @param saleId
     */
    private void updateSaleState(Integer saleId) {
        /**
         * 统计状态
         */
        List<Integer> stateList = baseMapper.selectStateBySaleId(saleId);
        //转换状态枚举
        List<SaleOrderLineStateEnum> stateEnumList = stateList.stream()
                .map(SaleOrderLineStateEnum::fromValue).collect(Collectors.toList());

        SaleOrderStateEnum stateEnum;
//        当所有的状态都是取消的时候 将销售单状态改成取消
        if (stateEnumList.stream().allMatch(t -> t.getLineDesc().equals(SeeeaseConstant.CANCEL_WHOLE))) {
            stateEnum = SaleOrderStateEnum.CANCEL_WHOLE;
        }
        //所有行状态都是已完成 将销售单改成已完成
        else if (stateEnumList.stream().allMatch(t -> t.getLineDesc().equals(SeeeaseConstant.COMPLETE)
                ||  t.getLineDesc().equals(SeeeaseConstant.CANCEL_WHOLE))) {
            stateEnum = SaleOrderStateEnum.COMPLETE;

            List<BillSaleOrderLine> billSaleOrderLines = selectBySaleIds(Collections.singletonList(saleId));
            billSaleOrderLines.forEach(t->{
                if (t.getStockId() != null){
                    Stock stock = stockMapper.selectById(t.getStockId());
                    if (null != stock.getNewSettlePrice() && !BigDecimal.ZERO.equals(stock.getNewSettlePrice())){
                        t.setNewSettlePrice(stock.getNewSettlePrice());
                    }else {
                        t.setNewSettlePrice(stock.getConsignmentPrice());
                    }
                }
                baseMapper.updateById(t);
            });



        } else if (stateEnumList.stream().allMatch(t -> t.getLineDesc().equals(SeeeaseConstant.UN_CONFIRMED))) {
            stateEnum = SaleOrderStateEnum.UN_CONFIRMED;
        }
        //所有行状态都是待开始 将销售单改成进行中
        else if (stateEnumList.stream().allMatch(t -> t.getLineDesc().equals(SeeeaseConstant.UN_STARTED))) {
            stateEnum = SaleOrderStateEnum.UN_STARTED;
        } else {
            stateEnum = SaleOrderStateEnum.UNDER_WAY;
        }
        BillSaleOrder saleOrder = new BillSaleOrder();
        saleOrder.setId(saleId);
        saleOrder.setSaleState(stateEnum);
        billSaleOrderMapper.updateById(saleOrder);
    }
}




