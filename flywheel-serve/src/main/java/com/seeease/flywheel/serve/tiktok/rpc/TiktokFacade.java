package com.seeease.flywheel.serve.tiktok.rpc;

import com.seeease.flywheel.serve.tiktok.convert.TiktokLiveStreamConvert;
import com.seeease.flywheel.serve.tiktok.service.TiktokLiveStreamService;
import com.seeease.flywheel.tiktok.ITiktokFacade;
import com.seeease.flywheel.tiktok.request.TiktokLIveStreamSubmitRequest;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(version = "1.0.0")
public class TiktokFacade implements ITiktokFacade {
    @Resource
    private TiktokLiveStreamService tiktokLiveStreamService;

    @Override
    public void liveStreamSubmit(TiktokLIveStreamSubmitRequest request) {
        tiktokLiveStreamService.save(TiktokLiveStreamConvert.INSTANCE.toDO(request));
    }
}
