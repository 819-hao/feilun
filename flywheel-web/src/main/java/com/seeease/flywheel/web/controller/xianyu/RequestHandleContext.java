package com.seeease.flywheel.web.controller.xianyu;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMethodEnum;
import com.seeease.flywheel.web.controller.xianyu.request.QiMenBaseRequest;
import com.seeease.flywheel.web.controller.xianyu.result.QiMenBaseResult;
import com.seeease.flywheel.web.controller.xianyu.strategy.BaseQiMenRequestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TOP商家处理奇门请求
 *
 * @author Tiro
 * @date 2023/10/13
 */
@Slf4j
@Component
public class RequestHandleContext {
    private final Map<XianYuMethodEnum, BaseQiMenRequestProcessor> processorCache;

    public RequestHandleContext(List<BaseQiMenRequestProcessor> processorList) {
        processorCache = processorList.stream()
                .filter(t -> Objects.nonNull(t.getMethodEnum()))
                .collect(Collectors.toMap(BaseQiMenRequestProcessor::getMethodEnum, Function.identity()));
    }

    /**
     * @param method
     * @param params
     * @param <T>
     * @return
     */
    public <T extends QiMenBaseRequest> Object handle(String method, JSONObject params) {
        BaseQiMenRequestProcessor processor = processorCache.get(XianYuMethodEnum.fromMethod(method));
        if (Objects.isNull(processor)) {
            log.warn("闲鱼奇门请求处理失败，未定义请求处理! {}", params.toJSONString());
            processor = processorCache.get(XianYuMethodEnum.UNDEFINED);
        }
        try {
            T request = params.toJavaObject((Type) processor.requestClass());
            return processor.execute(request);
        } catch (Exception e) {
            log.error("闲鱼奇门请求处理异常:{}", e.getMessage(), e);
            return QiMenBaseResult.buildFail();
        }

    }
}
