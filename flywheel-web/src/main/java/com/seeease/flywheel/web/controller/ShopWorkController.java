package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.StoreWorkEditRequest;
import com.seeease.flywheel.storework.request.StoreWorkListByModelRequest;
import com.seeease.flywheel.storework.request.StoreWorkLogRequest;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * @author Tiro
 * @date 2023/3/10
 */
@Slf4j
@RestController
@RequestMapping("/shopWork")
public class ShopWorkController {
    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkQueryFacade storeWorkQueryFacade;


    @PostMapping("/edit")
    public SingleResponse edit(@RequestBody StoreWorkEditRequest request) {
        storeWorkFacade.edit(request);
        return SingleResponse.buildSuccess();
    }

    @PostMapping("/log/list")
    public SingleResponse logList(@RequestBody StoreWorkLogRequest request) {
        request.setReceiptOrDelivery(true);
        //设置登陆门店
        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        //总部降级
        if (request.getBelongingStoreId() == FlywheelConstant._ZB_ID) {
            return SingleResponse.of(PageResult.builder()
                    .result(Collections.EMPTY_LIST)
                    .build());
        }
        return SingleResponse.of(storeWorkQueryFacade.logList(request));
    }

    /**
     * 门店
     * @param request
     * @return
     */
    @PostMapping("/listDelivery/byMode")
    public SingleResponse deliveryByMode(@RequestBody StoreWorkListByModelRequest request) {
        request.setNeedAggregation(true);
        //设置登陆门店
        request.setBelongingStoreId(UserContext.getUser().getStore().getId());
        //门店降级
        if (request.getBelongingStoreId() == FlywheelConstant._ZB_ID) {
            return SingleResponse.of(PageResult.builder()
                    .result(Collections.EMPTY_LIST)
                    .build());
        }
        return SingleResponse.of(storeWorkQueryFacade.listDeliveryByMode(request));
    }
}
