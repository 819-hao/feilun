package com.seeease.flywheel.serve.allocate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.allocate.request.AllocateCreateRequest;
import com.seeease.flywheel.allocate.request.AllocateExportListRequest;
import com.seeease.flywheel.allocate.request.AllocateListRequest;
import com.seeease.flywheel.serve.allocate.convert.AllocateConverter;
import com.seeease.flywheel.serve.allocate.entity.BillAllocate;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateDTO;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateLine;
import com.seeease.flywheel.serve.allocate.entity.BillAllocatePO;
import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateStateEnum;
import com.seeease.flywheel.serve.allocate.mapper.BillAllocateLineMapper;
import com.seeease.flywheel.serve.allocate.mapper.BillAllocateMapper;
import com.seeease.flywheel.serve.allocate.service.BillAllocateService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.service.StockService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @description 针对表【bill_allocate(调拨单)】的数据库操作Service实现
 * @createDate 2023-03-07 10:39:58
 */
@Service
public class BillAllocateServiceImpl extends ServiceImpl<BillAllocateMapper, BillAllocate>
        implements BillAllocateService {
    @Resource
    private BillAllocateLineMapper lineMapper;
    @Resource
    private StockService stockService;

    /**
     * 分组单号
     *
     * @param serialNo
     * @param size
     * @param offs
     * @return
     */
    private String groupSerialNo(String serialNo, int size, int offs) {
        if (size == NumberUtils.INTEGER_ONE) {
            return serialNo;
        }
        return serialNo + "-" + offs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BillAllocateDTO> create(AllocateCreateRequest request) {
        Map<Integer, List<AllocateCreateRequest.AllocateLineDto>> groupDetails = request.getDetails()
                .stream()
                .collect(Collectors.groupingBy(AllocateCreateRequest.AllocateLineDto::getFromId));
        //分组校验
        groupDetails.values()
                .forEach(t -> {
                    Assert.isTrue(t.stream().map(AllocateCreateRequest.AllocateLineDto::getFromStoreId)
                            .distinct()
                            .count() == 1, "调出方仓库不唯一");
//                    Assert.isTrue(t.stream().map(AllocateCreateRequest.AllocateLineDto::getFromRightOfManagement)
//                            .distinct()
//                            .count() == 1, "调出方经营权不唯一");
                    Assert.isTrue(t.stream().map(AllocateCreateRequest.AllocateLineDto::getToRightOfManagement)
                            .distinct()
                            .count() == 1, "调入方经营权不唯一");
                });

        int size = groupDetails.size();
        AtomicInteger offs = new AtomicInteger(1);

        List<BillAllocateDTO> result = new ArrayList<>(size);
        //分组新增
        groupDetails.forEach((fid, details) -> {
            BillAllocate allocate = AllocateConverter.INSTANCE.convert(request);
            //出库方
            allocate.setFromId(fid);
            //出库仓库
            allocate.setFromStoreId(details.get(0).getFromStoreId());
            //分组单号
            allocate.setSerialNo(groupSerialNo(request.getSerialNo(), size, offs.getAndIncrement()));
            //状态
            allocate.setAllocateState(AllocateStateEnum.CREATE);
            allocate.setTotalNumber(details.size());
            //计算总成本
            allocate.setTotalCostPrice(details.stream().map(AllocateCreateRequest.AllocateLineDto::getCostPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            //计算总寄售
            allocate.setTotalConsignmentPrice(details.stream().map(AllocateCreateRequest.AllocateLineDto::getConsignmentPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            //新增调拨单
            baseMapper.insert(allocate);

            List<BillAllocateLine> lines = details.stream()
                    .map(t -> {
                        BillAllocateLine line = AllocateConverter.INSTANCE.convert(t);
                        line.setBelongingStoreId(allocate.getBelongingStoreId());
                        //设置行状态
                        line.setAllocateId(Objects.requireNonNull(allocate.getId()));
                        line.setAllocateLineState(AllocateLineStateEnum.INIT);

                        Stock stock = stockService.getById(t.getStockId());


                        if (null != stock.getNewSettlePrice() && !BigDecimal.ZERO.equals(stock.getNewSettlePrice())){
                            line.setNewSettlePrice(stock.getNewSettlePrice());
                        }else {
                            line.setNewSettlePrice(stock.getConsignmentPrice());
                        }


                        line.setTransferPrice(t.getTransferPrice() == null ? t.getNewSettlePrice() : t.getTransferPrice());
                        return line;
                    }).collect(Collectors.toList());




            //新增调拨单详情
            lineMapper.insertBatchSomeColumn(lines);

            //单据结果
            result.add(BillAllocateDTO.builder()
                    .allocate(allocate)
                    .lines(lines)
                    .build());
        });
        return result;
    }

    @Override
    public Page<BillAllocate> listByRequest(AllocateListRequest request) {
        return baseMapper.listByRequest(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<BillAllocate> exportListByRequest(AllocateExportListRequest request) {
        return baseMapper.exportListByRequest(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public int completeBrandTaskStatus(String serialNo, List<Integer> stockIdList) {
        return baseMapper.completeBrandTaskStatus(serialNo, stockIdList);
    }

    @Override
    public List<BillAllocatePO> selectByStockIds(List<Integer> stockIdList) {
        return baseMapper.selectByStockIds(stockIdList);
    }
}




