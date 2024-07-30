package com.seeease.flywheel.serve.maindata.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.fix.request.FixListRequest;
import com.seeease.flywheel.fix.result.FixListResult;
import com.seeease.flywheel.helper.request.BreakPriceAuditPageRequest;
import com.seeease.flywheel.helper.result.BreakPriceAuditPageResult;
import com.seeease.flywheel.maindata.IHomePageFacade;
import com.seeease.flywheel.maindata.result.HomePagePendingEventResult;
import com.seeease.flywheel.pricing.request.PricingListRequest;
import com.seeease.flywheel.pricing.result.PricingListResult;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateTask;
import com.seeease.flywheel.serve.allocate.service.BillAllocateTaskService;
import com.seeease.flywheel.serve.fix.service.BillFixService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.helper.service.BreakPriceAuditService;
import com.seeease.flywheel.serve.pricing.service.BillPricingService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author Tiro
 * @date 2023/4/13
 */
@DubboService(version = "1.0.0")
public class HomePageFacade implements IHomePageFacade {
    @Resource
    private BreakPriceAuditService breakPriceAuditService;
    @Resource
    private BillPricingService billPricingService;
    @Resource
    private StockService stockService;
    @Resource
    private BillAllocateTaskService billAllocateTaskService;
    @Resource
    private BillFixService billFixService;
    @Override
    public HomePagePendingEventResult pendingEvent() {
        //之后的 将方法抽出来吧
        Page<BreakPriceAuditPageResult> applyBreakPricePage = breakPriceAuditService.pageOf(BreakPriceAuditPageRequest.builder().status(1).build());

        PageResult<PricingListResult> pendingPricingPage = billPricingService.list(PricingListRequest.builder().pricingState(2).build());

        long recycleAllocationCount = stockService.queryUnallocatedGoodsCount();

        Page<BillAllocateTask> allocateTaskPage = billAllocateTaskService.page(Page.of(1, 10), Wrappers.<BillAllocateTask>lambdaQuery()
                .orderByDesc(BillAllocateTask::getId)
                .eq(BillAllocateTask::getTaskState, 1));

        //Page<FixListResult> page = billFixService.page(FixListRequest.builder().fixState(0).build());

        return HomePagePendingEventResult.builder()
                .transferredCount(allocateTaskPage.getTotal())
                .recycleAllocationCount(recycleAllocationCount)
                .pendingPricingCount(pendingPricingPage.getTotalCount())
                .applyBreakPriceCount(applyBreakPricePage.getTotal())
                //.pendingRepairCount(page.getTotal())
                .build();
    }
}
