package com.seeease.flywheel.serve.fix.rpc;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.*;
import com.seeease.flywheel.fix.result.*;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.fix.convert.FixConverter;
import com.seeease.flywheel.serve.fix.convert.FixProjectConverter;
import com.seeease.flywheel.serve.fix.convert.LogFixOptConverter;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.entity.LogFixOpt;
import com.seeease.flywheel.serve.fix.enums.FixStateEnum;
import com.seeease.flywheel.serve.fix.enums.OrderTypeEnum;
import com.seeease.flywheel.serve.fix.enums.TagTypeEnum;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.fix.service.FixProjectService;
import com.seeease.flywheel.serve.fix.service.LogFixOptService;
import com.seeease.flywheel.serve.goods.entity.AttachmentConsumeLog;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.AttachmentConsumeLogService;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.maindata.entity.FixSite;
import com.seeease.flywheel.serve.maindata.entity.ShopMemberDto;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.FixSiteService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.LoginStore;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/18 10:46
 */
@DubboService(version = "1.0.0")
public class FixFacade implements IFixFacade {

    @Resource
    private BillFixService billFixService;

    @Resource
    private LogFixOptService logFixOptService;

    @Resource
    private UserService userService;

    @Resource
    private FixProjectService projectService;

    @Resource
    private StoreManagementService storeManagementService;

    @Resource
    private BillPurchaseService billPurchaseService;

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private TagService tagService;

    @Resource
    private AttachmentConsumeLogService attachmentConsumeLogService;

    @Resource
    private BrandService brandService;

