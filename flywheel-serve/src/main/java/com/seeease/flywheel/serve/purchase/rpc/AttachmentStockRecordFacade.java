package com.seeease.flywheel.serve.purchase.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.IAttachmentStockRecordFacade;
import com.seeease.flywheel.purchase.request.AttachmentStockImportRequest;
import com.seeease.flywheel.purchase.request.AttachmentStockRecordListRequest;
import com.seeease.flywheel.purchase.result.AttachmentStockImportResult;
import com.seeease.flywheel.purchase.result.AttachmentStockRecordListResult;
import com.seeease.flywheel.serve.base.PJStockSnGenerator;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.purchase.convert.AttachmentStockRecordConverter;
import com.seeease.flywheel.serve.purchase.entity.AttachmentStockImportDto;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStockRecord;
import com.seeease.flywheel.serve.goods.service.ExtAttachmentStockRecordService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Tiro
 * @date 2023/9/25
 */
@DubboService(version = "1.0.0")
public class AttachmentStockRecordFacade implements IAttachmentStockRecordFacade {
    @Resource
    private CustomerService customerService;
    @Resource
    private ExtAttachmentStockRecordService extAttachmentStockRecordService;

    @Override
    public PageResult<AttachmentStockRecordListResult> list(AttachmentStockRecordListRequest request) {

        LambdaQueryWrapper<ExtAttachmentStockRecord> wrapper = Wrappers.<ExtAttachmentStockRecord>lambdaQuery()
                .like(StringUtils.isNoneBlank(request.getPurchaseSerialNo()), ExtAttachmentStockRecord::getPurchaseSerialNo, request.getPurchaseSerialNo())
                .like(StringUtils.isNoneBlank(request.getCreatedBy()), ExtAttachmentStockRecord::getCreatedBy, request.getCreatedBy())
                .between(StringUtils.isNoneBlank(request.getStartTime()) && StringUtils.isNoneBlank(request.getEndTime()), ExtAttachmentStockRecord::getCreatedTime, request.getStartTime(), request.getEndTime());

        if (StringUtils.isNotBlank(request.getCustomerName())) {
            List<Integer> customerIdList = customerService.list(Wrappers.<Customer>lambdaQuery()
                            .like(Customer::getCustomerName, request.getCustomerName()))
                    .stream()
                    .map(Customer::getId)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(customerIdList)) {
                return PageResult.<AttachmentStockRecordListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .totalCount(NumberUtils.LONG_ZERO)
                        .totalPage(NumberUtils.LONG_ZERO)
                        .build();
            }
            wrapper.in(ExtAttachmentStockRecord::getCustomerId, customerIdList);
        }

        Page<ExtAttachmentStockRecord> pageResult = extAttachmentStockRecordService.page(Page.of(request.getPage(), request.getLimit()), wrapper);

        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            return PageResult.<AttachmentStockRecordListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .totalCount(pageResult.getTotal())
                    .totalPage(pageResult.getPages())
                    .build();
        }

        Map<Integer, String> customerMap = customerService.listByIds(pageResult.getRecords().stream()
                        .map(ExtAttachmentStockRecord::getCustomerId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Customer::getId, Customer::getCustomerName, (k1, k2) -> k1));

        return PageResult.<AttachmentStockRecordListResult>builder()
                .result(pageResult.getRecords()
                        .stream()
                        .map(t -> {
                            AttachmentStockRecordListResult r = AttachmentStockRecordConverter.INSTANCE.convert(t);
                            r.setCustomerName(customerMap.get(t.getCustomerId()));
                            return r;
                        })
                        .collect(Collectors.toList()))
                .totalCount(pageResult.getTotal())
                .totalPage(pageResult.getPages())
                .build();
    }

    @Override
    public AttachmentStockImportResult importHandle(AttachmentStockImportRequest request) {
        Assert.isTrue(CollectionUtils.isNotEmpty(request.getDataList()), "导入数据空");

        //数量转化
        List<AttachmentStockImportDto> dtoList = request.getDataList()
                .stream()
                .map(t -> IntStream.range(NumberUtils.INTEGER_ZERO, t.getNumber())
                        .mapToObj(i -> {
                            String stockSn = PJStockSnGenerator.generateStockSn();
                            AttachmentStockImportDto dto = AttachmentStockRecordConverter.INSTANCE.convertDto(t);
                            dto.setStockSn(stockSn + i);
                            dto.setFiness(FlywheelConstant.FINESS_S_99_NEW);
                            if (StringUtils.isNotBlank(t.getLengthSize())
                                    && StringUtils.isNotBlank(t.getWidthSize())) {
                                dto.setSize("S:" + StringUtils.join(Lists.newArrayList(t.getLengthSize(), t.getWidthSize()), " x "));
                            } else if (StringUtils.isNotBlank(t.getDiameterSize())
                                    && StringUtils.isNotBlank(t.getThicknessSize())
                                    && StringUtils.isNotBlank(t.getRadianSize())) {
                                dto.setSize("G:" + StringUtils.join(Lists.newArrayList(t.getDiameterSize(), t.getThicknessSize(), t.getRadianSize()), " x "));
                            } else if (StringUtils.isNotBlank(t.getMovementNo())) {
                                dto.setSize("M:" + t.getMovementNo());
                            } else if (StringUtils.isNotBlank(t.getBatteryModel())) {
                                dto.setSize("B:" + t.getBatteryModel());
                            }
                            return dto;
                        })
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<ExtAttachmentStockRecord> recordList = extAttachmentStockRecordService.importCreate(dtoList);

        return AttachmentStockImportResult.builder()
                .purchaseIdList(recordList.stream().map(ExtAttachmentStockRecord::getPurchaseId).collect(Collectors.toList()))
                .build();
    }
}
