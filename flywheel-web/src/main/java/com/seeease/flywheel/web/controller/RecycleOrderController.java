package com.seeease.flywheel.web.controller;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.seeease.firework.facade.common.request.UpdateUserTaskRequest;
import com.seeease.firework.facade.service.ITaskFacade;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.*;
import com.seeease.flywheel.recycle.result.*;
import com.seeease.flywheel.web.common.work.cmd.CancelCmd;
import com.seeease.flywheel.web.common.work.cmd.CreateCmd;
import com.seeease.flywheel.web.common.work.cmd.QueryCmd;
import com.seeease.flywheel.web.common.work.executor.CancelCmdExe;
import com.seeease.flywheel.web.common.work.executor.CreateCmdExe;
import com.seeease.flywheel.web.common.work.executor.QueryCmdExe;
import com.seeease.flywheel.web.common.work.executor.SubmitCmdExe;
import com.seeease.flywheel.web.common.work.result.QuerySingleResult;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 回购、回收查询
 */
@Slf4j
@RestController
@RequestMapping("/recycle")
public class RecycleOrderController {

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade iRecycleOderFacade;

    @Resource
    private CreateCmdExe workCreateCmdExe;
    @Resource
    private CancelCmdExe cancelCmdExe;
    @Resource
    private SubmitCmdExe submitCmdExe;
    @Resource
    private QueryCmdExe queryCmdExe;

    /**
     * 远程调用
     */
    @DubboReference(check = false, version = "1.0.0")
    private ITaskFacade taskFacade;

    /**
     * 查询列表数据
     *
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse<PageResult<RecyclingListResult>> queryList(@RequestBody RecycleOrderListRequest request) {
        return SingleResponse.of(iRecycleOderFacade.list(request));
    }

    /**
     * 查询明细数据
     *
     * @param request
     * @return
     */
    @PostMapping("/recycleForSaleDetail")
    public SingleResponse<BuyBackForSaleResult> purchaseForSale(@RequestBody RecycleOrderVerifyRequest request) {
        return SingleResponse.of(iRecycleOderFacade.recycleForSaleDetail(request));
    }

//    /**
//     * 查询状态
//     */
//    @GetMapping("/statusList")
//    public SingleResponse<RecycleStatusList> statusList() {
//        return SingleResponse.of(iRecycleOderFacade.statusList());
//    }

    /**
     * 进行回收或置换保存操作
     */
    @PostMapping("/replacementOrRecycleCreate")
    public SingleResponse replacementOrRecycleCreate(@RequestBody ReplacementOrRecycleCreateRequest reqest) {
        return SingleResponse.of(iRecycleOderFacade.replacementOrRecycleCreate(reqest));
    }

    /**
     * 上传打款信息
     *
     * @param request
     * @return
     */
    @PostMapping("/uploadRemit")
    public SingleResponse<RecycleOrderResult> uploadRemit(@RequestBody MarkektRecycleUserBankRequest request) {
        return SingleResponse.of(iRecycleOderFacade.uploadRemit(request));
    }

    /**
     * 取消订单
     *
     * @param request
     * @return
     */
    @PostMapping("/cancelOrder")
    public SingleResponse<RecycleOrderClientCancelResult> cancelOrder(@RequestBody RecycleOrderClientCancelRequest request) {

        Assert.notNull(request, "请求不能为空");
        Assert.notNull(request.getId(), "请求ID不能为空");

        CancelCmd cancelCmd = new CancelCmd();
        cancelCmd.setBizCode(BizCode.MALL);
        cancelCmd.setUseCase(UseCase.CANCEL);
        cancelCmd.setRequest(request);

        return SingleResponse.of((RecycleOrderClientCancelResult) cancelCmdExe.cancel(cancelCmd));

    }

    /**
     * 更换用户
     *
     * @param request
     * @return
     */
    @PostMapping("/replaceUser")
    public SingleResponse replaceUser(@RequestBody RecycleReplaceUserRequest request) {

        Assert.notNull(request, "不能为空");
        Assert.notNull(request.getId(), "不能为空");

        RecycleReplaceUserResult result;

        /**
         * 任务详情
         */
        try {

            result = iRecycleOderFacade.replaceUser(request);

            QueryCmd<RecycleOrderDetailsRequest> cmd = new QueryCmd<RecycleOrderDetailsRequest>();
            cmd.setQueryTask(Boolean.TRUE);
            cmd.setBizCode(BizCode.MALL);
            cmd.setUseCase(UseCase.QUERY_DETAILS);
            cmd.setRequest(RecycleOrderDetailsRequest.builder().id(request.getId()).build());

            QuerySingleResult query = (QuerySingleResult) queryCmdExe.query(cmd);

            //变更工作流
            if (ObjectUtils.isNotEmpty(query) && ObjectUtils.isNotEmpty(query.getResult())) {
                UpdateUserTaskRequest build = UpdateUserTaskRequest.builder()
                        .taskId(query.getTask().getTaskId())
                        .userId(result.getUserId())
                        .fromUserId(result.getFromUserId())
                        .processInstanceId(query.getTask().getTaskId())
                        .build();
                taskFacade.updateByTaskUser(build);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return SingleResponse.buildFailure(800, "更换失败");
        }
        return SingleResponse.of(result);
    }

    /**
     * 付款的前提 肯定有销售单 开启销售流程
     * 模拟
     *
     * @param request
     * @return
     */
    @PostMapping("/clientPay")
    public SingleResponse<RecycleOrderPayResult> clientPay(@RequestBody MarkektRecyclePayRequest request) {

        RecycleOrderPayResult result = iRecycleOderFacade.clientPay(request);

        MarkektRecycleGetSaleProcessResult process = iRecycleOderFacade.getStartSaleProcess(MarkektRecycleGetSaleProcessRequest.builder().recycleId(request.getRecycleId()).build());

        try {
            if (ObjectUtils.isNotEmpty(process) && ObjectUtils.isNotEmpty(process.getSaleLoadRequest())) {
                CreateCmd cmd = new CreateCmd();
                cmd.setBizCode(BizCode.SALE);
                cmd.setUseCase(UseCase.PROCESS_LOAD);
                cmd.setRequest(process.getSaleLoadRequest());
                workCreateCmdExe.create(cmd);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return SingleResponse.of(result);
    }


}
