package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.allocate.IAllocateTaskFacade;
import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.allocate.request.AllocateTaskCreateRequest;
import com.seeease.flywheel.allocate.request.AllocateTaskListRequest;
import com.seeease.flywheel.allocate.result.AllocateTaskCreateResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 调拨任务
 *
 * @author Tiro
 * @date 2023/8/29
 */
@Slf4j
@RestController
@RequestMapping("/allocateTask")
public class AllocateTaskController {
    @Resource
    private CreateCmdExe workCreateCmdExe;

    @DubboReference(check = false, version = "1.0.0")
    private IAllocateTaskFacade allocateTaskFacade;

    /**
     * 创建
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public SingleResponse create(@RequestBody AllocateTaskCreateRequest request) {
        boolean isAdmin = UserContext.getUser().getRoles().stream().anyMatch(v->v.equals("admin"));
        if (!isAdmin){
            throw new RuntimeException("无权操作");
        }


        if (request.getAllocateStockList()
                .stream()
                .anyMatch(t -> Objects.isNull(t.getStockId())
                        || Objects.isNull(t.getToId())
                        || Objects.isNull(t.getToStoreId()))) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.ACCOUNT_NON_NULL);
        }

        List<String> errStockSnList = new ArrayList<>();
        request.getAllocateStockList()
                .stream()
                .collect(Collectors.groupingBy(t -> t.getRightOfManagement() + "_" + t.getToId())) //根据经营权和收货方分组
                .values()
                .forEach(lis -> {
                    try {
                        CreateCmd cmd = new CreateCmd();
                        cmd.setBizCode(BizCode.ALLOCATE);
                        cmd.setUseCase(UseCase.PROCESS_CREATE);
                        AllocateTaskCreateRequest.TaskDto taskDto = lis.get(0);
                        cmd.setRequest(AllocateCreateRequest.builder()
                                .allocateType(taskDto.getToId() == FlywheelConstant._ZB_ID ? AllocateTypeEnum.CONSIGN_RETURN.getValue()
                                        : AllocateTypeEnum.FLAT.getValue()) //平调,或寄售归还
                                .toId(taskDto.getToId())
                                .toStoreId(taskDto.getToStoreId())
                                .rightOfManagement(taskDto.getRightOfManagement())
                                .isBrandTask(true)
                                .remarks("品牌调拨任务")
                                .details(lis.stream()
                                        .map(t -> AllocateCreateRequest.AllocateLineDto.builder()
                                                .stockId(t.getStockId())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build());
                        workCreateCmdExe.create(cmd);
                    } catch (Exception e) {
                        log.error("调拨任务创建异常:{}", e.getMessage(), e);
                        errStockSnList.addAll(lis.stream().map(AllocateTaskCreateRequest.TaskDto::getStockSn).collect(Collectors.toList()));
                    }
                });

        return SingleResponse.of(AllocateTaskCreateResult.builder().errStockSnList(errStockSnList).build());
    }

    /**
     * 列表
     *
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody AllocateTaskListRequest request) {
        return SingleResponse.of(allocateTaskFacade.list(request));
    }

    @Getter
    @AllArgsConstructor
    private enum AllocateTypeEnum {

        CONSIGN(1, "寄售"),
        CONSIGN_RETURN(2, "寄售归还"),
        FLAT(3, "平调"),
        BORROW(4, "借调"),
        ;
        private Integer value;
        private String desc;
    }
}
