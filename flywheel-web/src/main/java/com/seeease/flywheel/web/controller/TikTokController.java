package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.tiktok.ITiktokFacade;
import com.seeease.flywheel.tiktok.request.TiktokLIveStreamSubmitRequest;
import com.seeease.springframework.SingleResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 抖音
 */
@RestController
@RequestMapping("/tiktok")
public class TikTokController {
    @DubboReference(check = false, version = "1.0.0")
    private ITiktokFacade tiktokFacade;

    /**
     * 直播数据保存
     * @param request
     * @return
     */
    @PostMapping("/live/stream")
    private SingleResponse liveStreamSubmit(@RequestBody TiktokLIveStreamSubmitRequest request){
        tiktokFacade.liveStreamSubmit(request);
        return SingleResponse.buildSuccess();
    }


}
