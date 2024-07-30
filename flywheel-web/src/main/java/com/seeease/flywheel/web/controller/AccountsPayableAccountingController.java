package com.seeease.flywheel.web.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.IAccountsPayableAccountingFacade;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应收应付
 *
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@RestController
@RequestMapping("/accountsPayableAccounting")
public class AccountsPayableAccountingController {
    @DubboReference(check = false, version = "1.0.0")
    private IAccountsPayableAccountingFacade facade;

    @NacosValue(value = "${subjectPayment.thpl:22}", autoRefreshed = true)
    private List<Integer> paymentThplList;

    /**
     * 列表查询
     *
     * @param request
     * @return
     */
    @PostMapping("/query")
    public SingleResponse query(@RequestBody AccountsPayableAccountingQueryRequest request) {

        return SingleResponse.of(facade.query(request));
    }

    /**
     * 导出
     *
     * @param request
     * @return
     */
    @PostMapping("/export")
    public SingleResponse export(@RequestBody AccountsPayableAccountingQueryRequest request) {

        return SingleResponse.of(facade.export(request));
    }

    /**
     * 批量审核
     *
     * @param request
     * @return
     */
    @PostMapping("/batchAudit")
    public SingleResponse batchAudit(@RequestBody AccountsPayableAccountingBatchAuditRequest request) {
        facade.batchAudit(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 创建申请打款单
     *
     * @param request
     * @return
     */
    @PostMapping("/createAfp")
    public SingleResponse createAfp(@RequestBody AccountsPayableAccountingCreateAfpRequest request) {

        Assert.isTrue(paymentThplList.contains(request.getSubjectPayment()), "打款账号不符合");

        facade.createAfp(request);

        return SingleResponse.buildSuccess();
    }
}
