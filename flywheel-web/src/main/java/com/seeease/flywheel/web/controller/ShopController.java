package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.maindata.IShopFacade;
import com.seeease.flywheel.maindata.request.ShopStaffListRequest;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tiro
 * @date 2023/5/5
 */
@Slf4j
@RestController
@RequestMapping("/shop")
public class ShopController {
    @DubboReference(check = false, version = "1.0.0")
    private IShopFacade shopFacade;

    /**
     * 店铺员工列表
     *
     * @param request
     * @return
     */
    @PostMapping("/staffList")
    public SingleResponse staffList(@RequestBody ShopStaffListRequest request) {
        request.setShopId(UserContext.getUser().getStore().getId());
        return SingleResponse.of(shopFacade.staffList(request));
    }

}
