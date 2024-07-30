package com.seeease.flywheel.serve.allocate.strategy;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.serve.allocate.enums.AllocateTypeEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 门店调拨
 *
 * @author Tiro
 * @date 2023/3/7
 */
@Component
public class AllocateStrategyMD extends AllocateStrategy {


    @Override
    void preRequestProcessing(AllocateCreateRequest request) {
        //设置调拨来源
        request.setAllocateSource(BusinessBillTypeEnum.MD_DB.getValue());
        //平调变更经营权
        if (AllocateTypeEnum.FLAT.getValue() == request.getAllocateType()) {
            Integer toSubjectId = Objects.requireNonNull(storeRelationshipSubjectService.getByShopId(request.getToId()).getSubjectId());
            request.getDetails().forEach(t -> t.setToRightOfManagement(toSubjectId));
        }
    }

    @Override
    void checkRequest(AllocateCreateRequest request) throws BusinessException {
        Assert.isTrue(request.getBelongingStoreId() != FlywheelConstant._ZB_ID, "调拨类型异常");
        //调入方是门店
        Assert.isTrue(request.getToId() != FlywheelConstant._ZB_ID, "调入方异常");
        request.getDetails().forEach(t -> {
            Assert.notNull(t.getStockId(), "库存商品不存在");
            Assert.notNull(t.getCostPrice(), "库存商品成本不存在");
            Assert.notNull(t.getConsignmentPrice(), "库存商品寄售价不存在");
            //调出方是门店
            Assert.isTrue(t.getFromId() != FlywheelConstant._ZB_ID, "调出方异常");
            Assert.isTrue(AllocateTypeEnum.FLAT.getValue().intValue() != request.getAllocateType()
                    || (Objects.nonNull(t.getToRightOfManagement()) && t.getToRightOfManagement() > 0), "调拨经营权异常");
            Assert.isTrue(StockStatusEnum.MARKETABLE.getValue().equals(t.getFromStockStatus()), "商品不允许调拨");
        });

        //平调校验经营权
        if (!request.isBrandTask() && AllocateTypeEnum.FLAT.getValue() == request.getAllocateType()) {
            Integer checkSubjectId = Objects.requireNonNull(storeRelationshipSubjectService.getByShopId(request.getBelongingStoreId()).getSubjectId());
            request.getDetails().forEach(t -> {
                if (t.getFromRightOfManagement() != checkSubjectId.intValue()) {
                    throw new OperationRejectedException(OperationExceptionCode.ALLOCATE_ERROR_1);
                }
            });
        }

    }

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.MD_DB;
    }
}
