package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.financial.IFinancialStatementFacade;
import com.seeease.flywheel.financial.request.HfTradeFlowSaveRequest;
import com.seeease.flywheel.web.entity.HfTradeFlow;
import com.seeease.flywheel.web.infrastructure.service.HfTradeFlowService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.utils.BigDecimalUtil;
import com.seeease.springframework.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author Tiro
 * @date 2024/3/5
 */
@Slf4j
@RestController
@RequestMapping("/hfTradeFlow")
public class HfTradeFlowController {
    @Resource
    private HfTradeFlowService hfTradeFlowService;

    @DubboReference(check = false, version = "1.0.0")
    private IFinancialStatementFacade facade;

    @PostMapping("/save")
    public SingleResponse save(@RequestBody HfTradeFlowSaveRequest request) {
        HfTradeFlow hfTradeFlow = new HfTradeFlow();
        hfTradeFlow.setTradeNo(request.getTradeNo());
        hfTradeFlow.setOrdAmt(BigDecimalUtil.centToYuan(request.getOrdAmt()));
        hfTradeFlow.setMobilePayType(request.getMobilePayType());
        hfTradeFlow.setMemberId(request.getMemberId());
        hfTradeFlow.setMerName(request.getMerName());
        hfTradeFlow.setTransDateTime(DateUtils.parseDate(request.getTransDateTime(), DateUtils.YMD_HMS2));
        hfTradeFlow.setDeviceId(request.getDeviceId());
        hfTradeFlow.setTermOrdId(request.getTermOrdId());
        hfTradeFlow.setOrdId(request.getPartOrderId());
        hfTradeFlow.setFlowState(WhetherEnum.NO.getValue());
        hfTradeFlow.setRemarks(request.getRemarks());

        try {
            facade.saveHfTradeFlow(request);
            hfTradeFlow.setFlowState(WhetherEnum.YES.getValue());
        } catch (Exception e) {
            log.error("汇付流水同步异常:{}", e.getMessage(), e);
        }

        return SingleResponse.of(hfTradeFlowService.save(hfTradeFlow));
    }
}
