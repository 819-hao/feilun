package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSON;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.financial.IFinancialStatementFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.springframework.SingleResponse;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 财务流水记录
 *
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@RestController
@RequestMapping("/financialStatement")
public class FinancialStatementController {
    @DubboReference(check = false, version = "1.0.0")
    private IFinancialStatementFacade facade;


    /**
     * 流水详情
     *
     * @param request
     * @return
     */
    @PostMapping("/detail")
    public SingleResponse detail(@RequestBody FinancialStatementDetailsRequest request) {
        return SingleResponse.of(facade.detail(request));
    }

    /**
     * 流水列表
     *
     * @param request
     * @return
     */
    @PostMapping("/queryAll")
    public SingleResponse all(@RequestBody FinancialStatementQueryAllRequest request) {
        Integer shopId = UserContext.getUser().getStore().getId();

        if (FlywheelConstant._ZB_ID != shopId) {
            request.setShopId(shopId);
        }
        return SingleResponse.of(facade.queryAll(request));
    }

    /**
     * 流水导出
     *
     * @param request
     * @return
     */
    @PostMapping("/export")
    public SingleResponse export(@RequestBody FinancialStatementQueryAllRequest request) {
        return SingleResponse.of(facade.export(request));
    }

    /**
     * 批量审核
     *
     * @param request
     * @return
     */
    @PostMapping("/batchAudit")
    public SingleResponse batchAudit(@RequestBody FinancialStatementBatchAuditRequest request) {
        facade.batchAudit(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 企业微信---新建确认收款---搜索流水
     *
     * @param request
     * @return
     */
    @PostMapping("/miniPageQuery")
    public SingleResponse miniPageQuery(@RequestBody FinancialStatementMiniPageQueryRequest request) {
        log.info("miniPageQuery function of FinancialStatementController start and request = {}", JSON.toJSONString(request));

        Integer shopId = UserContext.getUser().getStore().getId();

        if (FlywheelConstant._ZB_ID != shopId) {
            request.setShopId(shopId);
        }

        return SingleResponse.of(facade.miniPageQuery(request));
    }

    @PostMapping("/queryNotAudit")
    public SingleResponse allNotAudit(@RequestBody FinancialStatementQueryAllRequest request) {

        Integer shopId = UserContext.getUser().getStore().getId();

        if (FlywheelConstant._ZB_ID != shopId) {
            request.setShopId(shopId);
        }

        return SingleResponse.of(facade.allNotAudit(request));
    }

    @PostMapping("/querySubjectName")
    public SingleResponse querySubjectName(@RequestBody FinancialStatementSubjectNameQueryRequest request) {

        return SingleResponse.of(facade.subjectCompanyQry(request));
    }

    /**
     *  财务要求的 打款/收款 公司
     * @return
     */
    @PostMapping("/queryAllSubjectName")
    public SingleResponse queryAllSubjectName() {

        return SingleResponse.of(facade.queryAllSubjectName());
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/wx/create")
    public SingleResponse create(@RequestBody FinancialStatementCreateRequest request) {
        facade.create(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 匹配核销
     * @param request
     * @return
     */
    @PostMapping("/wx/matchingWriteOff")
    public SingleResponse matchingWriteOff(@RequestBody FinancialStatementMatchingWriteOffRequest request) {
        facade.matchingWriteOff(request);
        return SingleResponse.buildSuccess();
    }
}
