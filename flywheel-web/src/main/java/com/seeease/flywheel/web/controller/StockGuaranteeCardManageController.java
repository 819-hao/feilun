package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IStockGuaranteeCardManageFacade;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageEditRequest;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageListRequest;
import com.seeease.flywheel.goods.request.StockGuaranteeCardManageUpdateRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 保卡管理，和商品保卡信息无关，总部调拨管理用
 *
 * @author Tiro
 * @date 2023/11/20
 */
@Slf4j
@RestController
@RequestMapping("/stockGuaranteeCardManage")
public class StockGuaranteeCardManageController {

    @DubboReference(check = false, version = "1.0.0")
    private IStockGuaranteeCardManageFacade manageFacade;

    /**
     * 列表
     *
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody StockGuaranteeCardManageListRequest request) {
        return SingleResponse.of(manageFacade.list(request));
    }

    /**
     * 列表
     *
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public SingleResponse edit(@RequestBody StockGuaranteeCardManageUpdateRequest request) {
        //更新
        manageFacade.update(request);
        return SingleResponse.buildSuccess();
    }

    @PostMapping("/update")
    public SingleResponse update(@RequestBody StockGuaranteeCardManageEditRequest request) {
        //更新
        manageFacade.edit(request);
        return SingleResponse.buildSuccess();
    }
}
