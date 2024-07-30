package com.seeease.flywheel.serve.fix.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.fix.request.*;
import com.seeease.flywheel.fix.result.*;
import com.seeease.flywheel.qt.request.FixQualityTestingRequest;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.fix.convert.FixConverter;
import com.seeease.flywheel.serve.fix.entity.BillFix;
import com.seeease.flywheel.serve.fix.enums.FixStateEnum;
import com.seeease.flywheel.serve.fix.enums.FlowGradeEnum;
import com.seeease.flywheel.serve.fix.enums.OrderTypeEnum;
import com.seeease.flywheel.serve.fix.enums.TagTypeEnum;
import com.seeease.flywheel.serve.fix.event.FixFinishEvent;
import com.seeease.flywheel.serve.fix.event.FixReceiveEvent;
import com.seeease.flywheel.serve.fix.mapper.BillFixMapper;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.maindata.entity.FixSite;
import com.seeease.flywheel.serve.maindata.mapper.FixSiteMapper;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Tiro
 * @description 针对表【bill_fix(维修记录)】的数据库操作Service实现
 * @createDate 2023-01-17 11:25:35
 */
@Service
public class BillFixServiceImpl extends ServiceImpl<BillFixMapper, BillFix>
        implements BillFixService {

    @Resource
    private BillQualityTestingService billQualityTestingService;

    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private FixSiteMapper fixSiteMapper;

    /**
     * 注意内部建单流程
     *
     * @param request
     * @return
     */
    @Override
    public FixCreateResult create(FixCreateRequest request) {

        OrderTypeEnum orderType = OrderTypeEnum.fromValue(request.getOrderType());

        BillFix billFix = FixConverter.INSTANCE.convert(request);
        //公共数据
        billFix.setFixTimes(1);
        billFix.setRepairFlag(0);
        billFix.setTaskArriveTime(new Date());
        billFix.setSerialNo(SerialNoGenerator.generateFixSerialNo());
        billFix.setResultContent(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));

        if (Objects.nonNull(request.getStockId())) {
            //客户名
            List<FixSite> fixSiteList = fixSiteMapper.selectList(Wrappers.<FixSite>lambdaQuery().eq(FixSite::getOriginStoreId, 1));

            if (CollectionUtils.isNotEmpty(fixSiteList)) {
                FixSite fixSite = fixSiteList.get(FlywheelConstant.INDEX);
                billFix.setCustomerName(fixSite.getSiteName());
                billFix.setCustomerPhone(fixSite.getSitePhone());
                billFix.setCustomerAddress(fixSite.getSiteAddress());
            }
        }

        //系统自建
        if (OrderTypeEnum.UNDEFINED == orderType) {

            billFix.setFixState(FixStateEnum.CREATE);

            FlowGradeEnum flowGrade;

            switch (billFix.getFixSource()) {
                case TH_CG_QK:
                case TH_CG_DJTP:
                case TH_CG_DJ:
                    flowGrade = FlowGradeEnum.RECEIVE;
                    break;
                case GR_JS:
                    flowGrade = FlowGradeEnum.CREATE;
                    break;
                case TH_JS:
                    flowGrade = FlowGradeEnum.ANOMALY2;
                    break;
                case TH_CG_BH:
                    flowGrade = FlowGradeEnum.NORMAL;
                    break;
                case TH_CG_PL:
                    flowGrade = FlowGradeEnum.ANOMALY;
                    break;
                case GR_HS_ZH:
                case GR_HS_JHS:
                    flowGrade = FlowGradeEnum.EXTERNAL;
                    break;
                case GR_HG_ZH:
                case GR_HG_JHS:
                    flowGrade = FlowGradeEnum.EXTERNAL2;
                    break;
                default:
                    flowGrade = FlowGradeEnum.UNDEFINED;
            }
            billFix.setFlowGrade(flowGrade);

            if (Objects.nonNull(request.getParentStoreId())) {
                billFix.setCreatedId(request.getCreatedId());
                billFix.setCreatedBy(request.getCreatedBy());
                billFix.setUpdatedId(request.getCreatedId());
                billFix.setUpdatedBy(request.getCreatedBy());
            }
        } else if (OrderTypeEnum.CREATE == orderType) {
            //有维修师
            if (Objects.nonNull(request.getMaintenanceMasterId())) {
                billFix.setFixState(FixStateEnum.RECEIVE);
                billFix.setRepairTime(new Date());
            } else {
                if (Objects.nonNull(request.getParentStoreId())) {
                    billFix.setFixState(FixStateEnum.CREATE);
                    billFix.setOrderType(OrderTypeEnum.UNDEFINED);
                } else {
                    billFix.setFixState(FixStateEnum.ALLOT);
                    billFix.setRepairTime(new Date());
                }

            }
            if (Objects.nonNull(request.getParentStoreId())) {
                billFix.setCreatedId(request.getCreatedId());
                billFix.setCreatedBy(request.getCreatedBy());
                billFix.setUpdatedId(request.getCreatedId());
                billFix.setUpdatedBy(request.getCreatedBy());
            }
        }

        if (ObjectUtils.isNotEmpty(request.getContent())
                && CollectionUtils.isNotEmpty(request.getContent())
                && request.getContent().stream().allMatch(Objects::nonNull)
                && request.getContent().stream().allMatch(i -> Objects.nonNull(i.getFixMoney()))
        ) {
            BigDecimal fixMoney = request.getContent().stream().map(FixCreateRequest.FixProjectMapper::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            billFix.setFixMoney(fixMoney);
        }

        //插入数据
        baseMapper.insert(billFix);

        return FixConverter.INSTANCE.convertBillFixCreateResult(billFix);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FixReceiveListResult> receive(List<FixReceiveRequest.FixReceiveListRequest> request) {

        List<FixReceiveListResult> resultList = new ArrayList<>();

        request.forEach(fixReceiveListRequest -> {

            BillFix billFix = FixConverter.INSTANCE.convertList(fixReceiveListRequest);
            billFix.setRepairTime(new Date());
            billFix.setTransitionStateEnum(FixStateEnum.TransitionEnum.CREATE_RECEIVE_DELIVERY);
            UpdateByIdCheckState.update(baseMapper, billFix);

            BillFix select = baseMapper.selectById(fixReceiveListRequest.getFixId());
            FixReceiveListResult build = FixReceiveListResult.builder()
                    .serialNo(select.getSerialNo())
                    .stockId(select.getStockId())
                    .fixSource(select.getFixSource().getValue())
                    .id(select.getId())
                    .build();

            //接修时 取商品里的是否有瑕疵 存到维修单里
            Stock stock = stockMapper.selectById(select.getStockId());
            BillFix bf = new BillFix();
            bf.setDefectOrNot(stock.getDefectOrNot());
            bf.setId(fixReceiveListRequest.getFixId());
            baseMapper.updateById(bf);

            resultList.add(build);

            billHandlerEventPublisher.publishEvent(new FixReceiveEvent(fixReceiveListRequest.getFixId()));
        });

        return resultList;
    }

    @Override
    public FixRepairResult repair(FixRepairRequest request) {

        /**
         * 查询维修单
         */
        BillFix selectFix = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getFixId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> this.getOne(Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getId, t.getFixId())
                        .or().eq(BillFix::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_NOT_EXIT));

        //维修项采用覆盖
        if (WhetherEnum.YES == WhetherEnum.fromValue(request.getAccept())) {

            BillFix billFix = FixConverter.INSTANCE.convert(request);

            //接修时 取商品里的是否有瑕疵 存到维修单里
            billFix.setRepairTime(new Date());
            billFix.setId(selectFix.getId());
            billFix.setTransitionStateEnum(Objects.nonNull(selectFix.getMaintenanceMasterId()) ? FixStateEnum.TransitionEnum.CREATE_RECEIVE_DELIVERY : FixStateEnum.TransitionEnum.CREATE_ALLOT);

            Optional.ofNullable(selectFix.getStockId()).map(fix -> {
                Stock stock = stockMapper.selectById(selectFix.getStockId());
                billFix.setDefectOrNot(stock.getDefectOrNot());
                return null;
            }).orElse(null);


            if (ObjectUtils.isNotEmpty(request.getContent())) {

                BigDecimal fixMoney = request.getContent().stream().map(FixRepairRequest.FixProjectMapper::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add);

                billFix.setFixMoney(fixMoney);
            }
            billFix.setFixRemark(request.getFixRemark());
            billFix.setReturnRemark(request.getReturnRemark());
            UpdateByIdCheckState.update(baseMapper, billFix);

            billHandlerEventPublisher.publishEvent(new FixReceiveEvent(request.getFixId()));

            return FixRepairResult.builder()
                    .serialNo(selectFix.getSerialNo())
                    .stockId(selectFix.getStockId())
                    .fixSource(Objects.nonNull(selectFix.getFixSource()) ? selectFix.getFixSource().getValue() : null)
                    .id(selectFix.getId())
                    .attachmentCostPrice(selectFix.getAttachmentCostPrice())
                    //是否分配
                    .isAllot(Objects.nonNull(selectFix.getMaintenanceMasterId()) ? 0 : 1)
                    .isAccept(1)
                    .build();

        } else if (WhetherEnum.NO == WhetherEnum.fromValue(request.getAccept())) {

            BillFix billFix = FixConverter.INSTANCE.convert(request);

            //接修时 取商品里的是否有瑕疵 存到维修单里
            billFix.setRepairTime(new Date());
            billFix.setId(selectFix.getId());
            billFix.setTransitionStateEnum(FixStateEnum.TransitionEnum.CREATE_CANCEL);

            UpdateByIdCheckState.update(baseMapper, billFix);

            billHandlerEventPublisher.publishEvent(new FixReceiveEvent(request.getFixId()));

            Optional.ofNullable(selectFix.getStockId()).map(f -> {
                //通知质检单，修改状态
                FixQualityTestingRequest fixQualityTestingRequest = new FixQualityTestingRequest();
                fixQualityTestingRequest.setFixId(selectFix.getId());
                billQualityTestingService.fix(fixQualityTestingRequest);

                //维修完成
                billHandlerEventPublisher.publishEvent(new FixFinishEvent(selectFix.getId()));

                return null;
            }).orElse(null);

            return FixRepairResult.builder()
                    .serialNo(selectFix.getSerialNo())
                    .stockId(selectFix.getStockId())
                    .fixSource(Objects.nonNull(selectFix.getFixSource()) ? selectFix.getFixSource().getValue() : null)
                    .id(selectFix.getId())
                    .attachmentCostPrice(selectFix.getAttachmentCostPrice())
                    .isAccept(0)
                    .parentFixSerialNo(Objects.nonNull(selectFix.getParentFixId()) ? this.getById(selectFix.getParentFixId()).getSerialNo() : null)
                    .build();
        } else {
            throw new OperationRejectedException(OperationExceptionCode.FIX_NOT_EXIT);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FixFinishResult finish(FixFinishRequest request) {

        BillFix billFix = FixConverter.INSTANCE.convert(request);

        /**
         * 查询维修单
         */
        BillFix selectFix = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getFixId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> this.getOne(Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getId, t.getFixId())
                        .or().eq(BillFix::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_NOT_EXIT));

        billFix.setId(selectFix.getId());

        billFix.setTransitionStateEnum(FixStateEnum.TransitionEnum.RECEIVE_NORMAL_DELIVERY);

        if (ObjectUtils.isNotEmpty(request.getContent())) {

            BigDecimal fixMoney = request.getContent().stream().map(FixFinishRequest.FixProjectMapper::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add);

            billFix.setFixMoney(fixMoney);
        }

        UpdateByIdCheckState.update(baseMapper, billFix);

        Optional.ofNullable(selectFix.getStockId()).map(f -> {
            //通知质检单，修改状态
            FixQualityTestingRequest fixQualityTestingRequest = new FixQualityTestingRequest();
            fixQualityTestingRequest.setFixId(selectFix.getId());
            billQualityTestingService.fix(fixQualityTestingRequest);

            //维修完成
            billHandlerEventPublisher.publishEvent(new FixFinishEvent(selectFix.getId()));

            return null;
        }).orElse(null);

        FixFinishResult result = FixConverter.INSTANCE.convertBillFixFinishResult(selectFix);

        result.setParentFixSerialNo(Objects.nonNull(selectFix.getParentFixId()) ? this.getById(selectFix.getParentFixId()).getSerialNo() : null);
        result.setFixSource(Objects.nonNull(selectFix.getFixSource()) ? selectFix.getFixSource().getValue() : null);

        return result;
    }

    @Override
    public QtFixResult qt(QtFixRequest request) {

        BillFix fix = baseMapper.selectById(request.getFixId());
        //维修次数
        request.setFixTimes(fix.getFixTimes() + 1);
        request.setRepairFlag(1);
        BillFix billFix = FixConverter.INSTANCE.convert(request);
        billFix.setTransitionStateEnum(fix.getFixState() == FixStateEnum.NORMAL ? FixStateEnum.TransitionEnum.NORMAL_CREATE : FixStateEnum.TransitionEnum.CANCEL_CREATE);
        billFix.setTaskArriveTime(new Date());

        //已经接修
        if (ObjectUtils.isNotEmpty(fix.getRepairTime())) {
            fix.setFinishTime(DateUtil.formatDate(DateUtil.offsetDay(fix.getRepairTime(), request.getFixDay())));
        }

        UpdateByIdCheckState.update(baseMapper, billFix);

        //二次更新
        BillFix bf = new BillFix();
        bf.setId(fix.getId());
        bf.setFinishTime(null);
        bf.setRepairTime(null);

        baseMapper.update(bf, new UpdateWrapper<BillFix>()
                .lambda()
                .eq(BillFix::getId, bf.getId())
                .set(BillFix::getRepairTime, null)
                .set(BillFix::getFinishTime, null)
        );

        return FixConverter.INSTANCE.convertQtBillFixResult(fix);
    }

    @Override
    public Page<FixListResult> page(FixListRequest request) {

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FixEditResult edit(FixEditRequest request) {

        /**
         * 查询维修单
         */
        BillFix selectFix = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getFixId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> this.getOne(Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getId, t.getFixId())
                        .or().eq(BillFix::getSerialNo, t.getSerialNo())))
                .filter(t -> t.getFixState() == FixStateEnum.RECEIVE)
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_NOT_EXIT));

        BillFix billFix = FixConverter.INSTANCE.conver(request);

        if (ObjectUtils.isNotEmpty(request.getContent())) {

            BigDecimal fixMoney = request.getContent().stream().map(FixEditRequest.FixProjectMapper::getFixMoney).reduce(BigDecimal.ZERO, BigDecimal::add);

            billFix.setFixMoney(fixMoney);
        }
        billFix.setId(selectFix.getId());
        billFix.setFixRemark(request.getFixRemark());
        billFix.setReturnRemark(request.getReturnRemark());
        updateById(billFix);

        return FixConverter.INSTANCE.convertBillFixEditResult(selectFix);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(FixSpecialExpeditingRequest request) {

        request.getFixIdList().stream().forEach(item -> {

            BillFix billFix = new BillFix();
            billFix.setId(item);
            billFix.setSpecialExpediting(WhetherEnum.YES.getValue());
            updateById(billFix);
        });
    }

    @Override
    public FixAllotResult allot(FixAllotRequest request) {

        /**
         * 查询维修单
         */
        BillFix selectFix = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getFixId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> this.getOne(Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getId, t.getFixId())
                        .or().eq(BillFix::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_NOT_EXIT));

        BillFix billFix = FixConverter.INSTANCE.convert(request);
        billFix.setTransitionStateEnum(FixStateEnum.TransitionEnum.ALLOT_RECEIVE);

        UpdateByIdCheckState.update(baseMapper, billFix);

        return FixConverter.INSTANCE.convertFixAllotResult(selectFix);
    }

    @Override
    public FixForeignResult foreign(FixForeignRequest request) {

        /**
         * 查询维修单
         */
        BillFix selectFix = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getFixId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> this.getOne(Wrappers.<BillFix>lambdaQuery()
                        .eq(BillFix::getId, t.getFixId())
                        .or().eq(BillFix::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.FIX_NOT_EXIT));

        if ((selectFix.getOrderType() == OrderTypeEnum.UNDEFINED
                && (Objects.nonNull(selectFix.getOriginSerialNo()) || Objects.nonNull(selectFix.getParentFixId()))
        ) || Arrays.asList(TagTypeEnum.CREATE, TagTypeEnum.CREAT).contains(selectFix.getTagType())) {
            throw new BusinessException(ExceptionCode.FIX_NOT_FOREIGN);
        }

        BillFix billFix = FixConverter.INSTANCE.convert(request);

        baseMapper.updateById(billFix);

        FixForeignResult result = FixConverter.INSTANCE.convertFixForeignResult(selectFix);

        result.setIsLocal(request.getTagType());

        //新建单据数据 二次查询
        if (request.getTagType().equals(WhetherEnum.YES.getValue()) && Objects.nonNull(request.getFixSiteId())) {

            BillFix select = baseMapper.selectById(selectFix.getId());

            FixCreateRequest fixCreateRequest = FixConverter.INSTANCE.convertFixCreateRequest(select);
            fixCreateRequest.setParentFixId(select.getId());
            fixCreateRequest.setDeliveryExpressNo(select.getDeliverExpressNo());
            fixCreateRequest.setParentStoreId(select.getStoreId());
            fixCreateRequest.setOrderType(0);
            result.setFixCreateRequest(fixCreateRequest);
        }
        return result;
    }
}




