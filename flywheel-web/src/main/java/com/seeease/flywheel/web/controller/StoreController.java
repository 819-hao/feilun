package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.maindata.IStoreFacade;
import com.seeease.flywheel.maindata.request.StoreQuotaAddRequest;
import com.seeease.flywheel.maindata.request.StoreQuotaDelRequest;
import com.seeease.flywheel.maindata.request.StoreQuotaQueryRequest;
import com.seeease.flywheel.maindata.request.StoreQuotaRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;


/**
 * <p>门店</p>
 *
 * @author 西门 游
 * @version 1.0
 * @since 1/10/24
 **/
@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreFacade iStoreFacade;



    @PostMapping("/quota")
    public SingleResponse quotaAdd(@RequestBody StoreQuotaRequest request) {
        return SingleResponse.of(iStoreFacade.query(request.getShopId()));
    }

    /**
     * 新增配额
     * @return
     */
    @PostMapping("/quota/submit")
    public SingleResponse quotaAdd(@RequestBody StoreQuotaAddRequest request) {
        return SingleResponse.of(iStoreFacade.quotaSubmit(request));
    }

    /**
     * 配额分页查询
     * @return
     */
    @PostMapping("/quota/page")
    public SingleResponse quotaAdd(@RequestBody StoreQuotaQueryRequest request) {
        return SingleResponse.of(iStoreFacade.quotaPage(request));
    }


    /**
     * 配额删除
     * @return
     */
    @PostMapping("/quota/del")
    public SingleResponse quotaDel(@RequestBody StoreQuotaDelRequest request) {
        return SingleResponse.of(iStoreFacade.quotaDel(request.getId()));
    }


}
