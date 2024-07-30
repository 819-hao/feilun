package com.seeease.flywheel.web.controller.xianyu.strategy;

import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMethodEnum;
import com.seeease.flywheel.web.controller.xianyu.request.QiMenBaseRequest;
import com.seeease.flywheel.web.controller.xianyu.result.QiMenBaseResult;

/**
 * 奇门请求处理
 *
 * @author Tiro
 * @date 2023/10/13
 */
public interface BaseQiMenRequestProcessor<T extends QiMenBaseRequest, R extends QiMenBaseResult> {

    /**
     * @return
     */
    Class<T> requestClass();

    /**
     * @return
     */
    XianYuMethodEnum getMethodEnum();

    /**
     * @param request
     * @return
     */
    R execute(T request);
}
