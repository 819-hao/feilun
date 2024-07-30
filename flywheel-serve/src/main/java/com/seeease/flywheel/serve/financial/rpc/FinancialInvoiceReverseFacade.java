package com.seeease.flywheel.serve.financial.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.IFinancialInvoiceReverseFacade;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseCancelRequest;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseFlushingRequest;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseQueryByConditionRequest;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseFlushingCancelResult;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseFlushingCreateResult;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseQueryByConditionResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoice;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceReverse;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceStock;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceOrderTypeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.financial.service.FinancialInvoiceReverseService;
import com.seeease.flywheel.serve.financial.service.FinancialInvoiceService;
import com.seeease.flywheel.serve.financial.service.FinancialInvoiceStockService;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@DubboService(version = "1.0.0")
public class FinancialInvoiceReverseFacade implements IFinancialInvoiceReverseFacade {

    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private FinancialInvoiceStockService invoiceStockService;
    @Resource
    private FinancialInvoiceService invoiceService;
    @Resource
    private FinancialInvoiceReverseService invoiceReverseService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;

    @Override
    public PageResult<FinancialInvoiceReverseQueryByConditionResult> queryByCondition(FinancialInvoiceReverseQueryByConditionRequest request) {
        Page<FinancialInvoiceReverseQueryByConditionResult> page = invoiceReverseService.queryByCondition(request);
        List<FinancialInvoiceReverseQueryByConditionResult> list = page.getRecords();
        if (CollectionUtils.isEmpty(list)) {
            return PageResult.<FinancialInvoiceReverseQueryByConditionResult>builder()
                    .result(Lists.newArrayList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        Map<Integer, WatchDataFusion> fusionMap = goodsWatchService.getWatchDataFusionListByStockIds(list.stream().map(FinancialInvoiceReverseQueryByConditionResult::getStockId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getStockId, watchDataFusion -> watchDataFusion));
        Map<Integer, String> storeMap = storeManagementService.getStoreMap();
        Map<Integer, String> subjectMap = purchaseSubjectService.list().stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
        list.forEach(r -> {
            r.setShopName(storeMap.getOrDefault(r.getShopId(), "-"));
            r.setInvoiceSubjectName(subjectMap.get(r.getInvoiceSubject()));
            if (r.getInvoiceSubject() == FlywheelConstant.SUBJECT_NN_YD || r.getInvoiceSubject() == FlywheelConstant.SUBJECT_NN_ED) {
                r.setInvoiceSubjectName("南宁稀蜴");
            }
            r.setBrandName(fusionMap.get(r.getStockId()).getBrandName());
            r.setStockSn(fusionMap.get(r.getStockId()).getStockSn());
            r.setSeriesName(fusionMap.get(r.getStockId()).getSeriesName());
            r.setModel(fusionMap.get(r.getStockId()).getModel());
        });
        return PageResult.<FinancialInvoiceReverseQueryByConditionResult>builder()
                .result(list)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PageResult<FinancialInvoiceReverseQueryByConditionResult> export(FinancialInvoiceReverseQueryByConditionRequest request) {
        //导出手选择项
        if (Objects.isNull(request.getDocBatchIds()) || request.getDocBatchIds().size() == 0) {
            request.setDocBatchIds(null);
        }
        return queryByCondition(request);
    }

    @Override
    @Transactional
    public FinancialInvoiceReverseFlushingCreateResult flushing(FinancialInvoiceReverseFlushingRequest request) {

        //待红冲数据

        List<FinancialInvoiceReverse> reverseList = invoiceReverseService.listByIds(request.getIds());

        //能否多次红冲
        if (CollectionUtils.isEmpty(reverseList) ||
                (reverseList.stream().anyMatch(a -> !a.getState().equals(0)) || reverseList.stream().map(FinancialInvoiceReverse::getFiId).collect(Collectors.toSet()).size() != 1)) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_INVOICE);
        }

        //正向开票的
        FinancialInvoice financialInvoice = invoiceService.getById(reverseList.stream().findFirst().get().getFiId());
        //反向开票数据
        FinancialInvoice invoice = new FinancialInvoice();
        invoice.setSerialNo(SerialNoGenerator.generateFinancialInvoiceSerialNo());
        invoice.setInvoiceAmount(reverseList.stream().map(FinancialInvoiceReverse::getOriginPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        invoice.setTotalNumber(reverseList.size());
        invoice.setInvoiceSubject(financialInvoice.getInvoiceSubject());
        invoice.setCustomerContactsId(financialInvoice.getCustomerContactsId());
        invoice.setCustomerId(financialInvoice.getCustomerId());
        invoice.setCustomerEmail(financialInvoice.getCustomerEmail());
        invoice.setCustomerName(financialInvoice.getCustomerName());
        invoice.setPaymentMethod(financialInvoice.getPaymentMethod());
        invoice.setOrderType(FinancialInvoiceOrderTypeEnum.GR_XS.equals(financialInvoice.getOrderType()) ? FinancialInvoiceOrderTypeEnum.GR_XS_TH : FinancialInvoiceOrderTypeEnum.TH_XS_TH);
        invoice.setInvoiceTitle(financialInvoice.getInvoiceTitle());
        invoice.setUnitTaxNumber(financialInvoice.getUnitTaxNumber());
        invoice.setInvoiceType(financialInvoice.getInvoiceType());
        invoice.setInvoiceOrigin(financialInvoice.getInvoiceOrigin());
        invoice.setState(FinancialInvoiceStateEnum.PENDING_INVOICED);
        invoice.setShopId(financialInvoice.getShopId());
        invoice.setOriginalInvoiceSerialNo(reverseList.stream().findFirst().get().getFiSerialNo());

        invoiceService.save(invoice);

        List<FinancialInvoiceStock> invoiceStockList = reverseList
                .stream()
                .map(r -> FinancialInvoiceStock.builder()
                        .financialInvoiceId(invoice.getId())
                        .lineId(r.getLineId())
                        .originPrice(r.getOriginPrice())
                        .originSerialNo(r.getOriginSerialNo())
                        .stockId(r.getStockId())
                        .forwardFiId(r.getFiId())
                        .direction(WhetherEnum.YES)
                        .build())
                .collect(Collectors.toList());
        invoiceStockService.saveBatch(invoiceStockList);

        invoiceReverseService.updateBatchById(reverseList.stream().map(a -> FinancialInvoiceReverse.builder().id(a.getId())
                .state(WhetherEnum.YES.getValue()).build()).collect(Collectors.toList()));

        return FinancialInvoiceReverseFlushingCreateResult
                .builder()
                .id(invoice.getId())
                .serialNo(invoice.getSerialNo())
                .build();
    }

    @Override
    public FinancialInvoiceReverseFlushingCancelResult cancel(FinancialInvoiceReverseCancelRequest request) {

        //待红冲数据
        List<FinancialInvoiceReverse> financialInvoiceReverseList = invoiceReverseService.listByIds(request.getIds());

        //能否多次红冲
        if (CollectionUtils.isEmpty(financialInvoiceReverseList) ||
                //只有等于1
                (financialInvoiceReverseList.stream().anyMatch(a -> !a.getState().equals(1)) || financialInvoiceReverseList.stream().map(FinancialInvoiceReverse::getFiId).collect(Collectors.toSet()).size() != 1)) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_INVOICE);
        }

        //需要红冲的
        List<FinancialInvoice> financialInvoiceList = invoiceService.list(Wrappers.<FinancialInvoice>lambdaQuery().in(FinancialInvoice::getOriginalInvoiceSerialNo,
                        financialInvoiceReverseList.stream().map(FinancialInvoiceReverse::getFiSerialNo).filter(StringUtils::isNotBlank).collect(Collectors.toList()))
                .orderByDesc(FinancialInvoice::getCreatedTime)
        );

        List<FinancialInvoice> invoiceList = new ArrayList<>();

        for (Map.Entry<String, List<FinancialInvoice>> entry : financialInvoiceList.stream().collect(Collectors.groupingBy(FinancialInvoice::getOriginalInvoiceSerialNo)).entrySet()) {
            invoiceList.add(entry.getValue().stream().findFirst().get());
        }

        if (invoiceList.stream().anyMatch(a -> !Objects.equals(FinancialInvoiceStateEnum.PENDING_INVOICED, a.getState()))) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_INVOICE);
        }

        for (FinancialInvoiceReverse financialInvoiceReverse : financialInvoiceReverseList) {
            FinancialInvoiceReverse invoiceReverse = new FinancialInvoiceReverse();
            invoiceReverse.setState(0);
            invoiceReverse.setId(financialInvoiceReverse.getId());

            invoiceReverseService.updateById(invoiceReverse);
        }

        for (FinancialInvoice financialInvoice : invoiceList) {
            FinancialInvoice invoice = new FinancialInvoice();
            invoice.setState(FinancialInvoiceStateEnum.CANCELED);
            invoice.setId(financialInvoice.getId());

            invoiceService.updateById(invoice);
        }

        return FinancialInvoiceReverseFlushingCancelResult
                .builder()
                .id(invoiceList.stream().findFirst().get().getId())
                .serialNo(invoiceList.stream().findFirst().get().getSerialNo())
                .build();
    }

}
