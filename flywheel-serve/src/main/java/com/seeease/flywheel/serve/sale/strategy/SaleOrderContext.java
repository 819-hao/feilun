package com.seeease.flywheel.serve.sale.strategy;

import com.seeease.flywheel.sale.request.SaleOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleOrderCreateResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.template.Bill;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderTypeEnum;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@Component
public class SaleOrderContext {
    private final Map<BusinessBillTypeEnum, SaleOrderStrategy> strategyCache;

    public SaleOrderContext(List<SaleOrderStrategy> strategyList) {
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
    public SaleOrderCreateResult create(SaleOrderCreateRequest request) {
        SaleOrderStrategy strategy = strategyCache.get(this.convert(request));
        if (Objects.isNull(strategy)) {
            throw new BusinessException(ExceptionCode.STRATEGY_EXCEPTION);
        }
        return strategy.crete(request);
    }

    /**
     * @param request
     * @return
     */
    private BusinessBillTypeEnum convert(SaleOrderCreateRequest request) {
        SaleOrderTypeEnum typeEnum = SaleOrderTypeEnum.fromCode(request.getSaleType());
        SaleOrderModeEnum modeEnum = SaleOrderModeEnum.fromCode(request.getSaleMode());
        switch (typeEnum) {
            case TO_C_XS:
                switch (modeEnum) {
                    case ON_LINE:
                        return BusinessBillTypeEnum.TO_C_ON_LINE;
                    default:
                        return BusinessBillTypeEnum.TO_C_XS;
                }

            case TO_B_JS:
                switch (modeEnum) {
                    case NORMAL:
                    case RETURN_POINT:
                        return BusinessBillTypeEnum.TO_B_XS;
                    case CONSIGN_FOR_SALE:
                        return BusinessBillTypeEnum.TO_B_JS;
                }

            default:
                throw new BusinessException(ExceptionCode.SALE_ORDER_TYPE_MODE_NOT_SUPPORT);
        }

    }
}
