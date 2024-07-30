package com.seeease.flywheel.web.controller;

/**
 * @author Tiro
 * @date 2023/9/1
 */

import com.seeease.flywheel.storework.IWmsWorkCollectFacade;
import com.seeease.flywheel.storework.request.WmsWaitWorkCollectRequest;
import com.seeease.flywheel.storework.request.WmsWorkCollectCountRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/workCollect")
public class WmsWorkCollectController {

    @DubboReference(check = false, version = "1.0.0")
    private IWmsWorkCollectFacade wmsWorkCollectFacade;

    /**
     * 导出型号+表身号分组统计商品及数量
     *
     * @param request
     * @return
     */
    @PostMapping("/count")
    public SingleResponse count(@RequestBody WmsWorkCollectCountRequest request) {
        return SingleResponse.of(wmsWorkCollectFacade.count(request.getRequest()));
    }


    /**
     * 集单
     *
     * @param request
     * @return
     */
    @PostMapping("/collectWork")
    public SingleResponse collectWork(@RequestBody WmsWaitWorkCollectRequest request) {
        return SingleResponse.of(wmsWorkCollectFacade.collectWork(request));
    }

}
