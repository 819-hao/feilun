package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.financial.IAuditLoggingFacade;
import com.seeease.flywheel.financial.request.AuditLoggingDetailRequest;
import com.seeease.flywheel.financial.request.AuditLoggingQueryRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 核销记录
 */
@Slf4j
@RestController
@RequestMapping("/auditLogging")
public class AuditLoggingController {
    @DubboReference(check = false, version = "1.0.0")
    private IAuditLoggingFacade facade;

    /**
     * 列表查询
     * @param request
     * @return
     */
    @PostMapping("/query")
    public SingleResponse query(@RequestBody AuditLoggingQueryRequest request) {

        return SingleResponse.of(facade.query(request));
    }

    /**
     * 详情
     * @param request
     * @return
     */
    @PostMapping("/detail")
    public SingleResponse detail(@RequestBody AuditLoggingDetailRequest request) {
        return SingleResponse.of(facade.detail(request));
    }
}
