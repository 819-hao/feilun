package com.seeease.flywheel.serve.stocktaking.rpc;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockManageShelvesInfo;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockManageShelvesInfoService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.flywheel.serve.maindata.service.StoreService;
import com.seeease.flywheel.serve.stocktaking.convert.StocktakingConverter;
import com.seeease.flywheel.serve.stocktaking.entity.BillStocktaking;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingLineStateEnum;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingStateEnum;
import com.seeease.flywheel.serve.stocktaking.service.BillStocktakingLineService;
import com.seeease.flywheel.serve.stocktaking.service.BillStocktakingService;
import com.seeease.flywheel.stocktaking.IStocktakingFacade;
import com.seeease.flywheel.stocktaking.request.StocktakingDetailsRequest;
import com.seeease.flywheel.stocktaking.request.StocktakingListRequest;
import com.seeease.flywheel.stocktaking.request.StocktakingSubmitRequest;
import com.seeease.flywheel.stocktaking.result.*;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/6/15
 */
@DubboService(version = "1.0.0")
public class StocktakingFacade implements IStocktakingFacade {
    @NacosValue(value = "${saleOrder.ipRoleName}", autoRefreshed = true)
    private List<String> IP_ROLE_NAMES;
    @NacosValue(value = "${saleOrder.ipShopId}", autoRefreshed = true)
    private List<Integer> IP_SHOP_ID;

    @Resource
    private StoreService storeService;
    @Resource
    private StockService stockService;
    @Resource
    private BillStocktakingService billStocktakingService;
    @Resource
    private BillStocktakingLineService billStocktakingLineService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private StockManageShelvesInfoService stockManageShelvesInfoService;

