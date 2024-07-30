package com.seeease.flywheel.serve.purchase.rpc;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentDetailRequest;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.IPurchaseTaskFacade;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.flywheel.purchase.result.*;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.dict.service.DictDataService;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.purchase.convert.PurchaseTaskConverter;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseTask;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.enums.TaskStateEnum;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseTaskService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.LoginStore;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/10/25 15:32
 */
@DubboService(version = "1.0.0")
@Slf4j
public class PurchaseTaskFacade implements IPurchaseTaskFacade {

    /**
     * 主单service
     */
    @Resource
    private BillPurchaseTaskService baseService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private UserService userService;

    @Resource
    private TagService tagService;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;

    @Resource
    private IPurchaseFacade purchaseFacade;


    @Override
    public PurchaseTaskCreateResult create(PurchaseTaskCreateRequest request) {

        BillPurchaseTask billPurchaseTask = PurchaseTaskConverter.INSTANCE.convert(request);

        billPurchaseTask.setStoreId(UserContext.getUser().getStore().getId());

        //字典处理
//        List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));

        //封装附件id
//        billPurchaseTask.setAttachmentList(convert(dataList, request.getAttachmentMap()));

        billPurchaseTask.setTaskState(TaskStateEnum.WAIT_FOR_RECEIVING);
        billPurchaseTask.setSerialNo(SerialNoGenerator.generatePurchaseTaskSerialNo());
        baseService.save(billPurchaseTask);

        return PurchaseTaskCreateResult.builder()
                .id(billPurchaseTask.getId())
                .serialNo(billPurchaseTask.getSerialNo())
                .purchaseJoin(Optional.ofNullable(userService.getById(request.getPurchaseJoinId())).map(User::getUserid).orElse(""))
                .build();
    }

