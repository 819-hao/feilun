package com.seeease.flywheel.serve.anomaly.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.anomaly.IAnomalyFacade;
import com.seeease.flywheel.anomaly.request.AnomalyListRequest;
import com.seeease.flywheel.anomaly.request.AnomalyStockCreateRequest;
import com.seeease.flywheel.anomaly.result.AnomalyListResult;
import com.seeease.flywheel.anomaly.result.AnomalyStockCreateResult;
import com.seeease.flywheel.serve.anomaly.entity.BillAnomaly;
import com.seeease.flywheel.serve.anomaly.enums.AnomalyStateEnum;
import com.seeease.flywheel.serve.anomaly.service.BillAnomalyService;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.enums.QualityTestingConclusionEnum;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.enums.StoreWorkStateEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/7
 */
@DubboService(version = "1.0.0")
public class AnomalyFacade implements IAnomalyFacade {

    @Resource
    private StockService stockService;

    @Resource
    private BillAnomalyService billAnomalyService;

    @Resource
    private BillQualityTestingService billQualityTestingService;

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnomalyStockCreateResult create(AnomalyStockCreateRequest request) {

        List<Stock> stockList = stockService.listByIds(request.getStockIdList());

        stockService.removeUnusualDesc(request.getStockIdList());

        //1。状态 和 数量
        if (stockList.size() != request.getStockIdList().size()) {
            throw new OperationRejectedException(OperationExceptionCode.NUMBER_NO_EXIST);
        }

        if (!stockList.stream().allMatch(stock -> stock.getStockStatus() == StockStatusEnum.EXCEPTION)) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }

        //直接改商品状态
        if (request.getDirect().intValue() == WhetherEnum.YES.getValue()) {

            //都不为空 可售
            List<Integer> collect = stockList.stream().filter(stock ->
                            ObjectUtils.isNotEmpty(stock.getTobPrice()) &&
                            ObjectUtils.isNotEmpty(stock.getTocPrice()) &&
                            stock.getTobPrice().compareTo(BigDecimal.ZERO)>0 &&
                            stock.getTocPrice().compareTo(BigDecimal.ZERO)>0)
                    .map(stock -> stock.getId()).collect(Collectors.toList());

            //差集 待定价
            List<Integer> list = stockList.stream().filter(item -> !collect.contains(item.getId())).map(stock -> stock.getId()).collect(Collectors.toList());

            //可售
            if (CollectionUtils.isNotEmpty(collect)) {
                stockService.updateStockStatus(collect, StockStatusEnum.TransitionEnum.EXCEPTION_MARKETABLE);
            }
            //待定价
            if (CollectionUtils.isNotEmpty(list)) {
                stockService.updateStockStatus(list, StockStatusEnum.TransitionEnum.EXCEPTION_WAIT_PRICING);
            }

            return AnomalyStockCreateResult.builder()
                    .list(request.getStockIdList().stream().map(item -> AnomalyStockCreateResult.AnomalyStockCreateResultDto.builder().stockId(item).build()).collect(Collectors.toList()))
                    .build();
        }

        //走流转流程
        stockService.updateStockStatus(request.getStockIdList(), StockStatusEnum.TransitionEnum.EXCEPTION_EXCEPTION_IN);

        //1.创建异常单

        return AnomalyStockCreateResult.builder()
                .list(getList(request, stockList, billQualityTestingService.list(Wrappers.<BillQualityTesting>lambdaQuery()
                        .in(BillQualityTesting::getStockId, request.getStockIdList())
                        .eq(BillQualityTesting::getQtConclusion, QualityTestingConclusionEnum.ANOMALY)
                        .orderByDesc(BillQualityTesting::getId)).stream().collect(Collectors.groupingBy(BillQualityTesting::getStockId)))
                ).build();
    }

    @NonNull
    private List<AnomalyStockCreateResult.AnomalyStockCreateResultDto> getList(AnomalyStockCreateRequest request, List<Stock> stockList, Map<Integer, List<BillQualityTesting>> map) {

        String serialNo = SerialNoGenerator.generateAnomalySerialNo();

        AtomicInteger offs = new AtomicInteger(1);

        return request.getStockIdList().stream().map(item -> {

            String groupSerialNo = groupSerialNo(serialNo, request.getStockIdList().size(), offs.getAndIncrement());

            BillAnomaly billAnomaly = new BillAnomaly();
            billAnomaly.setAnomalyState(AnomalyStateEnum.OUT_STOCK);
            //新建插入
            List<BillQualityTesting> billQualityTestingList = map.get(item);
            if (map.containsKey(item) && CollectionUtils.isNotEmpty(billQualityTestingList)) {
                billAnomaly.setQtId(billQualityTestingList.get(billQualityTestingList.size() - FlywheelConstant.ONE).getId());
            }

            billAnomaly.setStockId(item);
            billAnomaly.setTaskArriveTime(new Date());
            billAnomaly.setSerialNo(groupSerialNo);

            billAnomalyService.save(billAnomaly);

            //2.创建出库单
            BillStoreWorkPre billStoreWorkPre = new BillStoreWorkPre();
            billStoreWorkPre.setBelongingStoreId(FlywheelConstant._ZB_ID);
            billStoreWorkPre.setWorkType(StoreWorkTypeEnum.OUT_STORE);
            billStoreWorkPre.setWorkSource(BusinessBillTypeEnum.YC_CL);
            billStoreWorkPre.setSerialNo(SerialNoGenerator.generateOutStoreSerialNo());
            billStoreWorkPre.setOriginSerialNo(groupSerialNo);
            billStoreWorkPre.setWorkState(StoreWorkStateEnum.WAIT_FOR_OUT_STORAGE);
            billStoreWorkPre.setStockId(item);
            billStoreWorkPre.setCustomerContactId(0);
            billStoreWorkPre.setCustomerId(0);

            Stock queryStock = stockList.stream().filter(stock -> stock.getId().equals(item)).findAny().get();

            billStoreWorkPre.setGoodsId(queryStock.getGoodsId());
            billStoreWorkPre.setCustomerId(queryStock.getCcId());
            billStoreWorkPreService.save(billStoreWorkPre);
            //关联单据
            BillAnomaly anomaly = new BillAnomaly();

            anomaly.setStoreWorkSerialNo(billStoreWorkPre.getSerialNo());
            anomaly.setWorkId(billStoreWorkPre.getId());
            anomaly.setId(billAnomaly.getId());
            billAnomalyService.updateById(anomaly);

            return AnomalyStockCreateResult.AnomalyStockCreateResultDto.builder()
                    .stockId(item)
                    .serialNo(billStoreWorkPre.getSerialNo())
                    .parentSerialNo(groupSerialNo)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<AnomalyListResult> list(AnomalyListRequest request) {

        return billAnomalyService.list(request);
    }


    /**
     * 单号分组
     *
     * @param serialNo
     * @param size
     * @param offs
     * @return
     */
    private String groupSerialNo(String serialNo, int size, int offs) {
        if (size == NumberUtils.INTEGER_ONE) {
            return serialNo;
        }
        return serialNo + "-" + offs;
    }
}
