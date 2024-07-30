package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.maindata.IHomePageFacade;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wbh
 * @date 2023/3/1
 */
@Slf4j
@RestController
@RequestMapping("/homePage")
public class HomePageController {
    @DubboReference(check = false, version = "1.0.0")
    private IHomePageFacade facade;

    @PostMapping("/pendingEvent")
    public SingleResponse pendingEvent() {

        return SingleResponse.of(facade.pendingEvent());
    }
}