    @Resource
    private FixSiteService fixSiteService;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Override
    public List<FixReceiveListResult> receive(List<FixReceiveRequest.FixReceiveListRequest> request) {
        List<FixReceiveListResult> receiveListResultList = billFixService.receive(request);

        for (FixReceiveRequest.FixReceiveListRequest fixReceiveListRequest : request) {
            savaLog(fixReceiveListRequest.getFixId());
        }
        //查询门店店长用户id
        receiveListResultList.forEach(receiveListResult -> {
            if (Arrays.asList(BusinessBillTypeEnum.TH_CG_DJ.getValue(), BusinessBillTypeEnum.TH_CG_BH.getValue(), BusinessBillTypeEnum.TH_CG_QK.getValue(), BusinessBillTypeEnum.TH_CG_DJTP.getValue()).contains(receiveListResult.getFixSource())) {

                BillPurchase billPurchase = Optional.ofNullable(Optional.ofNullable(receiveListResult)
                                .filter(t -> Objects.nonNull(t.getStockId())).map(t -> billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId())))
                                .orElse(null))
                        .filter(t -> Objects.nonNull(t.getPurchaseId())).map(t -> billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getId, t.getPurchaseId())))
                        .orElse(null);
                if (ObjectUtils.isNotEmpty(billPurchase) && ObjectUtils.isNotEmpty(billPurchase.getDemanderStoreId())) {
                    receiveListResult.setShopId(billPurchase.getDemanderStoreId());
                }
            }
        });

        return receiveListResultList;
    }

    /**
     * 接修
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FixRepairResult repair(FixRepairRequest request) {

        FixRepairResult result = billFixService.repair(request);

        //配件增量数据
        editAttachment(request.getAttachmentIdList(),
                result.getSerialNo(),
                result.getId(),
                result.getAttachmentCostPrice()
        );

        //插入数据
        savaLog(request.getFixId());

        if (Objects.nonNull(result.getFixSource()) && Arrays.asList(BusinessBillTypeEnum.TH_CG_DJ.getValue(), BusinessBillTypeEnum.TH_CG_BH.getValue(), BusinessBillTypeEnum.TH_CG_QK.getValue(), BusinessBillTypeEnum.TH_CG_DJTP.getValue()).contains(result.getFixSource())) {
            BillPurchase billPurchase = Optional.ofNullable(Optional.ofNullable(result)
                            .filter(t -> Objects.nonNull(t.getStockId())).map(t -> billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId())))
                            .orElse(null))
                    .filter(t -> Objects.nonNull(t.getPurchaseId())).map(t -> billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getId, t.getPurchaseId())))
                    .orElse(null);
            if (ObjectUtils.isNotEmpty(billPurchase) && ObjectUtils.isNotEmpty(billPurchase.getDemanderStoreId())) {
                result.setShopId(billPurchase.getDemanderStoreId());
            }
        }

        return result;
    }

    @Override
    public FixFinishResult finish(FixFinishRequest request) {

        FixFinishResult result = billFixService.finish(request);
        //配件增量数据
        editAttachment(request.getAttachmentIdList(),
                result.getSerialNo(),
                result.getId(),
                result.getAttachmentCostPrice()
        );

        savaLog(request.getFixId());

        if (Objects.nonNull(result.getFixSource()) && Arrays.asList(BusinessBillTypeEnum.TH_CG_DJ.getValue(), BusinessBillTypeEnum.TH_CG_BH.getValue(), BusinessBillTypeEnum.TH_CG_QK.getValue(), BusinessBillTypeEnum.TH_CG_DJTP.getValue()).contains(result.getFixSource())) {

            BillPurchase billPurchase = Optional.ofNullable(Optional.ofNullable(result)
                            .filter(t -> Objects.nonNull(t.getStockId())).map(t -> billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId())))
                            .orElse(null))
                    .filter(t -> Objects.nonNull(t.getPurchaseId())).map(t -> billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getId, t.getPurchaseId())))
                    .orElse(null);
            if (ObjectUtils.isNotEmpty(billPurchase) && ObjectUtils.isNotEmpty(billPurchase.getDemanderStoreId())) {
                result.setShopId(billPurchase.getDemanderStoreId());
            }
        }
        return result;
    }

    @Override
    public PageResult<FixListResult> list(FixListRequest request) {

        LoginUser loginUser = UserContext.getUser();
        List<LoginRole> loginRoleList = loginUser.getRoles();
        LoginStore loginStore = loginUser.getStore();

        //非admin,皆是当前门店
        if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("admin").contains(loginRole.getRoleName()))
                && loginStore.getId().equals(FlywheelConstant._ZB_ID)
        ) {
            request.setStoreId(Optional.ofNullable(request.getFixSiteId())
                    .filter(v -> v != -1)
                    .map(fixSiteService::getById)
                    .filter(Objects::nonNull)
                    .map(FixSite::getOriginStoreId)
                    //看全部
                    .orElse(null));
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("接修员", "维修员").contains(loginRole.getRoleName())) && loginStore.getId().equals(FlywheelConstant._ZB_ID)) {
            //门店只能看门店的 重置掉
            request.setFixSiteId(null);
            request.setStoreId(loginStore.getId());
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("门店接修员", "门店维修员").contains(loginRole.getRoleName())) && loginStore.getId().compareTo(FlywheelConstant._ZB_ID) > 0) {
            //门店只能看门店的 重置掉
            request.setFixSiteId(null);
            request.setStoreId(loginStore.getId());
        } else {
            return PageResult.<FixListResult>builder().result(Arrays.asList()).totalCount(0).totalPage(0).build();
        }

        /**
         * 参数检验
         */
        validRequest(request);

        Page<BillFix> page = billFixService.page(new Page<>(request.getPage(), request.getLimit()), wrapper(request));

        Map<Integer, FixSite> fixSiteMap = new HashMap<>(100);
        Map<Integer, Tag> tagMap = new HashMap<>(100);
        Map<Integer, User> userMap = new HashMap<>();

        List<FixProjectResult> projectResultList = Optional.ofNullable(projectService.list(null))
                .orElse(Lists.newArrayList())
                .stream()
                .map(FixProjectConverter.INSTANCE::convertFixProjectResult)
                .collect(Collectors.toList());

        List<FixListResult> collect = page.getRecords().stream().map(billFix -> {

            FixListResult result = FixConverter.INSTANCE.convertFixListResult(billFix);

            if (Objects.nonNull(billFix.getStoreId()) && !fixSiteMap.containsKey(billFix.getStoreId())) {
                fixSiteMap.put(billFix.getStoreId(), fixSiteService.getOne(Wrappers.<FixSite>lambdaQuery().eq(FixSite::getOriginStoreId, billFix.getStoreId())));
            }
            result.setFixSiteName(Objects.nonNull(billFix.getStoreId()) ? fixSiteMap.get(billFix.getStoreId()).getSiteName() : "");

            if (Objects.nonNull(billFix.getStoreId()) && !tagMap.containsKey(billFix.getStoreId())) {
                tagMap.put(billFix.getStoreId(), tagService.selectByStoreManagementId(billFix.getStoreId()));
            }
            result.setStoreName(Objects.nonNull(billFix.getStoreId()) ? tagMap.get(billFix.getStoreId()).getTagName() : "");

            if (Objects.nonNull(billFix.getMaintenanceMasterId()) && !userMap.containsKey(billFix.getMaintenanceMasterId())) {
                userMap.put(billFix.getMaintenanceMasterId(), userService.getById(billFix.getMaintenanceMasterId()));
            }
            result.setMaintenanceMasterName(Objects.nonNull(billFix.getMaintenanceMasterId()) ? userMap.get(billFix.getMaintenanceMasterId()).getName() : "");

            result.setFinishTime(Objects.nonNull(billFix.getRepairTime()) && StringUtils.isBlank(billFix.getFinishTime()) ? DateUtil.formatDate(DateUtil.offsetDay(billFix.getRepairTime(), billFix.getFixDay())) : StringUtils.isNotBlank(billFix.getFinishTime()) ? billFix.getFinishTime() : "");

            result.setRealityFixTime(FixStateEnum.NORMAL == billFix.getFixState() && Objects.nonNull(billFix.getRepairTime()) ? (int) DateUtil.betweenDay(billFix.getUpdatedTime(), billFix.getRepairTime(), true) : 0);

            //维修项目明细
            result.setProjectContent(StringUtils.join(billFix.getContent().stream().map(i -> projectResultList.stream().filter(j -> j.getId().equals(i.getFixProjectId())).map(j -> j.getName()).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList()), ","));

            result.setProjectList(projectResultList);
            //配件

            List<FixListResult.AttachmentMapper> attachmentMapperList = Optional.ofNullable(billFix.getSerialNo()).map(i -> {

                List<AttachmentConsumeLog> attachmentConsumeLogList = attachmentConsumeLogService.list(Wrappers.<AttachmentConsumeLog>lambdaQuery().eq(AttachmentConsumeLog::getOriginOrderSerialNo, i));

                if (CollectionUtils.isNotEmpty(attachmentConsumeLogList)) {
                    List<FixDetailsResult.AttachmentMapper> collect2 = attachmentConsumeLogList.stream().map(FixConverter.INSTANCE::convertAttachmentMapper).collect(Collectors.toList());
                    List<WatchDataFusion> fusionList = goodsWatchService.getWatchDataFusionListByStockIds(collect2.stream().map(FixDetailsResult.AttachmentMapper::getStockId).collect(Collectors.toList()));

                    return fusionList.stream().map(FixConverter.INSTANCE::convertAttachmentMapper2).collect(Collectors.toList());
                }
                return null;
            }).orElse(null);
            result.setAttachmentList(attachmentMapperList);

            result.setFixRemark(billFix.getFixRemark());
            result.setReturnRemark(billFix.getReturnRemark());

            result.setAttachmentIdList(CollectionUtils.isEmpty(attachmentMapperList) ? Arrays.asList() : attachmentMapperList.stream().map(FixListResult.AttachmentMapper::getStockId).collect(Collectors.toList()));

            result.setTag(Arrays.asList(
                    Objects.nonNull(billFix.getSpecialExpediting()) && billFix.getSpecialExpediting().equals(1) ? "加急" : ""
                    , Objects.nonNull(billFix.getFixTimes()) && billFix.getFixTimes().compareTo(1) > 0 ? "返修" : ""
            ).stream().filter(StringUtils::isNotBlank).collect(Collectors.joining("/")));

            return result;
        }).collect(Collectors.toList());

        return PageResult.<FixListResult>builder()
                .result(collect)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    private LambdaQueryWrapper<BillFix> wrapper(FixListRequest request) {

        return Wrappers.<BillFix>query()
                //table true 维修中超时
                .last(Objects.nonNull(request.getTable()) && request.getTable(), "and if(finish_time is null ,(DATEDIFF(now(),repair_time)) > fix_day ,true ) and repair_time is not null and fix_state = 1 order by id desc")
                .last(Objects.nonNull(request.getTable()) && !request.getTable(), "and if(finish_time is null ,(DATEDIFF(now(),repair_time)) <= fix_day ,false ) and repair_time is not null and fix_state = 1 order by id desc")
                //timeoutSelect true 超时
                .last(Objects.nonNull(request.getTimeoutSelect()) && request.getTimeoutSelect(), "and if(finish_time is null ,if(fix_state = 2,(DATEDIFF(updated_time,repair_time)) > fix_day,(DATEDIFF(now(),repair_time)) > fix_day) ,true ) and repair_time is not null order by id desc")
                .last(Objects.nonNull(request.getTimeoutSelect()) && !request.getTimeoutSelect(), "and if(finish_time is null ,if(fix_state = 2,(DATEDIFF(updated_time,repair_time)) <= fix_day,(DATEDIFF(now(),repair_time)) <= fix_day) ,false ) and repair_time is not null order by id desc")
                .last(!(Objects.nonNull(request.getTable()) || Objects.nonNull(request.getTimeoutSelect())), "order by id desc")
                .lambda()
                .eq(StringUtils.isNotBlank(request.getBrandName()), BillFix::getBrandName, request.getBrandName())
                .eq(Objects.nonNull(request.getRepairFlag()), BillFix::getRepairFlag, request.getRepairFlag())
                .eq(Objects.nonNull(request.getSpecialExpediting()), BillFix::getSpecialExpediting, request.getSpecialExpediting())
                .eq(Objects.nonNull(request.getOrderType()), BillFix::getOrderType, request.getOrderType())
                .eq(Objects.nonNull(request.getFixState()), BillFix::getFixState, request.getFixState())
                .eq(Objects.nonNull(request.getFlowGrade()), BillFix::getFlowGrade, request.getFlowGrade())
                .between(StringUtils.isNotBlank(request.getStartTime()) && StringUtils.isNotBlank(request.getEndTime()), BillFix::getCreatedTime, request.getStartTime(), request.getEndTime())
                .like(StringUtils.isNotBlank(request.getStockSn()), BillFix::getStockSn, request.getStockSn())
                .eq(Objects.nonNull(request.getStoreId()), BillFix::getStoreId, request.getStoreId())
                .in(Objects.nonNull(request.getLocal()) && request.getLocal(), BillFix::getTagType, Arrays.asList(TagTypeEnum.CREATE.getValue(), TagTypeEnum.CREAT.getValue()))
                .in(Objects.nonNull(request.getFinish()) && request.getFinish(), BillFix::getFixState, Arrays.asList(FixStateEnum.NORMAL.getValue(), FixStateEnum.CANCEL.getValue()))
                .and(StringUtils.isNotBlank(request.getKeyword()), i -> i.apply("brand_name = \"" + request.getKeyword() + "\" OR stock_sn = \"" + request.getKeyword() + "\" OR customer_name = \"" + request.getKeyword() + "\""));

    }

    private void validRequest(FixListRequest request) {
        //返修
        request.setRepairFlag(Optional.ofNullable(request.getRepairFlag())
                .filter(v -> v != -1)
                .orElse(null));

        //加急
        request.setSpecialExpediting(Optional.ofNullable(request.getSpecialExpediting())
                .filter(v -> v != -1)
                .orElse(null));

        //订单类型
        request.setOrderType(Optional.ofNullable(request.getOrderType())
                .filter(v -> v != -1)
                .orElse(null));
        //状态
        request.setFixState(Optional.ofNullable(request.getFixState())
                .filter(v -> v != -1)
                .orElse(null));
        //流转等级
        request.setFlowGrade(Optional.ofNullable(request.getFlowGrade())
                .filter(v -> v != -1)
                .orElse(null));

        //维修单归属
        request.setStoreId(Optional.ofNullable(request.getStoreId())
                .filter(v -> v != -1)
                .orElse(null));
    }

    @Override
    public FixDetailsResult details(FixDetailsRequest request) {

        BillFix billFix = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> billFixService.getOne(Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getId, t.getId())
                        .or().eq(BillFix::getSerialNo, t.getSerialNo())
                ))
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_BILL_NOT_EXIST));

        FixDetailsResult result = FixConverter.INSTANCE.convertFixDetailsResult(billFix);

        result.setFixSiteName(Optional.ofNullable(billFix.getStoreId()).map(i -> Optional.ofNullable(fixSiteService.getOne(Wrappers.<FixSite>lambdaQuery().eq(FixSite::getOriginStoreId, billFix.getStoreId()))).map(FixSite::getSiteName).orElse("")).orElse(""));
        result.setStoreName(Optional.ofNullable(billFix.getStoreId()).map(i -> Optional.ofNullable(tagService.selectByStoreManagementId(billFix.getStoreId())).map(Tag::getTagName).orElse("")).orElse(""));
        result.setMaintenanceMasterName(Optional.ofNullable(billFix.getMaintenanceMasterId()).map(i -> Optional.ofNullable(userService.getById(billFix.getMaintenanceMasterId())).map(User::getName).orElse("")).orElse(""));
        List<BillFix> billFixList = billFixService.list(Wrappers.<BillFix>lambdaQuery().eq(BillFix::getParentFixId, billFix.getId()));
        if (CollectionUtils.isNotEmpty(billFixList)) {
            result.setParentFixSerialNo(billFixList.get(FlywheelConstant.INDEX).getSerialNo());
        }
        result.setFinishTime(Objects.nonNull(billFix.getRepairTime()) && StringUtils.isBlank(billFix.getFinishTime()) ? DateUtil.formatDate(DateUtil.offsetDay(billFix.getRepairTime(), billFix.getFixDay())) : StringUtils.isNotBlank(billFix.getFinishTime()) ? billFix.getFinishTime() : "");
        result.setRealityFixTime(FixStateEnum.NORMAL == billFix.getFixState() && Objects.nonNull(billFix.getRepairTime()) ? ((int) DateUtil.betweenDay(billFix.getUpdatedTime(), billFix.getRepairTime(), true)) + 1 : 0);
        result.setRealityFinishTime(FixStateEnum.NORMAL == billFix.getFixState() ? DateUtil.formatDateTime(billFix.getUpdatedTime()) : "");

        //解析json 读取维修项目
        result.setProjectList(Optional.ofNullable(projectService.list(null))
                .orElse(Lists.newArrayList())
                .stream()
                .map(FixProjectConverter.INSTANCE::convertFixProjectResult)
                .collect(Collectors.toList()));

        //维修内容字符串
        result.setProjectContent(StringUtils.join(result.getContent().stream().map(i -> result.getProjectList().stream().filter(j -> j.getId().equals(i.getFixProjectId())).map(j -> j.getName() + "(" + i.getFixMoney() + ")").collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList()), ","));
        result.setTimeout(Objects.nonNull(billFix.getRepairTime()) ? (StringUtils.isBlank(billFix.getFinishTime()) ? ((int) DateUtil.betweenDay(billFix.getRepairTime(), FixStateEnum.NORMAL == billFix.getFixState() ? billFix.getUpdatedTime() : DateUtil.date(), true) > billFix.getFixDay()) : Boolean.TRUE) : Boolean.FALSE);
        List<FixDetailsResult.AttachmentMapper> attachmentMapperList = Optional.ofNullable(billFix.getSerialNo()).map(i -> {

            List<AttachmentConsumeLog> attachmentConsumeLogList = attachmentConsumeLogService.list(Wrappers.<AttachmentConsumeLog>lambdaQuery().eq(AttachmentConsumeLog::getOriginOrderSerialNo, i));

            if (CollectionUtils.isNotEmpty(attachmentConsumeLogList)) {
                List<FixDetailsResult.AttachmentMapper> collect = attachmentConsumeLogList.stream().map(FixConverter.INSTANCE::convertAttachmentMapper).collect(Collectors.toList());
                List<WatchDataFusion> fusionList = goodsWatchService.getWatchDataFusionListByStockIds(collect.stream().map(FixDetailsResult.AttachmentMapper::getStockId).collect(Collectors.toList()));

                return fusionList.stream().map(FixConverter.INSTANCE::convertAttachmentMapper).collect(Collectors.toList());
            }
            return null;
        }).orElse(null);
        result.setAttachmentList(attachmentMapperList);

        result.setAttachmentIdList(CollectionUtils.isEmpty(attachmentMapperList) ? Arrays.asList() : attachmentMapperList.stream().map(FixDetailsResult.AttachmentMapper::getStockId).collect(Collectors.toList()));

        result.setAttachmentContent(CollectionUtils.isEmpty(result.getAttachmentList()) ? "" : StringUtils.join(result.getAttachmentList().stream().map(i -> StringUtils.join(Arrays.asList(i.getModel(), i.getStockSn(), i.getCostPrice()), "-")).collect(Collectors.toList()), "\n"));

        //查询维修日志记录
        result.setLogList(logFixOptService.list(Wrappers.<LogFixOpt>lambdaQuery().eq(LogFixOpt::getSerialNo, result.getSerialNo()).orderByDesc(LogFixOpt::getCreatedTime)).stream().map(FixConverter.INSTANCE::convertFixLog).collect(Collectors.toList()));
        //能否操作送外维修
        result.setTag(
                //内部新建
                (billFix.getOrderType() == OrderTypeEnum.UNDEFINED
                        && (Objects.nonNull(billFix.getOriginSerialNo()) || Objects.nonNull(billFix.getParentFixId()))
                ) || Arrays.asList(TagTypeEnum.CREATE, TagTypeEnum.CREAT).contains(billFix.getTagType())
                        ? Boolean.FALSE : Boolean.TRUE);
        //维修次数
        Integer fixTimes = billFix.getFixTimes();

        Map<Integer, List<FixLog>> collect = result.getLogList().stream().collect(Collectors.groupingBy(FixLog::getFixNode));

        //待分配
        List<FixLog> logList = collect.get(FixStateEnum.ALLOT.getValue());

        //维修中
        List<FixLog> logListFix = collect.get(FixStateEnum.RECEIVE.getValue());

        //大前提是自建单
        if (billFix.getOrderType() == OrderTypeEnum.UNDEFINED) {

            if (CollectionUtils.isNotEmpty(logList) && fixTimes.compareTo(1) == 0) {
                FixLog fixLog = logList.get(FlywheelConstant.INDEX);
                result.setRepairName(fixLog.getCreatedBy());
                result.setRepairTime(fixLog.getCreatedTime());
            } else if (CollectionUtils.isNotEmpty(logListFix)
                    && fixTimes.compareTo(1) > 0
            ) {
                //变更接修时间
                FixLog fixLog = logListFix.get(FlywheelConstant.INDEX);
                result.setRepairName(fixLog.getCreatedBy());
                result.setRepairTime(fixLog.getCreatedTime());
            }
            FixLog fixLog = result.getLogList().get(FlywheelConstant.INDEX);
            if (fixLog.getFixNode().equals(4)) {
                result.setRepairName(fixLog.getCreatedBy());
                result.setRepairTime(fixLog.getCreatedTime());
            }
        } else if (billFix.getOrderType() == OrderTypeEnum.CREATE) {
            //需要分配
            if (CollectionUtils.isNotEmpty(logList)) {
                FixLog fixLog = logList.get(FlywheelConstant.INDEX);
                result.setRepairName(fixLog.getCreatedBy());
                result.setRepairTime(fixLog.getCreatedTime());
            } else if (CollectionUtils.isNotEmpty(logListFix)) {
                FixLog fixLog = logListFix.get(logListFix.size() - 1);
                result.setRepairName(fixLog.getCreatedBy());
                result.setRepairTime(fixLog.getCreatedTime());
            }

            FixLog fixLog = result.getLogList().get(FlywheelConstant.INDEX);
            if (fixLog.getFixNode().equals(4)) {
                result.setRepairName(fixLog.getCreatedBy());
                result.setRepairTime(fixLog.getCreatedTime());
            }
        }


        if (CollectionUtils.isNotEmpty(logListFix)) {
            //分配时间
            result.setAllotTime(logListFix.get(logListFix.size() - 1).getCreatedTime());
        }
        return result;
    }

    @Override
    public FixEditResult edit(FixEditRequest request) {

        FixEditResult result = billFixService.edit(request);

        editAttachment(request.getAttachmentIdList(),
                result.getSerialNo(),
                result.getId(),
                result.getAttachmentCostPrice()
        );

        return result;
    }

    @Override
    public FixDelayResult delay(FixDelayRequest request) {
        BillFix billFix = FixConverter.INSTANCE.conver(request);
        billFixService.updateById(billFix);

        BillFix fix = billFixService.getById(request.getFixId());

        return FixConverter.INSTANCE.convertBillFixDelayResult(fix);
    }

    @Override
    public void edit(FixSpecialExpeditingRequest request) {
        billFixService.edit(request);
    }

    @Override
    public PageResult<FixLogResult> logList(FixLogRequest request) {

        Page<FixLogResult> page = logFixOptService.page(request);
        return PageResult.<FixLogResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public List<FixListResult> allList() {

        FixListRequest request = new FixListRequest();
        //维修中超时表
        request.setPage(1);
        request.setLimit(1000);
        request.setTable(true);

        return list(request).getResult().stream()
                .filter(result -> Arrays.asList(BusinessBillTypeEnum.TH_CG_DJ.getValue(), BusinessBillTypeEnum.TH_CG_BH.getValue(), BusinessBillTypeEnum.TH_CG_QK.getValue(), BusinessBillTypeEnum.TH_CG_DJTP.getValue()).contains(result.getFixSource())).map(result -> {
                    BillPurchase billPurchase = Optional.ofNullable(Optional.ofNullable(result)
                                    .filter(t -> Objects.nonNull(t.getStockId())).map(t -> billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getStockId, t.getStockId())))
                                    .orElse(null))
                            .filter(t -> Objects.nonNull(t.getPurchaseId())).map(t -> billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery().eq(BillPurchase::getId, t.getPurchaseId())))
                            .orElse(null);

                    LogFixOpt logFixOpt = logFixOptService.list(Wrappers.<LogFixOpt>lambdaQuery()
                            .eq(LogFixOpt::getFixState, FixStateEnum.RECEIVE)
                            .eq(LogFixOpt::getStockId, result.getStockId())
                            .eq(LogFixOpt::getSerialNo, result.getSerialNo())
                            .orderByAsc(LogFixOpt::getCreatedTime)
                    ).stream().findAny().orElse(null);

                    if (ObjectUtils.isNotEmpty(logFixOpt)) {
                        result.setCreatedBy(logFixOpt.getCreatedBy());
                    }

                    if (ObjectUtils.isNotEmpty(billPurchase) && ObjectUtils.isNotEmpty(billPurchase.getDemanderStoreId())) {
                        //老的没有需求门店
                        result.setShopId(billPurchase.getDemanderStoreId());
                        return result;
                    }
                    return null;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FixAllotResult allot(FixAllotRequest request) {

        FixAllotResult result = billFixService.allot(request);

        savaLog(request.getFixId());

        return result;
    }

    @Override
    public FixForeignResult foreign(FixForeignRequest request) {

        FixForeignResult result = billFixService.foreign(request);

        if (Objects.nonNull(result.getFixCreateRequest())) {

            FixCreateRequest fixCreateRequest = result.getFixCreateRequest();

            //客户名
            List<FixSite> fixSiteList = fixSiteService.list(Wrappers.<FixSite>lambdaQuery().eq(FixSite::getOriginStoreId, UserContext.getUser().getStore().getId()));

            if (CollectionUtils.isNotEmpty(fixSiteList)) {
                FixSite fixSite = fixSiteList.get(FlywheelConstant.INDEX);
                fixCreateRequest.setCustomerName(fixSite.getSiteName());
                fixCreateRequest.setCustomerPhone(fixSite.getSitePhone());
                fixCreateRequest.setCustomerAddress(fixSite.getSiteAddress());
            }

            FixSite fixSite = fixSiteService.getById(request.getFixSiteId());
            if (Objects.nonNull(fixSite)) {
                fixCreateRequest.setStoreId(fixSite.getOriginStoreId());
            }

            fixCreateRequest.setCreatedId(UserContext.getUser().getId());
            fixCreateRequest.setCreatedBy(UserContext.getUser().getUserName());
        }

        savaLog(request.getFixId());

        return result;
    }

    @Override
    public FixCreateResult create(FixCreateRequest request) {

        Assert.isTrue(CollectionUtils.isNotEmpty(fixSiteService.list(Wrappers.<FixSite>lambdaQuery().eq(FixSite::getOriginStoreId, UserContext.getUser().getStore().getId()))), "未创建维修站点禁止新建采购单");

        Brand brand = brandService.getOne(new LambdaQueryWrapper<Brand>().eq(Brand::getId, request.getBrandId()));
        request.setBrandName(Objects.nonNull(brand) ? brand.getName() : null);

        FixCreateResult result = billFixService.create(request);

        //配件增量数据
        editAttachment(request.getAttachmentIdList(),
                result.getSerialNo(),
                result.getId(),
                BigDecimal.ZERO
        );

        //插入日志
        savaLog(result.getId());

        //工作流参数 不接修 但可能分配
        result.setIsRepair(0);
        result.setIsAllot(Objects.nonNull(request.getMaintenanceMasterId()) ? 0 : 1);
        //查询当前登陆用户的简码
        result.setShortcodes(tagService.selectByStoreManagementId(request.getStoreId()).getShortcodes());

        if (Objects.nonNull(request) && Objects.nonNull(request.getParentStoreId())) {
            //要不要接修流程 内部送外
            result.setIsRepair(1);
            result.setIsAllot(1);
            result.setStoreId(request.getStoreId());
        }

        return result;
    }


    @Override
    public List<FixGanttChartResult> ganttChart() {

        LoginUser loginUser = UserContext.getUser();
        List<LoginRole> loginRoleList = loginUser.getRoles();
        LoginStore loginStore = loginUser.getStore();

        List<ShopMemberDto> shopMemberDtoList;

        Integer storeId = loginStore.getId();

        //非admin,皆是当前门店
        if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("admin").contains(loginRole.getRoleName()))
                && loginStore.getId().equals(FlywheelConstant._ZB_ID)) {
            shopMemberDtoList = storeManagementService.listShopMember(null, Arrays.asList("maintenance", "shopMaintenance"));
            storeId = null;
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("接修员", "维修员").contains(loginRole.getRoleName())) && loginStore.getId().equals(FlywheelConstant._ZB_ID)) {
            //门店只能看门店的 重置掉
            shopMemberDtoList = storeManagementService.listShopMember(Arrays.asList(loginStore.getId()), Arrays.asList("maintenance"));
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("门店接修员", "门店维修员").contains(loginRole.getRoleName())) && loginStore.getId().compareTo(FlywheelConstant._ZB_ID) > 0) {
            //门店只能看门店的 重置掉
            shopMemberDtoList = storeManagementService.listShopMember(Arrays.asList(loginStore.getId()), Arrays.asList("shopMaintenance"));
        } else {
            return Arrays.asList();
        }

        if (CollectionUtils.isEmpty(shopMemberDtoList)) {
            return Arrays.asList();
        }

        List<BillFix> billFixList = billFixService.list(
                Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getFixState, FixStateEnum.RECEIVE)
                        .eq(Objects.nonNull(storeId), BillFix::getStoreId, storeId)
        );

        if (CollectionUtils.isEmpty(billFixList)) {
            return shopMemberDtoList.stream().map(i -> FixGanttChartResult
                    .builder()
                    .maintenanceMasterId(i.getId())
                    .maintenanceMasterName(i.getUserName())
                    .currentTask(FlywheelConstant.INTEGER_DAFULT_VALUE)
                    .todayTask(FlywheelConstant.INTEGER_DAFULT_VALUE)
                    .specialTask(FlywheelConstant.INTEGER_DAFULT_VALUE)
                    .stockTaskList(Arrays.asList())
                    .build()).collect(Collectors.toList());
        }

        return packageMain(shopMemberDtoList
                , billFixList.stream().filter(i -> Objects.nonNull(i.getMaintenanceMasterId())).collect(Collectors.groupingBy(BillFix::getMaintenanceMasterId))
                , Optional.ofNullable(projectService.list(null))
                        .orElse(Lists.newArrayList())
                        .stream()
                        .map(FixProjectConverter.INSTANCE::convertFixProjectResult)
                        .collect(Collectors.toList())
                , DateTime.now());
    }

    @Override
    public FixEditResultResult editResult(FixEditResultRequest request) {

        /**
         * 查询维修单
         */
        BillFix selectFix = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getFixId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> this.billFixService.getOne(Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getId, t.getFixId())
                        .or().eq(BillFix::getSerialNo, t.getSerialNo())))
                .filter(t -> t.getFixState() == FixStateEnum.RECEIVE)
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_NOT_EXIT));

        BillFix billFix = FixConverter.INSTANCE.conver(request);

        billFix.setId(selectFix.getId());
        this.billFixService.updateById(billFix);

        return FixConverter.INSTANCE.convertFixEditResultResult(selectFix);
    }

    @Override
    public List<MaintenanceMasterListResult> maintenanceMasterList() {

        LoginUser loginUser = UserContext.getUser();
        List<LoginRole> loginRoleList = loginUser.getRoles();
        LoginStore loginStore = loginUser.getStore();

        List<ShopMemberDto> shopMemberDtoList;

        Integer storeId = loginStore.getId();
        //非admin,皆是当前门店
        if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("admin").contains(loginRole.getRoleName()))
                && loginStore.getId().equals(FlywheelConstant._ZB_ID)
        ) {
            shopMemberDtoList = storeManagementService.listShopMember(null, Arrays.asList("maintenance", "shopMaintenance"));
            storeId = null;
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("接修员", "维修员").contains(loginRole.getRoleName())) && loginStore.getId().equals(FlywheelConstant._ZB_ID)) {
            //门店只能看门店的 重置掉
            shopMemberDtoList = storeManagementService.listShopMember(Arrays.asList(loginStore.getId()), Arrays.asList("maintenance"));
        } else if (loginRoleList.stream().anyMatch(loginRole -> Arrays.asList("门店接修员", "门店维修员").contains(loginRole.getRoleName())) && loginStore.getId().compareTo(FlywheelConstant._ZB_ID) > 0) {
            //门店只能看门店的 重置掉
            shopMemberDtoList = storeManagementService.listShopMember(Arrays.asList(loginStore.getId()), Arrays.asList("shopMaintenance"));
        } else {
            return Arrays.asList();
        }

        if (CollectionUtils.isEmpty(shopMemberDtoList)) {
            return Arrays.asList();
        }

        List<BillFix> billFixList = billFixService.list(
                Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getFixState, FixStateEnum.RECEIVE)
                        .eq(Objects.nonNull(storeId), BillFix::getStoreId, storeId)
        );

        if (CollectionUtils.isEmpty(billFixList)) {
            return shopMemberDtoList.stream().map(i -> MaintenanceMasterListResult
                            .builder()
                            .maintenanceMasterName(i.getUserName())
                            .maintenanceMasterId(i.getId())
                            .currentTask(0)
                            .build())
                    .collect(Collectors.toList());
        }

        Map<Integer, Long> map = billFixList.stream().filter(i -> Objects.nonNull(i.getMaintenanceMasterId())).collect(Collectors.groupingBy(BillFix::getMaintenanceMasterId, Collectors.counting()));

        return shopMemberDtoList.stream().map(i -> MaintenanceMasterListResult
                        .builder()
                        .maintenanceMasterName(i.getUserName())
                        .maintenanceMasterId(i.getId())
                        .currentTask(map.containsKey(i.getId()) ? map.get(i.getId()).intValue() : 0)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public FixMaintenanceResult editMaintenance(FixMaintenanceRequest request) {

        /**
         * 查询维修单
         */
        BillFix selectFix = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getFixId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> this.billFixService.getOne(Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getId, t.getFixId())
                        .or().eq(BillFix::getSerialNo, t.getSerialNo())))
                .filter(t -> t.getFixState() == FixStateEnum.RECEIVE)
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_NOT_EXIT));

        BillFix billFix = FixConverter.INSTANCE.conver(request);

        billFix.setId(selectFix.getId());
        this.billFixService.updateById(billFix);

        return FixConverter.INSTANCE.convertFixMaintenanceResult(selectFix);
    }

    private void savaLog(Integer fixId) {

        BillFix billFix = billFixService.getById(fixId);

        LogFixOpt convert = LogFixOptConverter.INSTANCE.convert(billFix);

        if (Objects.nonNull(billFix) && Objects.nonNull(billFix.getParentFixId()) && billFix.getFixState() == FixStateEnum.CREATE) {
            convert.setCreatedId(billFix.getCreatedId());
            convert.setCreatedBy(billFix.getCreatedBy());
            convert.setUpdatedId(billFix.getCreatedId());
            convert.setUpdatedBy(billFix.getCreatedBy());
        } else {
            convert.setCreatedId(null);
            convert.setCreatedBy(null);
            convert.setUpdatedBy(null);
            convert.setUpdatedId(null);
        }

        logFixOptService.save(convert);
    }

    /**
     * 附件统一方法
     *
     * @param originOrderSerialNo
     * @param attachmentIdList
     * @return
     */
    private BigDecimal attachmentAdd(String originOrderSerialNo, List<Integer> attachmentIdList) {

        List<Stock> stockList = attachmentIdList.stream().map(r -> Optional.ofNullable(stockMapper.selectById(r)).filter(t -> Objects.nonNull(t.getPurchasePrice()) && !Arrays.asList(
                // 已销售和已使用
                StockStatusEnum.SOLD_OUT
//                    , StockStatusEnum.USED
        ).contains(t.getStockStatus())).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());

        if (ObjectUtils.isNotEmpty(stockList) && CollectionUtils.isNotEmpty(stockList)) {
            for (Stock stock : stockList) {
                Stock s = new Stock();
                s.setId(stock.getId());
                s.setTransitionStateEnum(StockStatusEnum.TransitionEnum.SALE);

                UpdateByIdCheckState.update(stockMapper, s);

                AttachmentConsumeLog attachmentConsumeLog = new AttachmentConsumeLog();
                attachmentConsumeLog.setOriginOrderSerialNo(originOrderSerialNo);
                attachmentConsumeLog.setCostPrice(stock.getPurchasePrice());
                attachmentConsumeLog.setStockId(stock.getId());
                attachmentConsumeLog.setStockSn(stock.getSn());
                attachmentConsumeLogService.save(attachmentConsumeLog);
            }
        }

        return stockList.stream().map(Stock::getPurchasePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 删掉
     *
     * @param originOrderSerialNo
     * @param attachmentIdList
     * @return
     */
    private BigDecimal attachmentDel(String originOrderSerialNo, List<Integer> attachmentIdList) {

        List<Stock> stockList = attachmentIdList.stream().map(r -> Optional.ofNullable(stockMapper.selectById(r)).filter(t -> Objects.nonNull(t.getPurchasePrice()) && Arrays.asList(
                // 已销售和已使用
                StockStatusEnum.SOLD_OUT
        ).contains(t.getStockStatus())).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());

        if (ObjectUtils.isNotEmpty(stockList) && CollectionUtils.isNotEmpty(stockList)) {
            for (Stock stock : stockList) {
                Stock s = new Stock();
                s.setId(stock.getId());
                s.setTransitionStateEnum(StockStatusEnum.TransitionEnum.SALE_CANCEL);

                UpdateByIdCheckState.update(stockMapper, s);

                attachmentConsumeLogService.remove(Wrappers.<AttachmentConsumeLog>lambdaQuery()
                        .eq(AttachmentConsumeLog::getOriginOrderSerialNo, originOrderSerialNo)
                        .eq(AttachmentConsumeLog::getStockId, stock.getId())
                        .eq(AttachmentConsumeLog::getStockSn, stock.getSn())
                );
            }
        }

        return stockList.stream().map(Stock::getPurchasePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 更新维修单
     *
     * @param attachmentIdList    配件id 增量数据
     * @param serialNo            关联单号
     * @param id                  关联id
     * @param attachmentCostPrice 变更字段
     */
    private void editAttachment(List<Integer> attachmentIdList, String serialNo, Integer id, BigDecimal attachmentCostPrice) {

        if (Objects.isNull(attachmentIdList) || attachmentIdList.stream().anyMatch(Objects::isNull)) {
            attachmentIdList = new ArrayList<>();
        }

        //存量数据
        List<Integer> list = attachmentConsumeLogService.list(Wrappers.<AttachmentConsumeLog>lambdaQuery()
                        .eq(AttachmentConsumeLog::getOriginOrderSerialNo, serialNo))
                .stream().map(AttachmentConsumeLog::getStockId)
                .collect(Collectors.toList());
        //并集的数据
        ArrayList<Integer> collection = new ArrayList<>(CollectionUtils.union(attachmentIdList, list));

        //删除的数据
        List<Integer> finalAttachmentIdList = attachmentIdList;
        List<Integer> oldDel = collection.stream().filter(item -> !finalAttachmentIdList.contains(item)).collect(Collectors.toList());

        //新增的数据
        List<Integer> newAdd = attachmentIdList.stream().filter(item -> !list.contains(item)).collect(Collectors.toList());

        BigDecimal addPrice = BigDecimal.ZERO;

        BigDecimal delPrice = BigDecimal.ZERO;

        //新增数据
        if (CollectionUtils.isNotEmpty(newAdd)) {

            //修改配件状态
            //插入记录使用 自动创建不会选配件
            addPrice = attachmentAdd(serialNo, newAdd);
        }

        if (CollectionUtils.isNotEmpty(oldDel)) {
            delPrice = attachmentDel(serialNo, oldDel);
        }

        BillFix billFix = new BillFix();
        billFix.setId(id);
        billFix.setAttachmentCostPrice(attachmentCostPrice.add(addPrice).subtract(delPrice));
        billFixService.updateById(billFix);
    }

    /**
     * 维修师任务包装
     *
     * @param shopMemberDtoList
     * @param map
     * @return
     */
    private List<FixGanttChartResult> packageMain(List<ShopMemberDto> shopMemberDtoList, Map<Integer, List<BillFix>> map, List<FixProjectResult> collect, DateTime now) {

        return shopMemberDtoList.stream().filter(Objects::nonNull).map(dto -> {

                    AtomicInteger currentTask = new AtomicInteger();

                    AtomicInteger todayTask = new AtomicInteger();

                    AtomicInteger specialTask = new AtomicInteger();

                    List<FixGanttChartResult.StockTaskMapper> mapperList = map.containsKey(dto.getId()) ? map.get(dto.getId()).stream().map(billFix -> {
                                FixGanttChartResult.StockTaskMapper.StockTaskMapperBuilder builder = FixGanttChartResult.StockTaskMapper
                                        .builder()
                                        .special(Boolean.FALSE)
                                        .today(Boolean.FALSE)
                                        .brandName(billFix.getBrandName())
                                        .id(billFix.getId())
                                        .serialNo(billFix.getSerialNo())
                                        .stockSn(billFix.getStockSn());
                                currentTask.getAndIncrement();

                                if (Objects.nonNull(billFix.getSpecialExpediting()) && billFix.getSpecialExpediting().equals(WhetherEnum.YES.getValue())) {
                                    specialTask.getAndIncrement();
                                    builder.special(Boolean.TRUE);
                                }

                                //可能为空 之前数据导致完成时间为空 yyyy-mm-dd
                                String finishTime = billFix.getFinishTime();

                                if (Objects.nonNull(billFix.getRepairTime()) && Objects.nonNull(billFix.getFixDay()) && StringUtils.isBlank(billFix.getFinishTime())) {
                                    finishTime = DateUtil.formatDate(DateUtil.offsetDay(billFix.getRepairTime(), billFix.getFixDay()));
                                }

                                List<String> fixDays = Lists.newArrayListWithCapacity(FlywheelConstant.WEEK);

                                DateTime beginOfWeek = DateUtil.beginOfDay(DateUtil.beginOfWeek(now));

                                if (StringUtils.isNotBlank(finishTime) && DateUtil.today().equals(finishTime)) {
                                    builder.today(Boolean.TRUE);
                                    todayTask.getAndIncrement();
                                }

                                while (DateUtil.between(beginOfWeek, DateUtil.beginOfDay(DateUtil.endOfWeek(now)), DateUnit.DAY, false) >= 0) {
                                    if (Objects.nonNull(billFix.getRepairTime()) && StringUtils.isNotBlank(finishTime)) {
                                        if (Range.closed(DateUtil.beginOfDay(billFix.getRepairTime()), DateUtil.parse(finishTime)).contains(beginOfWeek)) {
                                            fixDays.add(DateUtil.format(beginOfWeek, "MM月dd日"));
                                        }
                                    }
                                    beginOfWeek = DateUtil.offsetDay(beginOfWeek, FlywheelConstant.ONE);
                                }

                                return builder
                                        .projectContent(StringUtils.join(billFix.getContent().stream().map(i -> collect.stream().filter(j -> j.getId().equals(i.getFixProjectId())).map(j -> j.getName()).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList()), ","))
                                        .fixDays(fixDays)
                                        .build();
                            })
                            .collect(Collectors.toList())
                            : Arrays.asList();

                    if (CollectionUtils.isNotEmpty(mapperList)) {
                        Collections.sort(mapperList,
                                Ordering
                                        .from((Comparator<FixGanttChartResult.StockTaskMapper>) (o1, o2) -> Boolean.compare(CollectionUtils.isNotEmpty(o2.getFixDays()), CollectionUtils.isNotEmpty(o1.getFixDays())))
                                        .compound((o1, o2) -> o2.getSpecial().compareTo(o1.getSpecial()))
                                        .compound((o1, o2) -> o2.getToday().compareTo(o1.getToday()))
                                        .compound((o1, o2) -> o2.getFixDays().size() - o1.getFixDays().size())
                        );
                    }
                    return FixGanttChartResult
                            .builder()
                            .stockTaskList(mapperList)
                            .currentTask(currentTask.get()).todayTask(todayTask.get()).specialTask(specialTask.get()).maintenanceMasterName(dto.getUserName()).maintenanceMasterId(dto.getId())
                            .build();
                })
                .sorted(Ordering
                        .from((Comparator<FixGanttChartResult>) (o1, o2) -> o2.getStockTaskList().size() - o1.getStockTaskList().size())
                        .compound((o1, o2) -> o2.getSpecialTask().compareTo(o1.getSpecialTask()))
                        .compound((o1, o2) -> o2.getTodayTask().compareTo(o1.getTodayTask()))
                        .compound((o1, o2) -> o2.getCurrentTask().compareTo(o1.getCurrentTask()))

                )
                .collect(Collectors.toList());
    }
}
