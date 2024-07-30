package com.seeease.flywheel.serve.qt.rpc;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.fix.request.FixCreateRequest;
import com.seeease.flywheel.fix.request.QtFixRequest;
import com.seeease.flywheel.fix.result.FixCreateResult;
import com.seeease.flywheel.fix.result.FixProjectResult;
import com.seeease.flywheel.fix.result.QtFixResult;
import com.seeease.flywheel.qt.IQualityTestingFacade;
import com.seeease.flywheel.qt.request.*;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.qt.result.QualityTestingListResult;
import com.seeease.flywheel.qt.result.QualityTestingLogResult;
import com.seeease.flywheel.qt.result.QualityTestingWaitDeliverListResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.fix.convert.FixProjectConverter;
import com.seeease.flywheel.serve.fix.convert.LogFixOptConverter;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.enums.OrderTypeEnum;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.fix.service.FixProjectService;
import com.seeease.flywheel.serve.fix.service.LogFixOptService;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.qt.convert.LogQualityTestingOptConverter;
import com.seeease.flywheel.serve.qt.convert.QualityTestingConverter;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.entity.FixProjectMapper;
import com.seeease.flywheel.serve.qt.entity.LogQualityTestingOpt;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.event.QtDecisionEvent;
import com.seeease.flywheel.serve.qt.event.QtReceiveEvent;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.flywheel.serve.qt.service.LogQualityTestingOptService;
import com.seeease.flywheel.serve.qt.strategy.QtDecisionContext;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/17 14:27
 */
@DubboService(version = "1.0.0")
public class QualityTestingFacade implements IQualityTestingFacade {

    private static final Set<BusinessBillTypeEnum> SCOPE_BUSINESS = ImmutableSet.of(
//            BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_ZH,

            BusinessBillTypeEnum.YC_CL
    );

    private static final List<QualityTestingStateEnum> QT_SELECT_1 = ImmutableList.of(
            QualityTestingStateEnum.NORMAL, QualityTestingStateEnum.ANOMALY, QualityTestingStateEnum.RETURN, QualityTestingStateEnum.FIX,
            QualityTestingStateEnum.RETURN_FIX, QualityTestingStateEnum.RETURN_NEW
    );

    private static final List<QualityTestingStateEnum> QT_SELECT_2 = ImmutableList.of(
            QualityTestingStateEnum.NORMAL, QualityTestingStateEnum.ANOMALY, QualityTestingStateEnum.RETURN, QualityTestingStateEnum.FIX
    );
    private static final List<QualityTestingStateEnum> QT_SELECT_3 = ImmutableList.of(
            QualityTestingStateEnum.CONFIRM_FIX, QualityTestingStateEnum.RETURN
    );
    private static final List<QualityTestingStateEnum> QT_SELECT_4 = ImmutableList.of(
            QualityTestingStateEnum.NORMAL, QualityTestingStateEnum.ANOMALY, QualityTestingStateEnum.FIX
    );
    private static final List<QualityTestingStateEnum> QT_SELECT_5 = ImmutableList.of(
            QualityTestingStateEnum.NORMAL, QualityTestingStateEnum.ANOMALY
    );
    private static final List<QualityTestingStateEnum> QT_SELECT_6 = ImmutableList.of(
            QualityTestingStateEnum.NORMAL, QualityTestingStateEnum.ANOMALY, QualityTestingStateEnum.FIX, QualityTestingStateEnum.RETURN
    );
    private static final List<QualityTestingStateEnum> QT_SELECT_7 = ImmutableList.of(
            QualityTestingStateEnum.FIX
    );

    private static final List<QualityTestingStateEnum> QT_SELECT_8 = ImmutableList.of(
            QualityTestingStateEnum.CONFIRM_FIX, QualityTestingStateEnum.RETURN, QualityTestingStateEnum.NORMAL
    );

    @Resource
    private BillQualityTestingService billQualityTestingService;

    @Resource
    private StockService stockService;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private BillFixService billFixService;

    @Resource
    private QtDecisionContext qtDecisionContext;

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private LogQualityTestingOptService logQualityTestingOptService;

    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;

    @Resource
    private FixProjectService fixProjectService;

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private StockMapper stockMapper;


