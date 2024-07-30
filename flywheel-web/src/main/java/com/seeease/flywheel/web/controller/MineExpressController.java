package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.appexpress.IAppExpressFacade;
import com.seeease.flywheel.appexpress.request.AppExpressSubmitRequest;
import com.seeease.springframework.SingleResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;


/**
 * <p></p>
 *
 * @author 西门 游
 * @version 1.0
 * @since 5/10/24 6:08 下午
 **/
@RestController
@RequestMapping
public class MineExpressController {

    @DubboReference(check = false, version = "1.0.0")
    private IAppExpressFacade appExpressFacade;


    @GetMapping("mine/express")
    public SingleResponse queryMineExpress(@RequestParam String pageNum,
                                           @RequestParam String pageSize,
                                           @RequestParam(required = false) String code){
       return SingleResponse.of(appExpressFacade.queryPage(pageNum,pageSize,code));
    }

    @PostMapping("mine/express")
    public SingleResponse<Boolean> submitMineExpress(@RequestBody AppExpressSubmitRequest request){
         appExpressFacade.submit(request);
        return SingleResponse.of(Boolean.TRUE);
    }
}
