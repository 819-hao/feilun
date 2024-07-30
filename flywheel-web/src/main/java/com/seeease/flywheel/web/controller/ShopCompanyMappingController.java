package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.account.IShopCompanyMappingFacade;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 18:09
 */
@Slf4j
@RestController
@RequestMapping("/shopCompany")
public class ShopCompanyMappingController {

    @DubboReference(check = false, version = "1.0.0")
    private IShopCompanyMappingFacade shopCompanyMappingFacade;

    /**
     * 下拉选项
     *
     * @return
     */
    @PostMapping("/select")
    public SingleResponse listGoods() {
        return SingleResponse.of(shopCompanyMappingFacade.list());
    }
}
