package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.request.StockSnUpdateRequest;
import com.seeease.flywheel.qt.IQualityTestingFacade;
import com.seeease.flywheel.qt.request.QualityTestingEditRequest;
import com.seeease.flywheel.qt.request.QualityTestingLogRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Mr. Du
 * @Description 质检单
 * @Date create in 2023/2/4 11:01
 */
@Slf4j
@RestController
@RequestMapping("/qt")
public class QtController {

    @DubboReference(check = false, version = "1.0.0")
    private IQualityTestingFacade qualityTestingFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade iStockFacade;

    @PostMapping("/edit")
    public SingleResponse edit(@RequestBody QualityTestingEditRequest request) {

        qualityTestingFacade.edit(request);
        iStockFacade.updateStockSn(StockSnUpdateRequest.builder()
                .stockId(request.getStockId())
                .stockSn(request.getStockSn())
                .model(request.getModel())
                .workSource(request.getQtSource())
                .goodsId(request.getGoodsId()).build());
        return SingleResponse.buildSuccess();
    }

    @PostMapping("/log/list")
    public SingleResponse logList(@RequestBody QualityTestingLogRequest request) {

        return SingleResponse.of(qualityTestingFacade.logList(request));
    }

}
