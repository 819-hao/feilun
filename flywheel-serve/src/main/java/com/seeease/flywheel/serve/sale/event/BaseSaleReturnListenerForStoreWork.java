package com.seeease.flywheel.serve.sale.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/3/16
 */
public abstract class BaseSaleReturnListenerForStoreWork<E> {
    @Resource
    protected BillSaleReturnOrderService billSaleReturnOrderService;
    @Resource
    protected BillSaleReturnOrderLineService billSaleReturnOrderLineService;

    private static List<BusinessBillTypeEnum> SALE_RETURN_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TO_C_XS_TH,
            BusinessBillTypeEnum.TO_B_XS_TH
    );

    public void onApplicationEvent(List<BillStoreWorkPre> source, E e) {
        //事件匹配
        if (Objects.isNull(source)
                || CollectionUtils.isEmpty(source = source
                .stream().filter(t -> SALE_RETURN_TYPE.contains(t.getWorkSource()))
                .collect(Collectors.toList()))) return;

        //参数检查
        source.forEach(t -> {
            Assert.notNull(t.getOriginSerialNo(), "关联单号不能为空");
            Assert.notNull(t.getStockId(), "库存id不能为空");
        });
        //根据销售退货单分组
        Map<String, List<BillStoreWorkPre>> workGroup = source.stream()
                .collect(Collectors.groupingBy(BillStoreWorkPre::getOriginSerialNo));

        //查销售退货单
        Map<String, BillSaleReturnOrder> saleReturnMap = billSaleReturnOrderService.list(Wrappers.<BillSaleReturnOrder>lambdaQuery()
                        .in(BillSaleReturnOrder::getSerialNo, source.stream()
                                .map(BillStoreWorkPre::getOriginSerialNo)
                                .distinct()
                                .collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(BillSaleReturnOrder::getSerialNo, Function.identity()));

        //分组处理
        workGroup.forEach((originSerialNo, preList) -> {
            BillSaleReturnOrder saleReturnOrder = saleReturnMap.get(originSerialNo);
            if (Objects.isNull(saleReturnOrder)) {
                throw new BusinessException(ExceptionCode.SALE_RETURN_ORDER_BILL_NOT_EXIST);
            }

            this.handler(saleReturnOrder, preList, e);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    abstract void handler(BillSaleReturnOrder saleReturnOrder, List<BillStoreWorkPre> preList, E e);

}
