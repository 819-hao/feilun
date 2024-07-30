package com.seeease.flywheel.serve.financial.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.express.entity.FeilunInvoiceMaycurCancelMessage;
import com.seeease.flywheel.express.entity.FeilunInvoiceMaycurCreateMessage;
import com.seeease.flywheel.express.entity.FeilunInvoiceMaycurUpdateMessage;
import com.seeease.flywheel.financial.IFinancialInvoiceFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.financial.convert.FinancialInvoiceConvert;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoice;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceReverse;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceStock;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.mq.producer.FinancialInvoiceCancelProducers;
import com.seeease.flywheel.serve.financial.mq.producer.FinancialInvoiceCreateProducers;
import com.seeease.flywheel.serve.financial.mq.producer.FinancialInvoiceUpdateProducers;
import com.seeease.flywheel.serve.financial.service.FinancialInvoiceReverseService;
import com.seeease.flywheel.serve.financial.service.FinancialInvoiceService;
import com.seeease.flywheel.serve.financial.service.FinancialInvoiceStockService;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@DubboService(version = "1.0.0")
public class FinancialInvoiceFacade implements IFinancialInvoiceFacade {

    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private FinancialInvoiceService invoiceService;
    @Resource
    private FinancialInvoiceStockService invoiceStockService;
    @Resource
    private BillSaleOrderLineService orderLineService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private FinancialInvoiceCreateProducers invoiceCreateProducers;
    @Resource
    private FinancialInvoiceCancelProducers invoiceCancelProducers;
    @Resource
    private FinancialInvoiceUpdateProducers invoiceUpdateProducers;
    @Resource
    private BillSaleOrderLineService saleOrderLineService;

    @Resource
    private BillSaleOrderService billSaleOrderService;

