package com.seeease.flywheel.serve.sale.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
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
public abstract class BaseSaleListenerForStoreWork<E> {
    @Resource
    protected BillSaleOrderService billSaleOrderService;
    @Resource
    protected BillSaleOrderLineService billSaleOrderLineService;

    private static List<BusinessBillTypeEnum> SALE_TYPE = Lists.newArrayList(
            BusinessBillTypeEnum.TO_C_XS,
            BusinessBillTypeEnum.TO_B_JS,
            BusinessBillTypeEnum.TO_B_XS,
            BusinessBillTypeEnum.TO_C_ON_LINE
    );

    public void onApplicationEvent(List<BillStoreWorkPre> source, E e) {
        //事件匹配
        if (Objects.isNull(source)
                || CollectionUtils.isEmpty(source = source
                .stream().filter(t -> SALE_TYPE.contains(t.getWorkSource()))
                .collect(Collectors.toList()))) return;

        //参数检查
        source.forEach(t -> {
            Assert.notNull(t.getOriginSerialNo(), "关联单号不能为空");
            Assert.notNull(t.getStockId(), "库存id不能为空");
        });

        //根据销售单分组
        Map<String, List<BillStoreWorkPre>> workGroup = source.stream()
                .collect(Collectors.groupingBy(BillStoreWorkPre::getOriginSerialNo));

        //查销售单
        Map<String, BillSaleOrder> saleMap = billSaleOrderService.list(Wrappers.<BillSaleOrder>lambdaQuery()
                        .in(BillSaleOrder::getSerialNo, source.stream()
                                .map(BillStoreWorkPre::getOriginSerialNo)
                                .distinct()
                                .collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(BillSaleOrder::getSerialNo, Function.identity()));

        //分组处理
        workGroup.forEach((originSerialNo, preList) -> {
            BillSaleOrder sale = saleMap.get(originSerialNo);
            if (Objects.isNull(sale)) {
                throw new BusinessException(ExceptionCode.SALE_ORDER_BILL_NOT_EXIST);
            }

            this.handler(sale, preList, e);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    abstract void handler(BillSaleOrder saleOrder, List<BillStoreWorkPre> preList, E e);
}
