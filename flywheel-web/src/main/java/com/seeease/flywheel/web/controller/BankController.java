package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.customer.IBankFacade;
import com.seeease.flywheel.customer.request.BankCreateRequest;
import com.seeease.flywheel.customer.request.BankQueryRequest;
import com.seeease.flywheel.customer.request.BankUpdateRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wbh
 * @date 2023/3/1
 */
@Slf4j
@RestController
@RequestMapping("/bank")
public class BankController {
    @DubboReference(check = false, version = "1.0.0")
    private IBankFacade facade;

    @PostMapping("/create")
    public SingleResponse create(@RequestBody BankCreateRequest request) {

        return SingleResponse.of(facade.create(request));
    }

    @PostMapping("/edit")
    public SingleResponse update(@RequestBody BankUpdateRequest request) {

        facade.update(request);

        return SingleResponse.buildSuccess();
    }

    @PostMapping("/list")
    public SingleResponse list(@RequestBody BankQueryRequest request) {


        return SingleResponse.of(facade.query(request));
    }
}