    @Override
    public StocktakingStoreListResult storeList() {
        return StocktakingStoreListResult.builder()
                .storeList(Optional.ofNullable(UserContext.getUser().getRoles())
                        .filter(t -> t.stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains))
                        .map(t -> IP_SHOP_ID)
                        .orElseGet(() -> Lists.newArrayList(UserContext.getUser().getStore().getId()))
                        .stream()
                        .map(sid -> Optional.ofNullable(sid)
                                .map(storeService::selectByShopId)
                                .map(t -> StocktakingStoreListResult.StocktakingStoreDTO.builder()
                                        .storeId(t.getId())
                                        .storeName(t.getStoreName())
                                        .shopId(sid)
                                        .build())
                                .orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public StocktakingStockListResult stockList(Integer storeId, String brand, String model) {
        if (Objects.isNull(storeId)) {
            return StocktakingStockListResult.builder().build();
        }

        List<Stock> stockList = stockService.list(storeId, brand, model);

        Map<Integer, String> stockManageShelvesInfoMap = stockManageShelvesInfoService.list(Wrappers.<StockManageShelvesInfo>lambdaQuery()
                        .in(StockManageShelvesInfo::getGoodsId, stockList.stream().map(Stock::getGoodsId).collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(StockManageShelvesInfo::getGoodsId, StockManageShelvesInfo::getShelvesSimplifiedCode));


        return StocktakingStockListResult.builder()
                .stockList(stockList.stream()
                        .map(t -> StocktakingStockListResult.StocktakingStockDTO.builder()
                                .stockId(t.getId())
                                .stockSn(t.getSn())
                                .wno(t.getWno())
                                .shelvesSimplifiedCode(stockManageShelvesInfoMap.getOrDefault(t.getGoodsId(), StringUtils.EMPTY))
                                .build())
                        .sorted(Comparator.comparing(StocktakingStockListResult.StocktakingStockDTO::getShelvesSimplifiedCode))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public StocktakingSubmitResult stocktakingSubmit(StocktakingSubmitRequest request) {

        //生存盘点单号
        request.setSerialNo(SerialNoGenerator.generateStocktakingSerialNo());

        //提交盘点
        billStocktakingService.submit(request);

        return StocktakingSubmitResult
                .builder()
                .serialNo(request.getSerialNo())
                .build();
    }

    @Override
    public PageResult<StocktakingListResult> list(StocktakingListRequest request) {
        //登陆人门店
        Integer shopId = UserContext.getUser().getStore().getId();

        LambdaQueryWrapper<BillStocktaking> wrapper = Wrappers.<BillStocktaking>lambdaQuery()
                //时间范围
                .between(StringUtils.isNotEmpty(request.getStartTime()) && StringUtils.isNotEmpty(request.getEndTime()),
                        BillStocktaking::getCreatedTime, request.getStartTime(), request.getEndTime())
                //选择仓库
                .eq(Objects.nonNull(request.getStoreId()) && request.getStoreId() != -1, BillStocktaking::getStoreId, request.getStoreId())
                //盘点单号
                .like(StringUtils.isNotEmpty(request.getSerialNo()), BillStocktaking::getSerialNo, request.getSerialNo())
                //创建人
                .like(StringUtils.isNotEmpty(request.getCreatedBy()), BillStocktaking::getCreatedBy, request.getCreatedBy());
        //数据隔离
        if (shopId != FlywheelConstant._ZB_ID) {
            wrapper = wrapper.eq(BillStocktaking::getStoreId, storeService.selectByShopId(shopId).getId());
        }
        //盘点状态
        if (Objects.nonNull(request.getStocktakingState()) && request.getStocktakingState() != -1) {
            wrapper = wrapper.eq(BillStocktaking::getStocktakingState, StocktakingStateEnum.fromCode(request.getStocktakingState()));
        }
        //倒序
        wrapper.orderByDesc(BillStocktaking::getCreatedTime);


        Page<BillStocktaking> pageResult = billStocktakingService.page(Page.of(request.getPage(), request.getLimit()), wrapper);

        //仓库名字
        Map<Integer, String> storeMap = Optional.ofNullable(pageResult.getRecords())
                .filter(CollectionUtils::isNotEmpty)
                .map(t -> storeService.listByIds(t.stream()
                                .map(BillStocktaking::getStoreId)
                                .distinct()
                                .collect(Collectors.toList()))
                        .stream()
                        .collect(Collectors.toMap(Store::getId, Store::getStoreName)))
                .orElse(Collections.EMPTY_MAP);

        return PageResult.<StocktakingListResult>builder()
                .result(pageResult.getRecords()
                        .stream()
                        .map(t -> {
                            StocktakingListResult result = StocktakingConverter.INSTANCE.convertStocktakingListResult(t);
                            result.setStoreName(storeMap.get(t.getStoreId()));
                            return result;
                        })

                        .collect(Collectors.toList()))
                .totalCount(pageResult.getTotal())
                .totalPage(pageResult.getPages())
                .build();
    }

    @Override
    public PageResult<StocktakingDetailsResult> details(StocktakingDetailsRequest request) {

        BillStocktaking billStocktaking = billStocktakingService.getById(request.getId());
        if (Objects.isNull(billStocktaking)) {
            throw new OperationRejectedException(OperationExceptionCode.STOCKTAKING_BILL_NOT_EXIST);
        }

        Page<StocktakingDetailsResult> page = billStocktakingLineService.pageOf(request);

        return PageResult.<StocktakingDetailsResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();

    }

    @Override
    public StocktakingDetailStatisticsResult detailsStatistics(StocktakingDetailsRequest request) {
        BillStocktaking billStocktaking = billStocktakingService.getById(request.getId());
        if (Objects.isNull(billStocktaking)) {
            throw new OperationRejectedException(OperationExceptionCode.STOCKTAKING_BILL_NOT_EXIST);
        }
        List<Integer> status = billStocktakingLineService.listStatus(request);
        Map<Integer, List<Integer>> statusMap = status.stream().collect(Collectors.groupingBy(v -> v));
        int lq = statusMap.getOrDefault(StocktakingLineStateEnum.LOSS.getValue(), Collections.emptyList()).size();
        int pq = statusMap.getOrDefault(StocktakingLineStateEnum.PROFIT.getValue(), Collections.emptyList()).size();
        int mq = statusMap.getOrDefault(StocktakingLineStateEnum.MATCH.getValue(), Collections.emptyList()).size();
        return StocktakingDetailStatisticsResult.builder()
                .quantity(status.size())
                .lossQuantity(lq)
                .profitQuantity(pq)
                .matchQuantity(mq)
                .build();
    }
}
