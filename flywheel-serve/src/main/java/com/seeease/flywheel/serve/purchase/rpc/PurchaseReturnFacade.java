package com.seeease.flywheel.serve.purchase.rpc;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.request.StockListRequest;
import com.seeease.flywheel.purchase.IPurchaseReturnFacade;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.flywheel.purchase.result.*;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.customer.entity.CustomerPO;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.service.AccountsPayableAccountingService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.purchase.convert.PurchaseReturnConverter;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturn;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseReturnLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseReturnService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.storework.request.StoreWorKCreateRequest;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/1
 */
@DubboService(version = "1.0.0")
public class PurchaseReturnFacade implements IPurchaseReturnFacade {
    @Resource
    private BillPurchaseReturnService billPurchaseReturnService;

    @Resource
    private BillPurchaseReturnLineService billPurchaseReturnLineService;

    @Resource
    private CustomerService customerService;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private StockService stockService;

    @Resource
    private PurchaseSubjectService purchaseSubjectService;

    @Resource
    private TagService tagService;

    @Resource
    private BillPurchaseService billPurchaseService;

    @Resource
    private AccountsPayableAccountingService accountingService;

    @Resource
    private StoreManagementService storeManagementService;
    private static final Set<String> ROLE_NAMES = ImmutableSet.of("admin", "总部查看", "财务查看");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PurchaseReturnCreateResult> create(PurchaseReturnCreateRequest request) {

//        if (!request.getStoreId().equals(FlywheelConstant._ZB_ID)) {
//            throw new OperationRejectedException(OperationExceptionCode.PURCHASE_RETURN_ERROR);
//        }
        //传入表的id
        List<Integer> stockIdList = request.getDetails().stream().map(PurchaseReturnCreateRequest.BillPurchaseReturnLineDto::getStockId).collect(Collectors.toList());

        //新建
        List<PurchaseReturnCreateResult> purchaseReturnCreateResultList = billPurchaseReturnService.create(request);

        //按单号分组 按门店分组
        Map<String, Map<Integer, List<PurchaseReturnCreateResult>>> map = purchaseReturnCreateResultList.stream().collect(
                Collectors.groupingBy(PurchaseReturnCreateResult::getSerialNo, Collectors.groupingBy(PurchaseReturnCreateResult::getStoreId)));

        //4.更新表表未不可售 将商品状态修改为不可售状态
        stockService.updateStockStatus(stockIdList, StockStatusEnum.TransitionEnum.MARKETABLE_PURCHASE_RETURNED_ING);

        map.forEach((k, v) -> v.forEach((k1, v1) -> v1.forEach(purchaseReturnCreateResult -> {

            List<StoreWorKCreateRequest> collect = purchaseReturnCreateResult.getStoreWorkList().stream().map(t -> StoreWorKCreateRequest.builder()

                    .stockId(t.getStockId())
                    .originSerialNo(purchaseReturnCreateResult.getSerialNo())
                    .goodsId(t.getGoodsId())
                    .workSource(BusinessBillTypeEnum.CG_TH.getValue())
                    //发货
                    .workType(StoreWorkTypeEnum.OUT_STORE.getValue())
                    .belongingStoreId(k1)
                    .customerId(purchaseReturnCreateResult.getCustomerId())
                    .customerContactId(purchaseReturnCreateResult.getCustomerContactId())
                    .belongingStoreId(k1)
                    .build()).collect(Collectors.toList());

            //创建出库作业
            //出库单结果
            List<StoreWorkCreateResult> shopWorkList = billStoreWorkPreService.create(collect);

            purchaseReturnCreateResultList.stream().filter(purchaseReturnCreateResult1 -> purchaseReturnCreateResult1.getSerialNo().equals(k)).findAny().get().setSerialList(shopWorkList.stream().map(StoreWorkCreateResult::getSerialNo).collect(Collectors.toList()));

            if (!purchaseReturnCreateResult.getStoreId().equals(FlywheelConstant._ZB_ID)) {
                //查询当前登陆用户的简码
                purchaseReturnCreateResult.setShortcodes(tagService.selectByStoreManagementId(purchaseReturnCreateResult.getStoreId()).getShortcodes());
            }
        })));

//        for (Map.Entry<String, List<PurchaseReturnCreateResult>> entry : purchaseReturnCreateResultList.stream().collect(Collectors.groupingBy(PurchaseReturnCreateResult::getSerialNo)).entrySet()) {
//
//            BillPurchaseReturn billPurchaseReturn = billPurchaseReturnService.getOne(Wrappers.<BillPurchaseReturn>lambdaQuery().eq(BillPurchaseReturn::getSerialNo, entry.getKey()));
//
//            List<BillPurchaseReturnLine> billPurchaseReturnLineList = billPurchaseReturnLineService.list(Wrappers.<BillPurchaseReturnLine>lambdaQuery()
//                    .eq(BillPurchaseReturnLine::getPurchaseReturnId, billPurchaseReturn.getId()));
//
//            if (Objects.nonNull(billPurchaseReturn) && CollectionUtils.isNotEmpty(billPurchaseReturnLineList)) {
//
//                //一个采购退货单 采购退货行 关联多个采购单
//                paymentHTemplate.createReceiptAndGeneratePayable(new JSONObject()
//                                .fluentPut("purchaseReturn", billPurchaseReturn)
//                                .fluentPut("purchaseReturnLine", billPurchaseReturnLineList)
////                        .fluentPut("purchase", purchase)
//
//                );
//            }
//        }


        return purchaseReturnCreateResultList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseReturnCancelResult cancel(PurchaseReturnCancelRequest request) {

        PurchaseReturnCancelResult result = billPurchaseReturnService.cancel(request);

        billStoreWorkPreService.cancel(result.getSerialNo());

        return result;
    }

    @Override
    public PageResult<PurchaseReturnListResult> list(PurchaseReturnListRequest request) {
        if (CollectionUtils.isNotEmpty(UserContext.getUser().getRoles()) &&
                UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(ROLE_NAMES::contains)) {
            request.setStoreId(null);
        } else {
            request.setStoreId(UserContext.getUser().getStore().getId());
        }

        if (StringUtils.isNotBlank(request.getStockSn())) {

            List<Stock> stockList = stockService.findByStockSn(request.getStockSn());

            if (CollectionUtils.isNotEmpty(stockList)) {
                request.setStockIdList(stockList.stream().map(Stock::getId).collect(Collectors.toList()));
            } else {
                return PageResult.<PurchaseReturnListResult>builder().result(Arrays.asList()).totalCount(0).totalPage(0).build();
            }
        }

        Page<PurchaseReturnListResult> page = billPurchaseReturnService.page(request);

        return PageResult.<PurchaseReturnListResult>builder().result(page.getRecords()).totalCount(page.getTotal()).totalPage(page.getPages()).build();
    }

    @Override
    public PurchaseReturnDetailsResult details(PurchaseReturnDetailsRequest request) {

        PurchaseReturnDetailsResult result = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> billPurchaseReturnService.getOne(Wrappers.<BillPurchaseReturn>lambdaQuery()
                        .eq(BillPurchaseReturn::getStoreId, t.getStoreId())
                        .eq(BillPurchaseReturn::getId, t.getId()).or()
                        .eq(BillPurchaseReturn::getSerialNo, t.getSerialNo())))
                .map(PurchaseReturnConverter.INSTANCE::convertPurchaseReturnDetailsResult)
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_RETURN_NOT_EXIST));

        //对客户信息进行赋值
        CustomerPO customerPO = customerService.queryCustomerPO(result.getCustomerContactId());
        if (Objects.nonNull(customerPO)) {
            result.setCustomerName(customerPO.getCustomerName());
            result.setAccountName(customerPO.getAccountName());
            result.setBank(customerPO.getBank());
            result.setBankAccount(customerPO.getBankAccount());
            result.setContactName(customerPO.getContactName());
            result.setContactPhone(customerPO.getContactPhone());
            result.setContactAddress(customerPO.getContactAddress());
        }

        //对退货详情内列表数据 进行赋值

        List<BillPurchaseReturnLine> billPurchaseReturnLineList = billPurchaseReturnLineService
                .list(Wrappers.<BillPurchaseReturnLine>lambdaQuery()
                        .eq(BillPurchaseReturnLine::getPurchaseReturnId, result.getId()));

        //填充所在 采购主体
        Map<Integer, WatchDataFusion> watchDataFusionMap = goodsWatchService.getWatchDataFusionListByStockIds(billPurchaseReturnLineList.stream()
                        .map(BillPurchaseReturnLine::getStockId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getStockId, watchDataFusion -> watchDataFusion));

        Map<Integer, String> collectLocation = new HashMap<>(billPurchaseReturnLineList.size());

        result.setDetails(billPurchaseReturnLineList.stream().map(billPurchaseReturnLine -> {

            PurchaseReturnDetailsResult.PurchaseReturnLineVO purchaseReturnLineVO = PurchaseReturnConverter.INSTANCE.convertPurchaseReturnLineVO(billPurchaseReturnLine);

            WatchDataFusion watchDataFusion = watchDataFusionMap.getOrDefault(billPurchaseReturnLine.getStockId(), new WatchDataFusion());

            purchaseReturnLineVO.setModel(watchDataFusion.getModel());
            purchaseReturnLineVO.setPricePub(watchDataFusion.getPricePub());
            purchaseReturnLineVO.setMovement(watchDataFusion.getMovement());
            purchaseReturnLineVO.setWatchSize(watchDataFusion.getWatchSize());
            purchaseReturnLineVO.setSeriesName(watchDataFusion.getSeriesName());
            purchaseReturnLineVO.setBrandName(watchDataFusion.getBrandName());
            purchaseReturnLineVO.setStockSn(watchDataFusion.getStockSn());
            purchaseReturnLineVO.setAttachment(watchDataFusion.getAttachment());

            Map<Integer, String> collectSubject = purchaseSubjectService.listByIds(billPurchaseReturnLineList.stream().map(BillPurchaseReturnLine::getPurchaseSubjectId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
            purchaseReturnLineVO.setPurchaseSubjectName(collectSubject.get(billPurchaseReturnLine.getPurchaseSubjectId()));

            if (!collectLocation.containsKey(billPurchaseReturnLine.getLocationId())) {
                //查询门店名称
                collectLocation.put(billPurchaseReturnLine.getLocationId(), tagService.selectByStoreManagementId(billPurchaseReturnLine.getLocationId()).getTagName());
            }
            purchaseReturnLineVO.setLocationName(collectLocation.get(billPurchaseReturnLine.getLocationId()));
            return purchaseReturnLineVO;
        }).collect(Collectors.toList()));

        return result;
    }

    @Override
    public ImportResult<PurchaseReturnStockQueryImportResult> stockQueryImport(PurchaseReturnStockQueryImportRequest request) {

        Assert.notNull(request.getCustomerId(), "供应商不能为空");

        Set<String> errorList = new HashSet<>();

        StockListRequest build = StockListRequest.builder()
                .useScenario(StockListRequest.UseScenario.PURCHASE_RETURN)
                .stockStatus(StockStatusEnum.MARKETABLE.getValue())
                .customerId(request.getCustomerId())
                .build();
        build.setLimit(1);

        List<PurchaseReturnStockQueryImportResult> purchaseReturnStockQueryImportResultList = new ArrayList<>();

        for (PurchaseReturnStockQueryImportRequest.ImportDto importDto : request.getDataList()) {

            build.setStockSn(importDto.getStockSn());
            List<StockBaseInfo> records = billPurchaseService.listByReturn(build).getRecords();

            if (CollectionUtils.isEmpty(records)) {
                errorList.add(importDto.getStockSn());
            } else {
                StockBaseInfo stockBaseInfo = records.get(FlywheelConstant.INDEX);
                purchaseReturnStockQueryImportResultList.add(PurchaseReturnStockQueryImportResult.builder()
                        .goodsId(stockBaseInfo.getGoodsId())
                        .stockSn(stockBaseInfo.getStockSn())
                        .stockId(stockBaseInfo.getStockId())
                        .purchasePrice(stockBaseInfo.getPurchasePrice())
                        .purchaseReturnPrice(stockBaseInfo.getPurchasePrice())
                        .attachment(stockBaseInfo.getAttachment())
                        .locationId(stockBaseInfo.getLocationId())
                        .purchaseType(stockBaseInfo.getPurchaseType())
                        .purchaseSubjectId(stockBaseInfo.getPurchaseSubjectId())
                        .remark(stockBaseInfo.getRemark())
                        .build());
            }
        }

        if (CollectionUtils.isEmpty(purchaseReturnStockQueryImportResultList)) {
            return ImportResult.<PurchaseReturnStockQueryImportResult>builder()
                    .successList(purchaseReturnStockQueryImportResultList)
                    .errList(Lists.newArrayList(errorList))
                    .build();
        }


        /**
         * 商品位置
         */
        Map<Integer, String> shopMap = storeManagementService.selectInfoByIds(purchaseReturnStockQueryImportResultList.stream().map(PurchaseReturnStockQueryImportResult::getLocationId).filter(Objects::nonNull).distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));

        /**
         * 品牌，系列，型号
         */
        Map<Integer, WatchDataFusion> watchDataFusionMap = goodsWatchService.getWatchDataFusionListByGoodsIds(
                purchaseReturnStockQueryImportResultList.stream().map(PurchaseReturnStockQueryImportResult::getGoodsId).collect(Collectors.toList())).stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, watchDataFusion -> watchDataFusion));

        /**
         * 采购主体
         */
        Map<Integer, String> map = purchaseSubjectService.list(Wrappers.<PurchaseSubject>lambdaQuery().in(PurchaseSubject::getId, purchaseReturnStockQueryImportResultList.stream().
                        collect(Collectors.groupingBy(PurchaseReturnStockQueryImportResult::getPurchaseSubjectId)).keySet().stream().collect(Collectors.toList()))).stream().
                collect(Collectors.toMap(PurchaseSubject::getId, purchaseSubject -> purchaseSubject.getName()));

        for (PurchaseReturnStockQueryImportResult purchaseReturnStockQueryImportResult : purchaseReturnStockQueryImportResultList) {

            purchaseReturnStockQueryImportResult.setLocationName(shopMap.get(purchaseReturnStockQueryImportResult.getLocationId()));

            WatchDataFusion watchDataFusion = watchDataFusionMap.get(purchaseReturnStockQueryImportResult.getGoodsId());
            purchaseReturnStockQueryImportResult.setBrandName(watchDataFusion.getBrandName());
            purchaseReturnStockQueryImportResult.setSeriesName(watchDataFusion.getSeriesName());
            purchaseReturnStockQueryImportResult.setModel(watchDataFusion.getModel());

            purchaseReturnStockQueryImportResult.setPurchaseSubjectName(map.get(purchaseReturnStockQueryImportResult.getPurchaseSubjectId()));
        }

        return ImportResult.<PurchaseReturnStockQueryImportResult>builder()
                .successList(purchaseReturnStockQueryImportResultList)
                .errList(Lists.newArrayList(errorList))
                .build();
    }
}
