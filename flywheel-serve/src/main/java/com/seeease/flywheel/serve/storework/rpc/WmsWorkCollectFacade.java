package com.seeease.flywheel.serve.storework.rpc;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.storework.convert.WmsWorkCollectConvert;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPreExt;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCapacityDTO;
import com.seeease.flywheel.serve.storework.entity.WmsWorkCollect;
import com.seeease.flywheel.serve.storework.enums.StoreWorkStateEnum;
import com.seeease.flywheel.serve.storework.enums.WmsWorkCollectWorkStateEnum;
import com.seeease.flywheel.serve.storework.enums.WmsWorkPrintExpressState;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.serve.storework.service.WmsWorkCollectService;
import com.seeease.flywheel.storework.IWmsWorkCollectFacade;
import com.seeease.flywheel.storework.request.WmsWaitWorkCollectRequest;
import com.seeease.flywheel.storework.request.WmsWorkListRequest;
import com.seeease.flywheel.storework.request.WmsWorkUploadExpressRequest;
import com.seeease.flywheel.storework.result.*;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/8/31
 */
@Slf4j
@DubboService(version = "1.0.0")
public class WmsWorkCollectFacade implements IWmsWorkCollectFacade {

    @NacosValue(value = "${saleOrder.replaceDeliveryShopId:}", autoRefreshed = true)
    private List<Integer> REPLACE_DELIVERY_SHOP_ID;

    @Resource
    private WmsWorkCollectService wmsWorkCollectService;
    @Resource
    private StockService stockService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private CustomerService customerService;

    @Resource
    private UserService userService;

    @Override
    public PageResult<WmsWorkListResult> listWork(WmsWorkListRequest request) {
        //型号条件转换
        if (StringUtils.isNotBlank(request.getModel())) {
            List<Integer> goodsIdList = goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                    .like(GoodsWatch::getModel, request.getModel()))
                    .stream()
                    .map(GoodsWatch::getId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(goodsIdList)) {
                return PageResult.<WmsWorkListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(NumberUtils.LONG_ZERO)
                        .totalPage(NumberUtils.LONG_ZERO)
                        .build();
            }
            request.setGoodsIdList(goodsIdList);
        }
        //表身号条件转换
        if (StringUtils.isNotBlank(request.getStockSn())) {
            List<Integer> stockIdList = stockService.list(Wrappers.<Stock>lambdaQuery()
                    .like(Stock::getSn, request.getStockSn()))
                    .stream()
                    .map(Stock::getId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(stockIdList)) {
                return PageResult.<WmsWorkListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(NumberUtils.LONG_ZERO)
                        .totalPage(NumberUtils.LONG_ZERO)
                        .build();
            }
            request.setStockIdList(stockIdList);
        }

