package com.seeease.flywheel.serve.purchase.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.purchase.request.PurchasePlanCreateRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanListRequest;
import com.seeease.flywheel.purchase.result.PurchasePlanCreateResult;
import com.seeease.flywheel.purchase.result.PurchasePlanExportResult;
import com.seeease.flywheel.purchase.result.PurchasePlanListResult;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.purchase.convert.PurchasePlanConverter;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlan;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlanLine;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchasePlanLineMapper;
import com.seeease.flywheel.serve.purchase.service.BillPurchasePlanService;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchasePlanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【bill_purchase_plan】的数据库操作Service实现
 * @createDate 2023-08-08 16:58:36
 */
@Service
public class BillPurchasePlanServiceImpl extends ServiceImpl<BillPurchasePlanMapper, BillPurchasePlan>
        implements BillPurchasePlanService {

    @Resource
    private BillPurchasePlanLineMapper planLineMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchasePlanCreateResult create(PurchasePlanCreateRequest request) {
        BillPurchasePlan purchasePlan = PurchasePlanConverter.INSTANCE.convertPlan(request);
        purchasePlan.setSerialNo(SerialNoGenerator.generatePurchasePlanSerialNo());
        purchasePlan.setPurchaseNumber(request.getDetails().stream()
                .mapToInt(PurchasePlanCreateRequest.BillPurchasePlanLineDto::getPlanNumber).sum());
        //新增采购计划
        this.baseMapper.insert(purchasePlan);
        List<BillPurchasePlanLine> planLineList = request.getDetails()
                .stream()
                .map(p -> {
                    BillPurchasePlanLine line = PurchasePlanConverter.INSTANCE.convertPlanLine(p);
                    line.setPlanId(purchasePlan.getId());
                    return line;
                }).collect(Collectors.toList());
        planLineMapper.insertBatchSomeColumn(planLineList);
        return PurchasePlanCreateResult.builder().id(purchasePlan.getId()).build();
    }

    @Override
    public Page<PurchasePlanListResult> listByRequest(PurchasePlanListRequest request) {
        return baseMapper.listByRequest(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<PurchasePlanExportResult> export(PurchasePlanListRequest request) {
        return baseMapper.export(Page.of(request.getPage(), request.getLimit()), request);
    }

}