    @Resource
    private BrandService brandService;

    @Resource
    private LogFixOptService logFixOptService;

    @Override
    public PageResult<QualityTestingListResult> list(QualityTestingListRequest request) {

        request.setQtState(Optional.ofNullable(request.getQtState())
                .filter(v -> v != -1)
                .orElse(null));

        request.setQtSource(Optional.ofNullable(request.getQtSource())
                .filter(v -> v != -1)
                .orElse(null));

        request.setFixOnQt(Optional.ofNullable(request.getFixOnQt())
                .filter(v -> v != -1)
                .orElse(null));

        Page<QualityTestingListResult> billQualityTestingPage = billQualityTestingService.page(request);

        List<QualityTestingListResult> records = billQualityTestingPage.getRecords();

        if (ObjectUtils.isNotEmpty(records)) {

            Map<Integer, WatchDataFusion> collect = goodsWatchService.getWatchDataFusionListByGoodsIds(records.stream().filter(qualityTestingListResult -> ObjectUtils.isNotEmpty(qualityTestingListResult.getGoodsId())).
                    map(qualityTestingListResult -> qualityTestingListResult.getGoodsId()).collect(Collectors.toList())).
                    stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, watchDataFusion -> watchDataFusion));

            billQualityTestingPage.getRecords().forEach(qualityTestingListResult -> {

                WatchDataFusion watchDataFusion = collect.getOrDefault(qualityTestingListResult.getGoodsId(), new WatchDataFusion());
                qualityTestingListResult.setBrandName(watchDataFusion.getBrandName());
                qualityTestingListResult.setSeriesName(watchDataFusion.getSeriesName());
                qualityTestingListResult.setSeriesType(watchDataFusion.getSeriesType());
                qualityTestingListResult.setModel(watchDataFusion.getModel());
                qualityTestingListResult.setImage(watchDataFusion.getImage());
                if (ObjectUtils.isNotEmpty(qualityTestingListResult.getStockId())) {
                    qualityTestingListResult.setWhetherProtect(stockService.selectWhetherProtectById(qualityTestingListResult.getStockId()));
                }
                //下拉可选项 525
                qualityTestingListResult.setSelect(convert(qualityTestingListResult.getStockId(), BusinessBillTypeEnum.fromValue(qualityTestingListResult.getQtSource()), qualityTestingListResult.getFixOnQt()));
            });
        }

        return PageResult.<QualityTestingListResult>builder()
                .result(records)
                .totalCount(billQualityTestingPage.getTotal())
                .totalPage(billQualityTestingPage.getPages())
                .build();
    }

    /**
     * //全部 -1 全部
     * //0 维修
     * //1 物流
     * //2 已转交
     * //3 待转交
     *
     * @param request
     * @return
     */
    @Override
    public PageResult<QualityTestingWaitDeliverListResult> qtWaitDeliver(QualityTestingWaitDeliverListRequest request) {

        request.setDeliver(Optional.ofNullable(request.getDeliver())
                .filter(v -> v != -1)
                .orElse(null));

        Page<QualityTestingWaitDeliverListResult> billQualityTestingPage = billQualityTestingService.page(request);

        List<QualityTestingWaitDeliverListResult> records = billQualityTestingPage.getRecords();

        if (ObjectUtils.isNotEmpty(records)) {

            Map<Integer, WatchDataFusion> collect = goodsWatchService.getWatchDataFusionListByGoodsIds(records.stream().filter(qualityTestingListResult -> ObjectUtils.isNotEmpty(qualityTestingListResult.getGoodsId())).
                    map(qualityTestingListResult -> qualityTestingListResult.getGoodsId()).collect(Collectors.toList())).
                    stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, watchDataFusion -> watchDataFusion));

            billQualityTestingPage.getRecords().forEach(qualityTestingListResult -> {

                WatchDataFusion watchDataFusion = collect.getOrDefault(qualityTestingListResult.getGoodsId(), new WatchDataFusion());
                qualityTestingListResult.setBrandName(watchDataFusion.getBrandName());
                qualityTestingListResult.setSeriesName(watchDataFusion.getSeriesName());
                qualityTestingListResult.setModel(watchDataFusion.getModel());

            });
        }

        return PageResult.<QualityTestingWaitDeliverListResult>builder()
                .result(records)
                .totalCount(billQualityTestingPage.getTotal())
                .totalPage(billQualityTestingPage.getPages())
                .build();
    }

    @Override
    public void edit(QualityTestingEditRequest request) {

        BillQualityTesting billQualityTesting = Optional.ofNullable(billQualityTestingService.getOne(
                Wrappers.<BillQualityTesting>lambdaQuery().eq(BillQualityTesting::getId, request.getQualityTestingId()))
        ).
                orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.QT_BILL_NOT_EXIST));

        switch (billQualityTesting.getQtState()) {

            case RECEIVE:
                request.setStockId(billQualityTesting.getStockId());

                billQualityTestingService.updateById(QualityTestingConverter.INSTANCE.convert(request));

                //同步表身上
                Stock stock = new Stock();
                stock.setId(billQualityTesting.getStockId());
                stock.setWeek(request.getWeek());
                stock.setWatchSection(request.getWatchSection());
                stock.setFiness(request.getFiness());
                stock.setStrapMaterial(request.getStrapMaterial());
                stock.setWhetherProtect(request.getWhetherProtect());
                stockService.updateById(stock);
                break;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QualityTestingDecisionListResult decision(QualityTestingDecisionRequest request) {

        QualityTestingDecisionListResult result = qtDecisionContext.decision(request);

        return result;
    }

    @Override
    public PageResult<QualityTestingLogResult> logList(QualityTestingLogRequest request) {

        Page<QualityTestingLogResult> logResultPage = logQualityTestingOptService.page(request);

        List<QualityTestingLogResult> records = logResultPage.getRecords();

        List<FixProjectResult> projectList = Optional.ofNullable(fixProjectService.list(null))
                .orElse(Lists.newArrayList())
                .stream()
                .map(FixProjectConverter.INSTANCE::convertFixProjectResult)
                .collect(Collectors.toList());

        if (ObjectUtils.isNotEmpty(records)) {
            Map<Integer, WatchDataFusion> collect = goodsWatchService.getWatchDataFusionListByGoodsIds(records.stream().filter(qualityTestingListResult -> ObjectUtils.isNotEmpty(qualityTestingListResult.getGoodsId())).
                    map(qualityTestingListResult -> qualityTestingListResult.getGoodsId()).collect(Collectors.toList())).
                    stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, watchDataFusion -> watchDataFusion));

            logResultPage.getRecords().forEach(qualityTestingListResult -> {
                WatchDataFusion watchDataFusion = collect.getOrDefault(qualityTestingListResult.getGoodsId(), new WatchDataFusion());
                qualityTestingListResult.setBrandName(watchDataFusion.getBrandName());
                qualityTestingListResult.setSeriesName(watchDataFusion.getSeriesName());
                qualityTestingListResult.setModel(watchDataFusion.getModel());

                String convert = convertByFix(projectList, JSON.parseArray(qualityTestingListResult.getContent(), FixProjectMapper.class), qualityTestingListResult.getFixDay(), qualityTestingListResult.getFixMoney());
                qualityTestingListResult.setContent(convert);
            });
        }

        return PageResult.<QualityTestingLogResult>builder()
                .result(records)
                .totalCount(logResultPage.getTotal())
                .totalPage(logResultPage.getPages())
                .build();
    }

    private String convertByFix(List<FixProjectResult> dataList, List<FixProjectMapper> itemList, Integer fixDay, BigDecimal fixMoney) {

        if (CollectionUtils.isEmpty(dataList) || CollectionUtils.isEmpty(itemList)) {
            return StringUtils.EMPTY;
        }

        List<String> list = new ArrayList<>();

        for (FixProjectMapper fixProjectMapper : itemList) {

            FixProjectResult project = dataList.stream().filter(result -> fixProjectMapper.getFixProjectId().equals(result.getId())).findAny().orElse(null);

            if (ObjectUtils.isNotEmpty(project)) {
                list.add(project.getName());
            }
        }

        return StringUtils.join(Arrays.asList(StringUtils.join(list, ","), "¥" + fixMoney, fixDay + ("天")), "/");
    }

    /**
     * 不自动定价的库存来源类型
     */
    private static final List<Integer> NOT_AUTO_PRICE_SRC = Lists.newArrayList(BusinessBillTypeEnum.GR_HS_JHS.getValue()
            , BusinessBillTypeEnum.GR_HS_ZH.getValue()
            , BusinessBillTypeEnum.GR_HG_JHS.getValue()
            , BusinessBillTypeEnum.GR_HG_ZH.getValue());

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<QualityTestingDecisionListResult> batchPass(List<Integer> list) {

        List<LogQualityTestingOpt> coll = new ArrayList<>();

        List<QualityTestingDecisionListResult> resultList = new ArrayList<>();

        list.forEach(item -> {

            BillQualityTesting qualityTesting = billQualityTestingService.getById(item);

            //个人回购 不能通过 必须走维修
            if (SCOPE_BUSINESS.contains(qualityTesting.getQtSource()) && ObjectUtils.isEmpty(qualityTesting.getFixId())) {
                throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_PARAMETER);
            }

            BillQualityTesting billQualityTesting = new BillQualityTesting();
            billQualityTesting.setId(item);
            billQualityTesting.setTransitionStateEnum(QualityTestingStateEnum.TransitionEnum.RECEIVE_NORMAL_DELIVERY);
            billQualityTesting.setQtConclusion(QualityTestingConclusionEnum.CREATE);

            billQualityTestingService.decision(billQualityTesting);

            QualityTestingDecisionListResult build = QualityTestingDecisionListResult.builder()
                    .fixOnQt(0)
                    .serialNo(qualityTesting.getSerialNo())
                    .stockId(qualityTesting.getStockId())
                    .originSerialNo(qualityTesting.getOriginSerialNo())
                    .storeWorkSerialNo(qualityTesting.getStoreWorkSerialNo())
                    .workSource(qualityTesting.getQtSource().getValue())
                    .fixSerialNo(ObjectUtils.isEmpty(qualityTesting.getFixId()) ? null : String.valueOf(qualityTesting.getFixId()))
                    .autoPrice(billQualityTestingService.autoPrice(qualityTesting.getStockId()))
                    .build();

            resultList.add(build);

            billHandlerEventPublisher.publishEvent(new QtDecisionEvent(qualityTesting.getStockId(), QualityTestingStateEnum.NORMAL
                    , qualityTesting.getOriginSerialNo(), BigDecimal.ZERO, null, qualityTesting.getQtSource(), item, null));

            billStoreWorkPreService.qtPassed(qualityTesting.getWorkId(), WhetherEnum.NO);

            LogQualityTestingOpt convert = LogQualityTestingOptConverter.INSTANCE.convert(qualityTesting);
            convert.setQtState(QualityTestingStateEnum.TransitionEnum.RECEIVE_NORMAL_DELIVERY.getToState());
            convert.setQtConclusion(QualityTestingConclusionEnum.CREATE);
            coll.add(convert);
        });

        logQualityTestingOptService.saveBatch(coll);

        return resultList;
    }

    /**
     * 质检转交
     *
     * @param qualityTestingId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public QualityTestingDecisionListResult receive(Integer qualityTestingId) {

        BillQualityTesting qualityTesting = Optional.ofNullable(billQualityTestingService.getById(qualityTestingId)).
                orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER));
        QualityTestingDecisionListResult.QualityTestingDecisionListResultBuilder builder = QualityTestingDecisionListResult.builder();
        //是否拒绝维修
        String fixSerialNo = null;
        switch (qualityTesting.getDeliverTo()) {
            case 0:

                //生成维修单
                Integer fixId = 0;

                //有维修单 更改维修单
                if (ObjectUtils.isNotEmpty(qualityTesting) && ObjectUtils.isNotEmpty(qualityTesting.getFixId())) {

                    QtFixRequest qtFixRequest = new QtFixRequest();

                    qtFixRequest.setFixId(qualityTesting.getFixId());
                    qtFixRequest.setFixAdvise("");

                    QtFixResult qtFixResult = billFixService.qt(qtFixRequest);

                    fixId = qtFixResult.getId();
                    fixSerialNo = qtFixResult.getSerialNo();

                } else {
                    //无维修单 创建维修单
                    FixCreateRequest fixCreateRequest = new FixCreateRequest();

                    fixCreateRequest.setFixAdvise("");
                    fixCreateRequest.setFixSource(qualityTesting.getQtSource().getValue());
                    fixCreateRequest.setCustomerId(qualityTesting.getCustomerId());
                    fixCreateRequest.setCustomerContactId(qualityTesting.getCustomerContactId());
                    fixCreateRequest.setOriginSerialNo(qualityTesting.getOriginSerialNo());
                    fixCreateRequest.setStoreWorkSerialNo(qualityTesting.getStoreWorkSerialNo());
                    fixCreateRequest.setStockId(qualityTesting.getStockId());

                    Stock stock = stockMapper.selectById(qualityTesting.getStockId());

                    //新数据
                    fixCreateRequest.setCustomerName(SeeeaseConstant.XY_WY_ZB);
                    fixCreateRequest.setCustomerPhone(SeeeaseConstant.XY_WY_ZB);
                    fixCreateRequest.setCustomerAddress(SeeeaseConstant.XY_WY_ZB_FIX);
                    fixCreateRequest.setOrderType(OrderTypeEnum.UNDEFINED.getValue());
                    fixCreateRequest.setStockSn(Objects.nonNull(stock) ? stock.getSn() : null);
                    fixCreateRequest.setStoreId(UserContext.getUser().getStore().getId());
                    fixCreateRequest.setBrandId(Optional.ofNullable(stock).map(s -> goodsWatchService.getById(stock.getGoodsId())).map(GoodsWatch::getBrandId).orElse(null));
                    fixCreateRequest.setStrapMaterial(Objects.nonNull(stock) ? stock.getStrapMaterial() : null);
                    fixCreateRequest.setWatchSection(Objects.nonNull(stock) ? stock.getWatchSection() : null);
                    if (Objects.nonNull(fixCreateRequest.getBrandId())) {
                        Brand brand = brandService.getOne(new LambdaQueryWrapper<Brand>().eq(Brand::getId, fixCreateRequest.getBrandId()));
                        fixCreateRequest.setBrandName(Objects.nonNull(brand) ? brand.getName() : null);
                    }
                    fixCreateRequest.setWatchSection(Objects.nonNull(stock) ? stock.getWatchSection() : null);

                    FixCreateResult fixCreateResult = billFixService.create(fixCreateRequest);
                    fixId = fixCreateResult.getId();
                    fixSerialNo = fixCreateResult.getSerialNo();

                    builder.isRepair(1);
                    builder.isAllot(1);
                }

                //更新一下质检单的维修id
                BillQualityTesting qt = new BillQualityTesting();
                qt.setId(qualityTesting.getId());
                qt.setFixId(fixId);

                billQualityTestingService.updateById(qt);

                LogQualityTestingOpt convert = LogQualityTestingOptConverter.INSTANCE.convert(qualityTesting);

                convert.setQtState(QualityTestingStateEnum.TransitionEnum.RECEIVE_FIX_DELIVERY.getToState());
                convert.setQtConclusion(QualityTestingConclusionEnum.FIX);

                logQualityTestingOptService.save(convert);

                BillFix billFix11 = billFixService.getById(fixId);

                logFixOptService.save(LogFixOptConverter.INSTANCE.convert(billFix11));

                break;

            case 1:
                //待发货
                billStoreWorkPreService.qtRejectWaitForDelivery(qualityTesting.getWorkId());

                LogQualityTestingOpt logQualityTestingOpt = LogQualityTestingOptConverter.INSTANCE.convert(qualityTesting);

                logQualityTestingOpt.setQtState(QualityTestingStateEnum.TransitionEnum.CONFIRM_FIX_OK.getToState());
                logQualityTestingOpt.setQtConclusion(QualityTestingConclusionEnum.RETURN);

                logQualityTestingOptService.save(logQualityTestingOpt);
                break;

            default:
                throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }

        BillQualityTesting billQualityTesting = new BillQualityTesting();

        billQualityTesting.setId(qualityTestingId);
        billQualityTesting.setDeliver(1);
        billQualityTestingService.updateById(billQualityTesting);

        billHandlerEventPublisher.publishEvent(new QtReceiveEvent(qualityTesting.getStockId(), qualityTesting.getOriginSerialNo()));

        return builder.fixSerialNo(fixSerialNo)
                .fixOnQt(0)
                .deliverTo(qualityTesting.getDeliverTo())
                .serialNo(qualityTesting.getSerialNo())
                .stockId(qualityTesting.getStockId())
                .build();
    }

    /**
     * 可选下拉选项
     *
     * @param stockId
     * @param businessBillTypeEnum
     * @param fixOnQt
     * @return
     */
    private List<QualityTestingListResult.SelectItem> convert(Integer stockId, BusinessBillTypeEnum businessBillTypeEnum, Integer fixOnQt) {
        List<QualityTestingListResult.SelectItem> list = new ArrayList<>();
//        Boolean selectApply = Boolean.FALSE;
//        if (SELECT_APPLY.contains(businessBillTypeEnum)) {
//            BillPurchaseLine billPurchaseLine = billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, stockId));
//            if (ObjectUtils.isEmpty(billPurchaseLine)) {
//                return Arrays.asList();
//            }
//            if (ObjectUtils.isNotEmpty(billPurchaseLine.getOriginApplyPurchaseId())) {
//                //采购需求来的
//                selectApply = Boolean.TRUE;
//            }
//        }
        switch (fixOnQt) {
            //首次质检
            case 0:
                switch (businessBillTypeEnum) {
                    //订金采购
                    case TH_CG_DJ:
                    case TH_CG_QK:
                    case TH_CG_DJTP:
//                        //需求过来
//                        if (selectApply) {
//                            list = QT_SELECT_1.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
//                        } else {
//                            list = Arrays.asList();
//                        }
//                        break;
                    case TH_CG_BH:
//                        //需求过来
//                        if (selectApply) {
//                            list = QT_SELECT_1.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
//                        } else {
//                            list = QT_SELECT_2.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
//                        }
//                        break;
                        list = QT_SELECT_1.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                    case TH_CG_PL:
                    case TH_JS:
                        list = QT_SELECT_2.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                    case GR_JS:
                        list = QT_SELECT_8.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                    case GR_HG_JHS:
                    case GR_HG_ZH:
//                        list = QT_SELECT_3.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
//                        break;
                    case GR_HS_JHS:
                    case GR_HS_ZH:
                        list = QT_SELECT_2.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                    case MD_DB_ZB:
                        list = QT_SELECT_4.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                    case ZB_DB:
                    case TO_C_XS:
                    case TO_B_XS:
                    case TO_B_JS:
                    case TO_C_ON_LINE:
                        list = QT_SELECT_5.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                    case TO_C_XS_TH:
                    case TO_B_XS_TH:
                    case TO_C_ON_LINE_TH:
                        list = QT_SELECT_6.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                    case YC_CL:
                        list = QT_SELECT_7.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                }
                break;
            //非首次质检
            case 1:
                switch (businessBillTypeEnum) {
                    case TH_CG_DJ:
                    case TH_CG_BH:
                        list = QT_SELECT_1.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                    case TH_CG_PL:
                    case TH_CG_QK:
                    case TH_CG_DJTP:
                    case TH_JS:
                    case GR_JS:
                    case GR_HS_JHS:
                    case GR_HS_ZH:
                    case GR_HG_ZH:
                    case GR_HG_JHS:
                    case ZB_DB:
                    case MD_DB_ZB:
                    case TO_C_XS:
                    case TO_B_JS:
                    case TO_B_XS:
                    case TO_C_ON_LINE:
                    case TO_B_XS_TH:
                    case TO_C_XS_TH:
                    case TO_C_ON_LINE_TH:
                    case YC_CL:
                        list = QT_SELECT_4.stream().map(qualityTestingStateEnum -> QualityTestingListResult.SelectItem.builder().key(qualityTestingStateEnum.getValue()).name(qualityTestingStateEnum.getDesc()).build()).collect(Collectors.toList());
                        break;
                }
        }
        return list;
    }

}
