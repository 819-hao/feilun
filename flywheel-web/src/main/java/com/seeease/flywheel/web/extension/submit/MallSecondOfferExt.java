package com.seeease.flywheel.web.extension.submit;

import com.alibaba.cola.extension.Extension;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.seeease.flywheel.common.StockLifeCycleResult;
import com.seeease.flywheel.recycle.IRecycleOderFacade;
import com.seeease.flywheel.recycle.request.RecycleOrderSecondOfferRequest;
import com.seeease.flywheel.recycle.result.RecycleOrderSecondOfferResult;
import com.seeease.flywheel.web.common.work.cmd.SubmitCmd;
import com.seeease.flywheel.web.common.work.pti.SubmitExtPtI;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr. Du
 * @Description 商城回收 3.客户经理首次报价
 * @Date create in 2023/9/8 15:42
 */
@Service
@Extension(bizId = BizCode.MALL, useCase = UseCase.SECOND_OFFER)
@Slf4j
public class MallSecondOfferExt implements SubmitExtPtI<RecycleOrderSecondOfferRequest, RecycleOrderSecondOfferResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IRecycleOderFacade recycleOderFacade;

    @Override
    public RecycleOrderSecondOfferResult submit(SubmitCmd<RecycleOrderSecondOfferRequest> cmd) {

        return recycleOderFacade.secondOffer(cmd.getRequest());
    }

    @Override
    public Map<String, Object> workflowVar(RecycleOrderSecondOfferRequest request, RecycleOrderSecondOfferResult result) {
        return new HashMap<>(0);
    }

    @Override
    public List<StockLifeCycleResult> lifeCycle(RecycleOrderSecondOfferRequest request, RecycleOrderSecondOfferResult result) {
        return Arrays.asList();
    }

    @Override
    public Class<RecycleOrderSecondOfferRequest> getRequestClass() {
        return RecycleOrderSecondOfferRequest.class;
    }

    @Override
    public void validate(SubmitCmd<RecycleOrderSecondOfferRequest> cmd) {
        Assert.notNull(cmd, "不能为空");
        Assert.notNull(cmd.getRequest(), "不能为空");
        Assert.notNull(cmd.getRequest().getId(), "不能为空");
        Assert.notNull(cmd.getRequest().getFiness(), "不能为空");
        //Assert.notNull(cmd.getRequest().getWatchSection(), "不能为空");
        Assert.notNull(cmd.getRequest().getGoodsId(), "不能为空");
        Assert.notNull(cmd.getRequest().getOfferList(), "不能为空");
        Assert.notNull(cmd.getRequest().getRecyclePrice(), "回收价不能为空");
        Assert.notNull(cmd.getRequest().getReplacementPrice(), "置换价不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getOfferList()), "不能为空");
        Assert.notNull(cmd.getRequest().getStockSn(), "不能为空");
    }
}
