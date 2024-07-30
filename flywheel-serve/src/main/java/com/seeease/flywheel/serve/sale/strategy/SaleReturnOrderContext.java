package com.seeease.flywheel.serve.sale.strategy;

import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.sale.result.SaleReturnOrderCreateResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.template.Bill;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderTypeEnum;
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
public class SaleReturnOrderContext {
    private final Map<BusinessBillTypeEnum, SaleReturnOrderStrategy> strategyCache;

    public SaleReturnOrderContext(List<SaleReturnOrderStrategy> strategyList) {
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
    public SaleReturnOrderCreateResult create(SaleReturnOrderCreateRequest request) {
        SaleReturnOrderStrategy strategy = strategyCache.get(this.convert(request));
        if (Objects.isNull(strategy)) {
            throw new BusinessException(ExceptionCode.STRATEGY_EXCEPTION);
        }
        return strategy.crete(request);
    }

    /**
     * @param request
     * @return
     */
    private BusinessBillTypeEnum convert(SaleReturnOrderCreateRequest request) {
        SaleReturnOrderTypeEnum typeEnum = SaleReturnOrderTypeEnum.fromCode(request.getSaleReturnType());

        switch (typeEnum) {
            case TO_C_XS_TH:
                return BusinessBillTypeEnum.TO_C_XS_TH;
            case TO_B_JS_TH:
                return BusinessBillTypeEnum.TO_B_XS_TH;
            default:
                throw new BusinessException(ExceptionCode.SALE_RETURN_ORDER_TYPE_MODE_NOT_SUPPORT);
        }

    }
}
