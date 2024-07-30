package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IGoodsWatchFacade;
import com.seeease.flywheel.goods.request.GoodsWatchInfoRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Slf4j
@RestController
@RequestMapping("/goodsWatch")
public class GoodsWatchController {
    @DubboReference(check = false, version = "1.0.0")
    private IGoodsWatchFacade facade;

    /**
     * 查型号信息
     *
     * @return
     */
    @PostMapping("/getAllList")
    public SingleResponse getAllList(@RequestBody GoodsWatchInfoRequest request) {
        return SingleResponse.of(facade.getAllList(request));
    }



}
