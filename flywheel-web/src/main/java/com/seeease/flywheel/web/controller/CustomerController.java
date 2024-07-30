package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSON;
import com.seeease.flywheel.customer.ICustomerFacade;
import com.seeease.flywheel.customer.request.*;
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
@RequestMapping("/customer")
public class CustomerController {
    @DubboReference(check = false, version = "1.0.0")
    private ICustomerFacade facade;

    @PostMapping("/create")
    public SingleResponse create(@RequestBody CustomerCreateRequest request) {

        return SingleResponse.of(facade.create(request));
    }

    @PostMapping("/edit")
    public SingleResponse edit(@RequestBody CustomerUpdateRequest request) {

        facade.update(request);

        return SingleResponse.buildSuccess();
    }

    /**
     * 供应商分页
     *
     * @param request
     * @return
     */
    @PostMapping("/customer/queryAll")
    public SingleResponse list(@RequestBody CustomerPageQueryRequest request) {

        return SingleResponse.of(facade.query(request));
    }

    /**
     * 联系人分页
     *
     * @param request
     * @return
     */
    @PostMapping("/contacts/queryAll")
    public SingleResponse list(@RequestBody ContactsPageQueryRequest request) {


        return SingleResponse.of(facade.query(request));
    }

    /**
     * 企业微信小程序-新建流水单，查询客户或联系人
     *
     * @param request
     * @return
     */
    @PostMapping("customerAndContractsPage")
    public SingleResponse customerAndContractQry(@RequestBody CustomerAndContactsPageQueryRequest request) {
        log.info("customerAndContractQry function start and request = {}", JSON.toJSONString(request));

        return SingleResponse.of(facade.customerAndContractsPageQry(request));
    }


}
