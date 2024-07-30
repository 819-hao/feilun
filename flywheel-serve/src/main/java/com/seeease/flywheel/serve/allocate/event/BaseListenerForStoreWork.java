package com.seeease.flywheel.serve.allocate.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.service.BillAllocateLineService;
import com.seeease.flywheel.serve.allocate.service.BillAllocateService;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/3/16
 */
public abstract class BaseListenerForStoreWork<E> {
    @Resource
    protected BillAllocateService billAllocateService;
    @Resource
    protected BillAllocateLineService billAllocateLineService;
    /**
     * 调拨来源
     */
    private static List<BusinessBillTypeEnum> ALLOCATE_WORK = Lists.newArrayList(
            BusinessBillTypeEnum.ZB_DB,
            BusinessBillTypeEnum.MD_DB,
            BusinessBillTypeEnum.MD_DB_ZB
    );


    public void onApplicationEvent(List<BillStoreWorkPre> source, E e) {
        //事件匹配
        if (Objects.isNull(source)
                || CollectionUtils.isEmpty(source = source
                .stream().filter(t -> ALLOCATE_WORK.contains(t.getWorkSource()))
                .collect(Collectors.toList()))) {
            return;
        }
        //参数检查
        source.forEach(t -> {
            Assert.notNull(t.getOriginSerialNo(), "关联单号不能为空");
            Assert.notNull(t.getStockId(), "库存id不能为空");
        });

        //根据调拨单分组
        Map<String, List<BillStoreWorkPre>> workGroup = source.stream()
                .collect(Collectors.groupingBy(BillStoreWorkPre::getOriginSerialNo));

        //查调拨单
        Map<String, BillAllocate> allocateMap = billAllocateService.list(Wrappers.<BillAllocate>lambdaQuery()
                        .in(BillAllocate::getSerialNo, source.stream()
                                .map(BillStoreWorkPre::getOriginSerialNo)
                                .distinct()
                                .collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(BillAllocate::getSerialNo, Function.identity()));

        //分组处理
        workGroup.forEach((originSerialNo, preList) -> {
            BillAllocate allocate = allocateMap.get(originSerialNo);
            if (Objects.isNull(allocate)) {
                throw new BusinessException(ExceptionCode.ALLOCATE_BILL_NOT_EXIST);
            }
            //排序
            preList.sort(Comparator.comparing(BillStoreWorkPre::getStockId));
            this.handler(allocate, preList, e);
        });
    }

    /**
     * @param allocate
     * @param workPreList
     */
    @Transactional(rollbackFor = Exception.class)
    abstract void handler(BillAllocate allocate, List<BillStoreWorkPre> workPreList, E e);
}
