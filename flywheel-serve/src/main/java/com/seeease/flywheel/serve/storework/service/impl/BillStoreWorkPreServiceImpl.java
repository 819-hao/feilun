package com.seeease.flywheel.serve.storework.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.rfid.result.RfidOutStoreListResult;
import com.seeease.flywheel.rfid.result.RfidShopReceiveListResult;
import com.seeease.flywheel.sale.request.B3SaleReturnOrderListRequest;
import com.seeease.flywheel.sale.result.B3SaleReturnOrderListResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderMapper;
import com.seeease.flywheel.serve.storework.convert.BillStoreWorkPreConvert;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.entity.LogStoreWorkOpt;
import com.seeease.flywheel.serve.storework.enums.*;
import com.seeease.flywheel.serve.storework.mapper.BillStoreWorkPreMapper;
import com.seeease.flywheel.serve.storework.mapper.LogStoreWorkOptMapper;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.storework.request.*;
import com.seeease.flywheel.storework.result.*;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @description 针对表【bill_store_work_pre(总部入库作业单)】的数据库操作Service实现
 * @createDate 2023-01-17 11:25:23
 */
@Service
public class BillStoreWorkPreServiceImpl extends ServiceImpl<BillStoreWorkPreMapper, BillStoreWorkPre> implements BillStoreWorkPreService {

    @Resource
    private BillSaleReturnOrderMapper billSaleReturnOrderMapper;

    @Resource
    private LogStoreWorkOptMapper logStoreWorkOptMapper;


