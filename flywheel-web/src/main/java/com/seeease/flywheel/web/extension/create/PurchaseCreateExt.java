package com.seeease.flywheel.web.extension.create;

import com.alibaba.cola.extension.Extension;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.firework.facade.common.request.WaitTaskRequest;
import com.seeease.firework.facade.service.ITaskFacade;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.consts.BusinessMappingProcessEnum;
import com.seeease.flywheel.web.common.work.consts.OperationDescConst;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.consts.VariateDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.flow.ProcessInstanceStartDto;
import com.seeease.flywheel.web.common.work.pti.CreateExtPtI;
import com.seeease.flywheel.web.entity.WorkflowStart;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.flywheel.web.infrastructure.mapper.WorkflowStartMapper;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Service
@Slf4j
@Extension(bizId = BizCode.PURCHASE, useCase = UseCase.PROCESS_CREATE)
public class PurchaseCreateExt implements CreateExtPtI<PurchaseCreateRequest, PurchaseCreateListResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseFacade purchaseFacade;

    @Resource
    private WorkflowStartMapper workflowStartMapper;

    @DubboReference(check = false, version = "1.0.0")
    private ITaskFacade taskFacade;


    @Override
    public Class<PurchaseCreateRequest> getRequestClass() {
        return PurchaseCreateRequest.class;
    }

    @Override
    public void validate(CreateCmd<PurchaseCreateRequest> cmd) {

        //采购必要参数校验
        Assert.notNull(cmd, "请求不能为空");
        Assert.notNull(cmd.getRequest(), "请求入参不能为空");
        PurchaseCreateRequest request = cmd.getRequest();

        Assert.isTrue(CollectionUtils.isNotEmpty(request.getDetails()) &&
                request.getDetails().stream().allMatch(Objects::nonNull), "采购信息不能为空或者非法");

        Assert.notNull(request.getPurchaseType(), "采购类型不能为空");
        Assert.notNull(request.getPurchaseMode(), "采购方式不能为空");

        Assert.notNull(request.getCustomerContactId(), "联系人不能为空");
        Assert.notNull(request.getPurchaseSubjectId(), "采购主体不能为空");

//        Boolean isNull = false;
//
//        Boolean notNull = false;

        for (PurchaseCreateRequest.BillPurchaseLineDto detail : request.getDetails()) {

            /**
             * 型号
             */
            if (ObjectUtils.isEmpty(detail.getGoodsId())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.MODEL_REQUIRE_NON_NULL);
            }
            /**
             * 成色
             */
            if (StringUtils.isBlank(detail.getFiness())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.FINESS_REQUIRE_NON_NULL);
            }

            /**
             * 表身号
             */
            if (StringUtils.isBlank(detail.getStockSn())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
            }
            //去除前后空格
            detail.setStockSn(detail.getStockSn().trim());

            /**
             * 采购价
             */
//            //排除回购--
//            if (ObjectUtils.isEmpty(detail.getPurchasePrice()) || detail.getPurchasePrice().equals(BigDecimal.ZERO)) {
//                throw new OperationRejectedException(OperationExceptionCodeEnum.PURCHASE_PRICE_NON_NULL);
//            }

//            /**
//             * 销售优先等级
//             */
//            if (ObjectUtils.isEmpty((detail.getSalesPriority()))) {
//                throw new OperationRejectedException(OperationExceptionCodeEnum.SALES_PRIORITY_NON_NULL);
//            }
//
//            /**
//             * 商品等级
//             */
//            if (StringUtils.isBlank(detail.getGoodsLevel())) {
//                throw new OperationRejectedException(OperationExceptionCodeEnum.GOODS_LEVEL_NON_NULL);
//            }

            /**
             * 附件信息
             */
            if (CollectionUtils.isEmpty(detail.getAttachmentMap().keySet()) &&
                    (ObjectUtils.isEmpty(detail.getIsCard()) || (detail.getIsCard().equals(1) && StringUtils.isBlank(detail.getWarrantyDate())))
            ) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.ATTACHMENT_NON_NULL);
            }

//            if (ObjectUtils.isNotEmpty(detail.getOriginApplyPurchaseId())) {
//                notNull = true;
//            } else if (ObjectUtils.isEmpty(detail.getOriginApplyPurchaseId())) {
//                isNull = true;
//            }
        }
//        //采购方式不匹配
//        if (isNull && notNull) {
//            throw new OperationRejectedException(OperationExceptionCodeEnum.PURCHASE_CONFUSION);
//        }

        String repeatStockSn = request.getDetails().stream().collect(Collectors.groupingBy(PurchaseCreateRequest.BillPurchaseLineDto::getStockSn))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> e.getKey())
                .collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(repeatStockSn)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REPEAT, repeatStockSn);
        }

        if (ObjectUtils.isNotEmpty(request.getPurchaseTaskId())) {
            if (!(Arrays.asList(1).contains(request.getPurchaseType()) && Arrays.asList(1, 2, 7, 8).contains(request.getPurchaseMode()))) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.PURCHASE_TASK__NON_NULL);
            }
        }
    }

    @Override
    public PurchaseCreateListResult create(CreateCmd<PurchaseCreateRequest> cmd) {
        //设置登陆门店
        cmd.getRequest().setStoreId(UserContext.getUser().getStore().getId().intValue());

        return purchaseFacade.create(cmd.getRequest());
    }

    @Override
    public List<ProcessInstanceStartDto> start(PurchaseCreateRequest request, PurchaseCreateListResult result) {
        Map<String, Object> workflowVar = new HashMap<>();
        //总部 && 门店
        switch (request.getStoreId()) {
            case FlywheelConstant._ZB_ID:
                break;
            default:
                workflowVar.put(VariateDefinitionKeyEnum.SHORT_CODES.getKey(), result.getShortcodes());
                break;
        }

        if (ObjectUtils.isNotEmpty(result.getPurchaseTaskVO()) && StringUtils.isNotBlank(result.getPurchaseTaskVO().getSerialNo())) {

            WorkflowStart workflowStart = workflowStartMapper.selectList(Wrappers.<WorkflowStart>lambdaQuery()
                    .eq(WorkflowStart::getBusinessKey, result.getPurchaseTaskVO().getSerialNo())).stream().findAny().orElse(null);

            if (ObjectUtils.isNotEmpty(workflowStart) && StringUtils.isNotBlank(workflowStart.getProcessInstanceId())) {
                //执行某某
                taskFacade.waitTask(WaitTaskRequest.builder()
                        .processInstanceId(workflowStart.getProcessInstanceId())
                        .activityId(TaskDefinitionKeyEnum.PURCHASE_CREATE2.getKey())
                        .build());
            }
        }

        return Arrays.asList(ProcessInstanceStartDto.builder()
                .serialNo(result.getSerialNo()).variables(workflowVar)
                .process(BusinessMappingProcessEnum.fromValue(result.getBusinessKey()))
                .build());
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(PurchaseCreateRequest request, PurchaseCreateListResult result) {

        return result.getLine().stream().map(purchaseLineVO -> StockLifeCycleResult.builder()
                .originSerialNo(result.getSerialNo())
                .stockWno(purchaseLineVO.getWno()).operationDesc(OperationDescConst.PURCHASE_CREATE).build()).collect(Collectors.toList());
    }
}
