package com.seeease.flywheel.web.controller;


import com.seeease.flywheel.maindata.IFirmShopFacade;
import com.seeease.flywheel.maindata.request.FirmShopQueryRequest;
import com.seeease.flywheel.maindata.request.FirmShopSubmitRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/4/13 11:31
 */
@Slf4j
@RestController
@RequestMapping("/firmshop")
public class FirmShopController {
    @DubboReference(check = false, version = "1.0.0")
    private IFirmShopFacade facade;

    /**
     * 新增更新
     * @param request
     * @return
     */
    @PostMapping("submit")
    private SingleResponse saveOrUpdate(@RequestBody FirmShopSubmitRequest request){
        facade.submit(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 删除
     * @param request
     * @return
     */
    @PostMapping("del")
    private SingleResponse del(@RequestBody FirmShopSubmitRequest request){
        facade.del(request.getId());
        return SingleResponse.buildSuccess();
    }



    /**
     * 删除
     * @param request
     * @return
     */
    @PostMapping("page")
    private SingleResponse page(@RequestBody FirmShopQueryRequest request){
        return SingleResponse.of(facade.page(request));
    }
}
