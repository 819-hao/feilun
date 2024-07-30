package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IStockManageInfoFacade;
import com.seeease.flywheel.goods.request.StockManageInfoListRequest;
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
@RequestMapping("/stockManageInfo")
public class StockManageInfoController {

    @DubboReference(check = false, version = "1.0.0")
    private IStockManageInfoFacade stockManageInfoFacade;

    /**
     * 列表
     *
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody StockManageInfoListRequest request) {
        return SingleResponse.of(stockManageInfoFacade.list(request));
    }
}
