package com.seeease.flywheel.serve.purchase.strategy;

import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.template.Bill;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseTypeEnum;
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
public class PurchaseContext {
    private final Map<BusinessBillTypeEnum, PurchaseStrategy> strategyCache;

    public PurchaseContext(List<PurchaseStrategy> strategyList) {
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
    public PurchaseCreateListResult create(PurchaseCreateRequest request) {
        PurchaseStrategy strategy = strategyCache.get(this.convert(request));
        if (Objects.isNull(strategy)) {
            throw new BusinessException(ExceptionCode.STRATEGY_EXCEPTION);
        }
        return strategy.crete(request);
    }

    /**
     * @param request
     * @return
     */
    private BusinessBillTypeEnum convert(PurchaseCreateRequest request) {
        PurchaseTypeEnum purchaseType = PurchaseTypeEnum.fromCode(request.getPurchaseType());
        PurchaseModeEnum purchaseMode = PurchaseModeEnum.fromCode(request.getPurchaseMode());

        switch (purchaseType) {
            case TH_CG:
                switch (purchaseMode) {
                    case DEPOSIT:
                        return BusinessBillTypeEnum.TH_CG_DJ;
                    case PREPARE:
                        return BusinessBillTypeEnum.TH_CG_BH;
                    case BATCH:
                        return BusinessBillTypeEnum.TH_CG_PL;
                    case FULL_PAYMENT:
                        return BusinessBillTypeEnum.TH_CG_QK;
                    case SPECIAL_GRANT_OF_DEPOSIT:
                        return BusinessBillTypeEnum.TH_CG_DJTP;
                }
            case TH_JS:
                return BusinessBillTypeEnum.TH_JS;
            case GR_JS:
                return BusinessBillTypeEnum.GR_JS;
            case GR_HS:
                switch (purchaseMode) {
                    case RECYCLE:
                        return BusinessBillTypeEnum.GR_HS_JHS;
                    case DISPLACE:
                        return BusinessBillTypeEnum.GR_HS_ZH;
                }
            case GR_HG:
                switch (purchaseMode) {
                    case RECYCLE:
                        return BusinessBillTypeEnum.GR_HG_JHS;
                    case DISPLACE:
                        return BusinessBillTypeEnum.GR_HG_ZH;
                }
            case JT_CG:
                switch (purchaseMode) {
                    case DEPOSIT:
                        return BusinessBillTypeEnum.JT_CG_DJ;
                    case PREPARE:
                        return BusinessBillTypeEnum.JT_CG_BH;
                    case BATCH:
                        return BusinessBillTypeEnum.JT_CG_PL;
                }
            default:
                throw new BusinessException(ExceptionCode.PURCHASE_TYPE_MODE_NOT_SUPPORT);
        }

    }
}
