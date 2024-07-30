package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.account.IAccountFacade;
import com.seeease.flywheel.account.request.AccountDeleteRequest;
import com.seeease.flywheel.account.request.AccountQueryRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 18:09
 */
@Slf4j
@RestController
@RequestMapping("/account")
public class KeepAccountController {

    @DubboReference(check = false, version = "1.0.0")
    private IAccountFacade accountFacade;

    /**
     * 查询数据
     *
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody AccountQueryRequest request) {

        Optional.ofNullable(request.getPageType()).orElseThrow(() -> new IllegalArgumentException("必传参数不能为空"));

        return SingleResponse.of(accountFacade.list(request));
    }

    /**
     * 删除
     *
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public SingleResponse delete(@RequestBody AccountDeleteRequest request) {

        Optional.ofNullable(request.getList())
                .filter(Objects::nonNull)
                .orElseThrow(() -> new IllegalArgumentException("数据不能为空"));
        accountFacade.delete(request);
        return SingleResponse.buildSuccess();
    }
}
