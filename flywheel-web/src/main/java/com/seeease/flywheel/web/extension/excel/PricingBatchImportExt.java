package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.google.common.collect.Lists;
import com.seeease.firework.facade.common.dto.TaskDTO;
import com.seeease.firework.facade.common.request.TaskBatchQueryRequest;
import com.seeease.firework.facade.service.ITaskFacade;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.PricingFinishBatchRequest;
import com.seeease.flywheel.pricing.request.PricingFinishRequest;
import com.seeease.flywheel.pricing.request.PricingStockQueryImportRequest;
import com.seeease.flywheel.pricing.result.PricingStockQueryImportResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.consts.TaskDefinitionKeyEnum;
import com.seeease.flywheel.web.common.work.executor.SubmitCmdExe;
import com.seeease.flywheel.web.common.work.flow.UserTaskDto;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import jodd.util.ArraysUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description 批量定价导入
 * @Date create in 2023/3/31 10:51
 */
@Slf4j
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.PRICING_CREATE)
public class PricingBatchImportExt implements ImportExtPtl<PricingStockQueryImportRequest, PricingStockQueryImportResult> {

    @Autowired
    private ApplicationContext applicationContext;
    @Resource
    private SubmitCmdExe workflowCmdExe;

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;
    @DubboReference(check = false, version = "1.0.0")
    private ITaskFacade taskFacade;

    @Override
    public Class<PricingStockQueryImportRequest> getRequestClass() {
        return PricingStockQueryImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<PricingStockQueryImportRequest> cmd) {

        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        for (PricingStockQueryImportRequest.ImportDto importDto : cmd.getRequest().getDataList()) {
            /**
             * 表身号
             */
            if (StringUtils.isBlank(importDto.getStockSn())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
            }
            //去除前后空格
            importDto.setStockSn(importDto.getStockSn().trim());

            /**
             * b价 c价
             */
            if ((ObjectUtils.isEmpty(importDto.getBPrice()) || importDto.getBPrice().compareTo(BigDecimal.ZERO) == 0)
                    || (ObjectUtils.isEmpty(importDto.getCPrice()) || importDto.getCPrice().compareTo(BigDecimal.ZERO) == 0)
                    || importDto.getBPrice().compareTo(importDto.getCPrice()) >= 0
            ) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.PRICING_NON_NULL);
            }
            if (StringUtils.isEmpty(importDto.getGoodsLevel())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.GOODS_LEVEL_NON_NULL);
            }else {
                if (!Objects.equals(importDto.getGoodsLevel(), "压货") && !Objects.equals(importDto.getGoodsLevel(), "代销")){
                    throw new OperationRejectedException(OperationExceptionCodeEnum.GOODS_LEVEL_NON_NULL);
                }
            }


            if (Objects.isNull(importDto.getSalesPriority())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.SALES_PRIORITY_NON_NULL);
            }else {
                if (!(Lists.newArrayList("0","1","2").contains(importDto.getSalesPriority()))){
                    throw new OperationRejectedException(OperationExceptionCodeEnum.SALES_PRIORITY_ERROR);
                }
            }
            if (Objects.isNull(importDto.getConsignmentPrice())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.CONSIGNMENT_PRICE_REQUIRE_NON_NULL);
            }
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(PricingStockQueryImportRequest.ImportDto::getStockSn))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> e.getKey())
                .collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(repeatStockSn)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REPEAT, repeatStockSn);
        }
    }

    @Override
    public ImportResult<PricingStockQueryImportResult> handle(ImportCmd<PricingStockQueryImportRequest> cmd) {
        ImportResult<PricingStockQueryImportResult> importResult = pricingFacade.stockQueryImport(cmd.getRequest(),1);
        List<String> serialNoList = importResult.getSuccessList().stream().map(PricingStockQueryImportResult::getSerialNo).collect(Collectors.toList());

        TaskBatchQueryRequest taskBatchQueryRequest = new TaskBatchQueryRequest();
        taskBatchQueryRequest.setBusinessKeys(serialNoList);
        taskBatchQueryRequest.setOperator(UserContext.getUser().getUserid());
        taskBatchQueryRequest.setTaskDefinitionKeys(Lists.newArrayList(TaskDefinitionKeyEnum.WAIT_PRICING.getKey()));
        Map<String, TaskDTO> taskMap = taskFacade.listTaskByBusinessKeys(taskBatchQueryRequest);

        if (serialNoList.stream().anyMatch(t -> Objects.isNull(taskMap.get(t)))) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.PREVIOUS_STEP_WAIT_COMPLETE);
        }

        List<PricingFinishRequest> requestList = new ArrayList<>();
        List<UserTaskDto> taskDtoList = new ArrayList<>();
        importResult.getSuccessList()
                .forEach(t -> {

                    requestList.add(PricingFinishRequest.builder()
                            .id(t.getId())
                            .serialNo(t.getSerialNo())
                            .goodsLevel(t.getGoodsLevel())
                            .tobPrice(t.getTobPrice())
                            .tocPrice(t.getTocPrice())
                            .consignmentPrice(t.getConsignmentPrice())
                            .salesPriority(t.getSalesPriority())
                            .build());
                    TaskDTO task = taskMap.get(t.getSerialNo());
                    taskDtoList.add(UserTaskDto.builder()
                            .taskName(task.getTaskName())
                            .businessKey(task.getBusinessKey())
                            .parentBusinessKey(task.getParentBusinessKey())
                            .taskDefinitionKey(task.getTaskDefinitionKey())
                            .taskId(task.getTaskId())
                            .build());
                });

        SubmitCmd<PricingFinishBatchRequest> pricingFinishCmd = new SubmitCmd<>();
        pricingFinishCmd.setBizCode(BizCode.PRICING);
        pricingFinishCmd.setUseCase(UseCase.BATCH_PRICING);
        pricingFinishCmd.setRequest(PricingFinishBatchRequest.builder()
                .requestList(requestList)
                .build());
        pricingFinishCmd.setTaskList(taskDtoList);
        //任务提交
        workflowCmdExe.submit(pricingFinishCmd);

        return importResult;
    }
}
