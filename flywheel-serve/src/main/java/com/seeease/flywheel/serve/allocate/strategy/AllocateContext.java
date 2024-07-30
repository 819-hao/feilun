package com.seeease.flywheel.serve.allocate.strategy;


import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.allocate.result.AllocateCreateResult;
import com.seeease.flywheel.serve.allocate.enums.AllocateTypeEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.template.Bill;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/2
 */
@Component
public class AllocateContext {
    private final Map<BusinessBillTypeEnum, AllocateStrategy> strategyCache;

    public AllocateContext(List<AllocateStrategy> strategyList) {
        if (CollectionUtils.isEmpty(strategyList)) {
            throw new BusinessException(ExceptionCode.STRATEGY_EXCEPTION);
        }
        strategyCache = strategyList.stream()
                .filter(t -> Objects.nonNull(t.getType()))
                .collect(Collectors.toMap(Bill::getType, Function.identity()));
    }

    /**
     * @param request
     * @return
     */
    public AllocateCreateResult create(AllocateCreateRequest request) {
        AllocateStrategy strategy = strategyCache.get(this.convert(request));
        if (Objects.isNull(strategy)) {
            throw new BusinessException(ExceptionCode.STRATEGY_EXCEPTION);
        }
        return strategy.crete(request);
    }

    /**
     * @param request
     * @return
     */
    private BusinessBillTypeEnum convert(AllocateCreateRequest request) {

        AllocateTypeEnum type = AllocateTypeEnum.fromCode(request.getAllocateType());
        switch (type) {
            case FLAT:
            case BORROW:
                return BusinessBillTypeEnum.MD_DB;
            case CONSIGN:
                return BusinessBillTypeEnum.ZB_DB;
            case CONSIGN_RETURN:
                return BusinessBillTypeEnum.MD_DB_ZB;

            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
    }
}
