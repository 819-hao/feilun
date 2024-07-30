package com.seeease.flywheel.serve.storework.rpc;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.rfid.result.RfidWorkDetailResult;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.service.BillAllocateService;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.dict.entity.DictData;
import com.seeease.flywheel.serve.dict.entity.StockDict;
import com.seeease.flywheel.serve.dict.service.DictDataService;
import com.seeease.flywheel.serve.dict.service.StockDictService;
import com.seeease.flywheel.serve.goods.entity.*;
import com.seeease.flywheel.serve.goods.service.*;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderService;
import com.seeease.flywheel.serve.storework.convert.BillStoreWorkPreConvert;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.enums.StoreWorkStateEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.serve.storework.service.LogStoreWorkOptService;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.*;
import com.seeease.flywheel.storework.result.*;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.seeease.flywheel.serve.base.BusinessBillTypeEnum.*;

/**
 * @author Tiro
 * @date 2023/3/14
 */
@DubboService(version = "1.0.0")
@Slf4j
public class StoreWorkQueryFacade implements IStoreWorkQueryFacade {
    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;
    @Resource
    private StockService stockService;
    @Resource
    private BrandService brandService;
    @Resource
    private StockDictService stockDictService;
    @Resource
    private DictDataService dictDataService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private BillPurchaseService billPurchaseService;
    @Resource
    private BillPurchaseLineService billPurchaseLineService;
    @Resource
    private LogStoreWorkOptService logStoreWorkOptService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private CustomerService customerService;
    @Resource
    private StockManageInfoService stockManageInfoService;
    @Resource
    private BillSaleReturnOrderService billSaleReturnOrderService;
    @Resource
    private BillSaleReturnOrderLineService billSaleReturnOrderLineService;
    @Resource
    private BillAllocateService allocateService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private ExtAttachmentStockService extAttachmentStockService;

    @NacosValue(value = "${saleOrder.ipRoleName}", autoRefreshed = true)
    private List<String> IP_ROLE_NAMES;
    @NacosValue(value = "${saleOrder.ipShopId}", autoRefreshed = true)
    private List<Integer> IP_SHOP_ID;


