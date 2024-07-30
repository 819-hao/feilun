package com.seeease.flywheel.serve.allocate.strategy;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.springframework.exception.e.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 门店归还总部
 *
 * @author Tiro
 * @date 2023/3/7
 */
@Component
public class AllocateStrategyMDReturn extends AllocateStrategy {

    @Override
    void preRequestProcessing(AllocateCreateRequest request) {
        //设置调拨来源
        request.setAllocateSource(BusinessBillTypeEnum.MD_DB_ZB.getValue());
        //门店归还变更经营权至总部
        Integer toSubjectId = Objects.requireNonNull(storeRelationshipSubjectService.getByShopId(request.getToId()).getSubjectId());
        request.getDetails().forEach(t -> t.setToRightOfManagement(toSubjectId));
    }

    @Override
    void checkRequest(AllocateCreateRequest request) throws BusinessException {
        Assert.isTrue(request.getBelongingStoreId() != FlywheelConstant._ZB_ID, "调拨类型异常");
        //调入方是总部
        Assert.isTrue(request.getToId() == FlywheelConstant._ZB_ID, "调入方异常");
        request.getDetails().forEach(t -> {
            Assert.notNull(t.getStockId(), "库存商品不存在");
            Assert.notNull(t.getCostPrice(), "库存商品成本不存在");
            Assert.notNull(t.getConsignmentPrice(), "库存商品寄售价不存在");
            //调出方是门店
            Assert.isTrue(t.getFromId() != FlywheelConstant._ZB_ID, "调出方异常");
            Assert.isTrue(Objects.nonNull(t.getToRightOfManagement())
                    && t.getToRightOfManagement() > 0, "调拨经营权异常");
            Assert.isTrue(StockStatusEnum.MARKETABLE.getValue().equals(t.getFromStockStatus()), "商品不允许调拨");
        });
    }

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.MD_DB_ZB;
    }
}
