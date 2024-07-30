package com.seeease.flywheel.serve.qt.strategy;

import com.seeease.flywheel.qt.request.QualityTestingDecisionRequest;
import com.seeease.flywheel.qt.result.QualityTestingDecisionListResult;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
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
 * @Date create in 2023/3/4 13:44
 */
@Component
public class QtDecisionContext {

    private final Map<QualityTestingStateEnum, QtDecisionStrategy> strategyMap;

    public QtDecisionContext(List<QtDecisionStrategy> strategyList) {

        if (CollectionUtils.isEmpty(strategyList)) {
            throw new BusinessException(ExceptionCode.STRATEGY_EXCEPTION);
        }
        strategyMap = strategyList.stream()
                .filter(t -> Objects.nonNull(t.getState()))
                .collect(Collectors.toMap(QtDecisionStrategy::getState, Function.identity()));
    }

    /**
     * 判定
     * @param request
     * @return
     */
    public QualityTestingDecisionListResult decision(QualityTestingDecisionRequest request) {

        QtDecisionStrategy strategy = strategyMap.get(QualityTestingStateEnum.fromCode(request.getQtState()));

        if (Objects.isNull(strategy)) {
            throw new BusinessException(ExceptionCode.STRATEGY_EXCEPTION);
        }
        return strategy.decision(request);
    }
}