    @Override
    public List<StoreWorkListResult> listByOriginSerialNo(List<String> originSerialNoList) {
        List<BillStoreWorkPre> res = billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaQuery()
                .in(BillStoreWorkPre::getOriginSerialNo, originSerialNoList));
        return BillStoreWorkPreConvert.INSTANCE.convertStoreWorkListResult(res);
    }

    /**
     * 收货列表
     *
     * @param request
     * @return
     */
    @Override
    public PageResult<StoreWorkListResult> listReceiving(StoreWorkListRequest request) {
        PageResult<StoreWorkListResult> result = this.list(request, StoreWorkStateEnum.WAIT_FOR_RECEIVING);

        //补充配件扩展参数
        if (request.isNeedExtParams() && CollectionUtils.isNotEmpty(result.getResult())) {
            Map<Integer, ExtAttachmentStock> extStockMap = extAttachmentStockService.list(Wrappers.<ExtAttachmentStock>lambdaQuery()
                            .in(ExtAttachmentStock::getStockId, result.getResult().stream().map(StoreWorkListResult::getStockId).collect(Collectors.toList())))
                    .stream()
                    .collect(Collectors.toMap(ExtAttachmentStock::getStockId, Function.identity(), (K1, K2) -> K2));
            result.getResult().forEach(t -> t.setExtAttachmentStock(BillStoreWorkPreConvert.INSTANCE.convert(extStockMap.get(t.getStockId()))));
        }

        return result;
    }

    /**
     * 发货列表
     *
     * @param request
     * @return
     */
    @Override
    public PageResult<StoreWorkListResult> listDelivery(StoreWorkListRequest request) {
        request.setNeedAggregation(true);
        return this.list(request, StoreWorkStateEnum.WAIT_FOR_DELIVERY);
    }

    /**
     * 出库列表
     *
     * @param request
     * @return
     */
    @Override
    public PageResult<StoreWorkListResult> listOutStorage(StoreWorkListRequest request) {
        request.setNeedAggregation(true);
        return this.list(request, StoreWorkStateEnum.WAIT_FOR_OUT_STORAGE);
    }

    /**
     * 入库列表
     *
     * @param request
     * @return
     */
    @Override
    public PageResult<StoreWorkListResult> listInStorage(StoreWorkListRequest request) {
        return this.list(request, StoreWorkStateEnum.WAIT_FOR_IN_STORAGE);
    }

    /**
     * 作业单列表
     *
     * @param request
     * @return
     */
    private PageResult<StoreWorkListResult> list(StoreWorkListRequest request, StoreWorkStateEnum stateEnum) {
        request.setWorkSource(Optional.ofNullable(request.getWorkSource())
                .filter(v -> v != -1)
                .orElse(null));
        request.setExceptionMark(Optional.ofNullable(request.getExceptionMark())
                .filter(v -> v != -1)
                .orElse(null));

        QueryWrapper<BillStoreWorkPre> query = Wrappers.<BillStoreWorkPre>query();
        if (request.isNeedAggregation()) {
            query.select("*,count(1) as number");
        }

        //IP组特殊逻辑
        boolean specialLogic = request.isStoreComprehensive()
                && UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains);

        LambdaQueryWrapper<BillStoreWorkPre> wrappers = query.lambda()
                .eq(!specialLogic, BillStoreWorkPre::getBelongingStoreId, request.getBelongingStoreId())
                .in(specialLogic, BillStoreWorkPre::getBelongingStoreId, IP_SHOP_ID)
                .eq(BillStoreWorkPre::getWorkState, stateEnum)
                .orderByDesc(BillStoreWorkPre::getId);

        if (request.isNeedAggregation()) {
            wrappers.groupBy(BillStoreWorkPre::getOriginSerialNo);
        }

        //时间范围
        if (Objects.nonNull(request.getBeginTime()) && Objects.nonNull(request.getEndTime())) {
            wrappers = wrappers.ge(BillStoreWorkPre::getTaskArriveTime, request.getBeginTime())
                    .le(BillStoreWorkPre::getTaskArriveTime, request.getEndTime());
        }
        //盒号
        if (StringUtils.isNotBlank(request.getBoxNumber())) {
            List<? extends Object> cList = Optional.ofNullable(request.getBoxNumber())
                    .map(t -> stockManageInfoService.list(Wrappers.<StockManageInfo>lambdaQuery()
                            .eq(StockManageInfo::getBoxNumber, t)))
                    .filter(CollectionUtils::isNotEmpty)
                    .map(t -> t.stream()
                            .map(StockManageInfo::getStockId)
                            .collect(Collectors.toList()))
                    .map(t -> {
                        if (request.isNeedAggregation()) {
                            return billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaQuery()
                                            .in(BillStoreWorkPre::getStockId, t))
                                    .stream()
                                    .map(BillStoreWorkPre::getOriginSerialNo)
                                    .collect(Collectors.toList());
                        } else {
                            return t;
                        }
                    })
                    .filter(CollectionUtils::isNotEmpty)
                    .orElse(null);

            if (CollectionUtils.isEmpty(cList)) {
                return PageResult.<StoreWorkListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(NumberUtils.LONG_ZERO)
                        .totalPage(NumberUtils.LONG_ZERO)
                        .build();
            }
            wrappers = wrappers.in(request.isNeedAggregation() ? BillStoreWorkPre::getOriginSerialNo : BillStoreWorkPre::getStockId, cList);
        }
        //品牌
        if (StringUtils.isNotBlank(request.getBrandName())) {
            Brand brand = brandService.getOne(new LambdaQueryWrapper<Brand>().eq(Brand::getName, request.getBrandName()));

            if (Objects.isNull(brand)) {
                return PageResult.<StoreWorkListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(NumberUtils.LONG_ZERO)
                        .totalPage(NumberUtils.LONG_ZERO)
                        .build();
            }
            List<GoodsWatch> watchList = goodsWatchService.list(new LambdaQueryWrapper<GoodsWatch>().eq(GoodsWatch::getBrandId, brand.getId()));
            if (CollectionUtils.isEmpty(watchList)) {
                return PageResult.<StoreWorkListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(NumberUtils.LONG_ZERO)
                        .totalPage(NumberUtils.LONG_ZERO)
                        .build();
            }
            wrappers = wrappers.in(BillStoreWorkPre::getGoodsId, watchList.stream().map(GoodsWatch::getId).collect(Collectors.toList()));
        }
        //表身号
        if (StringUtils.isNotBlank(request.getStockSn())) {
            List<? extends Object> cList = Optional.ofNullable(request.getStockSn())
                    .map(stockService::findByStockSn)
                    .filter(CollectionUtils::isNotEmpty)
                    .map(t -> t.stream()
                            .map(Stock::getId)
                            .collect(Collectors.toList()))
                    .map(t -> {
                        if (request.isNeedAggregation()) {
                            return billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaQuery()
                                            .in(BillStoreWorkPre::getStockId, t))
                                    .stream()
                                    .map(BillStoreWorkPre::getOriginSerialNo)
                                    .collect(Collectors.toList());
                        } else {
                            return t;
                        }
                    })
                    .filter(CollectionUtils::isNotEmpty)
                    .orElse(null);

            if (CollectionUtils.isEmpty(cList)) {
                return PageResult.<StoreWorkListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(NumberUtils.LONG_ZERO)
                        .totalPage(NumberUtils.LONG_ZERO)
                        .build();
            }
            wrappers = wrappers.in(request.isNeedAggregation() ? BillStoreWorkPre::getOriginSerialNo : BillStoreWorkPre::getStockId, cList);
        }
        if (StringUtils.isNotBlank(request.getModel())) {
            List<? extends Object> cList = Optional.ofNullable(request.getModel())
                    .map(t -> goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                            .like(GoodsWatch::getModel, t)))
                    .filter(CollectionUtils::isNotEmpty)
                    .map(t -> t.stream()
                            .map(GoodsWatch::getId)
                            .collect(Collectors.toList()))
                    .map(t -> {
                        if (request.isNeedAggregation()) {
                            return billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaQuery()
                                            .in(BillStoreWorkPre::getGoodsId, t))
                                    .stream()
                                    .map(BillStoreWorkPre::getOriginSerialNo)
                                    .collect(Collectors.toList());
                        } else {
                            return t;
                        }
                    })
                    .filter(CollectionUtils::isNotEmpty)
                    .orElse(null);

            if (CollectionUtils.isEmpty(cList)) {
                return PageResult.<StoreWorkListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(NumberUtils.LONG_ZERO)
                        .totalPage(NumberUtils.LONG_ZERO)
                        .build();
            }
            wrappers = wrappers.in(request.isNeedAggregation() ? BillStoreWorkPre::getOriginSerialNo : BillStoreWorkPre::getGoodsId, cList);
        }
        //关联单号
        if (StringUtils.isNotBlank(request.getOriginSerialNo())) {
            wrappers = wrappers.eq(BillStoreWorkPre::getOriginSerialNo, request.getOriginSerialNo());
        }
        if (StringUtils.isNotBlank(request.getSerialNo())) {
            wrappers = wrappers.eq(BillStoreWorkPre::getSerialNo, request.getSerialNo());
        }
        //由来
        if (Objects.nonNull(request.getWorkSource())) {
            wrappers = wrappers.eq(BillStoreWorkPre::getWorkSource, BusinessBillTypeEnum.fromValue(request.getWorkSource()));
        }
        //快递单号
        if (StringUtils.isNotBlank(request.getExpressNumber())) {
            wrappers = wrappers.eq(BillStoreWorkPre::getExpressNumber, request.getExpressNumber());
        }

        if (Objects.nonNull(request.getExceptionMark())) {
            wrappers = wrappers.eq(BillStoreWorkPre::getExceptionMark, WhetherEnum.fromValue(request.getExceptionMark()));
        }

        Page<BillStoreWorkPre> pageResult = billStoreWorkPreService.page(Page.of(request.getPage(), request.getLimit()), wrappers);

        List<StoreWorkListResult> result = BillStoreWorkPreConvert.INSTANCE.convertStoreWorkListResult(pageResult.getRecords());
        if (CollectionUtils.isEmpty(result)) {
            return PageResult.<StoreWorkListResult>builder()
                    .result(result)
                    .totalCount(pageResult.getTotal())
                    .totalPage(pageResult.getPages())
                    .build();
        }

        Map<Integer, String> customerMap = customerService.listByIds(result.stream().map(StoreWorkListResult::getCustomerId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Customer::getId, Customer::getCustomerName));
        result.forEach(t -> Optional.ofNullable(customerMap.get(t.getCustomerId()))
                .ifPresent(t::setCustomerName));


        //反查退货备注
        ArrayList<Integer> types = Lists.newArrayList(TO_C_XS_TH.getValue(), TO_B_XS_TH.getValue());
        List<String> nos = result.stream()
                .filter(v -> types.contains(v.getWorkSource()))
                .map(StoreWorkListResult::getOriginSerialNo).collect(Collectors.toList());
        if (!nos.isEmpty()) {
            LambdaQueryWrapper<BillSaleReturnOrder> bsroEq = Wrappers.<BillSaleReturnOrder>lambdaQuery()
                    .in(BillSaleReturnOrder::getSerialNo, nos);
            List<Integer> srIds = billSaleReturnOrderService.list(bsroEq).stream().map(BillSaleReturnOrder::getId).collect(Collectors.toList());
            if (!srIds.isEmpty()) {
                LambdaQueryWrapper<BillSaleReturnOrderLine> srolEq = Wrappers.<BillSaleReturnOrderLine>lambdaQuery()
                        .in(BillSaleReturnOrderLine::getSaleReturnId, srIds);
                List<BillSaleReturnOrderLine> saleReturnLines = billSaleReturnOrderLineService.list(srolEq);
                result.forEach(v -> {
                    for (BillSaleReturnOrderLine line : saleReturnLines) {
                        if (v.getStockId().equals(line.getStockId())) {
                            v.setRemark(line.getRemark());
                            break;
                        }
                    }
                });
            }
        }

        //反查调拨单 获得调出方 、 调拨类型
        List<String> allocateSerialNoList = result.stream()
                .filter(v -> Lists.newArrayList(ZB_DB.getValue(), MD_DB.getValue(), MD_DB_ZB.getValue()).contains(v.getWorkSource()))
                .map(StoreWorkListResult::getOriginSerialNo).collect(Collectors.toList());
        if (!allocateSerialNoList.isEmpty()) {
            Map<String, BillAllocate> allocateMap = allocateService.list(new LambdaQueryWrapper<BillAllocate>()
                            .in(BillAllocate::getSerialNo, allocateSerialNoList))
                    .stream()
                    .collect(Collectors.toMap(BillAllocate::getSerialNo, Function.identity()));
            Map<Integer, String> storeMap = storeManagementService.getStoreMap();
            result.forEach(t -> {
                if (allocateMap.containsKey(t.getOriginSerialNo())) {
                    t.setAllocateType(allocateMap.get(t.getOriginSerialNo()).getAllocateType().getValue());
                    t.setFromName(storeMap.get(allocateMap.get(t.getOriginSerialNo()).getFromId()));
                }
            });
        }

        if (Boolean.FALSE.equals(request.isNeedAggregation())) {
            this.withGoodsInfo(result);
        }
        return PageResult.

                <StoreWorkListResult>builder()
                .

                result(result)
                        .

                totalCount(pageResult.getTotal())
                        .

                totalPage(pageResult.getPages())
                        .

                build();

    }

    /**
     * 补充商品信息
     *
     * @param result
     */
    private void withGoodsInfo(List<StoreWorkListResult> result) {
        //型号信息
        Map<Integer/*goodsId*/, WatchDataFusion> goodsMap = Optional.ofNullable(result.stream()
                        .map(StoreWorkListResult::getGoodsId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(goodsWatchService::getWatchDataFusionListByGoodsIds)
                .orElseGet(Lists::newArrayList)
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        //库存信息
        Map<Integer/*stockId*/, StockExt> stockMap = Optional.ofNullable(result.stream()
                        .map(StoreWorkListResult::getStockId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(stockService::selectByStockIdList)
                .orElseGet(Lists::newArrayList)
                .stream()
                .collect(Collectors.toMap(StockExt::getStockId, Function.identity()));
        //附件 字典信息
        Map<Integer, List<StockDict>> stockDictMap = Optional.ofNullable(result.stream()
                        .map(StoreWorkListResult::getStockId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(stockDictService::selectByStockIdList)
                .orElseGet(Lists::newArrayList)
                .stream()
                .collect(Collectors.groupingBy(StockDict::getStockId));
        Map<Long, DictData> dictDataMap = dictDataService.list(new LambdaQueryWrapper<DictData>()
                        .likeRight(DictData::getDictType, "stock"))
                .stream()
                .collect(Collectors.toMap(DictData::getDictCode, Function.identity()));

        //盒号信息
        Map<Integer, StockManageInfo> stockManageInfoMap = Optional.ofNullable(result.stream()
                        .map(StoreWorkListResult::getStockId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(idList -> stockManageInfoService.list(Wrappers.<StockManageInfo>lambdaQuery()
                        .in(StockManageInfo::getStockId, idList)))
                .orElseGet(Lists::newArrayList)
                .stream()
                .collect(Collectors.toMap(StockManageInfo::getStockId, Function.identity()));

        result.forEach(t -> {
            //前端非要空值数据 当数据库新加的时候 代码也需要加
            Map<String, List<Integer>> dictChildList = new HashMap<>();
            dictDataMap.values().stream().map(DictData::getDictType).forEach(dictType -> {
                dictChildList.put(dictType, new ArrayList<>());
            });
            WatchDataFusion goods = goodsMap.get(t.getGoodsId());
            if (Objects.nonNull(goods)) {
                t.setImage(goods.getImage());
                t.setBrandName(goods.getBrandName());
                t.setSeriesName(goods.getSeriesName());
                t.setSeriesType(goods.getSeriesType());
                t.setModel(goods.getModel());
                t.setMovement(goods.getMovement());
                t.setPricePub(goods.getPricePub());
                t.setWatchSize(goods.getWatchSize());
            }
            StockExt stock = stockMap.get(t.getStockId());
            if (Objects.nonNull(stock)) {
                t.setStockSn(stock.getStockSn());
                t.setAttachment(stock.getAttachmentDetails());
                t.setFiness(stock.getFiness());
                t.setIsCard(stock.getIsCard());
                t.setWarrantyDate(stock.getWarrantyDate());
                t.setWno(stock.getWno());
                t.setTagPrice(stock.getTagPrice());

                if (stockDictMap.containsKey(t.getStockId())) {
                    stockDictMap.get(t.getStockId()).forEach(stockDict -> {
                        if (dictDataMap.containsKey(stockDict.getDictId())) {
                            DictData dictData = dictDataMap.get(stockDict.getDictId());
                            if (dictChildList.containsKey(dictData.getDictType())) {
                                dictChildList.get(dictData.getDictType()).add(Integer.parseInt(dictData.getDictValue()));
                            }
                        }
                    });
                }
            }
            t.setDictChildList(dictChildList);

            StockManageInfo stockManageInfo = stockManageInfoMap.get(t.getStockId());
            if (Objects.nonNull(stockManageInfo)) {
                t.setBoxNumber(stockManageInfo.getBoxNumber());
            }
        });
    }


    @Override
    public List<StoreWorkDetailResult> deliveryDetail(StoreWorkDeliveryDetailRequest request) {
        //IP组特殊逻辑
        boolean specialLogic = request.isStoreComprehensive()
                && UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains);

        List<BillStoreWorkPre> result = billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>query().lambda()
                //门店只能查看自己的
                .eq(!specialLogic, BillStoreWorkPre::getBelongingStoreId, request.getBelongingStoreId())
                .in(specialLogic, BillStoreWorkPre::getBelongingStoreId, IP_SHOP_ID)
                .eq(BillStoreWorkPre::getOriginSerialNo, request.getOriginSerialNo())
                .eq(BillStoreWorkPre::getWorkState, StoreWorkStateEnum.WAIT_FOR_DELIVERY)
                .orderByDesc(BillStoreWorkPre::getId));

        if (CollectionUtils.isEmpty(result)) {
            return Collections.EMPTY_LIST;
        }

        return this.convertStoreWorkDetailResult(result);
    }


    @Override
    public List<StoreWorkDetailResult> outStorageDetails(StoreWorkOutStorageDetailRequest request) {
        List<BillStoreWorkPre> result = billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>query().lambda()
                //门店只能查看自己的
                .eq(BillStoreWorkPre::getBelongingStoreId, request.getBelongingStoreId())
                .eq(BillStoreWorkPre::getOriginSerialNo, request.getOriginSerialNo())
                .eq(BillStoreWorkPre::getWorkState, StoreWorkStateEnum.WAIT_FOR_OUT_STORAGE)
                .orderByDesc(BillStoreWorkPre::getId));

        if (CollectionUtils.isEmpty(result)) {
            return Collections.EMPTY_LIST;
        }
        return this.convertStoreWorkDetailResult(result);
    }

    /**
     * @param result
     * @return
     */
    private List<StoreWorkDetailResult> convertStoreWorkDetailResult(List<BillStoreWorkPre> result) {
        Assert.isTrue(result.stream()
                .map(BillStoreWorkPre::getCustomerContactId)
                .distinct()
                .count() == NumberUtils.LONG_ONE, "联系人异常");
        CustomerContacts customerContacts = customerContactsService.getById(result.get(0).getCustomerContactId());
        Customer customer;
        if (ObjectUtils.isNotEmpty(customerContacts)) {
            customer = customerService.getById(Optional.ofNullable(result.get(0).getCustomerId()).orElse(customerContacts.getCustomerId()));
        } else {
            customer = customerService.getById(result.get(0).getCustomerId());
        }

        //型号信息
        Map<Integer/*goodsId*/, WatchDataFusion> goodsMap = Optional.ofNullable(result.stream()
                        .map(BillStoreWorkPre::getGoodsId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(goodsWatchService::getWatchDataFusionListByGoodsIds)
                .orElseGet(Lists::newArrayList)
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        //库存信息
        Map<Integer/*stockId*/, StockExt> stockMap = Optional.ofNullable(result.stream()
                        .map(BillStoreWorkPre::getStockId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(stockService::selectByStockIdList)
                .orElseGet(Lists::newArrayList)
                .stream()
                .collect(Collectors.toMap(StockExt::getStockId, Function.identity()));

        return result.stream()
                .map(t -> {
                    StoreWorkDetailResult res = BillStoreWorkPreConvert.INSTANCE.convertStoreWorkDetailResult(t);
                    res.setContactName(ObjectUtils.isNotEmpty(customerContacts) ? customerContacts.getName() : StringUtils.EMPTY);
                    res.setContactAddress(ObjectUtils.isNotEmpty(customerContacts) ? customerContacts.getAddress() : StringUtils.EMPTY);
                    res.setContactPhone(ObjectUtils.isNotEmpty(customerContacts) ? customerContacts.getPhone() : StringUtils.EMPTY);
                    res.setCustomerName(ObjectUtils.isNotEmpty(customer) ? customer.getCustomerName() : StringUtils.EMPTY);
                    WatchDataFusion goods = goodsMap.get(t.getGoodsId());
                    if (Objects.nonNull(goods)) {
                        res.setBrandName(goods.getBrandName());
                        res.setSeriesName(goods.getSeriesName());
                        res.setModel(goods.getModel());
                        res.setMovement(goods.getMovement());
                        res.setImage(goods.getImage());
                    }
                    StockExt stock = stockMap.get(t.getStockId());
                    if (Objects.nonNull(stock)) {
                        res.setStockSn(stock.getStockSn());
                        res.setAttachment(stock.getAttachmentDetails());
                        res.setFiness(stock.getFiness());
                    }
                    return res;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<StoreWorkPrintLabelResult> printLabel(List<Integer> request) {

        List<StoreWorkPrintLabelResult> list = new ArrayList<StoreWorkPrintLabelResult>();

        List<WatchDataFusion> watchDataFusionList = goodsWatchService.getWatchDataFusionListByStockIds(request);

        List<BillPurchaseLine> billPurchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery().in(BillPurchaseLine::getStockId, request));

        //采购单分组
        Map<Integer, List<Integer>> collect = billPurchaseLineList.stream().collect(Collectors.groupingBy
                (billPurchaseLine -> billPurchaseLine.getPurchaseId(), Collectors.mapping(billPurchaseLine -> billPurchaseLine.getStockId(), Collectors.toList())));

        List<BillPurchase> billPurchaseList = billPurchaseService.list(Wrappers.<BillPurchase>lambdaQuery().in(BillPurchase::getId, collect.keySet()));

        collect.forEach((k, v) -> {

            if (ObjectUtils.isNotEmpty(v)) {

                for (Integer item : v) {

                    StoreWorkPrintLabelResult result = new StoreWorkPrintLabelResult();

                    Optional<BillPurchase> anyBillPurchase = billPurchaseList.stream().filter(billPurchase -> billPurchase.getId().equals(k)).findAny();

                    if (anyBillPurchase.isPresent()) {
                        BillPurchase billPurchase = anyBillPurchase.get();

                        result.setCreatedBy(billPurchase.getCreatedBy());
                        result.setPurchaseSource(billPurchase.getPurchaseSource().getValue());
                        result.setPurchaseType(billPurchase.getPurchaseType().getValue());

                    }

                    Optional<WatchDataFusion> anyWatchDataFusion = watchDataFusionList.stream().filter(watchDataFusion -> watchDataFusion.getStockId().equals(item)).findAny();

                    if (anyWatchDataFusion.isPresent()) {

                        WatchDataFusion watchDataFusion = anyWatchDataFusion.get();

                        result.setAttachment(watchDataFusion.getAttachment());
                        result.setPricePub(watchDataFusion.getPricePub());
                        result.setStockSn(watchDataFusion.getStockSn());
                        result.setBrandName(watchDataFusion.getBrandName());
                        result.setSeriesName(watchDataFusion.getSeriesName());
                        result.setModel(watchDataFusion.getModel());
                    }

                    list.add(result);
                }
            }
        });


        return list;
    }

    @Override
    public List<StoreWorkDetailResult> deliveryDetailByPrint(StoreWorkDeliveryDetailRequest request) {
//        //IP组特殊逻辑
//        boolean specialLogic = request.isStoreComprehensive()
//                && UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains);

        List<BillStoreWorkPre> result = billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>query().lambda()
                //门店只能查看自己的
//                .eq(!specialLogic, BillStoreWorkPre::getBelongingStoreId, request.getBelongingStoreId())
//                .in(specialLogic, BillStoreWorkPre::getBelongingStoreId, IP_SHOP_ID)
                .eq(BillStoreWorkPre::getOriginSerialNo, request.getOriginSerialNo())
                .orderByDesc(BillStoreWorkPre::getId));

        if (CollectionUtils.isEmpty(result)) {
            return Collections.EMPTY_LIST;
        }

        return this.convertStoreWorkDetailResult(result);
    }

    @Override
    public PageResult<StoreWorkListByModeResult> listDeliveryByMode(StoreWorkListByModelRequest request) {

        return this.listByMode(request, StoreWorkStateEnum.WAIT_FOR_DELIVERY);
    }


    @Override
    public List<RfidWorkDetailResult> outStorageRfidDetails(StoreWorkOutStorageRfidDetailRequest request) {
        Integer storeId = UserContext.getUser().getStore().getId();
        Integer value = storeId == FlywheelConstant._ZB_ID ? StoreWorkStateEnum.WAIT_FOR_OUT_STORAGE.getValue() : StoreWorkStateEnum.WAIT_FOR_DELIVERY.getValue();
        List<BillStoreWorkPre> list = billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>query().lambda()
                .eq(BillStoreWorkPre::getOriginSerialNo, request.getNo())
                .eq(BillStoreWorkPre::getWorkState, value)
                .orderByDesc(BillStoreWorkPre::getId));

        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        //适配成rfid返回值
        List<RfidWorkDetailResult> collect = this.convertStoreWorkDetailResult(list)
                .stream()
                .map(BillStoreWorkPreConvert.INSTANCE::toRfidDetail).collect(Collectors.toList());


        List<Integer> stockIds = collect.stream().map(RfidWorkDetailResult::getStockId).collect(Collectors.toList());
        List<Stock> stocks = stockService.listByIds(stockIds);

        collect.forEach(v -> {
            for (Stock s : stocks) {
                if (s.getId().equals(v.getStockId())) {
                    v.setWno(s.getWno());
                    break;
                }
            }
        });
        return collect;

    }

    private void withGoodsInfo2(List<StoreWorkListByModeResult> result) {
        //型号信息
        Map<Integer/*goodsId*/, WatchDataFusion> goodsMap = Optional.ofNullable(result.stream()
                        .map(StoreWorkListByModeResult::getGoodsId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(goodsWatchService::getWatchDataFusionListByGoodsIds)
                .orElseGet(Lists::newArrayList)
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        //库存信息
        Map<Integer/*stockId*/, StockExt> stockMap = Optional.ofNullable(result.stream()
                        .map(StoreWorkListByModeResult::getStockId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(stockService::selectByStockIdList)
                .orElseGet(Lists::newArrayList)
                .stream()
                .collect(Collectors.toMap(StockExt::getStockId, Function.identity()));

        result.forEach(t -> {
            WatchDataFusion goods = goodsMap.get(t.getGoodsId());
            if (Objects.nonNull(goods)) {
                t.setModel(goods.getModel());
            }
            StockExt stock = stockMap.get(t.getStockId());
            if (Objects.nonNull(stock)) {
                t.setStockSn(stock.getStockSn());
            }
        });
    }

    @Override
    public PageResult<StoreWorkLogResult> logList(StoreWorkLogRequest request) {
        request.setOptType(Optional.ofNullable(request.getOptType())
                .filter(v -> v != -1)
                .orElse(null));

        request.setWorkSource(Optional.ofNullable(request.getWorkSource())
                .filter(v -> v != -1)
                .orElse(null));

        request.setStoreComprehensive(request.isStoreComprehensive()
                && UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains));
        if (request.isStoreComprehensive()) {
            request.setIpShopIds(IP_SHOP_ID);
        }

        Page<StoreWorkLogResult> logResultPage = logStoreWorkOptService.page(request);

        List<StoreWorkLogResult> records = logResultPage.getRecords();

        if (ObjectUtils.isNotEmpty(records)) {

            Map<Integer, WatchDataFusion> collect = goodsWatchService.getWatchDataFusionListByGoodsIds(records.stream().filter(logResult -> ObjectUtils.isNotEmpty(logResult.getGoodsId())).
                            map(purchaseLogResult -> purchaseLogResult.getGoodsId()).collect(Collectors.toList())).
                    stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, watchDataFusion -> watchDataFusion));

            logResultPage.getRecords().forEach(purchaseLogResult -> {
                WatchDataFusion watchDataFusion = collect.getOrDefault(purchaseLogResult.getGoodsId(), new WatchDataFusion());
                purchaseLogResult.setBrandName(watchDataFusion.getBrandName());
                purchaseLogResult.setSeriesName(watchDataFusion.getSeriesName());
                purchaseLogResult.setModel(watchDataFusion.getModel());
                purchaseLogResult.setMovement(watchDataFusion.getMovement());
            });
        }

        return PageResult.<StoreWorkLogResult>builder()
                .result(records)
                .totalCount(logResultPage.getTotal())
                .totalPage(logResultPage.getPages())
                .build();
    }


    private PageResult<StoreWorkListByModeResult> listByMode(StoreWorkListByModelRequest request, StoreWorkStateEnum stateEnum) {

        request.setWorkSource(Optional.ofNullable(request.getWorkSource())
                .filter(v -> v != -1)
                .orElse(null));
        request.setExceptionMark(Optional.ofNullable(request.getExceptionMark())
                .filter(v -> v != -1)
                .orElse(null));

        QueryWrapper<BillStoreWorkPre> query = Wrappers.<BillStoreWorkPre>query();
        if (request.isNeedAggregation()) {
            query.select("*,count(1) as number");
        }

        //IP组特殊逻辑
        boolean specialLogic = request.isStoreComprehensive()
                && UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(IP_ROLE_NAMES::contains);

        LambdaQueryWrapper<BillStoreWorkPre> wrappers = query.lambda()
                .eq(!specialLogic, BillStoreWorkPre::getBelongingStoreId, request.getBelongingStoreId())
                .in(specialLogic, BillStoreWorkPre::getBelongingStoreId, IP_SHOP_ID)
                .eq(BillStoreWorkPre::getWorkState, stateEnum)
                .orderByDesc(BillStoreWorkPre::getId);

        if (request.isNeedAggregation()) {
            wrappers.groupBy(BillStoreWorkPre::getGoodsId, BillStoreWorkPre::getStockId);
        }

        if (CollectionUtils.isNotEmpty(request.getList()) && request.getList().stream().allMatch(Objects::nonNull)) {
            wrappers = wrappers.in(BillStoreWorkPre::getOriginSerialNo, request.getList());
        } else {
            //时间范围
            if (Objects.nonNull(request.getBeginTime()) && Objects.nonNull(request.getEndTime())) {
                wrappers = wrappers.ge(BillStoreWorkPre::getTaskArriveTime, request.getBeginTime())
                        .le(BillStoreWorkPre::getTaskArriveTime, request.getEndTime());
            }
            //表身号
            if (StringUtils.isNotBlank(request.getStockSn())) {
                List<? extends Object> cList = Optional.ofNullable(request.getStockSn())
                        .map(stockService::findByStockSn)
                        .filter(CollectionUtils::isNotEmpty)
                        .map(t -> t.stream()
                                .map(Stock::getId)
                                .collect(Collectors.toList()))
                        .map(t -> {
                            if (request.isNeedAggregation()) {
                                return billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaQuery()
                                                .in(BillStoreWorkPre::getStockId, t))
                                        .stream()
                                        .map(BillStoreWorkPre::getOriginSerialNo)
                                        .collect(Collectors.toList());
                            } else {
                                return t;
                            }
                        })
                        .filter(CollectionUtils::isNotEmpty)
                        .orElse(null);

                if (CollectionUtils.isEmpty(cList)) {
                    return PageResult.<StoreWorkListByModeResult>builder()
                            .result(Collections.EMPTY_LIST)
                            .totalCount(NumberUtils.LONG_ZERO)
                            .totalPage(NumberUtils.LONG_ZERO)
                            .build();
                }
                wrappers = wrappers.in(request.isNeedAggregation() ? BillStoreWorkPre::getOriginSerialNo : BillStoreWorkPre::getStockId, cList);
            }
            if (StringUtils.isNotBlank(request.getModel())) {
                List<? extends Object> cList = Optional.ofNullable(request.getModel())
                        .map(t -> goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                                .like(GoodsWatch::getModel, t)))
                        .filter(CollectionUtils::isNotEmpty)
                        .map(t -> t.stream()
                                .map(GoodsWatch::getId)
                                .collect(Collectors.toList()))
                        .map(t -> {
                            if (request.isNeedAggregation()) {
                                return billStoreWorkPreService.list(Wrappers.<BillStoreWorkPre>lambdaQuery()
                                                .in(BillStoreWorkPre::getGoodsId, t))
                                        .stream()
                                        .map(BillStoreWorkPre::getOriginSerialNo)
                                        .collect(Collectors.toList());
                            } else {
                                return t;
                            }
                        })
                        .filter(CollectionUtils::isNotEmpty)
                        .orElse(null);

                if (CollectionUtils.isEmpty(cList)) {
                    return PageResult.<StoreWorkListByModeResult>builder()
                            .result(Collections.EMPTY_LIST)
                            .totalCount(NumberUtils.LONG_ZERO)
                            .totalPage(NumberUtils.LONG_ZERO)
                            .build();
                }
                wrappers = wrappers.in(request.isNeedAggregation() ? BillStoreWorkPre::getOriginSerialNo : BillStoreWorkPre::getGoodsId, cList);
            }

            //关联单号
            if (StringUtils.isNotBlank(request.getOriginSerialNo())) {
                wrappers = wrappers.eq(BillStoreWorkPre::getOriginSerialNo, request.getOriginSerialNo());
            }
            if (StringUtils.isNotBlank(request.getSerialNo())) {
                wrappers = wrappers.eq(BillStoreWorkPre::getSerialNo, request.getSerialNo());
            }
            //由来
            if (Objects.nonNull(request.getWorkSource())) {
                wrappers = wrappers.eq(BillStoreWorkPre::getWorkSource, BusinessBillTypeEnum.fromValue(request.getWorkSource()));
            }
            //快递单号
            if (StringUtils.isNotBlank(request.getExpressNumber())) {
                wrappers = wrappers.eq(BillStoreWorkPre::getExpressNumber, request.getExpressNumber());
            }

            if (Objects.nonNull(request.getExceptionMark())) {
                wrappers = wrappers.eq(BillStoreWorkPre::getExceptionMark, WhetherEnum.fromValue(request.getExceptionMark()));
            }
        }

        Page<BillStoreWorkPre> pageResult = billStoreWorkPreService.page(Page.of(request.getPage(), request.getLimit()), wrappers);

        List<StoreWorkListByModeResult> result = BillStoreWorkPreConvert.INSTANCE.convertStoreWorkListByModeResult(pageResult.getRecords());
        if (CollectionUtils.isEmpty(result)) {
            return PageResult.<StoreWorkListByModeResult>builder()
                    .result(result)
                    .totalCount(pageResult.getTotal())
                    .totalPage(pageResult.getPages())
                    .build();
        }

        if (Boolean.TRUE.equals(request.isNeedAggregation())) {
            this.withGoodsInfo2(result);
        }
        return PageResult.<StoreWorkListByModeResult>builder()
                .result(result)
                .totalCount(pageResult.getTotal())
                .totalPage(pageResult.getPages())
                .build();
    }
}
