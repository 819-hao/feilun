package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IStockManageShelvesInfoFacade;
import com.seeease.flywheel.goods.request.StockManageShelvesInfoListRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tiro
 * @date 2023/8/8
 */
@Slf4j
@RestController
@RequestMapping("/stockManageShelvesInfo")
public class StockManageShelvesInfoController {

    @DubboReference(check = false, version = "1.0.0")
    private IStockManageShelvesInfoFacade stockManageShelvesInfoFacade;

    /**
     * 货位流转码列表
     *
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody StockManageShelvesInfoListRequest request) {
        return SingleResponse.of(stockManageShelvesInfoFacade.list(request));
    }
}
