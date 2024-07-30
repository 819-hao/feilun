package com.seeease.flywheel.serve.financial.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;
import com.seeease.flywheel.recycle.entity.RecycleMessage;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.mapper.BankMapper;
import com.seeease.flywheel.serve.customer.mapper.CustomerContactsMapper;
import com.seeease.flywheel.serve.customer.mapper.CustomerMapper;
import com.seeease.flywheel.serve.financial.convert.ApplyFinancialPaymentConvert;
import com.seeease.flywheel.serve.financial.convert.ApplyFinancialPaymentObsoleteRecordConvert;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentStateEnum;
import com.seeease.flywheel.serve.financial.enums.ApplyFinancialPaymentTypeEnum;
import com.seeease.flywheel.serve.financial.event.ApplyFinancialPaymentCancelEvent;
import com.seeease.flywheel.serve.financial.event.ApplyFinancialPaymentPassEvent;
import com.seeease.flywheel.serve.financial.mapper.*;
import com.seeease.flywheel.serve.financial.service.ApplyFinancialPaymentService;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.mapper.PurchaseSubjectMapper;
import com.seeease.flywheel.serve.maindata.mapper.StoreManagementMapper;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseTask;
import com.seeease.flywheel.serve.purchase.enums.TaskStateEnum;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseTaskService;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import com.seeease.flywheel.serve.recycle.enums.RecycleOrderTypeEnum;
import com.seeease.flywheel.serve.recycle.mq.MallPayOrderProducers;
import com.seeease.flywheel.serve.recycle.service.IRecycleOrderService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【apply_financial_payment】的数据库操作Service实现
 * @createDate 2023-02-27 16:06:51
 */
