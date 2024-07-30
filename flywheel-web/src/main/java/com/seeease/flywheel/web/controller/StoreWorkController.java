package com.seeease.flywheel.web.controller;



import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.*;
import com.seeease.flywheel.storework.result.StoreWorkPrintLabelResult;
import com.seeease.flywheel.web.common.work.executor.QueryCmdExe;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;

import org.springframework.util.Assert;

import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.*;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/10 15:04
 */
@Slf4j
@RestController
@RequestMapping("/storeWork")
public class StoreWorkController {
    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkQueryFacade storeWorkQueryFacade;

    @Resource
    private QueryCmdExe workDetailsCmdExe;


    @PostMapping("/edit")
    public SingleResponse edit(@RequestBody StoreWorkEditRequest request) {

        storeWorkFacade.edit(request);

        return SingleResponse.buildSuccess();
    }

    /**
     * 出库保存表身号
     *
     * @param request
     * @return
     */
    @PostMapping("/outStorageSupplyStock")
    public SingleResponse outStorageSupplyStock(@RequestBody StoreWorkOutStorageSupplyStockRequest request) {
        Assert.notNull(request, "参数不能为空");
        Assert.notNull(request.getOriginSerialNo(), "参数不能为空");
        Assert.notNull(request.getScenario(), "参数不能为空");
        Assert.notNull(request.getLineList(), "参数不能为空");
        Assert.isTrue(request.getLineList().stream().allMatch(t -> Objects.nonNull(t.getId()) && t.getId() > 0 && StringUtils.isNotBlank(t.getStockSn())), "参数异常");

        return SingleResponse.of(storeWorkFacade.outStorageSupplyStock(request));
    }

    @PostMapping("/printLabel")
    @Deprecated
    public SingleResponse printLabel(@RequestBody Map<String, List<Integer>> request) {

        List<StoreWorkPrintLabelResult> resultList = storeWorkQueryFacade.printLabel(request.get("stockIdList"));

        return SingleResponse.of(resultList);
    }

    @PostMapping("/log/list")
    public SingleResponse logList(@RequestBody StoreWorkLogRequest request) {
        request.setReceiptOrDelivery(false);
        //设置登陆门店
        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        //门店降级
        if (request.getBelongingStoreId() != FlywheelConstant._ZB_ID) {
            return SingleResponse.of(PageResult.builder()
                    .result(Collections.EMPTY_LIST)
                    .build());
        }
        return SingleResponse.of(storeWorkQueryFacade.logList(request));
    }

    @PostMapping("/workLog/list")
    public SingleResponse workLogList(@RequestBody StoreWorkLogRequest request) {
        request.setReceiptOrDelivery(true);
        //设置登陆门店
        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        //门店降级
        if (request.getBelongingStoreId() != FlywheelConstant._ZB_ID) {
            return SingleResponse.of(PageResult.builder()
                    .result(Collections.EMPTY_LIST)
                    .build());
        }
        return SingleResponse.of(storeWorkQueryFacade.logList(request));
    }

    /**
     * 待发货列表
     *
     * @param request
     * @return
     */
    @PostMapping("/delivery/list")
    public SingleResponse deliveryList(@RequestBody StoreWorkListRequest request) {
        request.setNeedAggregation(true);
        //设置登陆门店
        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        //门店降级
        if (request.getBelongingStoreId() != FlywheelConstant._ZB_ID) {
            return SingleResponse.of(PageResult.builder()
                    .result(Collections.EMPTY_LIST)
                    .build());
        }
        return SingleResponse.of(storeWorkQueryFacade.listDelivery(request));
    }

    @PostMapping("/outStorage/list")
    public SingleResponse outStorage(@RequestBody StoreWorkListRequest request) {
        request.setNeedAggregation(true);
        //设置登陆门店
        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        //门店降级
        if (request.getBelongingStoreId() != FlywheelConstant._ZB_ID) {
            return SingleResponse.of(PageResult.builder()
                    .result(Collections.EMPTY_LIST)
                    .build());
        }
        return SingleResponse.of(storeWorkQueryFacade.listOutStorage(request));
    }

    /**
     * 待发货查询同一型号数据
     *
     * @param request
     * @return
     */
    @PostMapping("/listDelivery/byMode")
    public SingleResponse deliveryByMode(@RequestBody StoreWorkListByModelRequest request) {
        request.setNeedAggregation(true);
        //设置登陆门店
        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        //门店降级
        if (request.getBelongingStoreId() != FlywheelConstant._ZB_ID) {
            return SingleResponse.of(PageResult.builder()
                    .result(Collections.EMPTY_LIST)
                    .build());
        }
        return SingleResponse.of(storeWorkQueryFacade.listDeliveryByMode(request));
    }


}
