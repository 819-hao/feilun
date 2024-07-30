package com.seeease.flywheel.web.controller.xianyu.strategy;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMethodEnum;
import com.seeease.flywheel.web.controller.xianyu.request.QiMenBaseRequest;
import com.seeease.flywheel.web.controller.xianyu.result.QiMenBaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Tiro
 * @date 2023/10/13
 */
@Slf4j
@Component
public class UndefinedRequestProcessor implements BaseQiMenRequestProcessor<QiMenBaseRequest, QiMenBaseResult> {
    @Override
    public Class<QiMenBaseRequest> requestClass() {
        return QiMenBaseRequest.class;
    }

    @Override
    public XianYuMethodEnum getMethodEnum() {
        return XianYuMethodEnum.UNDEFINED;
    }

    @Override
    public QiMenBaseResult execute(QiMenBaseRequest request) {
        log.info("闲鱼默认处理:{}", JSONObject.toJSONString(request));
        return new QiMenBaseResult();
    }
}
