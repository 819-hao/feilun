package com.seeease.flywheel.serve.purchase.strategy;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.springframework.exception.e.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 个人回购-去置换
 *
 * @author Tiro
 * @date 2023/3/2
 */
@Component
public class BuyBackPersonReplaceStrategy extends PurchaseStrategy {

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.GR_HG_ZH;
    }

    @Override
    void preRequestProcessing(PurchaseCreateRequest request) {

        /**
         * 回购处理
         */
        super.preProcessingBuyBack(request, true);
        request.setDemanderStoreId(request.getStoreId());
        //*********价格计算结束***********
        //本次销售单子的销售价
        request.setSalePrice(request.getSaleOrderDetailsResult().getTotalSalePrice());
    }

    @Override
    void checkRequest(PurchaseCreateRequest request) throws BusinessException {

        Assert.isTrue(request.getDetails().size() == FlywheelConstant.ONE, "个人回购只能单只表");

        Assert.notNull(request.getOriginSaleSerialNo(), "关联销售单号不能为空");
        //采购价从回购政策中取
        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getOriginStockId())), "关联表身号不能为空");

        Assert.notNull(request.getSaleSerialNo(), "本次销售单号不能为空");
        Assert.notNull(request.getSalePrice(), "本次销售价格不能为空");
        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getPlanFixPrice())), "预计维修价不能为空");
        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getFixPrice())), "实际维修价不能为空");
        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getClinchPrice())), "关联销售成交价不能为空");
        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getConsignmentPrice())), "寄售价不能为空");
        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getReferenceBuyBackPrice())), "参考回购价不能为空");
        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getBuyBackPrice())), "实际回购价不能为空");
        Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getRecycleServePrice())), "回购服务费不能为空");

    }
}
