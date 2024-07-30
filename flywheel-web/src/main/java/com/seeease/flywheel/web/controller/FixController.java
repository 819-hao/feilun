package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.fix.IFixFacade;
import com.seeease.flywheel.fix.request.*;
import com.seeease.flywheel.fix.result.FixDelayResult;
import com.seeease.flywheel.fix.result.FixEditResult;
import com.seeease.flywheel.notify.IWxCpMessageFacade;
import com.seeease.flywheel.notify.entity.FixReceiveNotice;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/3 14:45
 */
@Slf4j
@RestController
@RequestMapping("/fix")
public class FixController {

    @DubboReference(check = false, version = "1.0.0")
    private IFixFacade fixFacade;

    @Resource
    private IWxCpMessageFacade wxCpMessageFacade;

    /**
     * 维修编辑
     *
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public SingleResponse edit(@RequestBody FixEditRequest request) {

        FixEditResult result = fixFacade.edit(request);

        if (Objects.nonNull(result.getFixSource()) && Arrays.asList(101, 102).contains(result.getFixSource()) && ObjectUtils.isNotEmpty(result.getShopId())) {

            wxCpMessageFacade.send(FixReceiveNotice.builder()
                    .createdBy(UserContext.getUser().getUserName())
                    .id(result.getId())
                    .createdTime(new Date())
                    .serialNo(result.getSerialNo())
                    .state(FlywheelConstant.FIX)
                    .toUserRoleKey(Arrays.asList("shopowner"))
                    .shopId(result.getShopId())
                    .build());
        }

        return SingleResponse.of(result);
    }

    @PostMapping("/delay")
    public SingleResponse delay(@RequestBody FixDelayRequest request) {

        FixDelayResult result = fixFacade.delay(request);

        if (Arrays.asList(101, 102).contains(result.getFixSource()) && ObjectUtils.isNotEmpty(result.getShopId())) {

            wxCpMessageFacade.send(FixReceiveNotice.builder()
                    .createdBy(UserContext.getUser().getUserName())
                    .id(result.getId())
                    .createdTime(new Date())
                    .serialNo(result.getSerialNo())
                    .state(FlywheelConstant.FIX)
                    .toUserRoleKey(Arrays.asList("shopowner"))
                    .shopId(result.getShopId())
                    .build());
        }

        return SingleResponse.buildSuccess();
    }


    @PostMapping("/log/list")
    public SingleResponse logList(@RequestBody FixLogRequest request) {

        return SingleResponse.of(fixFacade.logList(request));
    }

    /**
     * 是否加急
     *
     * @param request
     * @return
     */
    @PostMapping("/specialExpediting")
    public SingleResponse specialExpediting(@RequestBody FixSpecialExpeditingRequest request) {

        fixFacade.edit(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 甘特图
     *
     * @return
     */
    @PostMapping("/ganttChart")
    public SingleResponse ganttChart() {
        return SingleResponse.of(fixFacade.ganttChart());
    }

    /**
     * 维修结果集
     *
     * @param request
     * @return
     */
    @PostMapping("/editResult")
    public SingleResponse editResult(@RequestBody FixEditResultRequest request) {

        return SingleResponse.of(fixFacade.editResult(request));
    }

    /**
     * 维修师
     *
     * @return
     */
    @PostMapping("/maintenanceMasterList")
    public SingleResponse maintenanceMasterList() {
        return SingleResponse.of(fixFacade.maintenanceMasterList());
    }

    /**
     * 维修结果集
     *
     * @param request
     * @return
     */
    @PostMapping("/editMaintenance")
    public SingleResponse editMaintenance(@RequestBody FixMaintenanceRequest request) {
        return SingleResponse.of(fixFacade.editMaintenance(request));
    }
}
