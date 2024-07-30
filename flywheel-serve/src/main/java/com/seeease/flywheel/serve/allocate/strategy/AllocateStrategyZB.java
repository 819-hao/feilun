package com.seeease.flywheel.serve.allocate.strategy;

import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.maindata.entity.Store;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 总部调拨
 *
 * @author Tiro
 * @date 2023/3/7
 */
@Component
public class AllocateStrategyZB extends AllocateStrategy {
    @Override
    void preRequestProcessing(AllocateCreateRequest request) {
        //设置调拨来源
        request.setAllocateSource(BusinessBillTypeEnum.ZB_DB.getValue());
        //变更经营权至门店
        Integer toSubjectId = Objects.requireNonNull(storeRelationshipSubjectService.getByShopId(request.getToId()).getSubjectId());
        request.getDetails().forEach(t -> t.setToRightOfManagement(toSubjectId));

        //补充新品调拨发货方
        List<AllocateCreateRequest.AllocateLineDto> goodsDetails = request.getDetails()
                .stream()
                .filter(t -> Objects.isNull(t.getStockId()))
                .collect(Collectors.toList());

        //总部可以全新商品调拨，出库时指定表身号
        if (CollectionUtils.isNotEmpty(goodsDetails)) {
            Integer shopId = request.getBelongingStoreId();
            Store store = storeService.selectByShopId(shopId);
            Integer subjectId = Objects.requireNonNull(storeRelationshipSubjectService.getByShopId(shopId).getSubjectId());
            goodsDetails.forEach(t -> {
                t.setFromId(shopId);
                t.setFromStoreId(store.getId());
                t.setFromRightOfManagement(subjectId);
                t.setCostPrice(BigDecimal.ZERO);
                t.setConsignmentPrice(BigDecimal.ZERO);
            });
        }
    }

    @Override
    void checkRequest(AllocateCreateRequest request) throws BusinessException {
        Assert.isTrue(request.getBelongingStoreId() == FlywheelConstant._ZB_ID, "调拨类型异常");
        //调入方是门店
        Assert.isTrue(request.getToId() != FlywheelConstant._ZB_ID, "调入方异常");
        request.getDetails().forEach(t -> {
            //调出方是总部
            Assert.isTrue(t.getFromId() == FlywheelConstant._ZB_ID, "调出方异常");
            Assert.isTrue(Objects.nonNull(t.getToRightOfManagement()) && t.getToRightOfManagement() > 0, "调拨经营权异常");
            Assert.isTrue(Objects.isNull(t.getStockId())
                    || StockStatusEnum.MARKETABLE.getValue().equals(t.getFromStockStatus()), "商品不允许调拨");
        });
    }

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.ZB_DB;
    }
}
