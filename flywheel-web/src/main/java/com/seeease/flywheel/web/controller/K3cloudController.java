package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.k3cloud.IK3cloudGlVoucherFacade;
import com.seeease.flywheel.k3cloud.request.K3cloudGlVoucherRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/8/4 15:41
 */
@Slf4j
@RestController
@RequestMapping("/k3cloud")
public class K3cloudController {

//    @DubboReference(check = false, version = "1.0.0")
    @Resource
    private IK3cloudGlVoucherFacade facade;

    /**
     * 凭证导入
     *
     * @param request
     * @return
     */
    @PostMapping("/voucherExport")
    public SingleResponse voucherExport(@RequestBody K3cloudGlVoucherRequest request) {
        return SingleResponse.of(facade.executeBillQuery(request));
    }
}