    /**
     * 创建仓库作业
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StoreWorkCreateResult> create(List<StoreWorKCreateRequest> request) {
        Date time = new Date();
        List<BillStoreWorkPre> billStoreWorkPreList = BillStoreWorkPreConvert.INSTANCE.convert(request);
        billStoreWorkPreList.forEach(t -> {
            t.setCommoditySituation(StoreWorkCommoditySituationEnum.NORMAL);
            t.setTaskArriveTime(time);
            t.setExceptionMark(WhetherEnum.NO);
            //初始状态
            t.setWorkState(StoreWorkStateEnum.getInitState(t.getBelongingStoreId(), t.getWorkSource(), t.getWorkType()).getV1());
            switch (t.getWorkType()) {
                case INT_STORE:
                    t.setSerialNo(SerialNoGenerator.generateInStoreSerialNo());
                    break;
                case OUT_STORE:
                    t.setSerialNo(SerialNoGenerator.generateOutStoreSerialNo());
                    break;
                default:
                    throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
            }
        });
        baseMapper.insertBatchSomeColumn(billStoreWorkPreList);
        return BillStoreWorkPreConvert.INSTANCE.convertStoreWorkWaitReceivingResult(billStoreWorkPreList);
    }

    /**
     * 取消作业单
     *
     * @param originSerialNo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(String originSerialNo) {
        Assert.isTrue(StringUtils.isNotEmpty(originSerialNo), "作业单不能为空");
        List<BillStoreWorkPre> workList = baseMapper.selectList(Wrappers.<BillStoreWorkPre>lambdaQuery().eq(BillStoreWorkPre::getOriginSerialNo, originSerialNo));

        //更新作业单状态
        workList.forEach(t -> {
            BillStoreWorkPre up = new BillStoreWorkPre();
            up.setId(t.getId());
            up.setTransitionStateEnum(StoreWorkStateEnum.getInitState(t.getBelongingStoreId(), t.getWorkSource(), t.getWorkType()).getV2());
            UpdateByIdCheckState.update(baseMapper, up);
        });
    }


    /**
     * 物流收货
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreWorkReceivedListResult receiving(StoreWorkReceivedRequest request) {

        Assert.isTrue(CollectionUtils.isNotEmpty(request.getWorkIds()), "作业单不能为空");

        List<BillStoreWorkPre> billStoreWorkPreList = baseMapper.selectBatchIds(request.getWorkIds());

        StoreWorkLogisticsRejectStateEnum stateEnum = StoreWorkLogisticsRejectStateEnum.fromCode(request.getLogisticsRejectState());
        final StoreWorkOptTypeEnum optTypeEnum;

        switch (stateEnum) {
            case NORMAL:
                optTypeEnum = StoreWorkOptTypeEnum.RECEIPT;

                for (BillStoreWorkPre billStoreWorkPre : billStoreWorkPreList) {

                    StoreWorkStateEnum.TransitionEnum transitionEnum = request.isShopReceived() ? StoreWorkStateEnum.TransitionEnum.SHOP_RECEIVING : StoreWorkStateEnum.TransitionEnum.LOGISTICS_RECEIVING;

                    BillStoreWorkPre up = new BillStoreWorkPre();
                    up.setId(billStoreWorkPre.getId());
                    up.setTransitionStateEnum(transitionEnum);
                    UpdateByIdCheckState.update(baseMapper, up);

                    if (Objects.nonNull(optTypeEnum)) {
                        //新增收货
                        this.insertLog(Arrays.asList(billStoreWorkPre.getId()), transitionEnum, optTypeEnum);
                    }
                }

                break;
            case REJECT:
                //所有的收货操作都不能拒收 个人寄售 个人回购-（置换 仅回收）
                Assert.isTrue(billStoreWorkPreList.stream().allMatch(billStoreWorkPre -> !Arrays.asList(BusinessBillTypeEnum.GR_JS, BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_ZH).contains(billStoreWorkPre.getWorkSource())), "存在不能拒收的商品");

                optTypeEnum = StoreWorkOptTypeEnum.RETURN;

                for (BillStoreWorkPre billStoreWorkPre : billStoreWorkPreList) {

                    StoreWorkStateEnum.TransitionEnum transitionEnum = request.isShopReceived() ?
                            //门店 ：采购，调拨，销售退货 //个人销售退货 变成已退回
                            Arrays.asList(BusinessBillTypeEnum.TO_C_XS_TH).contains(billStoreWorkPre.getWorkSource()) ? StoreWorkStateEnum.TransitionEnum.SHOP_REFUSE_RECEIVING : StoreWorkStateEnum.TransitionEnum.SHOP_RECEIVING_RETURN
                            //总部 ：采购，调拨，销售退货 （）
                            : StoreWorkStateEnum.TransitionEnum.LOGISTICS_REFUSE_RECEIVING;

                    BillStoreWorkPre up = new BillStoreWorkPre();
                    up.setId(billStoreWorkPre.getId());
                    up.setTransitionStateEnum(transitionEnum);
                    UpdateByIdCheckState.update(baseMapper, up);

                    if (Objects.nonNull(optTypeEnum)) {
                        //新增收货
                        this.insertLog(Arrays.asList(billStoreWorkPre.getId()), transitionEnum, optTypeEnum);
                    }
                }

                break;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }

        return StoreWorkReceivedListResult.builder().workIds(request.getWorkIds()).build();
    }

    /**
     * 入库处理
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreWorkInStorageListResult inStorage(StoreWorkInStorageRequest request) {
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getWorkIds()), "作业单不能为空");
        final StoreWorkStateEnum.TransitionEnum transitionEnum = StoreWorkStateEnum.TransitionEnum.IN_STORAGE;
        request.getWorkIds().stream().sorted().forEach(id -> {
            BillStoreWorkPre up = new BillStoreWorkPre();
            up.setId(id);
            up.setTransitionStateEnum(transitionEnum);
            UpdateByIdCheckState.update(baseMapper, up);
        });
        //新增入库日志
        this.insertLog(request.getWorkIds(), transitionEnum, StoreWorkOptTypeEnum.IN_STORAGE);

        return StoreWorkInStorageListResult.builder().workIds(request.getWorkIds()).build();

    }

    /**
     * 出库处理
     *
     * @param request
     * @return
     */
    @Override
    public StoreWorkOutStorageResult outStorage(StoreWorkOutStorageRequest request) {
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getWorkIds()), "作业单不能为空");
        List<BillStoreWorkPre> workPreList = baseMapper.selectBatchIds(request.getWorkIds());
        Assert.isTrue(workPreList.stream().map(BillStoreWorkPre::getWorkSource).distinct().count() == 1, "出库来源不唯一");

        final StoreWorkStateEnum.TransitionEnum transitionEnum;
        final WhetherEnum needQt;
        switch (workPreList.get(0).getWorkSource()) {
            case CG_TH:
                needQt = WhetherEnum.NO;
                transitionEnum = StoreWorkStateEnum.TransitionEnum.OUT_STORAGE_NOT_QT;
                break;
            default:
                needQt = WhetherEnum.YES;
                transitionEnum = StoreWorkStateEnum.TransitionEnum.OUT_STORAGE;
        }
        request.getWorkIds().stream().sorted().forEach(id -> {
            BillStoreWorkPre up = new BillStoreWorkPre();
            up.setId(id);
            up.setTransitionStateEnum(transitionEnum);
            UpdateByIdCheckState.update(baseMapper, up);
        });
        //新增入库日志
        this.insertLog(request.getWorkIds(), transitionEnum, StoreWorkOptTypeEnum.OUT_STORAGE);
        return StoreWorkOutStorageResult.builder().needQt(needQt.getValue()).workIds(request.getWorkIds()).build();
    }

    /**
     * 质检通过
     *
     * @param workId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void qtPassed(Integer workId, WhetherEnum exceptionMark) {
        BillStoreWorkPre work = baseMapper.selectById(workId);
        final StoreWorkStateEnum.TransitionEnum transitionEnum;
        switch (work.getWorkType()) {
            case OUT_STORE:
                //出库被退回收货

                switch (work.getWorkSource()) {
                    case YC_CL:
                        transitionEnum = StoreWorkStateEnum.TransitionEnum.QT_PASSED_OUT_STORAGE_EXCEPTION;
                        break;
                    default:
                        if (StoreWorkStateEnum.RECEIVED.equals(work.getWorkState())) {
                            transitionEnum = StoreWorkStateEnum.TransitionEnum.QT_PASSED_IN_STORAGE;
                        } else {
                            //库存质检异常回到异常库
                            transitionEnum = WhetherEnum.YES.equals(exceptionMark) ? StoreWorkStateEnum.TransitionEnum.QT_PASSED_OUT_STORAGE_EXCEPTION : StoreWorkStateEnum.TransitionEnum.QT_PASSED_OUT_STORAGE;
                        }
                        break;
                }

                break;
            case INT_STORE:
                transitionEnum = StoreWorkStateEnum.TransitionEnum.QT_PASSED_IN_STORAGE;
                break;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
        BillStoreWorkPre up = new BillStoreWorkPre();
        up.setId(Objects.requireNonNull(workId));
        up.setExceptionMark(Objects.requireNonNull(exceptionMark));
        up.setTransitionStateEnum(transitionEnum);
        UpdateByIdCheckState.update(baseMapper, up);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void qtRejectWaitForDelivery(Integer workId) {
        BillStoreWorkPre up = new BillStoreWorkPre();
        up.setId(workId);
        up.setTransitionStateEnum(StoreWorkStateEnum.TransitionEnum.QT_REJECT_WAIT_FOR_DELIVERY);
        UpdateByIdCheckState.update(baseMapper, up);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void qtRejectWaitForDelivery(Integer workId, StoreWorkReturnTypeEnum returnType) {
        BillStoreWorkPre up = new BillStoreWorkPre();
        up.setId(workId);
        up.setTransitionStateEnum(StoreWorkStateEnum.TransitionEnum.QT_REJECT_WAIT_FOR_DELIVERY);
        up.setReturnType(returnType);
        UpdateByIdCheckState.update(baseMapper, up);
    }

    @Override
    public void qtRejectWaitForInStorage(Integer workId) {
        BillStoreWorkPre up = new BillStoreWorkPre();
        up.setId(workId);
        up.setTransitionStateEnum(StoreWorkStateEnum.TransitionEnum.QT_PASSED_IN_STORAGE);
        UpdateByIdCheckState.update(baseMapper, up);
    }


    /**
     * 物流发货
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreWorkDeliveryResult logisticsDelivery(StoreWorkDeliveryRequest request) {
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getWorkIds()), "作业单不能为空");
        Assert.isTrue(request.isBatchDelivery() || StringUtils.isNoneBlank(request.getDeliveryExpressNumber()), "物流单号不能为空");
        String deliveryExpressNumber = request.getDeliveryExpressNumber();
        final StoreWorkStateEnum.TransitionEnum transitionEnum = StoreWorkStateEnum.TransitionEnum.LOGISTICS_DELIVERY;
        request.getWorkIds().stream().sorted().forEach(id -> {
            BillStoreWorkPre up = new BillStoreWorkPre();
            up.setId(id);
            up.setDeliveryExpressNumber(deliveryExpressNumber);
            up.setTransitionStateEnum(transitionEnum);
            UpdateByIdCheckState.update(baseMapper, up);
        });
        //新增收发货日志
        this.insertLog(request.getWorkIds(), transitionEnum, StoreWorkOptTypeEnum.DELIVERY);

        return StoreWorkDeliveryResult.builder().workIds(request.getWorkIds()).build();

    }


    /**
     * 调拨上游发货处理
     *
     * @param originSerialNo
     * @param expressNumber
     * @param stockIdList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upstreamDelivery(String originSerialNo, String expressNumber, List<Integer> stockIdList) {
        Map<Integer, BillStoreWorkPre> shopWorkPreMap = baseMapper.selectList(Wrappers.<BillStoreWorkPre>lambdaQuery().eq(BillStoreWorkPre::getOriginSerialNo, originSerialNo).eq(BillStoreWorkPre::getWorkType, StoreWorkTypeEnum.INT_STORE).in(BillStoreWorkPre::getStockId, stockIdList)).stream().collect(Collectors.toMap(BillStoreWorkPre::getStockId, Function.identity()));

        stockIdList.forEach(t -> {
            BillStoreWorkPre workPre = Objects.requireNonNull(shopWorkPreMap.get(t));
            BillStoreWorkPre up = new BillStoreWorkPre();
            up.setId(workPre.getId());
            up.setExpressNumber(expressNumber);
            up.setTransitionStateEnum(StoreWorkStateEnum.TransitionEnum.UPSTREAM_DELIVERY);
            UpdateByIdCheckState.update(baseMapper, up);
        });
    }

    /**
     * 调拨上游发货取消
     *
     * @param originSerialNo
     * @param stockIdList
     */
    @Override
    public void upstreamDeliveryOfCancel(String originSerialNo, List<Integer> stockIdList) {
        Map<Integer, BillStoreWorkPre> shopWorkPreMap = baseMapper.selectList(Wrappers.<BillStoreWorkPre>lambdaQuery().eq(BillStoreWorkPre::getOriginSerialNo, originSerialNo).eq(BillStoreWorkPre::getWorkType, StoreWorkTypeEnum.INT_STORE).in(BillStoreWorkPre::getStockId, stockIdList)).stream().collect(Collectors.toMap(BillStoreWorkPre::getStockId, Function.identity()));

        stockIdList.forEach(t -> {
            BillStoreWorkPre workPre = Objects.requireNonNull(shopWorkPreMap.get(t));
            BillStoreWorkPre up = new BillStoreWorkPre();
            up.setId(workPre.getId());
            up.setTransitionStateEnum(StoreWorkStateEnum.TransitionEnum.UPSTREAM_DELIVERY_CANCEL);
            UpdateByIdCheckState.update(baseMapper, up);
        });
    }

    /**
     * 调拨下游退回发货
     *
     * @param originSerialNo
     * @param expressNumber
     * @param stockIdList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void downstreamDeliveryOfReturned(String originSerialNo, String expressNumber, List<Integer> stockIdList) {
        Map<Integer, BillStoreWorkPre> shopWorkPreMap = baseMapper.selectList(Wrappers.<BillStoreWorkPre>lambdaQuery().eq(BillStoreWorkPre::getOriginSerialNo, originSerialNo).eq(BillStoreWorkPre::getWorkType, StoreWorkTypeEnum.OUT_STORE).in(BillStoreWorkPre::getStockId, stockIdList)).stream().collect(Collectors.toMap(BillStoreWorkPre::getStockId, Function.identity()));

        stockIdList.forEach(t -> {
            BillStoreWorkPre workPre = Objects.requireNonNull(shopWorkPreMap.get(t));
            BillStoreWorkPre up = new BillStoreWorkPre();
            up.setId(workPre.getId());
            up.setExpressNumber(expressNumber);
            up.setTransitionStateEnum(StoreWorkStateEnum.TransitionEnum.DOWNSTREAM_REFUSE_RECEIVING);
            UpdateByIdCheckState.update(baseMapper, up);
        });
    }

    @Override
    public List<StoreWorkDeliveryQueryResult> storeWorkDeliveryQuery(List<StoreWorkDeliveryQueryRequest> request) {

        if (CollectionUtils.isEmpty(request)) {
            return Arrays.asList();
        }

        return request.stream().map(r -> {
            List<BillStoreWorkPre> list = baseMapper.selectList(Wrappers.<BillStoreWorkPre>lambdaQuery().eq(BillStoreWorkPre::getOriginSerialNo, r.getOriginSerialNo()).eq(BillStoreWorkPre::getStockId, r.getStockId()).orderByDesc(BillStoreWorkPre::getCreatedTime));

            if (CollectionUtils.isNotEmpty(list)) {
                BillStoreWorkPre workPre = list.get(0);
                return StoreWorkDeliveryQueryResult.builder().stockId(r.getStockId()).originSerialNo(r.getOriginSerialNo()).expressNumber(StringUtils.isNotEmpty(workPre.getDeliveryExpressNumber()) ? workPre.getDeliveryExpressNumber() : "").build();
            }
            return StoreWorkDeliveryQueryResult.builder().stockId(r.getStockId()).originSerialNo(r.getOriginSerialNo()).expressNumber("").build();
        }).collect(Collectors.toList());

    }

    @Override
    public List<RfidOutStoreListResult> rfidWaitOutStoreList(Integer shopId, String q, Integer goodsId, List<String> brandNameList) {
        return baseMapper.rfidWaitOutStoreList(shopId, q, goodsId, shopId == FlywheelConstant._ZB_ID ? StoreWorkStateEnum.WAIT_FOR_OUT_STORAGE.getValue() : StoreWorkStateEnum.WAIT_FOR_DELIVERY.getValue(), brandNameList);
    }

    @Override
    public List<RfidShopReceiveListResult> rfidWaitReceiveList(Integer storeId, String q, Integer goodsId) {
        return baseMapper.rfidWaitReceiveList(storeId, q, goodsId);
    }

    @Override
    public Page<B3SaleReturnOrderListResult> b3Page(List<Integer> b3ShopId, B3SaleReturnOrderListRequest request) {
        return baseMapper.b3Page(Page.of(request.getPage(), request.getLimit()), b3ShopId, request);
    }


    /**
     * 新增收发货/出入库日志
     *
     * @param workIds
     * @param transitionEnum
     * @param optTypeEnum
     */
    private void insertLog(List<Integer> workIds, StoreWorkStateEnum.TransitionEnum transitionEnum, StoreWorkOptTypeEnum optTypeEnum) {
        List<LogStoreWorkOpt> logs = baseMapper.selectBatchIds(workIds).stream().map(t -> {
            LogStoreWorkOpt log = BillStoreWorkPreConvert.INSTANCE.convertLogStoreWorkOpt(t);
            log.setId(null);
            log.setFromWorkState(transitionEnum.getFromState());
            log.setToWorkState(transitionEnum.getToState());
            log.setOptType(optTypeEnum);
            final String expressNumber;
            switch (optTypeEnum) {
                case RECEIPT:
                    expressNumber = t.getExpressNumber();
                    break;
                case DELIVERY:
                    expressNumber = t.getDeliveryExpressNumber();
                    break;
                default:
                    expressNumber = StringUtils.EMPTY;
            }
            log.setExpressNumber(expressNumber);
            return log;
        }).collect(Collectors.toList());

        logStoreWorkOptMapper.insertBatchSomeColumn(logs);
    }
}