        Integer shopId = UserContext.getUser().getStore().getId();
        //按场景查询
        switch (request.getUseScenario()) {
            //待集单
            case WAIT_COLLECT:
                request.setBelongingStoreIdList(Lists.newArrayList(shopId));
                //3号楼代发
                if (FlywheelConstant._DF3_SHOP_ID == shopId.intValue()
                        && CollectionUtils.isNotEmpty(REPLACE_DELIVERY_SHOP_ID)) {
                    request.getBelongingStoreIdList().addAll(REPLACE_DELIVERY_SHOP_ID);
                }
                return this.convertResult(wmsWorkCollectService.waitWorkList(request));
            //待打单
            case WAIT_PRINT:
                request.setShopId(shopId);
                request.setWorkStateList(Lists.newArrayList(WmsWorkCollectWorkStateEnum.WAIT_PRINT.getValue()));
                return this.convertResult(wmsWorkCollectService.listWorkCollect(request));
            //待发货
            case WAIT_DELIVERY:
                request.setShopId(shopId);
                request.setWorkStateList(Lists.newArrayList(WmsWorkCollectWorkStateEnum.WAIT_DELIVERY.getValue()));
                return this.convertResult(wmsWorkCollectService.listWorkCollect(request));

            //拦截
            case INTERCEPT:
                request.setShopId(shopId);
                request.setWorkStateList(Lists.newArrayList(WmsWorkCollectWorkStateEnum.WAIT_PRINT.getValue(),
                        WmsWorkCollectWorkStateEnum.WAIT_DELIVERY.getValue()));
                request.setWorkIntercept(WhetherEnum.YES.getValue());
                return this.convertResult(wmsWorkCollectService.listWorkCollect(request));
            //已发货
            case COMPLETE:
                request.setShopId(shopId);
                request.setWorkStateList(Lists.newArrayList(WmsWorkCollectWorkStateEnum.COMPLETE.getValue()));
                return this.convertResult(wmsWorkCollectService.pageWorkCollect(request));
            //已取消
            case CANCEL:
                request.setShopId(shopId);
                request.setWorkStateList(Lists.newArrayList(WmsWorkCollectWorkStateEnum.CANCEL.getValue()));
                return this.convertResult(wmsWorkCollectService.pageWorkCollect(request));
        }
        return PageResult.<WmsWorkListResult>builder()
                .result(Collections.EMPTY_LIST)
                .totalCount(NumberUtils.LONG_ZERO)
                .totalPage(NumberUtils.LONG_ZERO)
                .build();
    }

    /**
     * 渲染结果
     *
     * @param result
     * @return
     */
    private PageResult<WmsWorkListResult> convertResult(Page<BillStoreWorkPreExt> result) {
        if (CollectionUtils.isEmpty(result.getRecords())) {
            return PageResult.<WmsWorkListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(result.getTotal())
                    .totalPage(result.getPages())
                    .build();
        }

        Map<Integer, StockExt> stockMap = Optional.ofNullable(result.getRecords().stream()
                .map(BillStoreWorkPreExt::getStockId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))
                .map(stockService::selectByStockIdList)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(StockExt::getStockId, Function.identity()));


        Map<Integer, WatchDataFusion> goodsMap = Optional.ofNullable(result.getRecords().stream()
                .filter(t -> Objects.isNull(t.getStockId()))
                .map(BillStoreWorkPreExt::getGoodsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))
                .map(goodsWatchService::getWatchDataFusionListByGoodsIds)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        Map<Integer, String> tagList = storeManagementService.selectInfoByIds(result.getRecords().stream()
                .map(t -> Lists.newArrayList(t.getSaleStoreId(), t.getDeliveryStoreId()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));

        Map<Integer, String> customerMap = customerService.listByIds(result.getRecords().stream().map(BillStoreWorkPre::getCustomerId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Customer::getId, Customer::getCustomerName));

        Map<Integer, CustomerContacts> customerContactsMap = customerContactsService.listByIds(result.getRecords().stream().map(BillStoreWorkPre::getCustomerContactId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(CustomerContacts::getId, Function.identity()));

        return PageResult.<WmsWorkListResult>builder()
                .result(result.getRecords()
                        .stream().map(t -> {
                            WmsWorkListResult r = WmsWorkCollectConvert.INSTANCE.convertWmsWaitWorkListResult(t);
                            //第一销售人
                            if (StringUtils.isNotEmpty(r.getSaleBy())) {
                                Optional.ofNullable(userService.getById(r.getSaleBy())).ifPresent(v -> r.setSaleBy(v.getName()));
                            }


                            r.setNumber(NumberUtils.INTEGER_ONE);

                            r.setSaleStoreName(tagList.get(t.getSaleStoreId()));
                            r.setDeliveryStoreName(tagList.get(t.getDeliveryStoreId()));
                            StockExt stock = stockMap.get(t.getStockId());
                            if (Objects.nonNull(stock)) {
                                r.setImage(stock.getImage());
                                r.setBrandName(stock.getBrandName());
                                r.setSeriesName(stock.getSeriesName());
                                r.setModel(stock.getModel());
                                r.setStockSn(stock.getStockSn());
                                r.setWno(stock.getWno());
                                r.setAttachment(stock.getAttachmentDetails());
                            }
                            WatchDataFusion goods = goodsMap.get(t.getGoodsId());
                            if (Objects.nonNull(goods)) {
                                r.setImage(goods.getImage());
                                r.setBrandName(goods.getBrandName());
                                r.setSeriesName(goods.getSeriesName());
                                r.setModel(goods.getModel());
                                r.setPricePub(goods.getPricePub());
                            }

                            CustomerContacts customerContacts = customerContactsMap.get(t.getCustomerContactId());
                            if (Objects.nonNull(customerContacts)) {
                                r.setContactName(Optional.ofNullable(customerMap.get(t.getCustomerId())).orElse(customerContacts.getName()));
                                r.setContactAddress(customerContacts.getAddress());
                                r.setContactPhone(customerContacts.getPhone());
                            }

                            return r;
                        }).collect(Collectors.toList()))
                .totalCount(result.getTotal())
                .totalPage(result.getPages())
                .build();
    }


    @Override
    public List<WmsWorkCollectCountResult> count(WmsWorkListRequest request) {
        //型号条件转换
        if (StringUtils.isNotBlank(request.getModel())) {
            List<Integer> goodsIdList = goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                    .like(GoodsWatch::getModel, request.getModel()))
                    .stream()
                    .map(GoodsWatch::getId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(goodsIdList)) {
                return Collections.EMPTY_LIST;
            }
            request.setGoodsIdList(goodsIdList);
        }
        //表身号条件转换
        if (StringUtils.isNotBlank(request.getStockSn())) {
            List<Integer> stockIdList = stockService.list(Wrappers.<Stock>lambdaQuery()
                    .like(Stock::getSn, request.getStockSn()))
                    .stream()
                    .map(Stock::getId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(stockIdList)) {
                return Collections.EMPTY_LIST;
            }
            request.setStockIdList(stockIdList);
        }
        request.setShopId(UserContext.getUser().getStore().getId());
        return wmsWorkCollectService.countByGroupModelAndSn(request);
    }

    @Override
    public WmsWorkExpressResult express(String originSerialNo) {
        List<BillStoreWorkPreExt> result = wmsWorkCollectService.listWorkCollect(WmsWorkListRequest.builder()
                .shopId(UserContext.getUser().getStore().getId())
                .workStateList(Arrays.asList(WmsWorkCollectWorkStateEnum.WAIT_PRINT.getValue(), WmsWorkCollectWorkStateEnum.WAIT_DELIVERY.getValue()))
                .originSerialNo(originSerialNo)
                .build())
                .getRecords();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }

        Assert.isTrue(NumberUtils.LONG_ONE == result.stream().map(BillStoreWorkPreExt::getInspectionType).distinct().count(), "质检数据异常");
        Assert.isTrue(NumberUtils.LONG_ONE == result.stream().map(BillStoreWorkPreExt::getCustomerContactId).distinct().count(), "联系人数据异常");

        Map<Integer, StockExt> stockMap = Optional.ofNullable(result.stream()
                .map(BillStoreWorkPreExt::getStockId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))
                .map(stockService::selectByStockIdList)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(StockExt::getStockId, Function.identity()));


        Map<Integer, WatchDataFusion> goodsMap = Optional.ofNullable(result.stream()
                .filter(t -> Objects.isNull(t.getStockId()))
                .map(BillStoreWorkPreExt::getGoodsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))
                .map(goodsWatchService::getWatchDataFusionListByGoodsIds)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));


        CustomerContacts contact = customerContactsService.getById(result.get(0).getCustomerContactId());

        WmsWorkExpressResult.WmsWorkExpressResultBuilder builder = WmsWorkExpressResult.builder()
                .saleOrderChannel(result.stream().findFirst().get().getSaleOrderChannel())
                .goodsInfos(result.stream()
                        .map(t -> {
                            WmsWorkExpressResult.GoodsInfo goodsInfo = WmsWorkCollectConvert.INSTANCE.convertWmsWorkExpressGoodsInfo(t);
                            StockExt stock = stockMap.get(t.getStockId());
                            if (Objects.nonNull(stock)) {
                                goodsInfo.setBrandName(stock.getBrandName());
                                goodsInfo.setSeriesName(stock.getSeriesName());
                                goodsInfo.setModel(stock.getModel());
                                goodsInfo.setStockSn(stock.getStockSn());
                                goodsInfo.setWno(stock.getWno());
                            }
                            WatchDataFusion goods = goodsMap.get(t.getGoodsId());
                            if (Objects.nonNull(goods)) {
                                goodsInfo.setBrandName(goods.getBrandName());
                                goodsInfo.setSeriesName(goods.getSeriesName());
                                goodsInfo.setModel(goods.getModel());
                            }
                            return goodsInfo;
                        })
                        .collect(Collectors.toList()))
                .originSerialNo(originSerialNo)
                .inspectionType(result.get(0).getInspectionType())
                .saleStoreId(result.get(0).getSaleStoreId())
                .workIntercept(result.get(0).getWorkIntercept())
                .saleRemarks(result.get(0).getSaleRemarks())
                .contactName(contact.getName())
                .contactPhone(contact.getPhone());

        try {
            List<String> addressList = Arrays.stream(contact.getAddress().split("/")).collect(Collectors.toList());
            builder.province(addressList.get(0))
                    .city(addressList.get(1))
                    .town(addressList.get(2))
                    .contactAddress(addressList.get(3));
        } catch (Exception e) {
            log.error("地址解析异常{}");
            builder.contactAddress(contact.getAddress());
        }

        return builder.build();
    }

    @Override
    public WmsWorkUploadExpressResult uploadExpress(WmsWorkUploadExpressRequest request) {
        if (StringUtils.isNotBlank(request.getDeliveryExpressNumber())
                && StringUtils.isNotBlank(request.getOriginSerialNo())) {
            List<BillStoreWorkPre> preList = billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaQuery()
                    .eq(BillStoreWorkPre::getOriginSerialNo, request.getOriginSerialNo()));

            preList.forEach(t -> {
                BillStoreWorkPre up = new BillStoreWorkPre();
                up.setId(t.getId());
                up.setDeliveryExpressNumber(request.getDeliveryExpressNumber());
                billStoreWorkPreService.updateById(up);
            });
            //查集单数据
            List<WmsWorkCollect> collectList = wmsWorkCollectService.list(Wrappers.<WmsWorkCollect>lambdaQuery()
                    .in(WmsWorkCollect::getOriginSerialNo, Arrays.asList(request.getOriginSerialNo())));

            if (CollectionUtils.isNotEmpty(collectList)
                    && WmsWorkCollectWorkStateEnum.WAIT_PRINT.equals(collectList.get(0).getWorkState())) {
                //记录快递来源
                collectList.forEach(t -> t.setPrintExpressState(request.getIsSystemPrint() ? WmsWorkPrintExpressState.SYSTEM : WmsWorkPrintExpressState.MANUAL));
                //更新集单状态
                wmsWorkCollectService.updateCollectWorkState(collectList, WmsWorkCollectWorkStateEnum.TransitionEnum.PRINT);
            } else if (collectList.stream().anyMatch(t -> WmsWorkPrintExpressState.MANUAL.equals(t.getPrintExpressState()))) {
                //不允许手动录入单号补打
                throw new OperationRejectedException(OperationExceptionCode.EXPRESS_PRINT_REFUSE);
            }
        }
        return WmsWorkUploadExpressResult.builder()
                .originSerialNo(request.getOriginSerialNo())
                .build();
    }


    @Override
    public WmsWaitWorkCollectResult collectWork(WmsWaitWorkCollectRequest request) {
        List<BillStoreWorkPre> billStoreWorkPreList = billStoreWorkPreService.listByIds(request.getWorkIdList());
        Assert.isTrue(request.getWorkIdList().size() == billStoreWorkPreList.size(), "数据错误");
        if (billStoreWorkPreList.stream().anyMatch(t -> t.getWorkState() != StoreWorkStateEnum.WAIT_FOR_DELIVERY)) {
            throw new OperationRejectedException(OperationExceptionCode.WORK_COLLECT_DATA_CHANGES);
        }

        Integer belongingStoreId = billStoreWorkPreList.get(0).getBelongingStoreId();

        //销售受限的
        boolean restrictedSale = Optional.ofNullable(billStoreWorkPreList.stream()
                .filter(t -> Objects.isNull(t.getStockId()))
                .collect(Collectors.groupingBy(BillStoreWorkPre::getGoodsId)))
                .filter(MapUtils::isNotEmpty)
                .map(t -> {
                    Map<Integer, WmsWorkCapacityDTO> goodsMap = wmsWorkCollectService.inWorkStockCount(t.keySet().stream().collect(Collectors.toList())
                            , belongingStoreId);
                    return t.entrySet().stream()
                            .anyMatch(k -> goodsMap.get(k.getKey()).restrictedSale(k.getValue().size()));
                }).orElse(false);


        List<WmsWorkCapacityDTO> outStockList = new ArrayList<>();

        if (restrictedSale) {
            //循环集单操作
            billStoreWorkPreList.stream().collect(Collectors.groupingBy(BillStoreWorkPre::getOriginSerialNo, LinkedHashMap::new, Collectors.toList()))
                    .forEach((k, v) -> {
                        List<Integer> goodsIdList = v.stream()
                                .filter(t -> Objects.isNull(t.getStockId()))
                                .map(BillStoreWorkPre::getGoodsId)
                                .distinct()
                                .collect(Collectors.toList());

                        if (CollectionUtils.isNotEmpty(goodsIdList)) {
                            Map<Integer, WmsWorkCapacityDTO> goodsMap = wmsWorkCollectService.inWorkStockCount(goodsIdList, belongingStoreId);

                            List<WmsWorkCapacityDTO> out = goodsMap.values().stream()
                                    .filter(t -> t.restrictedSale(NumberUtils.INTEGER_ONE))
                                    .collect(Collectors.toList());

                            if (CollectionUtils.isNotEmpty(out)) {
                                outStockList.addAll(out);
                                return;
                            }
                        }
                        //集单操作
                        wmsWorkCollectService.collectWork(Objects.requireNonNull(UserContext.getUser().getStore().getId()), v);
                    });
        } else {
            //统一集单操作
            wmsWorkCollectService.collectWork(Objects.requireNonNull(UserContext.getUser().getStore().getId()), billStoreWorkPreList);
        }

        return WmsWaitWorkCollectResult.builder()
                .originSerialNoList(billStoreWorkPreList.stream().map(BillStoreWorkPre::getOriginSerialNo).distinct()
                        .collect(Collectors.toList()))
                .restrictedCollect(outStockList.stream().map(WmsWorkCollectConvert.INSTANCE::convert).collect(Collectors.toList()))
                .build();
    }

}
