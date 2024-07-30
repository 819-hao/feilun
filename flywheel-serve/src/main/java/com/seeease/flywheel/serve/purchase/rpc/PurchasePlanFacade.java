package com.seeease.flywheel.serve.purchase.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableSet;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.IPurchasePlanFacade;
import com.seeease.flywheel.purchase.request.PurchasePlanCreateRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanDetailsRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanListRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanUpdateRequest;
import com.seeease.flywheel.purchase.result.*;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.entity.StoreManagement;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.purchase.convert.PurchasePlanConverter;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlan;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlanLine;
import com.seeease.flywheel.serve.purchase.service.BillPurchasePlanLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchasePlanService;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/8/8
 */
@DubboService(version = "1.0.0")
public class PurchasePlanFacade implements IPurchasePlanFacade {
    @Resource
    private TagService tagService;
    @Resource
    private BillPurchasePlanService purchasePlanService;
    @Resource
    private BillPurchasePlanLineService purchasePlanLineService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private StoreManagementService storeManagementService;

    private static final Set<Integer> USER_ROLE = ImmutableSet.of(562, 462);


    @Override
    public PurchasePlanCreateResult create(PurchasePlanCreateRequest request) {
        Assert.notNull(request, "入参request不能为空");
        Assert.isTrue(!CollectionUtils.isEmpty(request.getDetails()), "详情列表不能为空");
        if (Objects.isNull(request.getDemanderStoreId())) {
            request.setDemanderStoreId(UserContext.getUser().getStore().getId());
        }
        request.setStoreId(UserContext.getUser().getStore().getId());
        request.setEnableChangeTime(new Date());
        //型号信息
        Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByGoodsIds(request.getDetails().stream()
                        .map(PurchasePlanCreateRequest.BillPurchasePlanLineDto::getGoodsId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));
        request.getDetails().forEach(p -> {
            if (goodsMap.containsKey(p.getGoodsId())) {
                WatchDataFusion watchDataFusion = goodsMap.get(p.getGoodsId());
                p.setPricePub(watchDataFusion.getPricePub());
                p.setCurrentPrice(watchDataFusion.getCurrentPrice());
                p.setTwoZeroFullPrice(watchDataFusion.getTwoZeroFullPrice());
                p.setTwoTwoFullPrice(watchDataFusion.getTwoTwoFullPrice());
            }
        });

        return purchasePlanService.create(request);
    }

    @Override
    public PageResult<PurchasePlanListResult> list(PurchasePlanListRequest request) {
        Page<PurchasePlanListResult> page = purchasePlanService.listByRequest(request);
        if (Objects.isNull(page) || CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<PurchasePlanListResult>builder()
                    .totalCount(0)
                    .totalPage(0)
                    .result(Collections.EMPTY_LIST).build();
        }
        Map<Integer, String> map = storeManagementService.selectInfoByIds(storeManagementService.list()
                        .stream().map(StoreManagement::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));
        page.getRecords().forEach(r -> {
            r.setDemanderStoreName(map.get(r.getDemanderStoreId()));
            r.setStoreName(map.get(r.getStoreId()));
        });
        return PageResult.<PurchasePlanListResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PurchasePlanDetailsResult details(PurchasePlanDetailsRequest request) {
        BillPurchasePlan purchasePlan = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> purchasePlanService.getOne(Wrappers.<BillPurchasePlan>lambdaQuery()
                        .eq(BillPurchasePlan::getId, t.getId())
                        .or().eq(BillPurchasePlan::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

        PurchasePlanDetailsResult result = PurchasePlanConverter.INSTANCE.convertPurchaseDetailsResult(purchasePlan);


        //需求门店
        Tag tag = Optional.ofNullable(purchasePlan.getDemanderStoreId())
                .map(tagService::selectByStoreManagementId)
                .orElse(null);
        if (Objects.nonNull(tag)) {
            result.setDemanderStoreName(tag.getTagName());
        }

        List<BillPurchasePlanLine> lineDetailsVOList = purchasePlanLineService.list(new LambdaQueryWrapper<BillPurchasePlanLine>()
                .eq(BillPurchasePlanLine::getPlanId, request.getId()));

        result.setLines(PurchasePlanConverter.INSTANCE.convertPurchasePlanLineVO(lineDetailsVOList));

        //型号信息
        Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByGoodsIds(lineDetailsVOList.stream()
                        .map(BillPurchasePlanLine::getGoodsId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        result.getLines().forEach(p -> {
            if (goodsMap.containsKey(p.getGoodsId())) {
                WatchDataFusion watchDataFusion = goodsMap.get(p.getGoodsId());
                p.setBrandName(watchDataFusion.getBrandName());
                p.setSeriesName(watchDataFusion.getSeriesName());
                p.setModel(watchDataFusion.getModel());
                p.setImage(watchDataFusion.getImage());
            }
        });

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PurchasePlanUpdateRequest request) {
        if (!USER_ROLE.contains(UserContext.getUser().getId())) {
            throw new OperationRejectedException(OperationExceptionCode.NO_MODIFICATION_ALLOWED);
        }
        Assert.notNull(request, "入参request不能为空");
        BillPurchasePlan purchasePlan = purchasePlanService.getById(request.getId());
        //允许时间内才能修改
//        if (DateUtils.compareTime(DateUtils.getNowDate(), purchasePlan.getEnableChangeTime())) {
//            throw new OperationRejectedException(OperationExceptionCode.NO_MODIFICATION_ALLOWED);
//        }
        //不是建单人不允许修改
//        if (!purchasePlan.getCreatedId().equals(UserContext.getUser().getId())) {
//            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER_CREATE);
//        }

        List<BillPurchasePlanLine> lineList = request.getDetails().stream()
                .map(PurchasePlanConverter.INSTANCE::convertPurchaseDetailPlanUpdate).collect(Collectors.toList());
        purchasePlanLineService.updateBatchById(lineList);

        BillPurchasePlan plan = PurchasePlanConverter.INSTANCE.convertPurchasePlanUpdate(request);
        plan.setId(request.getId());
        plan.setPurchaseNumber(lineList.stream().mapToInt(BillPurchasePlanLine::getPlanNumber).sum());
        purchasePlanService.updateById(plan);
    }

    @Override
    public PageResult<PurchasePlanExportResult> export(PurchasePlanListRequest request) {
        //导出手选择项
        if (Objects.nonNull(request.getDocBatchIds()) && request.getDocBatchIds().size() == 0) {
            request.setDocBatchIds(null);
        }
        Page<PurchasePlanExportResult> page = purchasePlanService.export(request);

        Map<Integer, String> map = storeManagementService.selectInfoByIds(storeManagementService.list()
                        .stream().map(StoreManagement::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));
        //型号信息
        Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByGoodsIds(page.getRecords().stream()
                        .map(PurchasePlanExportResult::getGoodsId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));
        page.getRecords().forEach(p -> {
            p.setDemanderStoreName(map.get(p.getDemanderStoreId()));
            p.setStoreName(map.get(p.getStoreId()));
            if (goodsMap.containsKey(p.getGoodsId())) {
                WatchDataFusion watchDataFusion = goodsMap.get(p.getGoodsId());
                p.setBrandName(watchDataFusion.getBrandName());
                p.setSeriesName(watchDataFusion.getSeriesName());
                p.setModel(watchDataFusion.getModel());
                p.setImage(watchDataFusion.getImage());
            }
        });
        return PageResult.<PurchasePlanExportResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

}