    private static final Set<FinancialInvoiceOrderTypeEnum> TYPE = ImmutableSet.of(FinancialInvoiceOrderTypeEnum.GR_XS_TH, FinancialInvoiceOrderTypeEnum.TH_XS_TH);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancialInvoiceCreateResult create(FinancialInvoiceCreateRequest request) {
        List<BillSaleOrderLine> billSaleOrderLines = saleOrderLineService.listByIds(request.getLines()
                .stream()
                .map(FinancialInvoiceCreateRequest.LineDto::getLineId)
                .collect(Collectors.toList()));
        if (billSaleOrderLines.stream().anyMatch(a -> !(SaleOrderLineStateEnum.DELIVERED.equals(a.getSaleLineState()) || SaleOrderLineStateEnum.CONSIGNMENT_SETTLED.equals(a.getSaleLineState())))) {
            throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
        }
//        Map<Integer, List<BillSaleOrderLine>> collect = billSaleOrderLines.stream().collect(Collectors.groupingBy(BillSaleOrderLine::getSaleId));

//        if (MapUtils.isNotEmpty(collect)) {
//            List<BillSaleOrder> billSaleOrders = billSaleOrderService.listByIds(new ArrayList<>(collect.keySet()));
//            if (CollectionUtils.isNotEmpty(billSaleOrders) && billSaleOrders.stream().anyMatch(s -> DateUtil.between(DateUtil.parse("2023-10-01 00:00:00"), s.getCreatedTime(), DateUnit.SECOND, false) < 0)) {
//                throw new BusinessException(ExceptionCode.OPT_TIME_SUPPORT);
//            }
//        }

        Integer belongId = request.getLines().stream().findFirst().get().getBelongId();
        FinancialInvoiceCreateRequest.UseScenario requestUseScenario = request.getUseScenario();

        FinancialInvoice invoice = FinancialInvoiceConvert.INSTANCE.convertCreate(request);
        invoice.setSerialNo(SerialNoGenerator.generateFinancialInvoiceSerialNo());
        invoice.setState(FinancialInvoiceStateEnum.PENDING_INVOICED);
        invoice.setInvoiceAmount(request.getLines()
                .stream()
                .map(FinancialInvoiceCreateRequest.LineDto::getClinchPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        invoice.setTotalNumber(request.getLines().size());
        invoice.setInvoiceSubject(belongId);
        invoice.setShopId(UserContext.getUser().getStore().getId());
        invoice.setInvoiceUser(UserContext.getUser().getUserid());
        switch (requestUseScenario) {
            case TH_XS:
                invoice.setOrderType(FinancialInvoiceOrderTypeEnum.TH_XS);
                break;
            case GR_XS:
                invoice.setOrderType(FinancialInvoiceOrderTypeEnum.GR_XS);
                if ((request.getShopId() == FlywheelConstant.SHOP_NN_ED || request.getShopId() == FlywheelConstant.SHOP_NN_YD) && (belongId == 1 || belongId == 22)) {
                    invoice.setInvoiceSubject(request.getShopId() == FlywheelConstant.SHOP_NN_ED ? FlywheelConstant.SUBJECT_NN_ED : FlywheelConstant.SUBJECT_NN_YD);
                }
                break;
            case GR_XS_TH:
                invoice.setOrderType(FinancialInvoiceOrderTypeEnum.GR_XS_TH);
                break;
            case TH_XS_TH:
                invoice.setOrderType(FinancialInvoiceOrderTypeEnum.TH_XS_TH);
                break;
        }
        invoiceService.save(invoice);

        List<FinancialInvoiceStock> invoiceStockList = request.getLines()
                .stream()
                .map(r -> FinancialInvoiceStock.builder()
                        .financialInvoiceId(invoice.getId())
                        .lineId(r.getLineId())
                        .originPrice(r.getClinchPrice())
                        .originSerialNo(r.getSerialNo())
                        .stockId(r.getStockId())
                        .direction(WhetherEnum.NO)
                        .build())
                .collect(Collectors.toList());
        invoiceStockService.saveBatch(invoiceStockList);

        List<BillSaleOrderLine> orderLineList = request.getLines()
                .stream()
                .map(r -> BillSaleOrderLine.builder()
                        .whetherInvoice(FinancialInvoiceStateEnum.IN_INVOICE)
                        .id(r.getLineId())
                        .build())
                .collect(Collectors.toList());
        orderLineService.updateBatchById(orderLineList);

        FeilunInvoiceMaycurCreateMessage message = FeilunInvoiceMaycurCreateMessage.builder()
                .lines(request.getLines()
                        .stream()
                        .map(FinancialInvoiceConvert.INSTANCE::convertLineDto)
                        .collect(Collectors.toList()))
                .serialNo(invoice.getSerialNo())
                .saleChannel(Objects.nonNull(request.getSaleChannel()) ? SaleOrderChannelEnum.fromCode(request.getSaleChannel()).getDesc() : "")
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .invoiceUser(invoice.getInvoiceUser())
                .invoiceOrigin(invoice.getInvoiceOrigin().getValue())
                .invoiceTitle(invoice.getInvoiceTitle())
                .invoiceType(invoice.getInvoiceType().getValue())
                .paymentMethod(request.getPaymentMethod())
                .remarks(request.getRemarks())
                .invoiceSubject(FlywheelConstant.SUBJECT_CODE_MAP.get(invoice.getInvoiceSubject()))
                .unitTaxNumber(invoice.getUnitTaxNumber())
                .address(request.getContactAddress())
                .phone(request.getContactPhone())
                .bank(FlywheelConstant.STRING_DAFULT_VALUE)
                .bankAccount(FlywheelConstant.STRING_DAFULT_VALUE)
                .buyTime(DateUtils.parseStrToDate(request.getCreatedTime()))
                .build();
        invoiceCreateProducers.sendMsg(message);

        return FinancialInvoiceCreateResult
                .builder()
                .id(invoice.getId())
                .serialNo(invoice.getSerialNo())
                .build();
    }

    @Override
    public PageResult<FinancialInvoicePageResult> query(FinancialInvoiceQueryRequest request) {
        Page<FinancialInvoicePageResult> page = invoiceService.queryPage(request);
        return PageResult.<FinancialInvoicePageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FinancialInvoiceUpdateRequest request) {
        FinancialInvoice invoice = invoiceService.getById(request.getId());
        if (FinancialInvoiceStateEnum.REJECTED.equals(invoice.getState()) ||
                FinancialInvoiceStateEnum.CANCELED.equals(invoice.getState())) {
            invoiceService.update(request);

            List<FinancialInvoiceStock> invoiceStockList = invoiceStockService.list(new LambdaQueryWrapper<FinancialInvoiceStock>()
                    .eq(FinancialInvoiceStock::getFinancialInvoiceId, invoice.getId()));

            saleOrderLineService.listByIds(invoiceStockList.stream()
                            .map(FinancialInvoiceStock::getLineId)
                            .collect(Collectors.toList()))
                    .forEach(s -> {
                        if (!(SaleOrderLineStateEnum.DELIVERED.equals(s.getSaleLineState()) || SaleOrderLineStateEnum.CONSIGNMENT_SETTLED.equals(s.getSaleLineState()))) {
                            throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
                        }
                    });

            switch (invoice.getOrderType()) {
                case GR_XS:
                case TH_XS:
                    //将销售详情内状态改成未开票
                    invoiceStockList.forEach(a -> saleOrderLineService.updateWhetherInvoiceById(a.getLineId(), FinancialInvoiceStateEnum.IN_INVOICE));
                    break;
            }

            invoiceUpdateProducers.sendMsg(FeilunInvoiceMaycurUpdateMessage.builder()
                    .invoiceTitle(request.getInvoiceTitle())
                    .unitTaxNumber(request.getUnitTaxNumber())
                    .invoiceOrigin(request.getInvoiceOrigin())
                    .invoiceType(request.getInvoiceType())
                    .serialNo(invoice.getSerialNo())
                    .remarks(request.getRemarks())
                    .paymentMethod(invoice.getPaymentMethod())
                    .build());
        }
    }

    @Override
    public FinancialInvoiceDetailResult detail(FinancialInvoiceDetailRequest request) {
        return invoiceService.detail(request);
    }

    @Override
    public PageResult<FinancialInvoiceRecordPageResult> approvedMemo(FinancialInvoiceRecordRequest request) {
        Page<FinancialInvoiceRecordPageResult> page = invoiceService.approvedMemo(request);
        return PageResult.<FinancialInvoiceRecordPageResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PageResult<FinancialInvoiceQueryByConditionResult> queryByCondition(FinancialInvoiceQueryByConditionRequest request) {

        request.setState(Optional.ofNullable(request.getState())
                .filter(v -> v != -1)
                .orElse(null));

        request.setShopId(Optional.ofNullable(request.getShopId())
                .filter(v -> v != -1)
                .orElse(null));

        request.setInvoiceSubject(Optional.ofNullable(request.getInvoiceSubject())
                .filter(v -> v != -1)
                .orElse(null));

        request.setOrderType(Optional.ofNullable(request.getOrderType())
                .filter(v -> v != -1)
                .orElse(null));

        Page<FinancialInvoiceQueryByConditionResult> page = invoiceService.queryByCondition(request);
        List<FinancialInvoiceQueryByConditionResult> list = page.getRecords();
        if (CollectionUtils.isEmpty(list)) {
            return PageResult.<FinancialInvoiceQueryByConditionResult>builder()
                    .result(Lists.newArrayList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        Map<Integer, String> storeMap = storeManagementService.getStoreMap();
        Map<Integer, String> subjectMap = purchaseSubjectService.list().stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
        list.forEach(r -> {
            r.setShopName(storeMap.getOrDefault(r.getShopId(), "-"));
            r.setInvoiceSubjectName(subjectMap.get(r.getInvoiceSubject()));
            if (Objects.nonNull(r.getInvoiceSubject()) && (r.getInvoiceSubject() == FlywheelConstant.SUBJECT_NN_YD || r.getInvoiceSubject() == FlywheelConstant.SUBJECT_NN_ED)) {
                r.setInvoiceSubjectName("南宁稀蜴");
            }
        });
        return PageResult.<FinancialInvoiceQueryByConditionResult>builder()
                .result(list)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public List<FinancialInvoiceDetailResult.LineDto> export(FinancialInvoiceQueryByConditionRequest request) {
        //导出手选择项
//        if (Objects.isNull(request.getDocBatchIds()) || request.getDocBatchIds().size() == 0) {
        request.setDocBatchIds(null);
        request.setPage(1);
        request.setLimit(9999);
//        }
        request.setState(Optional.ofNullable(request.getState())
                .filter(v -> v != -1)
                .orElse(null));

        request.setShopId(Optional.ofNullable(request.getShopId())
                .filter(v -> v != -1)
                .orElse(null));

        request.setInvoiceSubject(Optional.ofNullable(request.getInvoiceSubject())
                .filter(v -> v != -1)
                .orElse(null));

        request.setOrderType(Optional.ofNullable(request.getOrderType())
                .filter(v -> v != -1)
                .orElse(null));
        Page<FinancialInvoiceQueryByConditionResult> page = invoiceService.queryByCondition(request);

        if (CollectionUtils.isEmpty(page.getRecords())) {
            return Collections.emptyList();
        }

        return page.getRecords().stream().map(r -> {

            FinancialInvoiceDetailResult detail = invoiceService.detail(FinancialInvoiceDetailRequest.builder().id(r.getId()).build());

            if (Objects.nonNull(detail) && CollectionUtils.isNotEmpty(detail.getLines())) {
                detail.getLines().forEach(a -> a.setSerialNo(r.getSerialNo()));

                return detail.getLines();
            }

            return new ArrayList<FinancialInvoiceDetailResult.LineDto>();
        }).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(FinancialInvoiceCancelRequest request) {
        FinancialInvoice invoice = invoiceService.getById(request.getId());
        if (FinancialInvoiceStateEnum.REJECTED.equals(invoice.getState())) {
            FinancialInvoice financialInvoice = new FinancialInvoice();
            financialInvoice.setState(FinancialInvoiceStateEnum.CANCELED);
            financialInvoice.setId(request.getId());
            invoiceService.updateById(financialInvoice);

            List<FinancialInvoiceStock> invoiceStockList = invoiceStockService.list(new LambdaQueryWrapper<FinancialInvoiceStock>()
                    .eq(FinancialInvoiceStock::getFinancialInvoiceId, invoice.getId()));

            switch (invoice.getOrderType()) {
                case GR_XS:
                case TH_XS:
                    //将销售详情内状态改成未开票
                    invoiceStockList.forEach(a -> saleOrderLineService.updateWhetherInvoiceById(a.getLineId(), FinancialInvoiceStateEnum.NO_INVOICED));
                    break;
            }

            invoiceCancelProducers.sendMsg(FeilunInvoiceMaycurCancelMessage.builder().serialNo(invoice.getSerialNo()).build());
        }
    }

    @Override
    public List<PurchaseSubjectResult> queryInvoiceSubject() {
        return purchaseSubjectService.list()
                .stream()
                .filter(a -> FlywheelConstant.SUBJECT_NAME_MAP.containsKey(a.getId()))
                .map(b -> PurchaseSubjectResult.builder().id(b.getId()).name(b.getName()).build())
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<FinancialInvoiceStockInfosResult> stockInfos(FinancialInvoiceStockInfosRequest request) {
        Page<FinancialInvoiceStock> page = invoiceService.stockInfos(request);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<FinancialInvoiceStockInfosResult>builder()
                    .result(Lists.newArrayList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        List<WatchDataFusion> fusionList = goodsWatchService.getWatchDataFusionListByStockIds(page.getRecords().stream()
                .map(FinancialInvoiceStock::getStockId)
                .collect(Collectors.toList()));
        Map<Integer, WatchDataFusion> watchDataFusionMap = fusionList
                .stream().collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity()));

//        Map<Integer, String> map = invoiceService.listByIds(page.getRecords()
//                        .stream()
//                        .map(FinancialInvoiceStock::getForwardFiId)
//                        .filter(Objects::nonNull)
//                        .collect(Collectors.toList()))
//                .stream()
//                .collect(Collectors.toMap(FinancialInvoice::getId, FinancialInvoice::getSerialNo));

        Map<Integer, String> subjectMap = purchaseSubjectService.list().stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
        Map<Integer, String> storeMap = storeManagementService.getStoreMap();

        List<FinancialInvoiceStockInfosResult> list = page.getRecords().stream().map(a -> {
            FinancialInvoiceStockInfosResult dto = FinancialInvoiceStockInfosResult.builder()
                    .financialInvoiceId(a.getFinancialInvoiceId())
                    .stockId(a.getStockId())
                    .forwardFiId(a.getForwardFiId())
                    .originPrice(a.getOriginPrice())
                    .originSerialNo(a.getOriginSerialNo())
                    .build();

            if (Objects.nonNull(a.getForwardFiId())) {
                FinancialInvoice invoice = invoiceService.getById(a.getForwardFiId());
                if (Objects.nonNull(invoice)) {
                    dto.setForwardSerialNo(invoice.getSerialNo());
                }
            }

            WatchDataFusion fusion = watchDataFusionMap.get(dto.getStockId());
            if (Objects.nonNull(fusion)) {
                dto.setModel(fusion.getModel());
                dto.setBrandName(fusion.getBrandName());
                dto.setSeriesName(fusion.getSeriesName());
                dto.setAttachment(fusion.getAttachment());
                dto.setStockSn(fusion.getStockSn());
                dto.setWno(fusion.getWno());
                dto.setImage(fusion.getImage());
                dto.setRightOfManagementName(subjectMap.get(fusion.getRightOfManagement()));
                dto.setLocationName(storeMap.get(fusion.getLocationId()));
            }
            return dto;
        }).collect(Collectors.toList());
        return PageResult.<FinancialInvoiceStockInfosResult>builder()
                .result(list)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancialInvoiceMaycurResult maycurInvoice(FinancialInvoiceMaycurRequest request) {
        FinancialInvoiceMaycurRequest.UseScenario scenario = request.getUseScenario();
        FinancialInvoice invoice = invoiceService.getOne(new LambdaQueryWrapper<FinancialInvoice>()
                .eq(FinancialInvoice::getSerialNo, request.getSerialNo()));
        if (Objects.isNull(invoice)) {
            log.error("onMessage function of {} error for invoice is null", scenario);
        }

        invoice.setResult(MaycurStatusEnum.fromCode(request.getInvoiceStatus()).getDesc() + "--" + request.getRejectReason());

        switch (scenario) {
            case CANCEL:
                invoiceService.maycurInvoice(invoice, FinancialInvoiceStateEnum.CANCELED, FinancialInvoiceStateEnum.NO_INVOICED);
                break;
            case CREATE:
                invoiceService.maycurInvoice(invoice, FinancialInvoiceStateEnum.REJECTED, FinancialInvoiceStateEnum.NO_INVOICED);
                invoiceService.removeById(invoice.getId());
                invoiceStockService.deleteByInvoiceId(invoice.getId());
                break;
            case STATUS_NOTIFY_FAIL:
                invoiceService.maycurInvoice(invoice, FinancialInvoiceStateEnum.REJECTED, FinancialInvoiceStateEnum.IN_INVOICE);
                break;
            case STATUS_NOTIFY_SUCCEED:
                if (MaycurStatusEnum.COMPLETED.equals(MaycurStatusEnum.fromCode(request.getInvoiceStatus()))) {
                    invoice.setBatchPictureUrl(request.getPdfUrl());
                    invoice.setInvoiceTime(request.getOpenTicketTime());
                    invoice.setInvoiceNumber(request.getInvoiceNumber());
                    invoiceService.maycurInvoice(invoice, FinancialInvoiceStateEnum.INVOICE_COMPLETE, FinancialInvoiceStateEnum.INVOICE_COMPLETE);
                } else if (MaycurStatusEnum.REJECTED.equals(MaycurStatusEnum.fromCode(request.getInvoiceStatus()))) {
                    invoiceService.maycurInvoice(invoice, FinancialInvoiceStateEnum.REJECTED, FinancialInvoiceStateEnum.IN_INVOICE);
                }
                break;
            case QRY:
                break;
            case UPDATE:
                invoiceService.updateById(FinancialInvoice.builder()
                        .id(invoice.getId())
                        .invoiceTitle(request.getInvoiceTitle())
                        .unitTaxNumber(request.getUnitTaxNumber())
                        .invoiceOrigin(InvoiceOriginEnum.fromCode(request.getInvoiceOrigin()))
                        .invoiceType(InvoiceTypeEnum.fromCode(request.getInvoiceType()))
                        .build());
                break;
        }
        return FinancialInvoiceMaycurResult.builder()
                .id(invoice.getId())
                .createdId(invoice.getCreatedId())
                .shopId(invoice.getShopId())
                .createdBy(invoice.getCreatedBy())
                .createdTime(invoice.getCreatedTime())
                .serialNo(invoice.getSerialNo())
                .build();
    }

    @Override
    @Transactional
    public void uploadInvoice(FinancialInvoiceUploadInvoiceRequest request) {
        FinancialInvoice invoice = invoiceService.getById(request.getId());
        if (!TYPE.contains(invoice.getOrderType())) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }
        if (!FinancialInvoiceStateEnum.PENDING_INVOICED.equals(invoice.getState())) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }
        FinancialInvoice financialInvoice = new FinancialInvoice();
        financialInvoice.setId(request.getId());
        financialInvoice.setInvoiceTime(new Date());
        financialInvoice.setInvoiceNumber(request.getInvoiceNumber());
        financialInvoice.setBatchPictureUrl(request.getBatchPictureUrl());
        financialInvoice.setState(FinancialInvoiceStateEnum.INVOICE_COMPLETE);
        invoiceService.updateById(financialInvoice);

        if (StringUtils.isNotBlank(invoice.getOriginalInvoiceSerialNo())) {

            FinancialInvoiceReverse invoiceReverse = new FinancialInvoiceReverse();
            invoiceReverse.setState(2);

            financialInvoiceReverseService.update(invoiceReverse, Wrappers.<FinancialInvoiceReverse>lambdaUpdate().eq(FinancialInvoiceReverse::getFiSerialNo, invoice.getOriginalInvoiceSerialNo()));
        }
    }

    @Resource
    private FinancialInvoiceReverseService financialInvoiceReverseService;
}
