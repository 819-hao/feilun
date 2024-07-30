package com.seeease.flywheel.serve.purchase.strategy;

import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.springframework.exception.e.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 同行寄售-其他
 *
 * @author Tiro
 * @date 2023/3/2
 */
@Component
public class SendPeerOtherStrategy extends PurchaseStrategy {

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.TH_JS;
    }

    @Override
    void preRequestProcessing(PurchaseCreateRequest request) {
        request.setPurchaseMode(PurchaseModeEnum.OTHER.getValue());
        request.setApplyPaymentSerialNo(null);
    }

    @Override
    void checkRequest(PurchaseCreateRequest request) throws BusinessException {
        Assert.notNull(request.getCustomerId(), "供应商不能为空");
    }
}