@Slf4j
@Service
public class ApplyFinancialPaymentServiceImpl extends ServiceImpl<ApplyFinancialPaymentMapper, ApplyFinancialPayment>
        implements ApplyFinancialPaymentService {

    @Resource
    private ApplyFinancialPaymentRecordMapper applyFinancialPaymentRecordMapper;
    @Resource
    private PurchaseSubjectMapper purchaseSubjectMapper;
    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;
    @Resource
    private StoreManagementMapper storeManagementMapper;
    @Resource
    private CustomerContactsMapper customerContactsMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private BankMapper bankMapper;
    @Resource
    private AccountsPayableAccountingMapper accountingMapper;
    @Resource
    private BillPurchaseMapper purchaseMapper;
    @Resource
    private ApplyFinancialPaymentObsoleteRecordMapper obsoleteRecordMapper;

    @Resource
    private BillPurchaseTaskService billPurchaseTaskService;

    private static final List<ApplyFinancialPaymentStateEnum> stateEnum = ImmutableList.of(ApplyFinancialPaymentStateEnum.PAID, ApplyFinancialPaymentStateEnum.CANCEL);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplyFinancialPaymentCreateResult create(ApplyFinancialPaymentCreateRequest request) {

        ApplyFinancialPayment applyFinancialPayment = ApplyFinancialPaymentConvert.INSTANCE.convert(request);
        //参数校验
        //ValidValueUtils.doValidator(applyFinancialPayment);
        applyFinancialPayment.setSerialNo(SerialNoGenerator.generateApplyFinancialPaymentSerialNo());
        //待确认
        applyFinancialPayment.setState(ApplyFinancialPaymentStateEnum.PENDING_REVIEW);
        applyFinancialPayment.setApplicantTime(new Date());
        this.baseMapper.insert(applyFinancialPayment);

        //修改应收应付
//        accountingMapper.updateStatusByAfpSerialNo(
//                applyFinancialPayment.getSerialNo(),
//                FinancialStatusEnum.PENDING_REVIEW.getValue(),
//                FinancialStatusEnum.IN_REVIEW.getValue()
//        );
        if (ObjectUtils.isNotEmpty(request.getPurchaseTaskId())) {

            BillPurchaseTask billPurchaseTask = billPurchaseTaskService.getById(request.getPurchaseTaskId());

            BillPurchaseTask purchaseTask = new BillPurchaseTask();
            purchaseTask.setApplyFinancialPaymentId(applyFinancialPayment.getId());
            purchaseTask.setId(request.getPurchaseTaskId());

            if (ObjectUtils.isNotEmpty(billPurchaseTask) && billPurchaseTask.getTaskState() == TaskStateEnum.APPLY_FIN && ObjectUtils.isNotEmpty(billPurchaseTask.getApplyFinancialPaymentId())) {
                //变更id
                billPurchaseTaskService.updateById(purchaseTask);
            } else if (ObjectUtils.isNotEmpty(billPurchaseTask) && billPurchaseTask.getTaskState() == TaskStateEnum.RECEIVED && ObjectUtils.isEmpty(billPurchaseTask.getApplyFinancialPaymentId())) {
                purchaseTask.setTransitionStateEnum(TaskStateEnum.TransitionEnum.RECEIVED_APPLY_FIN);
                billPurchaseTaskService.updateByState(purchaseTask);
            }

        }
        log.info("tempPrice 2:{}", applyFinancialPayment.getPricePayment().toPlainString());
        return ApplyFinancialPaymentCreateResult.builder()
                .id(applyFinancialPayment.getId())
                .serialNo(applyFinancialPayment.getSerialNo())
                .pricePayment(applyFinancialPayment.getPricePayment())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplyFinancialPaymentUpdateResult update(ApplyFinancialPaymentUpdateRequest request) {

        ApplyFinancialPayment selectById = this.baseMapper.selectById(request.getId());

        Assert.notNull(selectById, "申请打款单不能为空");
        Assert.isFalse(Arrays.asList(ApplyFinancialPaymentStateEnum.PAID, ApplyFinancialPaymentStateEnum.CANCEL).contains(selectById.getState()), "已打款不能更改");

        ApplyFinancialPayment applyFinancialPayment = ApplyFinancialPaymentConvert.INSTANCE.convert(request);

        //解决一个乱七八糟的问题
        if(selectById.getTypePayment().equals(ApplyFinancialPaymentTypeEnum.BUY_BACK)){
            applyFinancialPayment.setRepurchaseCommitment(request.getRecoveryPricingRecord());
            applyFinancialPayment.setRecoveryPricingRecord(null);
        }
        //修改已驳回
        if (ApplyFinancialPaymentStateEnum.REJECTED.equals(selectById.getState())) {
            applyFinancialPayment.setTransitionStateEnum(ApplyFinancialPaymentStateEnum.TransitionEnum.REJECTED_TO_PENDING_REVIEW);
            applyFinancialPayment.setApplicantTime(new Date());
            UpdateByIdCheckState.update(baseMapper, applyFinancialPayment);
            //修改已作废
        } else if (ApplyFinancialPaymentStateEnum.OBSOLETE.equals(selectById.getState())) {
            applyFinancialPayment.setApplicantTime(new Date());
            applyFinancialPayment.setTransitionStateEnum(ApplyFinancialPaymentStateEnum.TransitionEnum.OBSOLETE_TO_PENDING_REVIEW);
            UpdateByIdCheckState.update(baseMapper, applyFinancialPayment);
        } else if (ApplyFinancialPaymentStateEnum.PENDING_REVIEW.equals(selectById.getState())) {
            //更改内容
            baseMapper.updateById(applyFinancialPayment);
        }

        return ApplyFinancialPaymentUpdateResult.builder().id(selectById.getId()).serialNo(selectById.getSerialNo()).build();
    }

    @Override
    public Page<ApplyFinancialPaymentPageResult> page(ApplyFinancialPaymentQueryRequest request) {

        return this.baseMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<ApplyFinancialPaymentPageAllResult> queryAll(ApplyFinancialPaymentQueryAllRequest request) {

        //改为存状态值

        return this.baseMapper.getPageAll(new Page(request.getPage(), request.getLimit()), request);
    }

    @Resource
    private IRecycleOrderService recycleOrderService;

    @Resource
    private MallPayOrderProducers mallPayOrderProducers;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplyFinancialPaymentOperateResult operate(ApplyFinancialPaymentOperateRequest request) {
        ApplyFinancialPayment applyFinancialPayment = ApplyFinancialPaymentConvert.INSTANCE.convert(request);

        ApplyFinancialPayment selectById = this.baseMapper.selectById(applyFinancialPayment.getId());
//        if (WhetherEnum.YES.equals(selectById.getWhetherUse()))
//            throw new OperationRejectedException(OperationExceptionCode.APPLY_FINANCIAL_PAYMENT);
        ApplyFinancialPaymentStateEnum code = ApplyFinancialPaymentStateEnum.fromCode(request.getState());
        //已打款
        ITransitionStateEnum transitionStateEnum = null;

        switch (code) {
            case PAID:
                transitionStateEnum = ApplyFinancialPaymentStateEnum.TransitionEnum.PENDING_REVIEW_TO_PAID;
                break;
            case REJECTED:
                transitionStateEnum = ApplyFinancialPaymentStateEnum.TransitionEnum.PENDING_REVIEW_TO_REJECTED;
                break;
            case CANCEL:
                transitionStateEnum = ApplyFinancialPaymentStateEnum.TransitionEnum.PENDING_REVIEW_TO_CANCEL;
                break;
            case OBSOLETE:
                transitionStateEnum = ApplyFinancialPaymentStateEnum.TransitionEnum.PAID_TO_OBSOLETE;
                break;
        }
        boolean flag = applyFinancialPayment.getState().equals(ApplyFinancialPaymentStateEnum.PAID);
        applyFinancialPayment.setTransitionStateEnum(transitionStateEnum);

        //打款记录 存在财务和业务共同操作记录
        ApplyFinancialPaymentRecord applyFinancialPaymentRecord = new ApplyFinancialPaymentRecord();
        applyFinancialPaymentRecord.setApplyFinancialPaymentId(applyFinancialPayment.getId());
        applyFinancialPaymentRecord.setResult(request.getResult());
        applyFinancialPaymentRecord.setState(applyFinancialPayment.getState());
        applyFinancialPaymentRecord.setApplicant(selectById.getUpdatedBy());
        applyFinancialPaymentRecord.setApplicantTime(selectById.getApplicantTime());
        applyFinancialPaymentRecordMapper.insert(applyFinancialPaymentRecord);

        applyFinancialPayment.setOperateTime(new Date());
        applyFinancialPayment.setWhetherRepeat(request.getWhetherRepeat());

        UpdateByIdCheckState.update(baseMapper, applyFinancialPayment);

        ApplyFinancialPaymentOperateResult result = ApplyFinancialPaymentOperateResult.builder()
                .id(selectById.getId())
                .serialNo(selectById.getSerialNo())
                .shopId(selectById.getShopId())
                .createdBy(selectById.getCreatedBy())
                .createdTime(selectById.getCreatedTime())
                .state(applyFinancialPayment.getState().getDesc())
                .createdId(selectById.getCreatedId())
                .build();

        if (flag) {
            //申请打款 通过后发送事件
            List<BillPurchase> purchaseList = purchaseMapper.selectList(new LambdaQueryWrapper<BillPurchase>()
                    .eq(BillPurchase::getApplyPaymentSerialNo, selectById.getSerialNo()));
            if (purchaseList.size() > 0) {
                purchaseList
                        .forEach(p -> {

                                    billHandlerEventPublisher.publishEvent(new ApplyFinancialPaymentPassEvent(selectById.getId(),
                                            selectById.getSerialNo(), p.getSerialNo(), selectById.getTypePayment()));

                                    MallRecyclingOrder mallRecyclingOrder = recycleOrderService.list(Wrappers.<MallRecyclingOrder>lambdaQuery().eq(MallRecyclingOrder::getPurchaseId, p.getId())).stream().findAny().orElse(null);

                                    //回收
                                    if (ObjectUtils.isNotEmpty(mallRecyclingOrder) && mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.RECYCLE) {
                                        MallRecyclingOrder r = new MallRecyclingOrder();
                                        r.setId(mallRecyclingOrder.getId());
                                        r.setSymbol(0);
                                        recycleOrderService.updateById(r);
                                        mallPayOrderProducers.sendMsg(new RecycleMessage()
                                                .setRecycleType(mallRecyclingOrder.getRecycleType().getValue())
                                                .setType(mallRecyclingOrder.getType().getValue())
                                                .setRecycleId(mallRecyclingOrder.getId())
                                                .setAssessId(mallRecyclingOrder.getAssessId())
                                                .setBizOrderCode(mallRecyclingOrder.getBizOrderCode()));
                                    } else if (ObjectUtils.isNotEmpty(mallRecyclingOrder) && mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.BUY_BACK) {
                                        //回购
                                        MallRecyclingOrder r = new MallRecyclingOrder();
                                        r.setId(mallRecyclingOrder.getId());
                                        r.setSymbol(0);
                                        /*if(mallRecyclingOrder.getType() == PurchaseModeEnum.DISPLACE){
                                            r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.WAIT_DELIVER_LOGISTICS_COMPLETE);
                                        }*/
                                        recycleOrderService.updateById(r);
                                    }
                                }
                        );
            } else {

                Map<String, List<AccountStockRelation>> map = accountStockRelationMapper.selectList(Wrappers.<AccountStockRelation>lambdaQuery()
                                .eq(AccountStockRelation::getAfpId, selectById.getId())).stream()
                        .collect(Collectors.groupingBy(AccountStockRelation::getOriginSerialNo));
                map.forEach((k, v) -> billHandlerEventPublisher.publishEvent(new ApplyFinancialPaymentPassEvent(selectById.getId(),
                        selectById.getSerialNo(), k, selectById.getTypePayment())));
            }

            BillPurchaseTask billPurchaseTask = billPurchaseTaskService.list(Wrappers.<BillPurchaseTask>lambdaQuery()
                    .eq(BillPurchaseTask::getApplyFinancialPaymentId, applyFinancialPayment.getId())).stream().findAny().orElse(null);

            if (ObjectUtils.isNotEmpty(billPurchaseTask) && billPurchaseTask.getTaskState() == TaskStateEnum.APPLY_FIN) {
                BillPurchaseTask purchaseTask = new BillPurchaseTask();
                purchaseTask.setId(billPurchaseTask.getId());
                purchaseTask.setTransitionStateEnum(TaskStateEnum.TransitionEnum.QT_PASSED_IN_STORAGE);
                //采购已打款
                billPurchaseTaskService.updateByState(purchaseTask);
                result.setPurchaseTaskVO(ApplyFinancialPaymentOperateResult.PurchaseTaskVO.builder().serialNo(billPurchaseTask.getSerialNo()).build());
            }//此时作废在操作一系列不影响采购需求单的变化

        } else {
            //申请打款驳回
            if (null == selectById.getCustomerContactsId()) {
                log.info("customerContactsId not exist of ApplyFinancialPaymentServiceImpl and ApplyFinancialPayment = {}", JSON.toJSONString(selectById));
            } else {
//                CustomerContacts customerContacts = customerContactsMapper.selectById(selectById.getCustomerContactsId());
//                List<CustomerBalance> customerBalances = customerBalanceMapper.selectList(Wrappers.<CustomerBalance>lambdaQuery()
//                        .eq(CustomerBalance::getOriginSerialNo, selectById.getSerialNo()));
//                billHandlerEventPublisher.publishEvent(new ApplyFinancialPaymentCancelEvent(selectById.getRefundType(),
//                        customerContacts.getCustomerId(), selectById.getPricePayment(), selectById.getCustomerContactsId(), ApplyFinancialPaymentTypeEnum.BALANCE_REFUND
//                        , CollectionUtils.isNotEmpty(customerBalances) ? customerBalances.get(0).getUserId() : null, selectById.getSerialNo()
//                ));

            }
        }

        return result;
    }

    @Resource
    private AccountStockRelationMapper accountStockRelationMapper;

    @Resource
    private CustomerBalanceMapper customerBalanceMapper;

    @Override
    public ApplyFinancialPaymentDetailResult detail(ApplyFinancialPaymentDetailRequest request) {
        ApplyFinancialPaymentDetailResult result = ApplyFinancialPaymentConvert.INSTANCE
                .convert(this.baseMapper.selectById(request.getId()));
        if (result == null) {
            return null;
        }

        result.setSubjectPaymentName(purchaseSubjectMapper.selectNameById(result.getSubjectPayment()));

        List<Integer> collect = Lists.newArrayList(result.getDemanderStoreId(), result.getShopId()).stream().filter(Objects::nonNull).collect(Collectors.toList());
        Map<Integer, String> storeMap;
        if (collect.isEmpty()) {
            storeMap = new HashMap<>();
        } else {
            storeMap = storeManagementMapper.selectByIds(collect).stream().collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));
        }

        result.setDemanderStoreName(Objects.nonNull(storeMap) && storeMap.containsKey(result.getDemanderStoreId()) ? storeMap.get(result.getDemanderStoreId()) : "-");
        result.setShopName(Objects.nonNull(storeMap) && storeMap.containsKey(result.getShopId()) ? storeMap.get(result.getShopId()) : "-");
        List<ApplyFinancialPaymentRecord> list = applyFinancialPaymentRecordMapper
                .selectList(new LambdaQueryWrapper<ApplyFinancialPaymentRecord>()
                        .eq(ApplyFinancialPaymentRecord::getApplyFinancialPaymentId, request.getId())
                        .orderByDesc(ApplyFinancialPaymentRecord::getId));
        if (!list.isEmpty())
            result.setResult(list.get(0).getResult());

        //查询作废单列表
        result.setObsoleteRecordCount(obsoleteRecordMapper.selectCount(new LambdaQueryWrapper<ApplyFinancialPaymentObsoleteRecord>()
                .eq(ApplyFinancialPaymentObsoleteRecord::getAfpId, request.getId())));

        if (Objects.nonNull(result) && result.getState() != null && result.getState().equals(ApplyFinancialPaymentStateEnum.CANCEL.getValue())) {
            result.setPurchaseTaskId(Optional.ofNullable(billPurchaseTaskService.list(Wrappers.<BillPurchaseTask>lambdaQuery()
                    .eq(BillPurchaseTask::getApplyFinancialPaymentId, result.getId())).stream().findAny().orElse(null)).map(BillPurchaseTask::getId).orElse(null));
        }

        return result;
    }

    @Override
    public Page<ApplyFinancialPaymentRecordPageResult> approvedMemo(ApplyFinancialPaymentRecordRequest request) {

        return applyFinancialPaymentRecordMapper.getPage(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<ApplyFinancialPaymentPageQueryByConditionResult> queryByCondition(ApplyFinancialPaymentQueryByConditionRequest request) {
        return this.baseMapper.queryByCondition(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(ApplyFinancialPaymentCancelRequest request) {
        log.info("cancel function of ApplyFinancialPaymentServiceImpl start and request = {}", JSON.toJSONString(request));
        ApplyFinancialPayment financialPayment = this.baseMapper.selectOne(new LambdaQueryWrapper<ApplyFinancialPayment>()
                .eq(ApplyFinancialPayment::getSerialNo, request.getSerialNo()));
        if (ObjectUtils.isEmpty(financialPayment)) {
            throw new OperationRejectedException(OperationExceptionCode.PAYMENT_MATERIAL_NON_NULL);
        }
        switch (request.getUseScenario()) {
            //采购单 绑定申请打款单 将申请打款单变成已使用
            case PURCHASE_BINDING:
                this.baseMapper.updateById(ApplyFinancialPayment
                        .builder()
                        .id(financialPayment.getId())
                        .whetherUse(WhetherEnum.YES)
                        .build());
                break;
            //同行采购取消 根据当前状态 将申请打款单状态变成已取消
            case PURCHASE_PEER_CANCEL:
                //状态是 已经打款 已经取消的 不允许取消
                cancelAfp(financialPayment);
                break;
            //个人回收取消 将申请打款单变成未使用
            case RECYCLE_PERSON_CANCEL:
                //如果是手动创建
                if (WhetherEnum.YES.getValue().equals(financialPayment.getManualCreation())) {
                    cancelAfp(financialPayment);
                } else {
                    this.baseMapper.updateById(ApplyFinancialPayment
                            .builder()
                            .id(financialPayment.getId())
                            .whetherUse(WhetherEnum.NO)
                            .build());
                }
                break;
            case PURCHASE_UNBIND:
                this.baseMapper.updateById(ApplyFinancialPayment
                        .builder()
                        .id(financialPayment.getId())
                        .whetherUse(WhetherEnum.NO)
                        .build());
                break;
            case BALANCE_REFUND:
                //代打款并更为已取消
                cancelAfp(financialPayment);
                List<CustomerBalance> customerBalances = customerBalanceMapper.selectList(Wrappers.<CustomerBalance>lambdaQuery()
                        .eq(CustomerBalance::getOriginSerialNo, financialPayment.getSerialNo()));
                CustomerContacts customerContacts = customerContactsMapper.selectById(financialPayment.getCustomerContactsId());
                billHandlerEventPublisher.publishEvent(new ApplyFinancialPaymentCancelEvent(financialPayment.getRefundType(),
                        customerContacts.getCustomerId(), financialPayment.getPricePayment(), financialPayment.getCustomerContactsId(), ApplyFinancialPaymentTypeEnum.BALANCE_REFUND
                        , CollectionUtils.isNotEmpty(customerBalances) ? customerBalances.get(0).getUserId() : null, financialPayment.getSerialNo()
                ));
                break;
        }
    }

    @Override
    public BigDecimal usedPrice(Integer tagId) {
        return getBaseMapper().usedPrice(tagId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplyFinancialPaymentObsoleteRecordResult obsolete(ApplyFinancialPaymentObsoleteRequest request) {
        ApplyFinancialPayment applyFinancialPayment = new ApplyFinancialPayment();
        applyFinancialPayment.setId(request.getId());
        applyFinancialPayment.setTransitionStateEnum(ApplyFinancialPaymentStateEnum.TransitionEnum.PAID_TO_OBSOLETE);
        UpdateByIdCheckState.update(baseMapper, applyFinancialPayment);

        ApplyFinancialPayment payment = baseMapper.selectById(request.getId());
        ApplyFinancialPaymentObsoleteRecord record = ApplyFinancialPaymentObsoleteRecordConvert.INSTANCE.convertApplyFinancialPayment(payment);
        record.setAfpId(request.getId());
        record.setId(null);
        record.setState(ApplyFinancialPaymentStateEnum.OBSOLETE);
        obsoleteRecordMapper.insert(record);
        return ApplyFinancialPaymentObsoleteRecordResult.builder()
                .id(payment.getId())
                .serialNo(payment.getSerialNo())
                .shopId(payment.getShopId())
                .createdBy(payment.getCreatedBy())
                .createdTime(payment.getCreatedTime())
                .state(ApplyFinancialPaymentStateEnum.OBSOLETE.getDesc())
                .createdId(payment.getCreatedId())
                .build();
    }

    @Override
    public ApplyFinancialPayment cancelTask(ApplyFinancialPaymentOrderCancelTaskRequest request) {
        ApplyFinancialPayment convert = ApplyFinancialPaymentConvert.INSTANCE.convert(request);
        convert.setWhetherUse(WhetherEnum.YES);
        baseMapper.updateById(convert);

        return this.baseMapper.selectById(request.getId());
    }

    /**
     * 取消申请打款单
     *
     * @param financialPayment
     */
    private void cancelAfp(ApplyFinancialPayment financialPayment) {
        if (stateEnum.contains(financialPayment.getState())) {
            throw new OperationRejectedException(OperationExceptionCode.PAYMENT_MATERIAL_STATE_NOT_ALLOWED);
        }
        ApplyFinancialPayment applyFinancialPayment = new ApplyFinancialPayment();
        applyFinancialPayment.setId(financialPayment.getId());
        applyFinancialPayment.setTransitionStateEnum(ApplyFinancialPaymentStateEnum.REJECTED.equals(financialPayment.getState()) ?
                ApplyFinancialPaymentStateEnum.TransitionEnum.REJECTED_TO_CANCEL :
                ApplyFinancialPaymentStateEnum.TransitionEnum.PENDING_REVIEW_TO_CANCEL);
        UpdateByIdCheckState.update(baseMapper, applyFinancialPayment);
    }
}




