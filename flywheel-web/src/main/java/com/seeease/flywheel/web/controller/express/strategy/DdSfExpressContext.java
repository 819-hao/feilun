package com.seeease.flywheel.web.controller.express.strategy;

import com.seeease.flywheel.web.controller.express.request.ExpressCreateRequest;
import com.seeease.flywheel.web.controller.express.result.ExpressCreateResult;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.BusinessExceptionCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/9/1 17:51
 */
@Component
public class DdSfExpressContext {

    private final Map<Integer, DdSfExpressStrategy> strategyCache;

    public DdSfExpressContext(List<DdSfExpressStrategy> strategyList) {
        if (CollectionUtils.isEmpty(strategyList)) {
            throw new BusinessException(new BusinessExceptionCode() {
                @Override
                public int getErrCode() {
                    return 1000;
                }

                @Override
                public String getErrMsg() {
                    return "枚举没找到";
                }
            });
        }
        strategyCache = strategyList.stream()
                .filter(t -> Objects.nonNull(t.getReceiverType()))
                .collect(Collectors.toMap(DdSfExpressStrategy::getReceiverType, Function.identity()));
    }

    /**
     * @param request
     * @return
     */
    public ExpressCreateResult create(ExpressCreateRequest request) {
        DdSfExpressStrategy strategy = strategyCache.get(request.getPrintOptionResult().getPrintOption());
        if (Objects.isNull(strategy)) {
            throw new BusinessException(new BusinessExceptionCode() {
                @Override
                public int getErrCode() {
                    return 1000;
                }

                @Override
                public String getErrMsg() {
                    return "枚举没找到";
                }
            });
        }
        return strategy.handle(request);
    }
}
