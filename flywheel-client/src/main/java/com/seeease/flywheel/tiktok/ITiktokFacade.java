package com.seeease.flywheel.tiktok;

import com.seeease.flywheel.tiktok.request.TiktokLIveStreamSubmitRequest;

public interface ITiktokFacade {
    /**
     * 直播数据提交
     * @param request
     */
    void liveStreamSubmit(TiktokLIveStreamSubmitRequest request);
}