    @Override
    public PageResult<PurchaseTaskPageResult> list(PurchaseTaskPageRequest request) {
        LoginUser loginUser = UserContext.getUser();
        List<LoginRole> loginRoleList = loginUser.getRoles();
        LoginStore loginStore = loginUser.getStore();

        LambdaQueryWrapper<BillPurchaseTask> wrapper = Wrappers.<BillPurchaseTask>lambdaQuery()
                .like(Optional.ofNullable(request.getKeyword()).isPresent() && StringUtils.isNotBlank(request.getKeyword()), BillPurchaseTask::getSerialNo, request.getKeyword())
                .eq(Optional.ofNullable(request.getStoreId()).isPresent(), BillPurchaseTask::getStoreId, request.getStoreId())
                .eq(Optional.ofNullable(request.getSerialNo()).isPresent(), BillPurchaseTask::getSerialNo, request.getSerialNo())
                .between(Optional.ofNullable(request.getStartTime()).isPresent() && Optional.ofNullable(request.getEndTime()).isPresent(), BillPurchaseTask::getCreatedTime, request.getStartTime(), request.getEndTime());
        if (ObjectUtils.isNotEmpty(request.getTaskState())) {
            wrapper.eq(Optional.ofNullable(request.getTaskState()).isPresent(), BillPurchaseTask::getTaskState, TaskStateEnum.fromCode(request.getTaskState()));
        }

        if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("admin").contains(loginRole.getRoleName()))) {
            //不处理
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("采购员").contains(loginRole.getRoleName())) && loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("店员").contains(loginRole.getRoleName()))) {
            wrapper.eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("采购员")), BillPurchaseTask::getPurchaseJoinId, loginUser.getId())
                    .or(wp -> wp.eq(loginStore.getId().compareTo(1) >= 0, BillPurchaseTask::getStoreId, loginStore.getId())
                            .eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("店员")), BillPurchaseTask::getCreatedId, loginUser.getId()));
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("采购员").contains(loginRole.getRoleName()))) {
            wrapper.eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("采购员")), BillPurchaseTask::getPurchaseJoinId, loginUser.getId());
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("店员").contains(loginRole.getRoleName()))) {
            wrapper.eq(loginStore.getId().compareTo(1) >= 0, BillPurchaseTask::getStoreId, loginStore.getId()).eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("店员")), BillPurchaseTask::getCreatedId, loginUser.getId());
        } else {
            return PageResult.<PurchaseTaskPageResult>builder()
                    .result(Arrays.asList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        wrapper.orderByDesc(BillPurchaseTask::getId);
        Page<BillPurchaseTask> page = baseService.page(Page.of(request.getPage(), request.getLimit()), wrapper);

        //采购建单参数
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return PageResult.<PurchaseTaskPageResult>builder()
                    .result(Arrays.asList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }
        //查型号
        Map<Integer, WatchDataFusion> goodsWatchMap = goodsWatchService.getWatchDataFusionListByGoodsIds(page.getRecords()
                        .stream()
                        .map(BillPurchaseTask::getGoodsId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        Map<Integer, Tag> tagMap = new HashMap<>();
        Map<Integer, User> userMap = new HashMap<>();

        List<PurchaseTaskPageResult> result = Optional.ofNullable(page.getRecords())
                .orElse(Lists.newArrayList())
                .stream()
                .map(t -> {
                    //转义
                    PurchaseTaskPageResult r = PurchaseTaskConverter.INSTANCE.convert(t);
                    //需求门店
                    if (tagMap.containsKey(t.getStoreId())) {
                        r.setStoreName(tagMap.get(t.getStoreId()).getTagName());
                    } else {
                        Tag tag = tagService.selectByStoreManagementId(t.getStoreId());
                        r.setStoreName(Optional.ofNullable(tag).map(Tag::getTagName).orElse(""));
                        tagMap.put(t.getStoreId(), tag);
                    }

                    //基本参数
                    WatchDataFusion goods = goodsWatchMap.getOrDefault(r.getGoodsId(), new WatchDataFusion());
                    r.setBrandName(goods.getBrandName());
                    r.setSeriesName(goods.getSeriesName());
                    r.setModel(goods.getModel());
                    r.setPricePub(goods.getPricePub());

                    //申请打款单
                    if (ObjectUtils.isNotEmpty(r.getApplyFinancialPaymentId())) {
                        ApplyFinancialPaymentDetailRequest applyFinancialPaymentDetailRequest = new ApplyFinancialPaymentDetailRequest();
                        applyFinancialPaymentDetailRequest.setId(r.getApplyFinancialPaymentId());
                        r.setApplyFinancialPaymentDetailResult(applyFinancialPaymentService.detail(applyFinancialPaymentDetailRequest));
                    }

                    //实际采购人
                    if (userMap.containsKey(t.getPurchaseJoinId())) {
                        r.setPurchaseJoinBy(userMap.get(t.getPurchaseJoinId()).getName());
                    } else {
                        User user = userService.getById(r.getPurchaseJoinId());
                        r.setPurchaseJoinBy(Optional.ofNullable(user).map(User::getName).orElse(""));
                        userMap.put(t.getPurchaseJoinId(), user);
                    }
                    return r;
                })
                .collect(Collectors.toList());

        return PageResult.<PurchaseTaskPageResult>builder()
                .result(result)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public PurchaseTaskDetailsResult details(PurchaseTaskDetailsRequest request) {

        BillPurchaseTask billPurchaseTask = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> baseService.getOne(Wrappers.<BillPurchaseTask>lambdaQuery()
                        .eq(BillPurchaseTask::getId, t.getId())
                        .or().eq(BillPurchaseTask::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TASK_BILL_NOT_EXIST));

        PurchaseTaskDetailsResult result = PurchaseTaskConverter.INSTANCE.convertDetail(billPurchaseTask);

//        List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));
//
//        result.setAttachment(convert(dataList, billPurchaseTask.getAttachmentList(), billPurchaseTask.getIsCard(), billPurchaseTask.getWarrantyDate()));

//        List<DictData> dictDataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));
//
//        Map<String, List<DictData>> collect = dictDataList.stream().filter(t -> billPurchaseTask.getAttachmentList().contains(t.getDictCode().intValue())).collect(Collectors.groupingBy(DictData::getDictType));
//        Map<String, List<Integer>> attachmentMap = new HashMap<>();
//        for (Map.Entry<String, List<DictData>> entry : collect.entrySet()) {
//            attachmentMap.put(entry.getKey(), entry.getValue().stream().map(dictData -> Integer.parseInt(dictData.getDictValue())).collect(Collectors.toList()));
//        }
//        result.setAttachmentMap(attachmentMap);
        Optional.ofNullable(goodsWatchService.getWatchDataFusionListByGoodsIds(Arrays.asList(billPurchaseTask.getGoodsId()))).map(r -> {
            Optional.ofNullable(r.stream().filter(Objects::nonNull).findFirst().orElse(null)).map(t -> {
                result.setBrandName(t.getBrandName());
                result.setSeriesName(t.getSeriesName());
                result.setModel(t.getModel());
                result.setPricePub(t.getPricePub());
                result.setSeriesType(t.getSeriesType());
                return t;
            }).orElse(null);
            return r;
        }).orElse(null);

        result.setApplyFinancialPaymentDetailResult(Optional.ofNullable(result.getApplyFinancialPaymentId()).map(r -> {
            ApplyFinancialPaymentDetailRequest applyFinancialPaymentDetailRequest = new ApplyFinancialPaymentDetailRequest();
            applyFinancialPaymentDetailRequest.setId(result.getApplyFinancialPaymentId());
            return applyFinancialPaymentService.detail(applyFinancialPaymentDetailRequest);
        }).orElse(null));

        result.setPurchaseDetailsResult(Optional.ofNullable(result.getPurchaseId()).map(r -> {
            PurchaseDetailsRequest purchaseDetailsRequest = new PurchaseDetailsRequest();
            purchaseDetailsRequest.setId(result.getPurchaseId());
            try {

                return purchaseFacade.details(purchaseDetailsRequest);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }

            return null;
        }).orElse(null));

        //需求门店
        result.setStoreName(Optional.ofNullable(tagService.selectByStoreManagementId(result.getStoreId())).map(Tag::getTagName).orElse(""));
        //名称
        result.setPurchaseJoinBy(Optional.ofNullable(userService.getById(result.getPurchaseJoinId())).map(User::getName).orElse(""));

        result.setRealityTaskNumber(ObjectUtils.isEmpty(result.getPurchaseDetailsResult()) ? 0 : result.getPurchaseDetailsResult().getLines().size());

        return result;
    }

    @Override
    public PurchaseTaskCheckResult check(PurchaseTaskCheckRequest request) {

        BillPurchaseTask billPurchaseTask = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> baseService.getOne(Wrappers.<BillPurchaseTask>lambdaQuery()
                        .eq(BillPurchaseTask::getId, t.getId())
                        .or().eq(BillPurchaseTask::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TASK_BILL_NOT_EXIST));

        BillPurchaseTask purchaseTask = new BillPurchaseTask();
        purchaseTask.setId(billPurchaseTask.getId());

        if (WhetherEnum.YES == WhetherEnum.fromValue(request.getCheck())) {
            //通过
            purchaseTask.setTransitionStateEnum(TaskStateEnum.TransitionEnum.LOGISTICS_RECEIVING);
            baseService.updateByState(purchaseTask);
        } else {
            //取消
            purchaseTask.setTransitionStateEnum(TaskStateEnum.TransitionEnum.WORK_CANCEL_RECEIVING);
            baseService.updateByState(purchaseTask);
        }
        return PurchaseTaskCheckResult.builder().serialNo(billPurchaseTask.getSerialNo()).id(billPurchaseTask.getId()).build();
    }

    @Override
    public PurchaseTaskEditResult edit(PurchaseTaskEditRequest request) {

        BillPurchaseTask billPurchaseTask = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> baseService.getOne(Wrappers.<BillPurchaseTask>lambdaQuery()
                        .eq(BillPurchaseTask::getId, t.getId())
                        .or().eq(BillPurchaseTask::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TASK_BILL_NOT_EXIST));

        BillPurchaseTask purchaseTask = PurchaseTaskConverter.INSTANCE.convert(request);

        //字典处理
//        List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));

        //封装附件id
//        purchaseTask.setAttachmentList(convert(dataList, request.getAttachmentMap()));
        purchaseTask.setSerialNo(null);
        purchaseTask.setId(billPurchaseTask.getId());

        baseService.updateById(purchaseTask);

        return PurchaseTaskEditResult.builder().id(billPurchaseTask.getId()).serialNo(billPurchaseTask.getSerialNo()).build();
    }

    @Override
    public Map<Integer, Long> groupBy() {

        LoginUser loginUser = UserContext.getUser();

        List<LoginRole> loginRoleList = loginUser.getRoles();
        LoginStore loginStore = loginUser.getStore();

        LambdaQueryWrapper<BillPurchaseTask> wrapper = Wrappers.<BillPurchaseTask>lambdaQuery();

        if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("admin").contains(loginRole.getRoleName()))) {
            //不处理
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("采购员").contains(loginRole.getRoleName())) && loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("店员").contains(loginRole.getRoleName()))) {
            wrapper.eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("采购员")), BillPurchaseTask::getPurchaseJoinId, loginUser.getId())
                    .or(wp -> wp.eq(loginStore.getId().compareTo(1) >= 0, BillPurchaseTask::getStoreId, loginStore.getId())
                            .eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("店员")), BillPurchaseTask::getCreatedId, loginUser.getId()));
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("采购员").contains(loginRole.getRoleName()))) {
            wrapper.eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("采购员")), BillPurchaseTask::getPurchaseJoinId, loginUser.getId());
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("店员").contains(loginRole.getRoleName()))) {
            wrapper.eq(loginStore.getId().compareTo(1) >= 0, BillPurchaseTask::getStoreId, loginStore.getId()).eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("店员")), BillPurchaseTask::getCreatedId, loginUser.getId());
        } else {
            return new HashMap<>();
        }

        List<BillPurchaseTask> billPurchaseTaskList = baseService.list(wrapper);

        Map<Integer, Long> collect = billPurchaseTaskList.stream().collect(Collectors.groupingBy(e -> e.getTaskState().getValue(), Collectors.counting()));

        collect.put(0, (long) billPurchaseTaskList.size());

        return collect;
    }

    @Override
    public List<PurchaseTaskExportResult> export(PurchaseTaskPageRequest request) {

        LoginUser loginUser = UserContext.getUser();
        List<LoginRole> loginRoleList = loginUser.getRoles();
        LoginStore loginStore = loginUser.getStore();

        LambdaQueryWrapper<BillPurchaseTask> wrapper = Wrappers.<BillPurchaseTask>lambdaQuery()
                .like(Optional.ofNullable(request.getKeyword()).isPresent() && StringUtils.isNotBlank(request.getKeyword()), BillPurchaseTask::getSerialNo, request.getKeyword())
                .eq(Optional.ofNullable(request.getStoreId()).isPresent(), BillPurchaseTask::getStoreId, request.getStoreId())
                .eq(Optional.ofNullable(request.getSerialNo()).isPresent(), BillPurchaseTask::getSerialNo, request.getSerialNo())
                .between(Optional.ofNullable(request.getStartTime()).isPresent() && Optional.ofNullable(request.getEndTime()).isPresent(), BillPurchaseTask::getCreatedTime, request.getStartTime(), request.getEndTime());
        if (ObjectUtils.isNotEmpty(request.getTaskState())) {
            wrapper.eq(Optional.ofNullable(request.getTaskState()).isPresent(), BillPurchaseTask::getTaskState, TaskStateEnum.fromCode(request.getTaskState()));
        }

        if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("admin").contains(loginRole.getRoleName()))) {
            //不处理
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("采购员").contains(loginRole.getRoleName()))) {
            wrapper.eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("采购员")), BillPurchaseTask::getPurchaseJoinId, loginUser.getId());
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("店员").contains(loginRole.getRoleName()))) {
            wrapper.eq(loginStore.getId().compareTo(1) > 0, BillPurchaseTask::getStoreId, loginStore.getId()).eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("店员")), BillPurchaseTask::getCreatedId, loginUser.getId());
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("采购员").contains(loginRole.getRoleName())) && loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("店员").contains(loginRole.getRoleName()))) {
            wrapper.eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("采购员")), BillPurchaseTask::getPurchaseJoinId, loginUser.getId())
                    .or(wp -> wp.eq(loginStore.getId().compareTo(1) > 0, BillPurchaseTask::getStoreId, loginStore.getId())
                            .eq(loginRoleList.stream().anyMatch(loginRole -> loginRole.getRoleName().contains("店员")), BillPurchaseTask::getCreatedId, loginUser.getId()));
        } else {
            return Arrays.asList();
        }

        List<BillPurchaseTask> billPurchaseTaskList = baseService.list(wrapper);

        if (CollectionUtils.isEmpty(billPurchaseTaskList)) {
            return Arrays.asList();
        }

        //查型号
        Map<Integer, WatchDataFusion> goodsWatchMap = goodsWatchService.getWatchDataFusionListByGoodsIds(billPurchaseTaskList
                        .stream()
                        .map(BillPurchaseTask::getGoodsId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        Map<Integer, Tag> tagMap = new HashMap<>();
        Map<Integer, User> userMap = new HashMap<>();

        return billPurchaseTaskList.stream().map(billPurchaseTask -> {
            //插入采购需求单数据

            //需求门店
            if (!tagMap.containsKey(billPurchaseTask.getStoreId())) {
                Tag tag = tagService.selectByStoreManagementId(billPurchaseTask.getStoreId());
                tagMap.put(billPurchaseTask.getStoreId(), tag);
            }
            //基本参数
            WatchDataFusion goods = goodsWatchMap.getOrDefault(billPurchaseTask.getGoodsId(), new WatchDataFusion());

            //实际采购人
            if (!userMap.containsKey(billPurchaseTask.getPurchaseJoinId())) {
                User user = userService.getById(billPurchaseTask.getPurchaseJoinId());
                userMap.put(billPurchaseTask.getPurchaseJoinId(), user);
            }

            return Optional.ofNullable(billPurchaseTask.getPurchaseId()).map(r -> {
                PurchaseDetailsRequest purchaseDetailsRequest = new PurchaseDetailsRequest();
                purchaseDetailsRequest.setId(billPurchaseTask.getPurchaseId());

                PurchaseDetailsResult details = null;

                try {
                    details = purchaseFacade.details(purchaseDetailsRequest);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                return Optional.ofNullable(details).map(a -> a.getLines().stream().map(purchaseLineVO -> PurchaseTaskExportResult.builder()
                                .createdTime(DateUtil.formatDateTime(billPurchaseTask.getCreatedTime()))
                                .createdBy(billPurchaseTask.getCreatedBy())
                                .storeName(Optional.ofNullable(tagService.selectByStoreManagementId(billPurchaseTask.getStoreId())).map(Tag::getTagName).orElse(""))
                                .brandName(goods.getBrandName())
                                .seriesName(goods.getSeriesName())
                                .model(goods.getModel())
                                .taskAttachment(Optional.ofNullable(billPurchaseTask.getIsCard()).map(p -> {
                                            if (p.equals(0)) {
                                                return "单表";
                                            } else if (p.equals(1)) {
                                                return "全套";
                                            } else {
                                                return "无";
                                            }
                                        })
                                        .orElse("无"))

                                .deliveryTime(String.format("%s年-%s年", billPurchaseTask.getDeliveryTimeStart(), billPurchaseTask.getDeliveryTimeEnd()))
                                .clinchPrice(billPurchaseTask.getClinchPrice())
                                .clinchRate(billPurchaseTask.getClinchRateStart() + "%-" + billPurchaseTask.getClinchRateEnd() + "%")
                                .purchaseJoinBy(Optional.ofNullable(userService.getById(billPurchaseTask.getPurchaseJoinId())).map(User::getName).orElse(""))
                                //采购单数据
                                .stockSn(purchaseLineVO.getStockSn())
                                .attachment(purchaseLineVO.getAttachment())
                                .purchasePrice(purchaseLineVO.getPurchasePrice())
                                .lineStateDesc(PurchaseLineStateEnum.fromValue(purchaseLineVO.getPurchaseLineState()).getDesc())
                                .remarks(billPurchaseTask.getRemarks())
                                .build()).collect(Collectors.toList()))
                        .orElse(new ArrayList<>())
                        ;
            }).orElse(Arrays.asList(PurchaseTaskExportResult.builder()
                    //没有采购单
                    .createdTime(DateUtil.formatDateTime(billPurchaseTask.getCreatedTime()))
                    .createdBy(billPurchaseTask.getCreatedBy())
                    .storeName(Optional.ofNullable(tagService.selectByStoreManagementId(billPurchaseTask.getStoreId())).map(Tag::getTagName).orElse(""))
                    .brandName(goods.getBrandName())
                    .seriesName(goods.getSeriesName())
                    .model(goods.getModel())
                    .taskAttachment(Optional.ofNullable(billPurchaseTask.getIsCard()).map(p -> {
                                if (p.equals(0)) {
                                    return "单表";
                                } else if (p.equals(1)) {
                                    return "全套";
                                } else {
                                    return "无";
                                }
                            })
                            .orElse("无"))

                    .deliveryTime(String.format("%s年-%s年", billPurchaseTask.getDeliveryTimeStart(), billPurchaseTask.getDeliveryTimeEnd()))
                    .clinchPrice(billPurchaseTask.getClinchPrice())
                    .clinchRate(billPurchaseTask.getClinchRateStart() + "%-" + billPurchaseTask.getClinchRateEnd() + "%")
                    .purchaseJoinBy(Optional.ofNullable(userService.getById(billPurchaseTask.getPurchaseJoinId())).map(User::getName).orElse(""))
                    .remarks(billPurchaseTask.getRemarks())
                    .build()));
        }).filter(c -> Objects.nonNull(c) && CollectionUtils.isNotEmpty(c)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public PurchaseTaskCancelResult cancel(PurchaseTaskCancelRequest request) {

        BillPurchaseTask purchaseTask = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> baseService.getOne(Wrappers.<BillPurchaseTask>lambdaQuery()
                        .eq(BillPurchaseTask::getId, t.getId())
                        .or().eq(BillPurchaseTask::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_TASK_NOT_EXIST));

        if (TaskStateEnum.RECEIVED != purchaseTask.getTaskState()) {
            throw new BusinessException(ExceptionCode.OPT_NOT_SUPPORT);
        }

        BillPurchaseTask task = new BillPurchaseTask();
        task.setId(purchaseTask.getId());
        task.setTransitionStateEnum(TaskStateEnum.TransitionEnum.RECEIVED_CANCEL_RECEIVING);

        baseService.updateByState(task);

        return PurchaseTaskConverter.INSTANCE.convertCancel(purchaseTask);
    }
}
